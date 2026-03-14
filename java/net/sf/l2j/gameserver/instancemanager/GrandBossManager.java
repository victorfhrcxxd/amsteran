/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.instancemanager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2GrandBossInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.zone.type.L2BossZone;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

/**
 * This class handles the status of all Grand Bosses, and manages L2BossZone zones.
 * @author DaRkRaGe, Emperorc
 */
public class GrandBossManager
{
	protected static Logger _log = Logger.getLogger(GrandBossManager.class.getName());
	
	private static final String SELECT_GRAND_BOSS_LIST = "SELECT * from grandboss_list ORDER BY zone";
	private static final String DELETE_GRAND_BOSS_LIST = "DELETE FROM grandboss_list";
	private static final String INSERT_GRAND_BOSS_LIST = "INSERT INTO grandboss_list (player_id,zone) VALUES (?,?)";
	private static final String SELECT_GRAND_BOSS_DATA = "SELECT * from grandboss_data ORDER BY boss_id";
	private static final String UPDATE_GRAND_BOSS_DATA = "UPDATE grandboss_data set loc_x = ?, loc_y = ?, loc_z = ?, heading = ?, respawn_time = ?, currentHP = ?, currentMP = ?, status = ? where boss_id = ?";
	private static final String UPDATE_GRAND_BOSS_DATA2 = "UPDATE grandboss_data set status = ? where boss_id = ?";
	
	private final Map<Integer, L2GrandBossInstance> _bosses = new HashMap<>();
	private final Map<Integer, StatsSet> _storedInfo = new HashMap<>();
	private final Map<Integer, Integer> _bossStatus = new HashMap<>();
	private final List<L2BossZone> _zones = new ArrayList<>();
	
	public static GrandBossManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	protected GrandBossManager()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SELECT_GRAND_BOSS_DATA);
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				StatsSet info = new StatsSet();
				
				int bossId = rset.getInt("boss_id");
				
				info.set("loc_x", rset.getInt("loc_x"));
				info.set("loc_y", rset.getInt("loc_y"));
				info.set("loc_z", rset.getInt("loc_z"));
				info.set("heading", rset.getInt("heading"));
				info.set("respawn_time", rset.getLong("respawn_time"));
				info.set("currentHP", rset.getDouble("currentHP"));
				info.set("currentMP", rset.getDouble("currentMP"));
				
				_bossStatus.put(bossId, rset.getInt("status"));
				_storedInfo.put(bossId, info);
			}
			rset.close();
			statement.close();
			
			_log.info("GrandBossManager: Loaded " + _storedInfo.size() + " GrandBosses instances.");
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Could not load grandboss data: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Load grandbosses players lists.
	 */
	public void initZones()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement(SELECT_GRAND_BOSS_LIST);
			ResultSet rset = statement.executeQuery();
			
			// Avoid to for loop a lot of time, using the zoneId as index.
			L2BossZone zone = null;
			
			while (rset.next())
			{
				final int currentZoneId = rset.getInt("zone");
				if (currentZoneId != ((zone == null) ? 0 : zone.getId()))
					zone = getZoneById(currentZoneId);
				
				if (zone != null)
					zone.allowPlayerEntry(rset.getInt("player_id"));
			}
			
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Could not load grandboss zones: " + e.getMessage(), e);
		}
	}
	
	public void addZone(L2BossZone zone)
	{
		if (!_zones.contains(zone))
			_zones.add(zone);
	}
	
	public boolean isInBossZone(L2Character character)
	{
		for (L2BossZone temp : _zones)
		{
			if (temp.isCharacterInZone(character))
				return true;
		}
		return false;
	}
	
	public L2BossZone getZoneById(int id)
	{
		for (L2BossZone temp : _zones)
		{
			if (temp.getId() == id)
				return temp;
		}
		return null;
	}
	
	public L2BossZone getZoneByXYZ(int x, int y, int z)
	{
		for (L2BossZone temp : _zones)
		{
			if (temp.isInsideZone(x, y, z))
				return temp;
		}
		return null;
	}
	
	public int getBossStatus(int bossId)
	{
		return _bossStatus.get(bossId);
	}
	
	public void setBossStatus(int bossId, int status)
	{
		_bossStatus.put(bossId, status);
		_log.info("GrandBossManager: Updated " + NpcTable.getInstance().getTemplate(bossId).getName() + " (id: " + bossId + ") status to " + status);
		updateDb(bossId, true);
		
		if (Config.ANNOUNCE_RAID_BOSS_ALIVE)
		{
			if (bossId == 29022 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Zaken has spawned in the world!");

			if (bossId == 29019 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Antharas has spawned in the world!");

			if (bossId == 29020 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Baium has spawned in the world!");

			if (bossId == 29001 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Queen Ant has spawned in the world!");

			if (bossId == 29006 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Core has spawned in the world!");

			if (bossId == 29014 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Orfen has spawned in the world!");

			if (bossId == 29028 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Valakas has spawned in the world!");

			if (bossId == 29045 && status == 0)
				Broadcast.gameAnnounceToOnlinePlayers("Epic Boss Frintezza has spawned in the world!");
		}
		
		if (Config.CUSTOM_BAIUM_CRYSTAL)
		{
			if (bossId == 29020 && status == 0)
			{
				Broadcast.gameAnnounceToOnlinePlayers("Teleport to Baium will be released in a random time of " + Config.CUSTOM_BAIUM_CRYSTAL_MIN_SPAWN + " to " + Config.CUSTOM_BAIUM_CRYSTAL_MAX_SPAWN + " minutes!");
				ThreadPoolManager.getInstance().scheduleGeneral(() -> spawnCrystal(), Rnd.get(Config.CUSTOM_BAIUM_CRYSTAL_MIN_SPAWN * 60 * 1000, Config.CUSTOM_BAIUM_CRYSTAL_MAX_SPAWN * 60 * 1000));
			}

			if (bossId == 29020 && status == 2)
				unspawnCrystal();
		}
	}
	
	public static L2Spawn _npcSpawn2;

	public static void spawnCrystal()
	{
		NpcTemplate tmpl = NpcTable.getInstance().getTemplate(31862);
		
		try
		{
			_npcSpawn2 = new L2Spawn(tmpl);

			_npcSpawn2.setLocx(112624); // loc x
			_npcSpawn2.setLocy(14032); // loc y
			_npcSpawn2.setLocz(10080); // loc z
			_npcSpawn2.getAmount();
			_npcSpawn2.setHeading(0);
			_npcSpawn2.setRespawnDelay(1);

			SpawnTable.getInstance().addNewSpawn(_npcSpawn2, false);

			_npcSpawn2.init();
			_npcSpawn2.getLastSpawn().getStatus().setCurrentHp(999999999);
			_npcSpawn2.getLastSpawn().isAggressive();
			_npcSpawn2.getLastSpawn().decayMe();
			_npcSpawn2.getLastSpawn().spawnMe(_npcSpawn2.getLastSpawn().getX(), _npcSpawn2.getLastSpawn().getY(), _npcSpawn2.getLastSpawn().getZ());

			_npcSpawn2.getLastSpawn().broadcastPacket(new MagicSkillUse(_npcSpawn2.getLastSpawn(), _npcSpawn2.getLastSpawn(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void unspawnCrystal()
	{
		if (_npcSpawn2 == null)
			return;
		
		_npcSpawn2.getLastSpawn().deleteMe();
		_npcSpawn2.stopRespawn();
		SpawnTable.getInstance().deleteSpawn(_npcSpawn2, true);
	}

	/**
	 * Adds a L2GrandBossInstance to the list of bosses.
	 * @param boss The boss to add.
	 */
	public void addBoss(L2GrandBossInstance boss)
	{
		if (boss != null)
			_bosses.put(boss.getNpcId(), boss);
	}
	
	/**
	 * Adds a L2GrandBossInstance to the list of bosses. Using this variant of addBoss, we can impose a npcId.
	 * @param npcId The npcId to use for registration.
	 * @param boss The boss to add.
	 */
	public void addBoss(int npcId, L2GrandBossInstance boss)
	{
		if (boss != null)
			_bosses.put(npcId, boss);
	}
	
	public L2GrandBossInstance getBoss(int bossId)
	{
		return _bosses.get(bossId);
	}
	
	public StatsSet getStatsSet(int bossId)
	{
		return _storedInfo.get(bossId);
	}
	
	public void setStatsSet(int bossId, StatsSet info)
	{
		_storedInfo.put(bossId, info);
		updateDb(bossId, false);
		
       if (Config.LIST_GRAND_BOSS_IDS.contains(bossId))
          RaidBossInfoManager.getInstance().updateRaidBossInfo(bossId, info.getLong("respawn_time"));
	}
	
	private void storeToDb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement deleteStatement = con.prepareStatement(DELETE_GRAND_BOSS_LIST);
			deleteStatement.executeUpdate();
			deleteStatement.close();
			
			PreparedStatement insertStatement = con.prepareStatement(INSERT_GRAND_BOSS_LIST);
			for (L2BossZone zone : _zones)
			{
				final int id = zone.getId();
				for (int player : zone.getAllowedPlayers())
				{
					insertStatement.setInt(1, player);
					insertStatement.setInt(2, id);
					insertStatement.executeUpdate();
					insertStatement.clearParameters();
				}
			}
			insertStatement.close();
			
			PreparedStatement updateStatement1 = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2);
			PreparedStatement updateStatement2 = con.prepareStatement(UPDATE_GRAND_BOSS_DATA);
			for (int bossId : _storedInfo.keySet())
			{
				L2GrandBossInstance boss = _bosses.get(bossId);
				StatsSet info = _storedInfo.get(bossId);
				if (boss == null || info == null)
				{
					updateStatement1.setInt(1, _bossStatus.get(bossId));
					updateStatement1.setInt(2, bossId);
					updateStatement1.executeUpdate();
					updateStatement1.clearParameters();
				}
				else
				{
					updateStatement2.setInt(1, boss.getX());
					updateStatement2.setInt(2, boss.getY());
					updateStatement2.setInt(3, boss.getZ());
					updateStatement2.setInt(4, boss.getHeading());
					updateStatement2.setLong(5, info.getLong("respawn_time"));
					updateStatement2.setDouble(6, (boss.isDead()) ? boss.getMaxHp() : boss.getCurrentHp());
					updateStatement2.setDouble(7, (boss.isDead()) ? boss.getMaxMp() : boss.getCurrentMp());
					updateStatement2.setInt(8, _bossStatus.get(bossId));
					updateStatement2.setInt(9, bossId);
					updateStatement2.executeUpdate();
					updateStatement2.clearParameters();
				}
			}
			updateStatement1.close();
			updateStatement2.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Couldn't store grandbosses to database:" + e.getMessage(), e);
		}
	}
	
	private void updateDb(int bossId, boolean statusOnly)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			L2GrandBossInstance boss = _bosses.get(bossId);
			StatsSet info = _storedInfo.get(bossId);
			PreparedStatement statement = null;
			
			if (statusOnly || boss == null || info == null)
			{
				statement = con.prepareStatement(UPDATE_GRAND_BOSS_DATA2);
				statement.setInt(1, _bossStatus.get(bossId));
				statement.setInt(2, bossId);
			}
			else
			{
				statement = con.prepareStatement(UPDATE_GRAND_BOSS_DATA);
				statement.setInt(1, boss.getX());
				statement.setInt(2, boss.getY());
				statement.setInt(3, boss.getZ());
				statement.setInt(4, boss.getHeading());
				statement.setLong(5, info.getLong("respawn_time"));
				statement.setDouble(6, (boss.isDead()) ? boss.getMaxHp() : boss.getCurrentHp());
				statement.setDouble(7, (boss.isDead()) ? boss.getMaxMp() : boss.getCurrentMp());
				statement.setInt(8, _bossStatus.get(bossId));
				statement.setInt(9, bossId);
			}
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.log(Level.WARNING, "GrandBossManager: Couldn't update grandbosses to database:" + e.getMessage(), e);
		}
	}
	
	/**
	 * Saves all Grand Boss info and then clears all info from memory, including all schedules.
	 */
	public void cleanUp()
	{
		storeToDb();
		
		_bosses.clear();
		_storedInfo.clear();
		_bossStatus.clear();
		_zones.clear();
	}
	
	public List<L2BossZone> getZones()
	{
		return _zones;
	}
	
	private static class SingletonHolder
	{
		protected static final GrandBossManager _instance = new GrandBossManager();
	}
}