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

import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFConfig;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMConfig;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSConfig;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEventTeleporter;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTConfig;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTConfig;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedEvent implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"ctfinfo",
		"ctfjoin",
		"ctfleave",
		"dminfo",
		"dmjoin",
		"dmleave",
		"fosinfo",
		"fosjoin",
		"fosleave",
		"lminfo",
		"lmjoin",
		"lmleave",
		"tvtinfo",
		"tvtjoin",
		"tvtleave",
		"ktbinfo",
		"ktbjoin",
		"ktbleave",
		"mtvtinfo",
		"mtvtjoin",
		"mtvtleave"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("ctfinfo"))
		{
			if (CTFEvent.isStarting() || CTFEvent.isStarted())
			{
				showCTFStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Capture the Flag event is not in progress.");
				return false;
			}
		}
		else if (command.equals("ctfjoin"))
		{
			if (!CTFEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				CTFEvent.onBypass("ctf_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equals("ctfleave"))
		{
			if (CTFEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				CTFEvent.onBypass("ctf_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equals("dminfo"))
		{
			if (DMEvent.isStarting() || DMEvent.isStarted())
			{
				showDMStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Deathmatch fight is not in progress.");
				return false;
			}
		}
		else if (command.equalsIgnoreCase("dmjoin"))
		{
			if (!DMEvent.isPlayerParticipant(activeChar))
			{
				DMEvent.onBypass("dm_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equalsIgnoreCase("dmleave"))
		{
			if (DMEvent.isPlayerParticipant(activeChar))
			{
				DMEvent.onBypass("dm_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equals("fosinfo"))
		{
			if (FOSEvent.isStarting() || FOSEvent.isStarted())
			{
				showFOSStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Fortress event is not in progress.");
				return false;
			}
		}
		else if (command.equals("fosjoin"))
		{
			if (!FOSEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				FOSEvent.onBypass("fos_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equals("fosleave"))
		{
			if (FOSEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				FOSEvent.onBypass("fos_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equals("lminfo"))
		{
			if (LMEvent.isStarting() || LMEvent.isStarted())
			{
				showLMStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Last Man fight is not in progress.");
				return false;
			}
		}
		else if (command.equalsIgnoreCase("lmjoin"))
		{
			if (!LMEvent.isPlayerParticipant(activeChar))
			{
				LMEvent.onBypass("lm_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equalsIgnoreCase("lmleave"))
		{
			if (LMEvent.isPlayerParticipant(activeChar))
			{
				LMEvent.onBypass("lm_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equals("tvtinfo"))
		{
			if (TvTEvent.isStarting() || TvTEvent.isStarted())
			{
				showTvTStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Team vs Team fight is not in progress.");
				return false;
			}
		}
		else if (command.equals("tvtjoin"))
		{
			if (!TvTEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				TvTEvent.onBypass("tvt_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equals("tvtleave"))
		{
			if (TvTEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				TvTEvent.onBypass("tvt_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equals("mtvtinfo"))
		{
			if (MultiTvTEvent.isStarting() || MultiTvTEvent.isStarted())
			{
				showMultiTvTStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Team vs Team fight is not in progress.");
				return false;
			}
		}
		else if (command.equals("mtvtjoin"))
		{
			if (!MultiTvTEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				MultiTvTEvent.onBypass("tvt_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equals("mtvtleave"))
		{
			if (MultiTvTEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				MultiTvTEvent.onBypass("tvt_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equals("ktbinfo"))
		{
			if (KTBEvent.isStarting() || KTBEvent.isStarted())
			{
				showKTBStatuPage(activeChar);
				return true;
			}
			else
			{
				activeChar.sendMessage("Kill the Boss event is not in progress.");
				return false;
			}
		}
		else if (command.equals("ktbjoin"))
		{
			if (!KTBEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				KTBEvent.onBypass("ktb_event_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are already registered.");
				return false;
			}
		}
		else if (command.equals("ktbleave"))
		{
			if (KTBEvent.isPlayerParticipant(activeChar.getObjectId()))
			{
				KTBEvent.onBypass("ktb_event_remove_participation", activeChar);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		else if (command.equalsIgnoreCase("port"))
		{
			if (LMEvent.isPlayerParticipant(activeChar) && LMEvent.isStarted())
			{
				new LMEventTeleporter(activeChar, false, false);
			}
			else
			{
				activeChar.sendMessage("You are not registered.");
				return false;
			}
		}
		return true;
	}

	private static void showCTFStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
		html.setFile("data/html/mods/events/ctf/Status.htm");
		html.replace("%team1name%", CTFConfig.CTF_EVENT_TEAM_1_NAME);
		html.replace("%team1playercount%", String.valueOf(CTFEvent.getTeamsPlayerCounts()[0]));
		html.replace("%team1points%", String.valueOf(CTFEvent.getTeamsPoints()[0]));
		html.replace("%team2name%", CTFConfig.CTF_EVENT_TEAM_2_NAME);
		html.replace("%team2playercount%", String.valueOf(CTFEvent.getTeamsPlayerCounts()[1]));
		html.replace("%team2points%", String.valueOf(CTFEvent.getTeamsPoints()[1]));
		activeChar.sendPacket(html);	
	}

	private static void showFOSStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
		html.setFile("data/html/mods/events/fos/Status.htm");
		html.replace("%team1name%", FOSConfig.FOS_EVENT_TEAM_1_NAME);
		html.replace("%team1playercount%", String.valueOf(FOSEvent.getTeamsPlayerCounts()[0]));
		html.replace("%team1points%", String.valueOf(FOSEvent.getTeamsPoints()[0]));
		html.replace("%team2name%", FOSConfig.FOS_EVENT_TEAM_2_NAME);
		html.replace("%team2playercount%", String.valueOf(FOSEvent.getTeamsPlayerCounts()[1]));
		html.replace("%team2points%", String.valueOf(FOSEvent.getTeamsPoints()[1]));
		activeChar.sendPacket(html);	
	}

	private static void showDMStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
		final String htmContent = HtmCache.getInstance().getHtm("data/html/mods/events/dm/Status.htm");

		String[] topPositions;
		String htmltext = "";
		if (DMConfig.DM_SHOW_TOP_RANK)
		{
			topPositions = DMEvent.getFirstPosition(DMConfig.DM_TOP_RANK);
			Boolean c = true;
			String c1 = "LEVEL";
			String c2 = "FFFFFF";
			if (topPositions != null)
				for (int i = 0; i < topPositions.length; i++)
				{
					String color = (c ? c1 : c2);
					String[] row = topPositions[i].split("\\,");
					htmltext += "<tr>";
					htmltext += "<td width=\"35\" align=\"center\"><font color=\"" + color + "\">" + String.valueOf(i + 1) + "</font></td>";
					htmltext += "<td width=\"100\" align=\"left\"><font color=\"" + color + "\">" + row[0] + "</font></td>";
					htmltext += "<td width=\"125\" align=\"right\"><font color=\"" + color + "\">" + row[1] + "</font></td>";
					htmltext += "</tr>";
					c = !c;
				}
		}
		
    	if (htmContent != null)
    	{
			npcHtmlMessage.setHtml(htmContent);
			npcHtmlMessage.replace("%positions%", htmltext);
			activeChar.sendPacket(npcHtmlMessage);
    	}
	}

	private static void showLMStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
		html.setFile("data/html/mods/events/lm/Status.htm");
		String htmltext = "";
		htmltext = String.valueOf(LMEvent.getPlayerCounts());
		html.replace("%countplayer%", htmltext);
		activeChar.sendPacket(html);
	}

	private static void showTvTStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
		html.setFile("data/html/mods/events/tvt/Status.htm");
		html.replace("%team1name%", TvTConfig.TVT_EVENT_TEAM_1_NAME);
		html.replace("%team1playercount%", String.valueOf(TvTEvent.getTeamsPlayerCounts()[0]));
		html.replace("%team1points%", String.valueOf(TvTEvent.getTeamsPoints()[0]));
		html.replace("%team2name%", TvTConfig.TVT_EVENT_TEAM_2_NAME);
		html.replace("%team2playercount%", String.valueOf(TvTEvent.getTeamsPlayerCounts()[1]));
		html.replace("%team2points%", String.valueOf(TvTEvent.getTeamsPoints()[1]));
		activeChar.sendPacket(html);	
	}

	private static void showMultiTvTStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
		html.setFile("data/html/mods/events/multitvt/Status.htm");
		html.replace("%team1name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_1_NAME);
		html.replace("%team1playercount%", String.valueOf(MultiTvTEvent.getTeamsPlayerCounts()[0]));
		html.replace("%team1points%", String.valueOf(MultiTvTEvent.getTeamsPoints()[0]));
		html.replace("%team2name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_2_NAME);
		html.replace("%team2playercount%", String.valueOf(MultiTvTEvent.getTeamsPlayerCounts()[1]));
		html.replace("%team2points%", String.valueOf(MultiTvTEvent.getTeamsPoints()[1]));
		html.replace("%team3name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_3_NAME);
		html.replace("%team3playercount%", String.valueOf(MultiTvTEvent.getTeamsPlayerCounts()[2]));
		html.replace("%team3points%", String.valueOf(MultiTvTEvent.getTeamsPoints()[2]));
		html.replace("%team4name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_4_NAME);
		html.replace("%team4playercount%", String.valueOf(MultiTvTEvent.getTeamsPlayerCounts()[3]));
		html.replace("%team4points%", String.valueOf(MultiTvTEvent.getTeamsPoints()[3]));
		activeChar.sendPacket(html);	
	}

	private static void showKTBStatuPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(activeChar.getObjectId());
		html.setFile("data/html/mods/events/ktb/Status.htm");
		String htmltext = "";
		htmltext = String.valueOf(KTBEvent.getPlayerCounts());
		html.replace("%countplayer%", htmltext);
		activeChar.sendPacket(html);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}