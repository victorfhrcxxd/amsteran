package net.sf.l2j.gameserver.model.entity.events.chaoticfarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.util.Broadcast;

public class ChaoticFarmManager
{
	protected static final Logger _log = Logger.getLogger(ChaoticFarmManager.class.getName());

	private final List<ChaoticFarmRoom> _rooms = new ArrayList<>();
	private final Queue<L2PcInstance> _queue = new ConcurrentLinkedQueue<>();
	private volatile ScheduledFuture<?> _queueOwnerTask = null;

	private ChaoticFarmManager()
	{
		if (!ChaoticFarmConfig.CF_ENABLED)
		{
			_log.info("Chaotic Farm: System is disabled.");
			return;
		}

		final List<ChaoticFarmConfig.RoomData> rooms = ChaoticFarmConfig.CF_ROOMS;
		for (int i = 0; i < rooms.size(); i++)
			_rooms.add(new ChaoticFarmRoom(i, rooms.get(i)));

		_log.info("Chaotic Farm: Loaded " + _rooms.size() + " room(s). System enabled.");
	}

	// =========================================================
	// Join and leave requests
	// =========================================================

	public void requestJoin(L2PcInstance player)
	{
		if (!ChaoticFarmConfig.CF_ENABLED)
		{
			player.sendMessage("Chaotic Farm nao esta disponivel no momento.");
			return;
		}

		if (isInRoom(player))
		{
			player.sendMessage("Voce ja esta em uma sala do Chaotic Farm.");
			return;
		}

		if (isInQueue(player))
		{
			player.sendMessage("Voce ja esta na fila do Chaotic Farm.");
			return;
		}

		if (player.isDead())
		{
			player.sendMessage("Voce nao pode entrar no Chaotic Farm estando morto.");
			return;
		}

		if (player.isInOlympiadMode())
		{
			player.sendMessage("Voce nao pode entrar no Chaotic Farm durante a Olympiad.");
			return;
		}

		if (player.isTeleporting())
		{
			player.sendMessage("Voce nao pode entrar no Chaotic Farm durante um teleporte.");
			return;
		}

		_queue.add(player);
		player.sendMessage("Você entrou na fila da Chaotic Farm. Aguarde um oponente...");
		processQueue();
	}

	public void requestLeave(L2PcInstance player)
	{
		if (_queue.remove(player))
		{
			player.sendMessage("Voce saiu da fila do Chaotic Farm.");
			if (_queue.isEmpty())
				cancelQueueOwnerTask();
			return;
		}

		final ChaoticFarmRoom room = findRoomForPlayer(player);
		if (room == null)
		{
			player.sendMessage("Voce nao esta no Chaotic Farm.");
			return;
		}

		if (room.getState() == ChaoticFarmState.IN_DUEL || room.getState() == ChaoticFarmState.DUEL_COUNTDOWN)
		{
			player.sendMessage("Voce nao pode sair durante o duelo.");
			return;
		}

		room.resetRoom();
		player.sendMessage("Voce saiu do Chaotic Farm.");
		processQueue();
	}

	// =========================================================
	// Queue management
	// =========================================================

	public void processQueue()
	{
		// Remove invalid entries
		_queue.removeIf(p -> p == null || !p.isOnline() || p.isDead());

		// Rule 1: Two or more in queue — match as a direct duel pair
		if (_queue.size() >= 2)
		{
			cancelQueueOwnerTask();
			final L2PcInstance player1 = _queue.poll();
			final L2PcInstance player2 = _queue.poll();

			if (isDualbox(player1, player2))
			{
				player1.sendMessage("Aguardando oponente valido (anti-dualbox)...");
				_queue.add(player1);
				_queue.add(player2);
				synchronized (this)
				{
					if (_queueOwnerTask == null)
						_queueOwnerTask = ThreadPoolManager.getInstance().scheduleGeneral(
							() -> promoteQueueLeaderAsOwner(),
							ChaoticFarmConfig.CF_QUEUE_WAIT_SECONDS * 1000L);
				}
				return;
			}

			final ChaoticFarmRoom room = findEmptyRoom();
			if (room != null)
			{
				room.joinAsDuelPair(player1, player2);
			}
			else
			{
				player1.sendMessage("Sem salas disponíveis. Aguarde...");
				player2.sendMessage("Sem salas disponíveis. Aguarde...");
				_queue.add(player1);
				_queue.add(player2);
				return;
			}
			// Process any remaining queue entries
			if (!_queue.isEmpty())
				processQueue();
			return;
		}

		// Rule 3: One player in queue — check if a farming room is open for a challenger
		if (!_queue.isEmpty())
		{
			final L2PcInstance player = _queue.peek();
			final ChaoticFarmRoom challengerRoom = findRoomWithOwnerOnly();
			if (challengerRoom != null && !isDualbox(player, challengerRoom.getOwner()))
			{
				cancelQueueOwnerTask();
				_queue.poll();
				challengerRoom.joinAsChallenger(player);
				if (!_queue.isEmpty())
					processQueue();
				return;
			}

			// Rule 2: No opponent yet — start the lone-player owner timer if not already running
			synchronized (this)
			{
				if (_queueOwnerTask == null)
					_queueOwnerTask = ThreadPoolManager.getInstance().scheduleGeneral(
						() -> promoteQueueLeaderAsOwner(),
						ChaoticFarmConfig.CF_QUEUE_WAIT_SECONDS * 1000L);
			}
		}
		else
		{
			cancelQueueOwnerTask();
		}
	}

	private void promoteQueueLeaderAsOwner()
	{
		synchronized (this)
		{
			_queueOwnerTask = null;
		}

		_queue.removeIf(p -> p == null || !p.isOnline() || p.isDead());

		if (_queue.isEmpty())
			return;

		// A second player may have joined while the timer was running — match as pair instead
		if (_queue.size() >= 2)
		{
			processQueue();
			return;
		}

		final L2PcInstance player = _queue.poll();
		if (player == null || !player.isOnline() || player.isDead())
		{
			processQueue();
			return;
		}

		final ChaoticFarmRoom room = findEmptyRoom();
		if (room != null)
		{
			room.joinAsOwner(player);
		}
		else
		{
			player.sendMessage("Sem salas disponíveis no momento. Re-enfileirando...");
			_queue.add(player);
			synchronized (this)
			{
				if (_queueOwnerTask == null)
					_queueOwnerTask = ThreadPoolManager.getInstance().scheduleGeneral(
						() -> promoteQueueLeaderAsOwner(),
						ChaoticFarmConfig.CF_QUEUE_WAIT_SECONDS * 1000L);
			}
		}
	}

	private void cancelQueueOwnerTask()
	{
		synchronized (this)
		{
			if (_queueOwnerTask != null)
			{
				_queueOwnerTask.cancel(false);
				_queueOwnerTask = null;
			}
		}
	}

	// =========================================================
	// Anti-dualbox helper
	// =========================================================

	private boolean isDualbox(L2PcInstance p1, L2PcInstance p2)
	{
		if (p1 == null || p2 == null)
			return false;

		if (ChaoticFarmConfig.CF_ANTI_DUALBOX_CHECK_HWID)
		{
			final String h1 = p1.getHWid();
			final String h2 = p2.getHWid();
			if (h1 != null && !h1.isEmpty() && h1.equals(h2))
				return true;
		}

		if (ChaoticFarmConfig.CF_ANTI_DUALBOX_CHECK_IP)
		{
			if (p1.getClient() != null && !p1.getClient().isDetached() && p1.getClient().getConnection() != null
					&& p2.getClient() != null && !p2.getClient().isDetached() && p2.getClient().getConnection() != null)
			{
				final String ip1 = p1.getClient().getConnection().getInetAddress().getHostAddress();
				final String ip2 = p2.getClient().getConnection().getInetAddress().getHostAddress();
				if (ip1.equals(ip2))
					return true;
			}
		}

		return false;
	}

	// =========================================================
	// Room finders
	// =========================================================

	ChaoticFarmRoom findEmptyRoom()
	{
		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.isAvailableAsOwner())
				return room;
		}
		return null;
	}

	ChaoticFarmRoom findRoomWithOwnerOnly()
	{
		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.isAvailableAsChallenger())
				return room;
		}
		return null;
	}

	public ChaoticFarmRoom findRoomForPlayer(L2PcInstance player)
	{
		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.hasPlayer(player))
				return room;
		}
		return null;
	}

	// =========================================================
	// Player state checks
	// =========================================================

	public boolean isInRoom(L2PcInstance player)
	{
		return findRoomForPlayer(player) != null;
	}

	public boolean isInQueue(L2PcInstance player)
	{
		return _queue.contains(player);
	}

	public List<ChaoticFarmRoom> getRooms()
	{
		return Collections.unmodifiableList(_rooms);
	}

	public ChaoticFarmRoom getRoom(int roomId)
	{
		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.getRoomId() == roomId)
				return room;
		}
		return null;
	}

	public int getQueueSize()
	{
		return _queue.size();
	}

	public int getFarmingRoomCount()
	{
		int count = 0;
		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.getState() == ChaoticFarmState.FARMING)
				count++;
		}
		return count;
	}

	public int getDuelRoomCount()
	{
		int count = 0;
		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.getState() == ChaoticFarmState.IN_DUEL || room.getState() == ChaoticFarmState.DUEL_COUNTDOWN)
				count++;
		}
		return count;
	}

	// =========================================================
	// Event hooks — implemented in Step 4
	// =========================================================

	public void onPlayerDisconnect(L2PcInstance player)
	{
		final ChaoticFarmRoom room = findRoomForPlayer(player);
		if (room != null)
			room.handleDisconnect(player);
		else if (_queue.remove(player) && _queue.isEmpty())
			cancelQueueOwnerTask();
	}

	public void onPlayerTeleportOut(L2PcInstance player)
	{
		final ChaoticFarmRoom room = findRoomForPlayer(player);
		if (room != null)
			room.handleTeleportOut(player);
	}

	public void onMobDeath(int npcId, int objectId, L2PcInstance killer)
	{
		if (!ChaoticFarmConfig.CF_ENABLED || npcId != ChaoticFarmConfig.CF_MOB_NPC_ID)
			return;

		for (ChaoticFarmRoom room : _rooms)
		{
			if (room.getCurrentMobObjectId() == objectId)
			{
				room.handleMobKill(killer);
				return;
			}
		}
	}

	public void onPlayerDeath(L2PcInstance victim)
	{
		final ChaoticFarmRoom room = findRoomForPlayer(victim);
		if (room != null)
			room.handleDeath(victim);
	}

	// =========================================================
	// Announcements
	// =========================================================

	public void announceGlobal(String message)
	{
		Broadcast.gameAnnounceToOnlinePlayers("[Chaotic Farm]: " + message);
	}

	// =========================================================
	// Shutdown cleanup
	// =========================================================

	public void shutdown()
	{
		cancelQueueOwnerTask();
		for (ChaoticFarmRoom room : _rooms)
			room.resetRoom();

		_queue.clear();
		_log.info("Chaotic Farm: All rooms reset on shutdown.");
	}

	// =========================================================
	// Singleton
	// =========================================================

	public static ChaoticFarmManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final ChaoticFarmManager _instance = new ChaoticFarmManager();
	}
}
