/*
 * Copyright (C) 2004-2014 L2J DataPack
 * 
 * This file is part of L2J DataPack.
 * 
 * L2J DataPack is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J DataPack is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.TopKillerRoundSystem;
import net.sf.l2j.gameserver.model.entity.events.clanranking.ClanRankingConfig;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaRanking;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskClanRankingReward;

public class VoicedRanking implements IVoicedCommandHandler
{
	public static final Logger _log = Logger.getLogger(VoicedRanking.class.getName());
	
	private static final String[] VOICED_COMMANDS =
	{
		"pvp",
		"level",
		"pks",
		"clan",
		"pvpHour",
		"1x1_rank",
		"9x9_rank",
		"clanPoint",
		"eloPoint",
		"ranking"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("ranking"))
			showRankingHtml(activeChar);      
		
		if (command.equals("pvpHour") && Config.TOP_KILLER_PLAYER_ROUND)
			TopKillerRoundSystem.getTopHtml(activeChar);
		
		if (command.equals("1x1_rank"))
			ArenaRanking.getTopRank1x1Html(activeChar);

		if (command.equals("9x9_rank"))
			ArenaRanking.getTopRank9x9Html(activeChar);

		if (command.equals("pvp"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking PvP</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pvp's</center></td><td><center>Status</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,pvpkills,online FROM characters WHERE pvpkills>0 AND accesslevel=0 order by pvpkills desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String pvps = result.getString("pvpkills");
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
					
					tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +pvps+ "</center></td><td><center>" +status+ "</center></td></tr>");
				}
				statement.close();
				result.close();
				con.close();
			}
	        catch (Exception e)
			{
	        	_log.warning("Error: could not restore pvp ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}

		if (command.equals("level"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking Level</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Level</center></td><td><center>Status</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,level,online FROM characters WHERE level>0 AND accesslevel=0 order by level desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String lvl = result.getString("level");
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
					
					tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +lvl+ "</center></td><td><center>" +status+ "</center></td></tr>");
				}
				statement.close();
				result.close();
				con.close();
			}
	        catch (Exception e)
			{
	        	_log.warning("Error: could not restore pvp ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		
		if (command.equals("pks"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking PK</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Pk's</center></td><td><center>Status</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,pkkills,online FROM characters WHERE pkkills>0 AND accesslevel=0 order by pkkills desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String pks = result.getString("pkkills");
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
					
					tb.append("<tr><td><center>" +pos+ "</td><td><center><font color=00FFFF>" +name+ "</font></center></td><td><center>" +pks+ "</center></td><td><center>" +status+ "</center></td></tr>");
				}
				statement.close();
				result.close();
				con.close();
			}
	        catch (Exception e)
			{
	        	_log.warning("Error: could not restore pk ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		
		if (command.equals("clan"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Clan Ranking</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Level</center></td><td><center>Clan Name</center></td><td><center>Reputation</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT clan_name,clan_level,reputation_score FROM clan_data WHERE clan_level>0 order by reputation_score desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String clan_name = result.getString("clan_name");
					String clan_level = result.getString("clan_level");
					String clan_score = result.getString("reputation_score");
					pos += 1;

					tb.append("<tr><td><center>" +pos+ "</center></td><td><center>" +clan_level+"</center></td><td><center><font color=00FFFF>" +clan_name+ "</font></center></td><td><center><font color=00FF00>" +clan_score+ "</font></center></td></tr>");
				}
				statement.close();
				result.close();
				con.close();
			}
	        catch (Exception e)
			{
	        	_log.warning("Error: could not restore clan ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		
		/*
		if (command.equals("1x1_rank"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking Event 1x1</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Win's</center></td><td><center>Status</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,1x1_wins,online FROM characters WHERE 1x1_wins>0 AND accesslevel=0 order by 1x1_wins desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String wins = result.getString("1x1_wins");
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
	        	_log.warning("Error: could not restore 1x1 ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		
		if (command.equals("2x2_rank"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking Event 2x2</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Win's</center></td><td><center>Status</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,2x2_wins,online FROM characters WHERE 2x2_wins>0 AND accesslevel=0 order by 2x2_wins desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String wins = result.getString("2x2_wins");
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
	        	_log.warning("Error: could not restore 1x1 ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		*/
		
		if (command.equals("eloPoint"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Ranking Elo Point's</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32></center><br1><table width=290><tr><td><center>Rank</center></td><td><center>Character</center></td><td><center>Point's</center></td><td><center>Status</center></td></tr>");
	        try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT char_name,pc_point,online FROM characters WHERE pc_point>0 AND accesslevel=0 order by pc_point desc limit 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String wins = result.getString("pc_point");
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
	        	_log.warning("Error: could not restore elo ranking data info: " + e);
			}        
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}
		
		if (command.equals("clanPoint") && ClanRankingConfig.ENABLE_CLAN_RANKING)
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			StringBuilder tb = new StringBuilder("<html><head><title>Clan Ranking</title></head><body><center><img src=\"l2ui_ch3.herotower_deco\" width=256 height=32><br>Next Rewarding: <font color=LEVEL>" + TaskClanRankingReward.getTimeToDate() +"</font><br1><table width=290><tr><td><center>Rank</center></td><td><center>Clan Name</center></td><td><center>Raid Point(s)</center></td><td><center>Castle Point(s)</center></td></tr>");

			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
            {
				PreparedStatement statement = con.prepareStatement("SELECT clan_id, boss_points, siege_points FROM clan_points WHERE ((boss_points + siege_points)>0) ORDER BY (boss_points + siege_points) desc LIMIT 15");
				ResultSet result = statement.executeQuery();
				int pos = 0;

				while (result.next())
				{
					String raid = result.getString("boss_points");
					String castle = result.getString("siege_points");
					String owner = result.getString("clan_id");
					pos += 1;
					
					PreparedStatement charname = con.prepareStatement("SELECT clan_name FROM clan_data WHERE clan_id=" + owner);
					ResultSet result2 = charname.executeQuery();
					
					while (result2.next())
					{
						String clan_name = result2.getString("clan_name");

						if (clan_name.equals("WWWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWWW") || clan_name.equals("WWWWWWWWWWW") || clan_name.equals("WWWWWWWWWW") || clan_name.equals("WWWWWWWWW") || clan_name.equals("WWWWWWWW") || clan_name.equals("WWWWWWW") || clan_name.equals("WWWWWW"))
							clan_name = clan_name.substring(0, 3) + "..";
						else if (clan_name.length() > 14)
							clan_name = clan_name.substring(0, 14) + "..";
			
						tb.append("<tr><td><center>" + pos + "</center></td><td><center><font color=LEVEL>" + clan_name + "</font></center></td><td><center><font color=00FFFF>" + raid + "</font></center></td><td><center><font color=00FF00>" + castle + "</font></center></td></tr>");
					}
					charname.close();
				}
				statement.close();
				result.close();
				con.close();
			}
	        catch (Exception e)
			{
	        	_log.warning("Error: could not restore clan_points ranking data info: " + e);
	        	e.printStackTrace();
			}   
	        tb.append("</table><br>");
			tb.append("<center><a action=\"bypass -h voiced_ranking\">Back</a></center>");
			tb.append("</body></html>");

			htm.setHtml(tb.toString());
			activeChar.sendPacket(htm);
		}

		return true;
	}

	private static void showRankingHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Ranking.htm"); 
		activeChar.sendPacket(html);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}