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
package net.sf.l2j.gameserver.model.entity.events;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import phantom.FakePlayer;
import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.util.Broadcast;

public class TopKillerRoundSystem
{
	private static final Logger _log = Logger.getLogger(TopKillerRoundSystem.class.getName());

	public static long getTimeToOclock()
	{
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);

		c.add(Calendar.HOUR, 1);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		long howMany = (c.getTimeInMillis()-d.getTime());
		return howMany;
	}

	public static String getNextHourToDate()
	{
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		c.add(Calendar.HOUR, 1);
		c.set(Calendar.MINUTE, 0);

		return c.getTime().toString();
	}

	public static String getTimeToDate()
	{
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		c.add(Calendar.HOUR, 1);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return c.getTime().toString();
	}

	public static int getZonePvp(L2PcInstance activeChar)
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT zone_pvp FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("zone_pvp");
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update " + activeChar.getName() + " getZonePvp(): " + e.getMessage(), e);
			e.printStackTrace();
		}

		return id;
	}

	public static void addZonePvp(L2PcInstance activeChar)
	{
		if (activeChar instanceof FakePlayer)
			return;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET zone_pvp=? WHERE obj_Id=?");
			statement.setInt(1, getZonePvp(activeChar) +1);
			statement.setInt(2, activeChar.getObjectId());
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "Could not update " + activeChar.getName() + " addZonePvp(): " + e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static void resetZonePvp()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET zone_pvp=0");
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			_log.log(Level.WARNING, "Could not update resetZonePvp(): " + e.getMessage(), e);
			e.printStackTrace();
		}
	}

	static int getTopZone1stPvpCount()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT zone_pvp FROM characters ORDER BY zone_pvp DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("zone_pvp");
			}
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone1stPvpCount(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return id;
	}

	static int getTopZone2ndPvpCount()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT zone_pvp FROM characters ORDER BY zone_pvp DESC LIMIT 1, 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("zone_pvp");
			}
			rset.close();
			statement.close();
		}

		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone2ndPvpCount(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return id;
	}
	
	static int getTopZone3rdPvpCount()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT zone_pvp FROM characters ORDER BY zone_pvp DESC LIMIT 2, 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("zone_pvp");
			}
			rset.close();
			statement.close();
		}

		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone3rdPvpCount(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return id;
	}
	
	static int getTopZone1stPlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY zone_pvp DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("obj_Id");
			}
			rset.close();
			statement.close();
		}

		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone1stPlayerReward(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return id;
	}
	
	static int getTopZone2ndPlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY zone_pvp DESC LIMIT 1, 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("obj_Id");
			}
			rset.close();
			statement.close();
		}

		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone2ndPlayerReward(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return id;
	}
	
	static int getTopZone3rdPlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY zone_pvp DESC LIMIT 2, 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("obj_Id");
			}
			rset.close();
			statement.close();
		}

		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone3rdPlayerReward(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return id;
	}
	
	static String getTopZone1stPvpName()
	{
		String name = null;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters ORDER BY zone_pvp DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				name = rset.getString("char_name");
			}

			rset.close();
			statement.close();

		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone1stPvpName(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return name;
	}
	
	static String getTopZone2ndPvpName()
	{
		String name = null;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters ORDER BY zone_pvp DESC LIMIT 1, 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				name = rset.getString("char_name");
			}

			rset.close();
			statement.close();

		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone2ndPvpName(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return name;
	}	
	
	static String getTopZone3rdPvpName()
	{
		String name = null;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters ORDER BY zone_pvp DESC LIMIT 2, 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				name = rset.getString("char_name");
			}

			rset.close();
			statement.close();

		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Could not update getTopZone3rdPvpName(): " + e.getMessage(), e);
			e.printStackTrace();
		}
		return name;
	}
	
	private TopKillerRoundSystem()
	{

	}

	public static void getTopHtml(L2PcInstance activeChar)
	{
		
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		StringBuilder tb = new StringBuilder("<html><head><title>Ranking PvP Round</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>Next Round: <font color=LEVEL>" + getTimeToDate() +"</font><br1></center><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pvp's</center></td><td><center>Status</center></td></tr>");

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
			PreparedStatement statement = con.prepareStatement("SELECT char_name,zone_pvp,accesslevel,online FROM characters ORDER BY zone_pvp DESC LIMIT 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				int accessLevel = result.getInt("accesslevel");

				if (accessLevel > 0)
					continue;

				int pvpKills = result.getInt("zone_pvp");

				if (pvpKills == 0)
					continue;

				String pl = result.getString("char_name");
				pos += 1;
				String statu = result.getString("online");
				String status;
				
				if (statu.equals("1"))
					status = "<font color=00FF00>Online</font>";
				else
					status = "<font color=FF0000>Offline</font>";

				tb.append("<tr><td><center>" + pos + "</td><td><center><font color=00FFFF>" + pl + "</font></center></td><td><center>" + pvpKills + "</center></td><td><center>" + status + "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
        catch (Exception e)
		{
        	_log.warning("Error while selecting top 15 pvp from database: " + e);
		}        
        tb.append("</table><br>");
		tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
		tb.append("</body></html>");

		htm.setHtml(tb.toString());
		activeChar.sendPacket(htm);
	}
	
	private static void addReward1st(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : Config.TOP_1ST_KILLER_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("Top Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage("Congratulations " + player.getName() + " you are the top killer of the last 1 hour.", 3000, 0x02, true));
				player.getInventory().updateDatabase();
				player.sendPacket(iu);
			}
			else
				addOfflineItem(objId, reward[0], reward[1]);
		}
	}
	
	private static void addReward2nd(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : Config.TOP_2ND_KILLER_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("Top Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage("Congratulations " + player.getName() + " you are the top killer of the last 1 hour.", 3000, 0x02, true));
				player.getInventory().updateDatabase();
				player.sendPacket(iu);
			}
			else
				addOfflineItem(objId, reward[0], reward[1]);
		}
	}
	
	private static void addReward3rd(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : Config.TOP_3RD_KILLER_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("Top Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage("Congratulations " + player.getName() + " you are the top killer of the last 1 hour.", 3000, 0x02, true));
				player.getInventory().updateDatabase();
				player.sendPacket(iu);
			}
			else
				addOfflineItem(objId, reward[0], reward[1]);
		}
	}
	
	private static void addOfflineItem(int ownerId, int itemId, int count)
	{
		Item item = ItemTable.getInstance().getTemplate(itemId);
		int objectId = IdFactory.getInstance().getNextId();
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if (count > 1 && !item.isStackable())
				return;
			
			PreparedStatement statement = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			statement.setInt(1, ownerId);
			statement.setInt(2, item.getItemId());
			statement.setInt(3, count);
			statement.setString(4, "INVENTORY");
			statement.setInt(5, 0);
			statement.setInt(6, 0);
			statement.setInt(7, objectId);
			statement.setInt(8, 0);
			statement.setInt(9, 0);
			statement.setInt(10, -1);
			statement.setLong(11, 0);
			statement.executeUpdate();
			statement.close();
		}
		catch (SQLException e)
		{
			_log.severe("Could not update item char: " + e);
			e.printStackTrace();
		}
	}

	public static void getInstance()
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				if (getTopZone1stPvpCount() == 0 && getTopZone2ndPvpCount() == 0 && getTopZone3rdPvpCount() == 0)
					Broadcast.ServerAnnounce("PvP Round is finished without winners ::");
				else
				{
					if (getTopZone1stPvpCount() != 0)
					{
						Broadcast.ServerAnnounce("1st PvP Round Winner is " + getTopZone1stPvpName() + " with " + getTopZone1stPvpCount() + " pvp's ::");
						addReward1st(getTopZone1stPlayerReward());
					}
					
					if (getTopZone2ndPvpCount() != 0)
					{
						Broadcast.ServerAnnounce("2nd PvP Round Winner is " + getTopZone2ndPvpName() + " with " + getTopZone2ndPvpCount() + " pvp's ::");
						addReward2nd(getTopZone2ndPlayerReward());
					}

					if (getTopZone3rdPvpCount() != 0)
					{
						Broadcast.ServerAnnounce("3rd PvP Round Winner is " + getTopZone3rdPvpName() + " with " + getTopZone3rdPvpCount() + " pvp's ::");
						addReward3rd(getTopZone3rdPlayerReward());
					}
				}

				resetZonePvp();
				Broadcast.ServerAnnounce("Next Round end: " + getNextHourToDate() + " ::");
			}

		}, getTimeToOclock(), 3600000);
	}
}