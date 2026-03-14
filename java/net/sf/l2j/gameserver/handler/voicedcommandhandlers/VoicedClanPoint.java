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

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskClanRankingReward;

public class VoicedClanPoint implements IVoicedCommandHandler
{
	public static final Logger _log = Logger.getLogger(VoicedClanPoint.class.getName());
	
	private static final String[] VOICED_COMMANDS =
	{
		"clanPoint"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("clanPoint"))
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

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}