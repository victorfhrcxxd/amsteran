package net.sf.l2j.gameserver.handler.community.ranking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.communitybbs.Manager.BaseBBSManager;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.handler.ICBBypassHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskClanRankingReward;

public class RankingCBBypasses implements ICBBypassHandler
{
	@Override
	public boolean handleBypass(String bypass, L2PcInstance activeChar)
	{
		if (bypass.startsWith("bp_showTopPvP"))
			showPvPBoard(activeChar, "pvp");
		
		if (bypass.startsWith("bp_showTopPK"))
			showPKBoard(activeChar, "pk");
		
		if (bypass.startsWith("bp_showClanPoint"))
			showClanPointBoard(activeChar, "clanPoint");
		
		if (bypass.startsWith("bp_showEnchant"))
			showTopEnchantBoard(activeChar, "topEnchant");

		return false;
	}

	public void showPvPBoard(L2PcInstance player, String file)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/" + file + ".htm");
		content = content.replace("%pvpList%", generateTopPvPHtml(player));

		BaseBBSManager.separateAndSend(content, player);
	}

	public void showPKBoard(L2PcInstance player, String file)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/" + file + ".htm");
		content = content.replace("%pkList%", generateTopPKHtml(player));

		BaseBBSManager.separateAndSend(content, player);
	}

	public void showClanPointBoard(L2PcInstance player, String file)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/" + file + ".htm");
		content = content.replace("%clanPointList%", generateTopClanPointHtml(player));
		content = content.replace("%restartClanPoint%", TaskClanRankingReward.getTimeToDate());

		BaseBBSManager.separateAndSend(content, player);
	}

	public void showTopEnchantBoard(L2PcInstance player, String file)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/" + file + ".htm");
		content = content.replace("%enchantList%", generateToEnchantHtml(player));

		BaseBBSManager.separateAndSend(content, player);
	}
	
	public String generateTopPvPHtml(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Class</center></td><td><center>Clan</center></td><td><center>Pvp's</center></td><td><center>Status</center></td></tr>");

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT c.char_name,c.pvpkills,c.online,c.clanid,c.classid,cl.clan_name FROM characters c LEFT JOIN clan_data cl ON cl.clan_id=c.clanid WHERE c.pvpkills>0 AND c.accesslevel=0 order by c.pvpkills desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String pvps = result.getString("pvpkills");
				String name = result.getString("char_name");
				String clname = result.getString("clan_name");
				int classname = result.getInt("classid");
				String clid = result.getString("clanid");

				if (clid.equals("0"))
					clname = "N/A Clan";

				pos += 1;
				String statu = result.getString("online");
				String status;

				if (statu.equals("1"))
					status = "<img src=\"panel.online\" width=\"16\" height=\"16\">";
				else
					status = "<img src=\"panel.offline\" width=\"16\" height=\"16\">";

				tb.append("<tr><td><center><font color=E9967A>" +pos+ "</font></td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center><font color=FFFF00>" +classId(classname)+ "</font></center></td><td><center><font color=FFA500>" +clname+ "</font></center></td><td><center><font color=74f22E>" +pvps+ "</font></center></td><td><center>" +status+ "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}     

		return tb.toString();
	}

	public String generateTopPKHtml(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Class</center></td><td><center>Clan</center></td><td><center>Pk's</center></td><td><center>Status</center></td></tr>");

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT c.char_name,c.pkkills,c.online,c.clanid,c.classid,cl.clan_name FROM characters c LEFT JOIN clan_data cl ON cl.clan_id=c.clanid WHERE c.pkkills>0 AND c.accesslevel=0 order by c.pkkills desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String pks = result.getString("pkkills");
				String name = result.getString("char_name");
				String clname = result.getString("clan_name");
				int classname = result.getInt("classid");
				String clid = result.getString("clanid");

				if (clid.equals("0"))
					clname = "N/A Clan";

				pos += 1;
				String statu = result.getString("online");
				String status;

				if (statu.equals("1"))
					status = "<img src=\"panel.online\" width=\"16\" height=\"16\">";
				else
					status = "<img src=\"panel.offline\" width=\"16\" height=\"16\">";

				tb.append("<tr><td><center><font color=E9967A>" +pos+ "</font></td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center><font color=FFFF00>" +classId(classname)+ "</font></center></td><td><center><font color=FFA500>" +clname+ "</font></center></td><td><center><font color=FF0000>" +pks+ "</font></center></td><td><center>" +status+ "</center></td></tr>");
			}
			statement.close();
			result.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}     

		return tb.toString();
	}
	
	public String generateTopClanPointHtml(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<tr><td><center>Rank</center></td><td><center>Clan Name</center></td><td><center>Leader Name</center></td><td><center>Champion Point(s)</center></td><td><center>Raid Point(s)</center></td><td><center>Castle Point(s)</center></td></tr>");

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT clan_id, champ_points, boss_points, siege_points FROM clan_points WHERE ((champ_points + boss_points + siege_points)>0) ORDER BY (champ_points + boss_points + siege_points) desc LIMIT 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String champ = result.getString("champ_points");
				String raid = result.getString("boss_points");
				String castle = result.getString("siege_points");
				String owner = result.getString("clan_id");
				pos += 1;

				PreparedStatement clanname = con.prepareStatement("SELECT cl.clan_name,c.char_name FROM clan_data cl LEFT JOIN characters c ON c.obj_Id=cl.leader_id WHERE cl.clan_id=" + owner);
				ResultSet result2 = clanname.executeQuery();
				
				while (result2.next())
				{
					String clan_name = result2.getString("clan_name");
					String name = result2.getString("char_name");
					
					tb.append("<tr><td><center><font color=E9967A>" +pos+ "</font></center></td><td><center><font color=LEVEL>" + clan_name + "</font></center></td><td><center><font color=00FF66>" + name + "</font></center></td><td><center><font color=ff6900>" + champ + "</font></center></td><td><center><font color=00FFFF>" + raid + "</font></center></td><td><center><font color=00FF00>" + castle + "</font></center></td></tr>");
				}
				clanname.close();
			}
			statement.close();
			result.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}     

		return tb.toString();
	}

	public String generateToEnchantHtml(L2PcInstance player)
	{
		StringBuilder tb = new StringBuilder();
		tb.append("<tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Weapon</center></td><td><center>Enchant(s)</center></td><td><center>Status</center></td></tr>");

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT item_id,enchant_level,owner_id FROM items WHERE item_id IN (" + Config.WEAPONS_ENCHANT_LIST_ID + ") AND enchant_level>=7 order by enchant_level desc limit 15");
			ResultSet result = statement.executeQuery();
			int pos = 0;

			while (result.next())
			{
				String enchant = result.getString("enchant_level");
				String owner = result.getString("owner_id");
				int it = result.getInt("item_id");
				pos += 1;
				
				Item item = ItemTable.getInstance().getTemplate(it);
				
				PreparedStatement charname = con.prepareStatement("SELECT char_name,online FROM characters WHERE obj_Id=" + owner +" AND accesslevel=0");
				ResultSet result2 = charname.executeQuery();
				
				while (result2.next())
				{
					String char_name = result2.getString("char_name");
					String statu = result2.getString("online");
					String status;

					if (statu.equals("1"))
						status = "<img src=\"panel.online\" width=\"16\" height=\"16\">";
					else
						status = "<img src=\"panel.offline\" width=\"16\" height=\"16\">";
					
					tb.append("<tr><td><center><font color=E9967A>" +pos+ "</font></center></td><td><center><font color=LEVEL>" + char_name + "</font></center></td><td><center><font color=00FF66>" + item.getName() + "</font></center></td><td><center><font color=00FFFF>+" + enchant + "</font></center></td><td><center>" +status+ "</center></td></tr>");
				}
				charname.close();
			}
			statement.close();
			result.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}     

		return tb.toString();
	}
	
	public static final String classId(int classId)
	{
		HashMap<Integer, String> fastMap = new HashMap<>();
		
		fastMap.put(Integer.valueOf(0), "Fighter");
		fastMap.put(Integer.valueOf(1), "Warrior");
		fastMap.put(Integer.valueOf(2), "Gladiator");
		fastMap.put(Integer.valueOf(3), "Warlord");
		fastMap.put(Integer.valueOf(4), "Knight");
		fastMap.put(Integer.valueOf(5), "Paladin");
		fastMap.put(Integer.valueOf(6), "Dark Avenger");
		fastMap.put(Integer.valueOf(7), "Rogue");
		fastMap.put(Integer.valueOf(8), "Treasure Hunter");
		fastMap.put(Integer.valueOf(9), "Hawkeye");
		fastMap.put(Integer.valueOf(10), "Mage");
		fastMap.put(Integer.valueOf(11), "Wizard");
		fastMap.put(Integer.valueOf(12), "Sorcerer");
		fastMap.put(Integer.valueOf(13), "Necromancer");
		fastMap.put(Integer.valueOf(14), "Warlock");
		fastMap.put(Integer.valueOf(15), "Cleric");
		fastMap.put(Integer.valueOf(16), "Bishop");
		fastMap.put(Integer.valueOf(17), "Prophet");
		fastMap.put(Integer.valueOf(18), "Elven Fighter");
		fastMap.put(Integer.valueOf(19), "Elven Knight");
		fastMap.put(Integer.valueOf(20), "Temple Knight");
		fastMap.put(Integer.valueOf(21), "Swordsinger");
		fastMap.put(Integer.valueOf(22), "Elven Scout");
		fastMap.put(Integer.valueOf(23), "Plains Walker");
		fastMap.put(Integer.valueOf(24), "Silver Ranger");
		fastMap.put(Integer.valueOf(25), "Elven Mage");
		fastMap.put(Integer.valueOf(26), "Elven Wizard");
		fastMap.put(Integer.valueOf(27), "Spellsinger");
		fastMap.put(Integer.valueOf(28), "Elemental Summoner");
		fastMap.put(Integer.valueOf(29), "Oracle");
		fastMap.put(Integer.valueOf(30), "Elder");
		fastMap.put(Integer.valueOf(31), "Dark Fighter");
		fastMap.put(Integer.valueOf(32), "Palus Knightr");
		fastMap.put(Integer.valueOf(33), "Shillien Knight");
		fastMap.put(Integer.valueOf(34), "Bladedancer");
		fastMap.put(Integer.valueOf(35), "Assasin");
		fastMap.put(Integer.valueOf(36), "Abyss Walker");
		fastMap.put(Integer.valueOf(37), "Phantom Ranger");
		fastMap.put(Integer.valueOf(38), "Dark Mage");
		fastMap.put(Integer.valueOf(39), "Dark Wizard");
		fastMap.put(Integer.valueOf(40), "Spellhowler");
		fastMap.put(Integer.valueOf(41), "Phantom Summoner");
		fastMap.put(Integer.valueOf(42), "Shillien Oracle");
		fastMap.put(Integer.valueOf(43), "Shilien Elder");
		fastMap.put(Integer.valueOf(44), "Orc Fighter");
		fastMap.put(Integer.valueOf(45), "Orc Raider");
		fastMap.put(Integer.valueOf(46), "Destroyer");
		fastMap.put(Integer.valueOf(47), "Orc Monk");
		fastMap.put(Integer.valueOf(48), "Tyrant");
		fastMap.put(Integer.valueOf(49), "Orc Mage");
		fastMap.put(Integer.valueOf(50), "Orc Shaman");
		fastMap.put(Integer.valueOf(51), "Overlord");
		fastMap.put(Integer.valueOf(52), "Warcryer");
		fastMap.put(Integer.valueOf(53), "Dwarven Fighter");
		fastMap.put(Integer.valueOf(54), "Scavenger");
		fastMap.put(Integer.valueOf(55), "Bounty Hunter");
		fastMap.put(Integer.valueOf(56), "Artisan");
		fastMap.put(Integer.valueOf(57), "Warsmith");
		fastMap.put(Integer.valueOf(88), "Duelist");
		fastMap.put(Integer.valueOf(89), "Dreadnought");
		fastMap.put(Integer.valueOf(90), "Phoenix Knight");
		fastMap.put(Integer.valueOf(91), "Hell Knight");
		fastMap.put(Integer.valueOf(92), "Sagittarius");
		fastMap.put(Integer.valueOf(93), "Adventurer");
		fastMap.put(Integer.valueOf(94), "Archmage");
		fastMap.put(Integer.valueOf(95), "Soultaker");
		fastMap.put(Integer.valueOf(96), "Arcana Lord");
		fastMap.put(Integer.valueOf(97), "Cardinal");
		fastMap.put(Integer.valueOf(98), "Hierophant");
		fastMap.put(Integer.valueOf(99), "Evas Templar");
		fastMap.put(Integer.valueOf(100), "Sword Muse");
		fastMap.put(Integer.valueOf(101), "Wind Rider");
		fastMap.put(Integer.valueOf(102), "Moonlight Sentinel");
		fastMap.put(Integer.valueOf(103), "Mystic Muse");
		fastMap.put(Integer.valueOf(104), "Elemental Master");
		fastMap.put(Integer.valueOf(105), "Evas Saint");
		fastMap.put(Integer.valueOf(106), "Shillien Templar");
		fastMap.put(Integer.valueOf(107), "Spectral Dancer");
		fastMap.put(Integer.valueOf(108), "Ghost Hunter");
		fastMap.put(Integer.valueOf(109), "Ghost Sentinel");
		fastMap.put(Integer.valueOf(110), "Storm Screamer");
		fastMap.put(Integer.valueOf(111), "Spectral Master");
		fastMap.put(Integer.valueOf(112), "Shillien Saint");
		fastMap.put(Integer.valueOf(113), "Titan");
		fastMap.put(Integer.valueOf(114), "Grand Khavatari");
		fastMap.put(Integer.valueOf(115), "Dominator");
		fastMap.put(Integer.valueOf(116), "Doomcryer");
		fastMap.put(Integer.valueOf(117), "Fortune Seeker");
		fastMap.put(Integer.valueOf(118), "Maestro");
		
		return fastMap.get(Integer.valueOf(classId));
	}
	  
	@Override
	public String[] getBypassHandlersList()
	{
		return new String[] { "bp_showTopPvP", "bp_showTopPK", "bp_showClanPoint", "bp_showEnchant" };
	}
}