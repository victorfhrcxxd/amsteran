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
package net.sf.l2j.gameserver.model.entity.events.tournaments.properties;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class ArenaRanking
{
	public static int getRank1x1(L2PcInstance activeChar)
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 1x1_rank FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("1x1_rank");
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
	
	public static int getRank3x3(L2PcInstance activeChar)
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 3x3_rank FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("3x3_rank");
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
	
	public static int getRank5x5(L2PcInstance activeChar)
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 5x5_rank FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("5x5_rank");
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
	
	public static int getRank9x9(L2PcInstance activeChar)
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 9x9_rank FROM characters WHERE obj_Id=?");
			statement.setInt(1, activeChar.getObjectId());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("9x9_rank");
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

	public static void addRank1x1(L2PcInstance activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 1x1_rank=? WHERE obj_Id=?");
			statement.setInt(1, getRank1x1(activeChar) +1);
			statement.setInt(2, activeChar.getObjectId());
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void addRank3x3(L2PcInstance activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 3x3_rank=? WHERE obj_Id=?");
			statement.setInt(1, getRank3x3(activeChar) +1);
			statement.setInt(2, activeChar.getObjectId());
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void addRank5x5(L2PcInstance activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 5x5_rank=? WHERE obj_Id=?");
			statement.setInt(1, getRank5x5(activeChar) +1);
			statement.setInt(2, activeChar.getObjectId());
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void addRank9x9(L2PcInstance activeChar)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 9x9_rank=? WHERE obj_Id=?");
			statement.setInt(1, getRank9x9(activeChar) +1);
			statement.setInt(2, activeChar.getObjectId());
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void resetRank1x1()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 1x1_rank=0");
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void resetRank3x3()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 3x3_rank=0");
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void resetRank5x5()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 5x5_rank=0");
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void resetRank9x9()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE characters SET 9x9_rank=0");
			statement.executeUpdate();
			statement.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	static int getRank1x1Count()
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 1x1_rank FROM characters ORDER BY 1x1_rank DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("1x1_rank");
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

	static int getRank3x3Count()
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 3x3_rank FROM characters ORDER BY 3x3_rank DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("3x3_rank");
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

	static int getRank5x5Count()
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 5x5_rank FROM characters ORDER BY 5x5_rank DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("5x5_rank");
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

	static int getRank9x9Count()
	{
		int id=0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT 9x9_rank FROM characters ORDER BY 9x9_rank DESC LIMIT 1");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				id = rset.getInt("9x9_rank");
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

	static int getTopRank1x1PlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY 1x1_rank DESC LIMIT 1");
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

	static int getTopRank3x3PlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY 3x3_rank DESC LIMIT 1");
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

	static int getTopRank5x5PlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY 5x5_rank DESC LIMIT 1");
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

	static int getTopRank9x9PlayerReward()
	{
		int id = 0;

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters ORDER BY 9x9_rank DESC LIMIT 1");
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

	public static void getTopRank1x1Html(L2PcInstance activeChar)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		StringBuilder tb = new StringBuilder("<html><head><title>Ranking Event 1x1</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Win's</center></td><td><center>Status</center></td></tr>");
        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
			PreparedStatement statement = con.prepareStatement("SELECT char_name,1x1_rank,online FROM characters WHERE 1x1_rank>0 AND accesslevel=0 order by 1x1_rank desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String wins = result.getString("1x1_rank");
				String name = result.getString("char_name");
				
				if (name.equals("WWWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWW")|| name.equals("WWWWWWWWWWW")|| name.equals("WWWWWWWWWW")|| name.equals("WWWWWWWWW")|| name.equals("WWWWWWWW")|| name.equals("WWWWWWW")|| name.equals("WWWWWW"))
					name = name.substring(0, 3) + "..";
				else if (name.length() > 14)
					name = name.substring(0, 14) + "..";
	
				pos += 1;
				String statu = result.getString("online");
				String status;
				
				if (statu.equals("1"))
					status = "<font color=00FF00>Online</font>";
				else
					status = "<font color=FF0000>Offline</font>";
				
				tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +wins+ "</center></td><td><center>" +status+ "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
        catch (Exception e)
		{
        	//_log.warning("Error: could not restore 1x1 ranking data info: " + e);
        	e.printStackTrace();
		}        
        tb.append("</table><br>");
		tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
		tb.append("</body></html>");

		htm.setHtml(tb.toString());
		activeChar.sendPacket(htm);
	}

	public static void getTopRank3x3Html(L2PcInstance activeChar)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		StringBuilder tb = new StringBuilder("<html><head><title>Ranking Event 3x3</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Win's</center></td><td><center>Status</center></td></tr>");
        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
			PreparedStatement statement = con.prepareStatement("SELECT char_name,3x3_rank,online FROM characters WHERE 3x3_rank>0 AND accesslevel=0 order by 3x3_rank desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String wins = result.getString("3x3_rank");
				String name = result.getString("char_name");
				
				if (name.equals("WWWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWW")|| name.equals("WWWWWWWWWWW")|| name.equals("WWWWWWWWWW")|| name.equals("WWWWWWWWW")|| name.equals("WWWWWWWW")|| name.equals("WWWWWWW")|| name.equals("WWWWWW"))
					name = name.substring(0, 3) + "..";
				else if (name.length() > 14)
					name = name.substring(0, 14) + "..";
	
				pos += 1;
				String statu = result.getString("online");
				String status;
				
				if (statu.equals("1"))
					status = "<font color=00FF00>Online</font>";
				else
					status = "<font color=FF0000>Offline</font>";
				
				tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +wins+ "</center></td><td><center>" +status+ "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
        catch (Exception e)
		{
        	//_log.warning("Error: could not restore 1x1 ranking data info: " + e);
        	e.printStackTrace();
		}        
        tb.append("</table><br>");
		tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
		tb.append("</body></html>");

		htm.setHtml(tb.toString());
		activeChar.sendPacket(htm);
	}

	public static void getTopRank5x5Html(L2PcInstance activeChar)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		StringBuilder tb = new StringBuilder("<html><head><title>Ranking Event 5x5</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Win's</center></td><td><center>Status</center></td></tr>");
        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
			PreparedStatement statement = con.prepareStatement("SELECT char_name,5x5_rank,online FROM characters WHERE 5x5_rank>0 AND accesslevel=0 order by 5x5_rank desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String wins = result.getString("5x5_rank");
				String name = result.getString("char_name");
				
				if (name.equals("WWWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWW")|| name.equals("WWWWWWWWWWW")|| name.equals("WWWWWWWWWW")|| name.equals("WWWWWWWWW")|| name.equals("WWWWWWWW")|| name.equals("WWWWWWW")|| name.equals("WWWWWW"))
					name = name.substring(0, 3) + "..";
				else if (name.length() > 14)
					name = name.substring(0, 14) + "..";
	
				pos += 1;
				String statu = result.getString("online");
				String status;
				
				if (statu.equals("1"))
					status = "<font color=00FF00>Online</font>";
				else
					status = "<font color=FF0000>Offline</font>";
				
				tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +wins+ "</center></td><td><center>" +status+ "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
        catch (Exception e)
		{
        	//_log.warning("Error: could not restore 1x1 ranking data info: " + e);
        	e.printStackTrace();
		}        
        tb.append("</table><br>");
		tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
		tb.append("</body></html>");

		htm.setHtml(tb.toString());
		activeChar.sendPacket(htm);
	}

	public static void getTopRank9x9Html(L2PcInstance activeChar)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		StringBuilder tb = new StringBuilder("<html><head><title>Ranking Event 9x9</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Win's</center></td><td><center>Status</center></td></tr>");
        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
        {
			PreparedStatement statement = con.prepareStatement("SELECT char_name,9x9_rank,online FROM characters WHERE 9x9_rank>0 AND accesslevel=0 order by 9x9_rank desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String wins = result.getString("9x9_rank");
				String name = result.getString("char_name");
				
				if (name.equals("WWWWWWWWWWWWWWWW") || name.equals("WWWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWWW")|| name.equals("WWWWWWWWWWWW")|| name.equals("WWWWWWWWWWW")|| name.equals("WWWWWWWWWW")|| name.equals("WWWWWWWWW")|| name.equals("WWWWWWWW")|| name.equals("WWWWWWW")|| name.equals("WWWWWW"))
					name = name.substring(0, 3) + "..";
				else if (name.length() > 14)
					name = name.substring(0, 14) + "..";
	
				pos += 1;
				String statu = result.getString("online");
				String status;
				
				if (statu.equals("1"))
					status = "<font color=00FF00>Online</font>";
				else
					status = "<font color=FF0000>Offline</font>";
				
				tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +wins+ "</center></td><td><center>" +status+ "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
        catch (Exception e)
		{
        	//_log.warning("Error: could not restore 1x1 ranking data info: " + e);
        	e.printStackTrace();
		}        
        tb.append("</table><br>");
		tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
		tb.append("</body></html>");

		htm.setHtml(tb.toString());
		activeChar.sendPacket(htm);
	}
	
	/*
	private static void addReward1x1(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : ArenaConfig.TOP_1X1_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("1x1 Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage(player.getName() + " you are the top winner of the 1x1 tournament!", 3000, 0x02, true));
				player.getInventory().updateDatabase();
				player.sendPacket(iu);
			}
			else
				addOfflineItem(objId, reward[0], reward[1]);
		}
	}
	
	private static void addReward3x3(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : ArenaConfig.TOP_3X3_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("3x3 Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage(player.getName() + " you are the top winner of the 3x3 tournament!", 3000, 0x02, true));
				player.getInventory().updateDatabase();
				player.sendPacket(iu);
			}
			else
				addOfflineItem(objId, reward[0], reward[1]);
		}
	}
	
	private static void addReward5x5(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : ArenaConfig.TOP_5X5_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("5x5 Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage(player.getName() + " you are the top winner of the 5x5 tournament!", 3000, 0x02, true));
				player.getInventory().updateDatabase();
				player.sendPacket(iu);
			}
			else
				addOfflineItem(objId, reward[0], reward[1]);
		}
	}
	
	private static void addReward9x9(int objId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objId);
		
		for (int[] reward : ArenaConfig.TOP_9X9_PLAYER_REWARDS)
		{
			if (player != null && player.isOnline())
			{
				InventoryUpdate iu = new InventoryUpdate();
				
				player.addItem("9x9 Reward", reward[0], reward[1], player, true);
				player.sendPacket(new ExShowScreenMessage(player.getName() + " you are the top winner of the 9x9 tournament!", 3000, 0x02, true));
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
	*/
	
	public static void rankingRewardPlayer()
	{
		//Reward Top Winner
		//addReward1x1(getTopRank1x1PlayerReward());
		//addReward3x3(getTopRank3x3PlayerReward());
		//addReward5x5(getTopRank5x5PlayerReward());
		//addReward9x9(getTopRank9x9PlayerReward());
		
		//Reset Ranking
		resetRank1x1();
		//resetRank3x3();
		//resetRank5x5();
		resetRank9x9();
	}
}