/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFConfig;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMConfig;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSConfig;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTConfig;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTConfig;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class L2EventManagerInstance extends L2NpcInstance
{
	private static final String ctfhtmlPath = "data/html/mods/events/ctf/";
	private static final String TvthtmlPath = "data/html/mods/events/tvt/";
	private static final String dmhtmlPath = "data/html/mods/events/dm/";
	private static final String lmhtmlPath = "data/html/mods/events/lm/";
	private static final String ktbhtmlPath = "data/html/mods/events/ktb/";
	private static final String foshtmlPath = "data/html/mods/events/fos/";
	private static final String MultihtmlPath = "data/html/mods/events/multitvt/";
	
	public L2EventManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(L2PcInstance playerInstance, String command)
	{
		CTFEvent.onBypass(command, playerInstance);
		TvTEvent.onBypass(command, playerInstance);
		DMEvent.onBypass(command, playerInstance);
		LMEvent.onBypass(command, playerInstance);
		KTBEvent.onBypass(command, playerInstance);
		FOSEvent.onBypass(command, playerInstance);
		MultiTvTEvent.onBypass(command, playerInstance);
	}
	
	@Override
	public void showChatWindow(L2PcInstance playerInstance, int val)
	{
		if (playerInstance == null)
			return;
		
		if (TvTEvent.isParticipating())
		{
			final boolean isParticipant = TvTEvent.isPlayerParticipant(playerInstance.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(TvthtmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(TvthtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", TvTConfig.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", TvTConfig.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
				if (!isParticipant)
				{
					npcHtmlMessage.replace("%fee%", TvTEvent.getParticipationFee());
				}
				
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (TvTEvent.isStarting() || TvTEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(TvthtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = TvTEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = TvTEvent.getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%team1name%", TvTConfig.TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", TvTConfig.TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (DMEvent.isParticipating())
		{
			final boolean isParticipant = DMEvent.isPlayerParticipant(playerInstance.getObjectId()); 
			final String htmContent;

			if (!isParticipant)
			    htmContent = HtmCache.getInstance().getHtm(dmhtmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(dmhtmlPath + "RemoveParticipation.htm");

	    	if (htmContent != null)
	    	{
	    		int PlayerCounts = DMEvent.getPlayerCounts();
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

				npcHtmlMessage.setHtml(htmContent);
	    		npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(PlayerCounts));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", DMEvent.getParticipationFee());

				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (DMEvent.isStarting() || DMEvent.isStarted())
		{
			NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(0);
			final String htmContent = HtmCache.getInstance().getHtm(dmhtmlPath + "Status.htm");

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
				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (LMEvent.isParticipating())
		{
			final boolean isParticipant = LMEvent.isPlayerParticipant(playerInstance.getObjectId()); 
			final String htmContent;

			if (!isParticipant)
			    htmContent = HtmCache.getInstance().getHtm(lmhtmlPath + "Participation.htm");
			else
			    htmContent = HtmCache.getInstance().getHtm(lmhtmlPath + "RemoveParticipation.htm");

	    	if (htmContent != null)
	    	{
	    		int PlayerCounts = LMEvent.getPlayerCounts();
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

				npcHtmlMessage.setHtml(htmContent);
	    		npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(PlayerCounts));
				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (LMEvent.isStarting() || LMEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(lmhtmlPath + "Status.htm");

	    	if (htmContent != null)
	    	{
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
	    		String htmltext = "";
	    		htmltext = String.valueOf(LMEvent.getPlayerCounts());
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%countplayer%", htmltext);
				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (CTFEvent.isParticipating())
		{
			final boolean isParticipant = CTFEvent.isPlayerParticipant(playerInstance.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(ctfhtmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(ctfhtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = CTFEvent.getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", CTFConfig.CTF_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", CTFConfig.CTF_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (CTFEvent.isStarting() || CTFEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(ctfhtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = CTFEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = CTFEvent.getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%team1name%", CTFConfig.CTF_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", CTFConfig.CTF_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (KTBEvent.isParticipating())
		{
			final boolean isParticipant = KTBEvent.isPlayerParticipant(playerInstance.getObjectId()); 
			final String htmContent;

			if (!isParticipant)
			    htmContent = HtmCache.getInstance().getHtm(ktbhtmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(ktbhtmlPath + "RemoveParticipation.htm");

	    	if (htmContent != null)
	    	{
	    		int PlayerCounts = KTBEvent.getPlayerCounts();
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());

				npcHtmlMessage.setHtml(htmContent);
	    		npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%playercount%", String.valueOf(PlayerCounts));
				if (!isParticipant)
					npcHtmlMessage.replace("%fee%", KTBEvent.getParticipationFee());

				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (KTBEvent.isStarting() || KTBEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(ktbhtmlPath + "Status.htm");
			
	    	if (htmContent != null)
	    	{
	    		NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
	    		String htmltext = "";
	    		htmltext = String.valueOf(KTBEvent.getPlayerCounts());
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%countplayer%", htmltext);
				playerInstance.sendPacket(npcHtmlMessage);
	    	}
		}
		else if (FOSEvent.isParticipating())
		{
			final boolean isParticipant = FOSEvent.isPlayerParticipant(playerInstance.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(foshtmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(foshtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = FOSEvent.getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", FOSConfig.FOS_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", FOSConfig.FOS_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1]));
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (FOSEvent.isStarting() || FOSEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(foshtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = FOSEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = FOSEvent.getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%team1name%", FOSConfig.FOS_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", FOSConfig.FOS_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (MultiTvTEvent.isParticipating())
		{
			final boolean isParticipant = MultiTvTEvent.isPlayerParticipant(playerInstance.getObjectId());
			final String htmContent;
			
			if (!isParticipant)
				htmContent = HtmCache.getInstance().getHtm(MultihtmlPath + "Participation.htm");
			else
				htmContent = HtmCache.getInstance().getHtm(MultihtmlPath + "RemoveParticipation.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = MultiTvTEvent.getTeamsPlayerCounts();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%objectId%", String.valueOf(getObjectId()));
				npcHtmlMessage.replace("%team1name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team2name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team3name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_3_NAME);
				npcHtmlMessage.replace("%team3playercount%", String.valueOf(teamsPlayerCounts[2]));
				npcHtmlMessage.replace("%team4name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_4_NAME);
				npcHtmlMessage.replace("%team4playercount%", String.valueOf(teamsPlayerCounts[3]));
				npcHtmlMessage.replace("%playercount%", String.valueOf(teamsPlayerCounts[0] + teamsPlayerCounts[1] + teamsPlayerCounts[2] + teamsPlayerCounts[3]));
				if (!isParticipant)
				{
					npcHtmlMessage.replace("%fee%", MultiTvTEvent.getParticipationFee());
				}
				
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		else if (MultiTvTEvent.isStarting() || MultiTvTEvent.isStarted())
		{
			final String htmContent = HtmCache.getInstance().getHtm(MultihtmlPath + "Status.htm");
			
			if (htmContent != null)
			{
				int[] teamsPlayerCounts = MultiTvTEvent.getTeamsPlayerCounts();
				int[] teamsPointsCounts = MultiTvTEvent.getTeamsPoints();
				NpcHtmlMessage npcHtmlMessage = new NpcHtmlMessage(getObjectId());
				
				npcHtmlMessage.setHtml(htmContent);
				npcHtmlMessage.replace("%team1name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_1_NAME);
				npcHtmlMessage.replace("%team1playercount%", String.valueOf(teamsPlayerCounts[0]));
				npcHtmlMessage.replace("%team1points%", String.valueOf(teamsPointsCounts[0]));
				npcHtmlMessage.replace("%team2name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_2_NAME);
				npcHtmlMessage.replace("%team2playercount%", String.valueOf(teamsPlayerCounts[1]));
				npcHtmlMessage.replace("%team2points%", String.valueOf(teamsPointsCounts[1])); // <---- array index from 0 to 1 thx DaRkRaGe
				npcHtmlMessage.replace("%team3name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_3_NAME);
				npcHtmlMessage.replace("%team3playercount%", String.valueOf(teamsPlayerCounts[2]));
				npcHtmlMessage.replace("%team3points%", String.valueOf(teamsPointsCounts[2])); // <---- array index from 0 to 1 thx DaRkRaGe
				npcHtmlMessage.replace("%team4name%", MultiTvTConfig.MULTI_TVT_EVENT_TEAM_4_NAME);
				npcHtmlMessage.replace("%team4playercount%", String.valueOf(teamsPlayerCounts[3]));
				npcHtmlMessage.replace("%team4points%", String.valueOf(teamsPointsCounts[3])); // <---- array index from 0 to 1 thx DaRkRaGe
				playerInstance.sendPacket(npcHtmlMessage);
			}
		}
		playerInstance.sendPacket(ActionFailed.STATIC_PACKET);
	}
}