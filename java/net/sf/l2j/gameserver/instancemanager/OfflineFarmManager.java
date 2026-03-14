package net.sf.l2j.gameserver.instancemanager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.util.Rnd;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.instancemanager.autofarm.AutofarmManager;
import net.sf.l2j.gameserver.model.L2Effect;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.L2CharPosition;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2WorldRegion;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2TeleporterInstance;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExAutoSoulShot;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.network.serverpackets.ServerClose;

public class OfflineFarmManager
{
	private static final Logger _log = Logger.getLogger(OfflineFarmManager.class.getName());

	private final ConcurrentHashMap<Integer, Long> _offlineFarmers = new ConcurrentHashMap<>();
	private ScheduledFuture<?> _timerTask;
	private ScheduledFuture<?> _eventTask;

	private OfflineFarmManager()
	{
		_timerTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::checkTimers, 60000, 60000);

		if (Config.OFFLINE_FARM_AUTO_EVENTS)
			_eventTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this::autoRegisterEvents, 30000, 30000);

		_log.info("OfflineFarmManager: Loaded.");
	}

	public void startOfflineFarm(L2PcInstance player)
	{
		if (player == null)
			return;

		int farmType = player.getOfflineFarmType();
		if (farmType == 0)
		{
			player.sendMessage("Selecione uma zona de farm primeiro.");
			return;
		}

		long endTime = player.getOfflineFarmEndTime();
		if (endTime <= 0 || endTime < System.currentTimeMillis())
		{
			player.sendMessage("Seu tempo de Offline Farm expirou. Renove primeiro.");
			return;
		}

		Location farmLoc = getFarmLocation(farmType);
		player.setOfflineFarmLoc(farmLoc.getX(), farmLoc.getY(), farmLoc.getZ());
		player.setOfflineFarm(true);
		player.setOfflineFarmSavedTitle(player.getTitle());
		player.setTitle(Config.OFFLINE_FARM_TITLE);
		player.getAppearance().setTitleColor(Integer.decode("0x" + Config.OFFLINE_FARM_TITLE_COLOR));
		player.broadcastUserInfo();

		_offlineFarmers.put(player.getObjectId(), endTime);

		// Auto-activate soulshot/spiritshot
		if (Config.OFFLINE_FARM_AUTO_SHOTS)
			autoActivateShots(player);

		// Activate autofarm
		if (!player.isAutoFarm())
		{
			AutofarmManager.INSTANCE.startFarm(player);
			player.setAutoFarm(true);
		}

		// Teleport to farm zone
		player.teleToLocation(farmLoc, 20);

		player.sendMessage("Offline Farm ativado! Desconectando...");
		player.sendPacket(new ExShowScreenMessage("Offline Farm Ativado!", 5 * 1000, SMPOS.BOTTOM_RIGHT, false));

		_log.info("OfflineFarmManager: " + player.getName() + " started offline farm (type " + farmType + ").");

		// Disconnect client after a short delay (character stays in-game)
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (player.getClient() != null)
				player.getClient().close(ServerClose.STATIC_PACKET);
		}, 2000);
	}

	public void stopOfflineFarm(L2PcInstance player)
	{
		if (player == null)
			return;

		_offlineFarmers.remove(player.getObjectId());
		player.setOfflineFarm(false);

		if (player.isAutoFarm())
		{
			AutofarmManager.INSTANCE.stopFarm(player);
			player.setAutoFarm(false);
		}

		final String savedTitle = player.getOfflineFarmSavedTitle();
		final int savedTitleColor = player.getOriginalTitleColor();
		if (savedTitle != null)
		{
			player.setTitle(savedTitle);
			player.setOfflineFarmSavedTitle(null);
		}
		player.getAppearance().setTitleColor(savedTitleColor);
		player.broadcastUserInfo();

		player.sendMessage("Offline Farm desativado.");
		player.sendPacket(new ExShowScreenMessage("Offline Farm Desativado!", 5 * 1000, SMPOS.BOTTOM_RIGHT, false));
	}

	public void onPlayerDeath(L2PcInstance player)
	{
		if (player == null || !player.isOfflineFarm())
			return;

		// If player is in an active event, let the event handle death/respawn
		if (isInActiveEvent(player))
			return;

		// Stop current autofarm
		if (player.isAutoFarm())
		{
			AutofarmManager.INSTANCE.stopFarm(player);
			player.setAutoFarm(false);
		}

		// Schedule auto-revive and teleport back
		int delay = Config.OFFLINE_FARM_REVIVE_DELAY * 1000;
		ThreadPoolManager.getInstance().scheduleGeneral(() -> reviveAndTeleport(player), delay);
	}

	private void reviveAndTeleport(L2PcInstance player)
	{
		if (player == null || !player.isOfflineFarm())
			return;

		// Check if time is still valid
		long endTime = player.getOfflineFarmEndTime();
		if (endTime <= 0 || endTime < System.currentTimeMillis())
		{
			stopOfflineFarm(player);
			player.sendMessage("Seu tempo de Offline Farm expirou.");
			return;
		}

		// Revive
		if (player.isDead())
			player.doRevive();

		// Set full HP/MP/CP
		player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
		player.setCurrentCp(player.getMaxCp());

		// Teleport to town first
		Location townLoc = getTownLocation();
		player.teleToLocation(townLoc, 20);
		player.sendMessage("Voce morreu. Teleportado para a cidade. Retornando ao farm em " + Config.OFFLINE_FARM_TOWN_DELAY + "s...");

		// After town delay, walk to gatekeeper then teleport back to farm
		long townDelayMs = Config.OFFLINE_FARM_TOWN_DELAY * 1000L;
		ThreadPoolManager.getInstance().scheduleGeneral(() -> walkToGatekeeperThenFarm(player), townDelayMs);
	}

	public void purchaseTime(L2PcInstance player)
	{
		if (player == null)
			return;

		int itemId = Config.OFFLINE_FARM_PRICE_ID;
		int itemCount = Config.OFFLINE_FARM_PRICE_COUNT;

		if (player.getInventory().getInventoryItemCount(itemId, -1) < itemCount)
		{
			player.sendMessage("Voce nao tem itens suficientes para comprar Offline Farm.");
			return;
		}

		player.destroyItemByItemId("OfflineFarm", itemId, itemCount, player, true);

		long durationMs = Config.OFFLINE_FARM_DURATION * 60000L;
		long currentEnd = player.getOfflineFarmEndTime();
		long now = System.currentTimeMillis();

		// If already has time, add to it; otherwise start from now
		if (currentEnd > now)
			player.setOfflineFarmEndTime(currentEnd + durationMs);
		else
			player.setOfflineFarmEndTime(now + durationMs);

		player.sendMessage("Offline Farm renovado com sucesso! +" + Config.OFFLINE_FARM_DURATION + " minutos.");
		player.sendPacket(new ExShowScreenMessage("Offline Farm Renovado!", 5 * 1000, SMPOS.BOTTOM_RIGHT, false));
	}

	public void onPlayerEnterWorld(L2PcInstance player)
	{
		if (player == null)
			return;

		long endTime = player.getOfflineFarmEndTime();
		if (endTime <= 0 || endTime < System.currentTimeMillis())
			return;

		// Player has valid remaining farm time — re-register in the active map
		// so the timer task keeps watching them, but don't force farm mode on login
		_offlineFarmers.put(player.getObjectId(), endTime);
	}

	public void onPlayerLogout(L2PcInstance player)
	{
		if (player == null)
			return;

		// Don't remove from farmers on logout - they stay farming offline
		// Only remove if time expired
		long endTime = player.getOfflineFarmEndTime();
		if (endTime <= 0 || endTime < System.currentTimeMillis())
		{
			_offlineFarmers.remove(player.getObjectId());
			player.setOfflineFarm(false);
		}
	}

	private void checkTimers()
	{
		long now = System.currentTimeMillis();
		for (java.util.Map.Entry<Integer, Long> entry : _offlineFarmers.entrySet())
		{
			if (entry.getValue() < now)
			{
				int objectId = entry.getKey();
				_offlineFarmers.remove(objectId);

				L2PcInstance player = net.sf.l2j.gameserver.model.L2World.getInstance().getPlayer(objectId);
				if (player != null)
				{
					stopOfflineFarm(player);
					player.sendMessage("Seu tempo de Offline Farm expirou.");
					player.sendPacket(new ExShowScreenMessage("Offline Farm Expirado!", 5 * 1000, SMPOS.BOTTOM_RIGHT, false));
				}
			}
		}
	}

	private void autoRegisterEvents()
	{
		for (java.util.Map.Entry<Integer, Long> entry : _offlineFarmers.entrySet())
		{
			L2PcInstance player = net.sf.l2j.gameserver.model.L2World.getInstance().getPlayer(entry.getKey());
			if (player == null || !player.isOfflineFarm())
				continue;

			try
			{
				// TvT
				if (Config.OFFLINE_FARM_AUTO_TVT && TvTEvent.isParticipating() && !TvTEvent.isPlayerParticipant(player.getObjectId()))
				{
					saveBuffs(player);
					TvTEvent.addParticipant(player);
					_log.info("OfflineFarmManager: " + player.getName() + " auto-registered for TvT.");
				}

				// CTF
				if (Config.OFFLINE_FARM_AUTO_CTF && CTFEvent.isParticipating() && !CTFEvent.isPlayerParticipant(player.getObjectId()))
				{
					saveBuffs(player);
					CTFEvent.addParticipant(player);
					_log.info("OfflineFarmManager: " + player.getName() + " auto-registered for CTF.");
				}

				// DM
				if (Config.OFFLINE_FARM_AUTO_DM && DMEvent.isParticipating() && !DMEvent.isPlayerParticipant(player))
				{
					saveBuffs(player);
					DMEvent.addParticipant(player);
					_log.info("OfflineFarmManager: " + player.getName() + " auto-registered for DM.");
				}

				// LM
				if (Config.OFFLINE_FARM_AUTO_LM && LMEvent.isParticipating() && !LMEvent.isPlayerParticipant(player))
				{
					saveBuffs(player);
					LMEvent.addParticipant(player);
					_log.info("OfflineFarmManager: " + player.getName() + " auto-registered for LM.");
				}

				// KTB
				if (Config.OFFLINE_FARM_AUTO_KTB && KTBEvent.isParticipating() && !KTBEvent.isPlayerParticipant(player.getObjectId()))
				{
					saveBuffs(player);
					KTBEvent.addParticipant(player);
					_log.info("OfflineFarmManager: " + player.getName() + " auto-registered for KTB.");
				}
			}
			catch (Exception e)
			{
				_log.warning("OfflineFarmManager: Error registering " + player.getName() + " for event: " + e.getMessage());
			}
		}
	}

	public boolean isOfflineFarming(L2PcInstance player)
	{
		return player != null && _offlineFarmers.containsKey(player.getObjectId());
	}

	public static Location getFarmLocation(int farmType)
	{
		List<int[]> locs = (farmType == 2) ? Config.OFFLINE_FARM_ZONE2_LOCS : Config.OFFLINE_FARM_ZONE1_LOCS;
		if (locs == null || locs.isEmpty())
			return new Location(83386, 148007, -3400);

		int idx = Rnd.get(locs.size());
		int[] loc = locs.get(idx);
		_log.info("OfflineFarmManager: getFarmLocation type=" + farmType + " picked index=" + idx + "/" + locs.size() + " loc=(" + loc[0] + "," + loc[1] + "," + loc[2] + ")");
		return new Location(loc[0], loc[1], loc[2]);
	}

	private void autoActivateShots(L2PcInstance player)
	{
		// Soulshot IDs: 1835(NG), 1463(D), 1464(C), 1465(B), 1466(A), 1467(S)
		// Spiritshot IDs: 2509(NG), 2510(D), 2511(C), 2512(B), 2513(A), 2514(S)
		// Blessed Spiritshot IDs: 3947(NG), 3948(D), 3949(C), 3950(B), 3951(A), 3952(S)
		int[] shotIds = { 1835, 1463, 1464, 1465, 1466, 1467, 2509, 2510, 2511, 2512, 2513, 2514, 3947, 3948, 3949, 3950, 3951, 3952 };

		for (int itemId : shotIds)
		{
			ItemInstance item = player.getInventory().getItemByItemId(itemId);
			if (item != null && item.getCount() > 0)
			{
				if (!player.getAutoSoulShot().contains(itemId))
				{
					player.addAutoSoulShot(itemId);
					player.sendPacket(new ExAutoSoulShot(itemId, 1));
				}
			}
		}
		player.rechargeShots(true, true);
	}

	public static boolean isInActiveEvent(L2PcInstance player)
	{
		if (player == null)
			return false;

		int objId = player.getObjectId();

		if ((TvTEvent.isStarting() || TvTEvent.isStarted()) && TvTEvent.isPlayerParticipant(objId))
			return true;
		if ((CTFEvent.isStarting() || CTFEvent.isStarted()) && CTFEvent.isPlayerParticipant(objId))
			return true;
		if ((DMEvent.isStarting() || DMEvent.isStarted()) && DMEvent.isPlayerParticipant(objId))
			return true;
		if ((LMEvent.isStarting() || LMEvent.isStarted()) && LMEvent.isPlayerParticipant(objId))
			return true;
		if ((KTBEvent.isStarting() || KTBEvent.isStarted()) && KTBEvent.isPlayerParticipant(objId))
			return true;

		return false;
	}

	public void resumeFarmAfterEvent(L2PcInstance player)
	{
		if (player == null || !player.isOfflineFarm())
			return;

		_log.info("OfflineFarmManager: " + player.getName() + " event ended, resuming farm.");

		// Stop autofarm so it doesn't interfere with walk-to-GK movement
		if (player.isAutoFarm())
		{
			AutofarmManager.INSTANCE.stopFarm(player);
			player.setAutoFarm(false);
		}

		// Wait for event teleport to finish, then teleport to town (event may teleport to lastCoords/farm spot where there is no GK)
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (player == null || !player.isOfflineFarm())
				return;

			// Revive if dead (might have died in event and not been revived yet)
			if (player.isDead())
				player.doRevive();

			player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
			player.setCurrentCp(player.getMaxCp());

			// Teleport to town where there IS a Gatekeeper NPC
			Location townLoc = getTownLocation();
			player.teleToLocation(townLoc, 20);
			_log.info("OfflineFarmManager: " + player.getName() + " teleported to town (" + townLoc.getX() + "," + townLoc.getY() + "), waiting for GK walk...");

			// Wait for world region to populate after teleport, then walk to GK
			ThreadPoolManager.getInstance().scheduleGeneral(() -> walkToGatekeeperThenFarm(player), 5000);
		}, 3000);
	}

	private void walkToGatekeeperThenFarm(L2PcInstance player)
	{
		if (player == null || !player.isOfflineFarm())
			return;

		// Search world region visible objects for NPC ID 1001 (Gatekeeper)
		L2Npc gatekeeper = null;
		double bestDist = Double.MAX_VALUE;

		L2WorldRegion region = player.getWorldRegion();
		_log.info("OfflineFarm walkToGK: " + player.getName() + " region=" + (region != null ? region.getName() : "null") + " pos=(" + player.getX() + "," + player.getY() + "," + player.getZ() + ")");

		if (region != null)
		{
			for (L2WorldRegion reg : region.getSurroundingRegions())
			{
				for (L2Object obj : reg.getVisibleObjects().values())
				{
					if (!(obj instanceof L2Npc))
						continue;

					L2Npc npc = (L2Npc) obj;
					if (npc.getNpcId() != 10001)
						continue;

					double dist = player.getDistanceSq(npc);
					_log.info("OfflineFarm walkToGK: found GK npc " + npc.getName() + " class=" + npc.getClass().getSimpleName() + " dist=" + (int) Math.sqrt(dist));
					if (dist < bestDist)
					{
						bestDist = dist;
						gatekeeper = npc;
					}
				}
			}
		}

		if (gatekeeper != null)
		{
			_log.info("OfflineFarm walkToGK: " + player.getName() + " moving to GK at (" + gatekeeper.getX() + "," + gatekeeper.getY() + "), dist=" + (int) Math.sqrt(bestDist));
			// Set running and move to gatekeeper
			player.setRunning();
			player.getAI().setIntention(CtrlIntention.MOVE_TO, new L2CharPosition(gatekeeper.getX(), gatekeeper.getY(), gatekeeper.getZ(), 0));

			// Estimate run time: distance / run speed, +2s buffer, capped at 15s
			double runSpeed = Math.max(50, player.getStat().getMoveSpeed());
			long walkMs = Math.min(15000, (long) (Math.sqrt(bestDist) / runSpeed * 1000) + 2000);

			ThreadPoolManager.getInstance().scheduleGeneral(() -> teleportToFarm(player), walkMs);
		}
		else
		{
			_log.info("OfflineFarm walkToGK: " + player.getName() + " NO gatekeeper found, teleporting directly.");
			// No gatekeeper found in range, teleport directly
			teleportToFarm(player);
		}
	}

	private void teleportToFarm(L2PcInstance player)
	{
		if (player == null || !player.isOfflineFarm())
			return;

		int farmType = player.getOfflineFarmType();
		Location farmLoc = getFarmLocation(farmType);
		player.teleToLocation(farmLoc, 20);

		// Re-activate shots, restore buffs and restart autofarm after arriving
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (player != null && player.isOfflineFarm())
			{
				if (Config.OFFLINE_FARM_AUTO_SHOTS)
					autoActivateShots(player);

				restoreBuffs(player);

				// Always restart to clear stale event target from the routine
				if (player.isAutoFarm())
				{
					AutofarmManager.INSTANCE.stopFarm(player);
					player.setAutoFarm(false);
				}
				AutofarmManager.INSTANCE.startFarm(player);
				player.setAutoFarm(true);
			}
		}, 3000);
	}

	private void saveBuffs(L2PcInstance player)
	{
		if (player == null || player.getOfflineFarmSavedBuffs() != null)
			return;

		L2Effect[] effects = player.getAllEffects();
		if (effects == null || effects.length == 0)
			return;

		java.util.List<int[]> list = new java.util.ArrayList<>();
		for (L2Effect ef : effects)
		{
			if (ef == null)
				continue;
			L2Skill sk = ef.getSkill();
			if (sk == null || sk.isDebuff() || sk.isOffensive())
				continue;
			list.add(new int[]{sk.getId(), sk.getLevel()});
		}

		if (!list.isEmpty())
			player.setOfflineFarmSavedBuffs(list.toArray(new int[0][]));
	}

	private void restoreBuffs(L2PcInstance player)
	{
		if (player == null)
			return;

		int[][] saved = player.getOfflineFarmSavedBuffs();
		if (saved == null)
			return;

		for (int[] entry : saved)
		{
			L2Skill skill = SkillTable.getInstance().getInfo(entry[0], entry[1]);
			if (skill == null || skill.isDebuff() || skill.isOffensive())
				continue;
			skill.getEffects(player, player);
		}

		player.setOfflineFarmSavedBuffs(null);
		_log.info("OfflineFarmManager: " + player.getName() + " buffs restored after event.");
	}

	public static Location getTownLocation()
	{
		return new Location(Config.OFFLINE_FARM_TOWN_X, Config.OFFLINE_FARM_TOWN_Y, Config.OFFLINE_FARM_TOWN_Z);
	}

	public String getRemainingTime(L2PcInstance player)
	{
		long endTime = player.getOfflineFarmEndTime();
		long now = System.currentTimeMillis();

		if (endTime <= 0 || endTime < now)
			return "<font color=FF0000>Expirado</font>";

		long remaining = endTime - now;
		long hours = remaining / 3600000;
		long minutes = (remaining % 3600000) / 60000;

		return "<font color=00FF00>" + hours + "h " + minutes + "m</font>";
	}

	public static OfflineFarmManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final OfflineFarmManager _instance = new OfflineFarmManager();
	}
}
