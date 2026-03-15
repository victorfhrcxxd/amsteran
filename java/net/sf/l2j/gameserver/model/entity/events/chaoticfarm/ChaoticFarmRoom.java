package net.sf.l2j.gameserver.model.entity.events.chaoticfarm;

import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.autofarm.AutofarmManager;
import net.sf.l2j.gameserver.instancemanager.dimension.Instance;
import net.sf.l2j.gameserver.instancemanager.dimension.InstanceManager;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.util.Rnd;

public class ChaoticFarmRoom
{
	protected static final Logger _log = Logger.getLogger(ChaoticFarmRoom.class.getName());

	private final int _roomId;
	private final int _centerX;
	private final int _centerY;
	private final int _centerZ;
	private final int _ownerSpawnX;
	private final int _ownerSpawnY;
	private final int _ownerSpawnZ;
	private final int _challengerSpawnX;
	private final int _challengerSpawnY;
	private final int _challengerSpawnZ;
	private final int _mobSpawnX;
	private final int _mobSpawnY;
	private final int _mobSpawnZ;

	private Instance _instance;

	private volatile ChaoticFarmState _state = ChaoticFarmState.EMPTY;
	private volatile boolean _isSafe = false;

	private L2PcInstance _owner = null;
	private L2PcInstance _challenger = null;
	private int[] _ownerSavedLoc = null;
	private int[] _challengerSavedLoc = null;

	private L2MonsterInstance _currentMob = null;

	private ScheduledFuture<?> _farmingTimeoutTask = null;
	private ScheduledFuture<?> _duelCountdownTask = null;
	private ScheduledFuture<?> _boundaryTask = null;
	private ScheduledFuture<?> _respawnTask = null;
	private ScheduledFuture<?> _safeTask = null;

	public ChaoticFarmRoom(int roomId, ChaoticFarmConfig.RoomData data)
	{
		_roomId = roomId;
		_centerX = data.centerX;
		_centerY = data.centerY;
		_centerZ = data.centerZ;
		_ownerSpawnX = data.ownerX;
		_ownerSpawnY = data.ownerY;
		_ownerSpawnZ = data.ownerZ;
		_challengerSpawnX = data.challengerX;
		_challengerSpawnY = data.challengerY;
		_challengerSpawnZ = data.challengerZ;
		_mobSpawnX = data.mobX;
		_mobSpawnY = data.mobY;
		_mobSpawnZ = data.mobZ;
		_instance = InstanceManager.getInstance().createInstance();
	}

	// =========================================================
	// Accessors
	// =========================================================

	public int getRoomId()
	{
		return _roomId;
	}

	public ChaoticFarmState getState()
	{
		return _state;
	}

	public boolean isAvailableAsOwner()
	{
		return _state == ChaoticFarmState.EMPTY;
	}

	public boolean isAvailableAsChallenger()
	{
		return _state == ChaoticFarmState.FARMING && _challenger == null && !_isSafe;
	}

	public boolean hasPlayer(L2PcInstance player)
	{
		return _owner == player || _challenger == player;
	}

	public int getCurrentMobObjectId()
	{
		return _currentMob != null ? _currentMob.getObjectId() : -1;
	}

	public L2MonsterInstance getCurrentMob()
	{
		return _currentMob;
	}

	public L2PcInstance getOwner()
	{
		return _owner;
	}

	public L2PcInstance getChallenger()
	{
		return _challenger;
	}

	public boolean isSafe()
	{
		return _isSafe;
	}

	public boolean isSafeActive()
	{
		return _isSafe;
	}

	public int getInstanceId()
	{
		return _instance != null ? _instance.getId() : -1;
	}

	public int getCenterX()
	{
		return _centerX;
	}

	public int getCenterY()
	{
		return _centerY;
	}

	public int getCenterZ()
	{
		return _centerZ;
	}

	public L2PcInstance getOpponent(L2PcInstance player)
	{
		if (player == _owner)
			return _challenger;
		if (player == _challenger)
			return _owner;
		return null;
	}

	// =========================================================
	// Entry points — implemented in Step 2 and Step 3
	// =========================================================

	private static void disableAutoFarmIfActive(L2PcInstance player)
	{
		if (!player.isAutoFarm())
			return;

		AutofarmManager.INSTANCE.stopFarm(player);
		player.setAutoFarm(false);
		player.sendMessage("AutoFarm foi desativado ao entrar no Chaotic Farm.");
	}

	public synchronized void joinAsOwner(L2PcInstance player)
	{
		if (_state != ChaoticFarmState.EMPTY)
			return;

		disableAutoFarmIfActive(player);

		_ownerSavedLoc = new int[] { player.getX(), player.getY(), player.getZ() };
		_owner = player;
		_state = ChaoticFarmState.FARMING;

		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.setNewInstance(_instance, true);
		player.teleToLocation(_ownerSpawnX, _ownerSpawnY, _ownerSpawnZ, 0);
		player.broadcastUserInfo();

		player.sendMessage("Voce esta no controle da sala! Derrote o monstro!");

		spawnMob();
		startFarmingTimeout();
		startSafeTimer();

		ChaoticFarmManager.getInstance().announceGlobal(player.getName() + " iniciou o farm!");
	}

	public synchronized void joinAsChallenger(L2PcInstance player)
	{
		if (_state != ChaoticFarmState.FARMING || _challenger != null || _isSafe)
			return;

		disableAutoFarmIfActive(player);

		cancelFarmingTimeout();
		cancelSafeTimer();
		despawnMob();

		_challengerSavedLoc = new int[] { player.getX(), player.getY(), player.getZ() };
		_challenger = player;
		_state = ChaoticFarmState.DUEL_COUNTDOWN;

		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.setNewInstance(_instance, true);
		player.teleToLocation(_challengerSpawnX, _challengerSpawnY, _challengerSpawnZ, 0);
		player.broadcastUserInfo();

		if (_owner != null)
		{
			_owner.setCurrentHp(_owner.getMaxHp());
			_owner.setCurrentMp(_owner.getMaxMp());
			_owner.setCurrentCp(_owner.getMaxCp());
			_owner.teleToLocation(_ownerSpawnX, _ownerSpawnY, _ownerSpawnZ, 0);
			_owner.broadcastUserInfo();
		}

		announceToRoom("Um desafiante apareceu!");
		startDuelCountdown();
	}

	public synchronized void joinAsDuelPair(L2PcInstance owner, L2PcInstance challenger)
	{
		if (_state != ChaoticFarmState.EMPTY)
			return;

		disableAutoFarmIfActive(owner);
		disableAutoFarmIfActive(challenger);

		_ownerSavedLoc = new int[] { owner.getX(), owner.getY(), owner.getZ() };
		_challengerSavedLoc = new int[] { challenger.getX(), challenger.getY(), challenger.getZ() };
		_owner = owner;
		_challenger = challenger;
		_state = ChaoticFarmState.DUEL_COUNTDOWN;

		owner.setCurrentHp(owner.getMaxHp());
		owner.setCurrentMp(owner.getMaxMp());
		owner.setCurrentCp(owner.getMaxCp());
		owner.setNewInstance(_instance, true);
		owner.teleToLocation(_ownerSpawnX, _ownerSpawnY, _ownerSpawnZ, 0);
		owner.broadcastUserInfo();

		challenger.setCurrentHp(challenger.getMaxHp());
		challenger.setCurrentMp(challenger.getMaxMp());
		challenger.setCurrentCp(challenger.getMaxCp());
		challenger.setNewInstance(_instance, true);
		challenger.teleToLocation(_challengerSpawnX, _challengerSpawnY, _challengerSpawnZ, 0);
		challenger.broadcastUserInfo();

		ChaoticFarmManager.getInstance().announceGlobal(owner.getName() + " vs " + challenger.getName() + " - Duelo direto!");
		startDuelCountdown();
	}

	// =========================================================
	// Duel lifecycle — implemented in Step 3
	// =========================================================

	private void startDuelCountdown()
	{
		freezePlayer(_owner);
		freezePlayer(_challenger);
		final int freezeSecs = ChaoticFarmConfig.CF_FREEZE_SECONDS;
		sendScreenMessage(_owner, "DESAFIO INICIADO! Batalha em " + freezeSecs + "s...", freezeSecs + 1);
		sendScreenMessage(_challenger, "DESAFIO INICIADO! Batalha em " + freezeSecs + "s...", freezeSecs + 1);
		_duelCountdownTask = ThreadPoolManager.getInstance().scheduleGeneral(() -> startDuel(), freezeSecs * 1000L);
	}

	private synchronized void startDuel()
	{
		if (_state != ChaoticFarmState.DUEL_COUNTDOWN)
			return;

		_duelCountdownTask = null;
		_state = ChaoticFarmState.IN_DUEL;

		unfreezePlayer(_owner);
		unfreezePlayer(_challenger);

		announceToRoom("BATALHA INICIADA! Boa sorte!");
		startBoundaryCheck();
	}

	public synchronized void handleDeath(L2PcInstance dead)
	{
		if (!hasPlayer(dead))
			return;

		if (_state != ChaoticFarmState.IN_DUEL && _state != ChaoticFarmState.DUEL_COUNTDOWN)
			return;

		final L2PcInstance winner = (dead == _owner) ? _challenger : _owner;
		if (winner != null)
			declareWinner(winner, dead);
		else
		{
			resetRoom();
			ChaoticFarmManager.getInstance().processQueue();
		}
	}

	public synchronized void handleDisconnect(L2PcInstance player)
	{
		if (!hasPlayer(player))
			return;

		if (_state == ChaoticFarmState.FARMING)
		{
			cancelFarmingTimeout();
			cancelSafeTimer();
			despawnMob();
			removePlayer(player, false);
			_state = ChaoticFarmState.EMPTY;
			ChaoticFarmManager.getInstance().processQueue();
			return;
		}

		if (_state == ChaoticFarmState.DUEL_COUNTDOWN || _state == ChaoticFarmState.IN_DUEL)
		{
			final L2PcInstance winner = (player == _owner) ? _challenger : _owner;
			if (winner != null)
				declareWinner(winner, player);
			else
			{
				resetRoom();
				ChaoticFarmManager.getInstance().processQueue();
			}
		}
	}

	private synchronized void declareWinner(L2PcInstance winner, L2PcInstance loser)
	{
		if (winner == null || loser == null)
			return;

		_state = ChaoticFarmState.RESETTING;

		cancelBoundaryCheck();
		if (_duelCountdownTask != null)
		{
			_duelCountdownTask.cancel(false);
			_duelCountdownTask = null;
		}

		unfreezePlayer(_owner);
		unfreezePlayer(_challenger);

		final int[] winnerSavedLoc = (winner == _owner) ? _ownerSavedLoc : _challengerSavedLoc;

		ChaoticFarmManager.getInstance().announceGlobal(winner.getName() + " derrotou " + loser.getName() + " e dominou a sala!");

		loser.sendMessage("Voce foi derrotado!");
		if (loser.isDead())
			loser.doRevive();
		loser.setCurrentHp(loser.getMaxHp());
		loser.setCurrentMp(loser.getMaxMp());
		loser.setCurrentCp(loser.getMaxCp());
		loser.broadcastUserInfo();
		loser.setNewInstance(InstanceManager.getInstance().getInstance(0), true);
		if (loser.isOnline())
		{
			final int[] exitLoc = ChaoticFarmConfig.CF_EXIT_LOC;
			loser.teleToLocation(exitLoc[0], exitLoc[1], exitLoc[2], 0);
		}

		if (loser == _owner)
		{
			_owner = null;
			_ownerSavedLoc = null;
		}
		else
		{
			_challenger = null;
			_challengerSavedLoc = null;
		}

		winner.sendMessage("Voce dominou a sala!");
		winner.setCurrentHp(winner.getMaxHp());
		winner.setCurrentMp(winner.getMaxMp());
		winner.setCurrentCp(winner.getMaxCp());
		winner.broadcastUserInfo();
		if (winner.isOnline())
			winner.teleToLocation(_ownerSpawnX, _ownerSpawnY, _ownerSpawnZ, 0);

		_owner = winner;
		_ownerSavedLoc = winnerSavedLoc;
		_challenger = null;
		_challengerSavedLoc = null;

		_state = ChaoticFarmState.FARMING;
		spawnMob();
		startFarmingTimeout();
		startSafeTimer();

		ChaoticFarmManager.getInstance().processQueue();
	}

	// =========================================================
	// Farming timeout — phase-based, not per-player total time
	// =========================================================

	private void startFarmingTimeout()
	{
		cancelFarmingTimeout();
		_farmingTimeoutTask = ThreadPoolManager.getInstance().scheduleGeneral(
			() -> handleFarmingTimeout(),
			ChaoticFarmConfig.CF_MAX_FARM_SECONDS * 1000L);
	}

	private void cancelFarmingTimeout()
	{
		if (_farmingTimeoutTask != null)
		{
			_farmingTimeoutTask.cancel(false);
			_farmingTimeoutTask = null;
		}
	}

	private synchronized void handleFarmingTimeout()
	{
		if (_state != ChaoticFarmState.FARMING)
			return;

		_farmingTimeoutTask = null;

		if (_owner != null)
			_owner.sendMessage("Seu tempo na sala terminou!");

		cancelSafeTimer();
		despawnMob();
		removePlayer(_owner, true);
		_state = ChaoticFarmState.EMPTY;

		ChaoticFarmManager.getInstance().processQueue();
	}

	// =========================================================
	// Boundary check — activated in Step 3
	// =========================================================

	private void startBoundaryCheck()
	{
		cancelBoundaryCheck();
		_boundaryTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(
			() -> checkBoundaries(), 1000L, 1000L);
	}

	private void cancelBoundaryCheck()
	{
		if (_boundaryTask != null)
		{
			_boundaryTask.cancel(false);
			_boundaryTask = null;
		}
	}

	private synchronized void checkBoundaries()
	{
		if (_state != ChaoticFarmState.IN_DUEL)
			return;

		if (_owner != null && isOutOfBounds(_owner))
		{
			handleOutOfBounds(_owner);
			return;
		}

		if (_challenger != null && isOutOfBounds(_challenger))
			handleOutOfBounds(_challenger);
	}

	private synchronized void handleOutOfBounds(L2PcInstance player)
	{
		if (!hasPlayer(player))
			return;

		player.sendMessage("Voce saiu da area do duelo e foi derrotado!");

		final L2PcInstance winner = (player == _owner) ? _challenger : _owner;
		if (winner != null)
			declareWinner(winner, player);
		else
		{
			resetRoom();
			ChaoticFarmManager.getInstance().processQueue();
		}
	}

	private boolean isOutOfBounds(L2PcInstance player)
	{
		if (player == null || !player.isOnline())
			return true;
		final double dx = player.getX() - _centerX;
		final double dy = player.getY() - _centerY;
		return Math.sqrt(dx * dx + dy * dy) > ChaoticFarmConfig.CF_ALLOWED_RADIUS;
	}

	// =========================================================
	// Mob lifecycle — BonusZoneManager spawn/despawn pattern
	// =========================================================

	private void spawnMob()
	{
		if (_state != ChaoticFarmState.FARMING || _owner == null || _challenger != null || _currentMob != null)
			return;

		cancelRespawnTask();

		final NpcTemplate template = NpcTable.getInstance().getTemplate(ChaoticFarmConfig.CF_MOB_NPC_ID);
		if (template == null)
		{
			_log.warning("ChaoticFarm Room " + _roomId + ": NPC template not found for id=" + ChaoticFarmConfig.CF_MOB_NPC_ID);
			return;
		}

		try
		{
			final L2MonsterInstance mob = new L2MonsterInstance(IdFactory.getInstance().getNextId(), template);
			mob.setNewInstance(_instance, true);
			mob.spawnMe(_mobSpawnX, _mobSpawnY, _mobSpawnZ);
			mob.setCurrentHp(mob.getMaxHp());
			mob.setCurrentMp(mob.getMaxMp());
			_currentMob = mob;
		}
		catch (Exception e)
		{
			_log.warning("ChaoticFarm Room " + _roomId + ": Failed to spawn mob. " + e.getMessage());
		}
	}

	private void despawnMob()
	{
		cancelRespawnTask();

		if (_currentMob == null)
			return;

		_currentMob.deleteMe();
		_currentMob = null;
	}

	public synchronized void handleMobKill(L2PcInstance killer)
	{
		if (_state != ChaoticFarmState.FARMING || _owner == null || _challenger != null)
			return;

		_currentMob = null;
		giveDrops(killer);
		cancelRespawnTask();
		_respawnTask = ThreadPoolManager.getInstance().scheduleGeneral(
			() -> scheduleRespawnIfFarming(),
			ChaoticFarmConfig.CF_MOB_RESPAWN_SECONDS * 1000L);
	}

	private synchronized void scheduleRespawnIfFarming()
	{
		_respawnTask = null;
		if (_state != ChaoticFarmState.FARMING || _owner == null || _challenger != null)
			return;
		spawnMob();
	}

	private void cancelRespawnTask()
	{
		if (_respawnTask != null)
		{
			_respawnTask.cancel(false);
			_respawnTask = null;
		}
	}

	// =========================================================
	// Player management
	// =========================================================

	public synchronized void handleTeleportOut(L2PcInstance player)
	{
		if (!hasPlayer(player))
			return;

		if (_state != ChaoticFarmState.FARMING)
			return;

		if (!isOutOfBounds(player))
			return;

		cancelFarmingTimeout();
		cancelSafeTimer();
		despawnMob();
		removePlayer(player, false);
		_state = ChaoticFarmState.EMPTY;
		ChaoticFarmManager.getInstance().processQueue();
	}

	void removePlayer(L2PcInstance player, boolean teleportOut)
	{
		if (player == null)
			return;

		player.setCurrentHp(player.getMaxHp());
		player.setCurrentMp(player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());
		player.broadcastUserInfo();
		player.setNewInstance(InstanceManager.getInstance().getInstance(0), true);

		if (teleportOut && player.isOnline())
		{
			final int[] savedLoc = (player == _owner) ? _ownerSavedLoc : _challengerSavedLoc;
			if (savedLoc != null)
				player.teleToLocation(savedLoc[0], savedLoc[1], savedLoc[2], 0);
		}

		if (player == _owner)
		{
			_owner = null;
			_ownerSavedLoc = null;
		}
		else if (player == _challenger)
		{
			_challenger = null;
			_challengerSavedLoc = null;
		}
	}

	public synchronized void resetRoom()
	{
		cancelFarmingTimeout();
		cancelBoundaryCheck();
		cancelSafeTimer();

		if (_duelCountdownTask != null)
		{
			_duelCountdownTask.cancel(false);
			_duelCountdownTask = null;
		}

		unfreezePlayer(_owner);
		unfreezePlayer(_challenger);

		if (_owner != null)
			_owner.setNewInstance(InstanceManager.getInstance().getInstance(0), true);
		if (_challenger != null)
			_challenger.setNewInstance(InstanceManager.getInstance().getInstance(0), true);

		despawnMob();

		_owner = null;
		_challenger = null;
		_ownerSavedLoc = null;
		_challengerSavedLoc = null;
		_state = ChaoticFarmState.EMPTY;
		reinitInstance();
	}

	// =========================================================
	// Instance management
	// =========================================================

	private void reinitInstance()
	{
		if (_instance != null)
			InstanceManager.getInstance().deleteInstance(_instance.getId());
		_instance = InstanceManager.getInstance().createInstance();
	}

	// =========================================================
	// Drop delivery
	// =========================================================

	private void giveDrops(L2PcInstance killer)
	{
		if (killer == null || !killer.isOnline() || ChaoticFarmConfig.CF_MOB_DROPS.isEmpty())
			return;

		for (ChaoticFarmDrop drop : ChaoticFarmConfig.CF_MOB_DROPS)
		{
			if (Math.random() * 100 < drop.chance)
			{
				final int amount = (drop.min >= drop.max) ? drop.min : drop.min + Rnd.get(drop.max - drop.min + 1);
				killer.addItem("ChaoticFarm", drop.itemId, amount, null, true);
			}
		}
	}

	// =========================================================
	// Safe timer — blocks challengers for CF_SAFE_SECONDS
	// =========================================================

	private void startSafeTimer()
	{
		cancelSafeTimer();
		_isSafe = true;
		_safeTask = ThreadPoolManager.getInstance().scheduleGeneral(() -> endSafeTimer(), ChaoticFarmConfig.CF_SAFE_SECONDS * 1000L);
	}

	private synchronized void endSafeTimer()
	{
		_safeTask = null;
		_isSafe = false;
	}

	private void cancelSafeTimer()
	{
		if (_safeTask != null)
		{
			_safeTask.cancel(false);
			_safeTask = null;
		}
		_isSafe = false;
	}

	// =========================================================
	// Freeze helpers
	// =========================================================

	private void freezePlayer(L2PcInstance player)
	{
		if (player == null || !player.isOnline())
			return;
		player.stopMove(null);
		player.setIsImmobilized(true);
	}

	private void unfreezePlayer(L2PcInstance player)
	{
		if (player == null || !player.isOnline())
			return;
		player.setIsImmobilized(false);
	}

	// =========================================================
	// Announcements
	// =========================================================

	void announceToRoom(String message)
	{
		final CreatureSay cs = new CreatureSay(0, Say2.PARTY, "Chaotic Farm", message);
		if (_owner != null && _owner.isOnline())
			_owner.sendPacket(cs);
		if (_challenger != null && _challenger.isOnline())
			_challenger.sendPacket(cs);
	}

	void sendScreenMessage(L2PcInstance player, String message, int durationSeconds)
	{
		if (player != null && player.isOnline())
			player.sendPacket(new ExShowScreenMessage(message, durationSeconds * 1000));
	}
}
