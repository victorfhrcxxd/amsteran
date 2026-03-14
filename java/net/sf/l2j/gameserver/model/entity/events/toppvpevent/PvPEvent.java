package net.sf.l2j.gameserver.model.entity.events.toppvpevent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.util.Broadcast;

public class PvPEvent 
{
	private static final Logger _log = Logger.getLogger(PvPEvent.class.getName());

	private PvPEventEngineState _state = PvPEventEngineState.INACTIVE;

	public boolean startPvPEvent() 
	{
		setState(PvPEventEngineState.ACTIVE);
		return true;
	}

	public boolean endPvPEvent() 
	{
		setState(PvPEventEngineState.INACTIVE);
		return true;
	}

	private void setState(PvPEventEngineState state) 
	{
		synchronized (state)
		{
			_state = state;
		} 
	}

	public boolean isActive()
	{
		synchronized (_state) 
		{
			return _state == PvPEventEngineState.ACTIVE;
		} 
	}

	public boolean isInactive() 
	{
		synchronized (_state) 
		{
			return _state == PvPEventEngineState.INACTIVE;
		} 
	}
	
	public void rewardFinish()
	{
		if (getTopZonePvpCount() == 0)
			Broadcast.gameAnnounceToOnlinePlayers("PvP Event is finished without winners!");
		else
		{
			Broadcast.gameAnnounceToOnlinePlayers("PvP Event Winner is " + getTopZonePvpName() + " with " + getTopZonePvpCount() + " pvp's.");
			addReward(getTopZonePlayerReward());
		}
		cleanPvpEvent();
	}

	public static void cleanPvpEvent()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET event_pvp=0");
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	static int getTopZonePlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY event_pvp DESC LIMIT 1");
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
			e.printStackTrace();
		}
		return id;
	}
	
	static int getTopZonePvpCount()
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT event_pvp FROM characters ORDER BY event_pvp DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("event_pvp");
			}
			rset.close();
			statement.close();
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}
		return id;
	}

	static String getTopZonePvpName()
	{
		String name = null;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters ORDER BY event_pvp DESC LIMIT 1");
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
			e.printStackTrace();
		}

		return name;
	}

	public synchronized static void addEventPvp(L2PcInstance activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET event_pvp=? WHERE obj_Id=?");
			statement.setInt(1, getEventPvp(activeChar) +1);
			statement.setInt(2, activeChar.getObjectId());
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized static int getEventPvp(L2PcInstance activeChar)
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT event_pvp FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("event_pvp");
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return id;
	}
	
	private static void addReward(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : Config.PVP_EVENT_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("Top Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage("Congratulations " + player.getName() + " you are the winner of PvP Event.", 3000, 0x02, true));
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

	public static void getTopHtml(L2PcInstance activeChar)
	{
		if (PvPEvent.getInstance().isActive())
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking PvP Event</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br><font color=LEVEL>PvP Event is Active!</font><br1></center><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pvp's</center></td><td><center>Status</center></td></tr>");

			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("SELECT char_name,event_pvp,accesslevel,online FROM characters ORDER BY event_pvp DESC LIMIT 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					int accessLevel = result.getInt("accesslevel");

					if (accessLevel > 0)
						continue;

					int pvpKills = result.getInt("event_pvp");

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

					tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +pl+ "</font></center></td><td><center>" +pvpKills+ "</center></td><td><center>" +status+ "</center></td></tr>");
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
		else
		{
			activeChar.sendMessage("PvP Event is not in progress!");
			return;
		}
	}
	
	public static PvPEvent getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder 
	{
		protected static final PvPEvent _instance = new PvPEvent();
	}
}
