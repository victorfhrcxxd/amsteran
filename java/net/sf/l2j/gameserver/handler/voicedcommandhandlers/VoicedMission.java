package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.events.MissionReset;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class VoicedMission implements IVoicedCommandHandler
{
	public static final Logger _log = Logger.getLogger(VoicedMission.class.getName());
	
	private static final String[] VOICED_COMMANDS = 
	{ 
		"mission",
		"tvt_mission",
		"tvt_mission_reward",
		"ctf_mission",
		"ctf_mission_reward",
		"ctf_mission",
		"ctf_mission_reward",
		"dm_mission",
		"dm_mission_reward",
		"ktb_mission",
		"ktb_mission_reward",
		"tournament_mission",
		"tournament_mission_reward",
		"1x1_mission",
		"1x1_mission_reward",
		"3x3_mission",
		"3x3_mission_reward",
		"5x5_mission",
		"5x5_mission_reward",
		"9x9_mission",
		"9x9_mission_reward",
		"farm_mission",
		"farm_mission_reward",
		"champion_mission",
		"champion_mission_reward",
		"pvp_mission",
		"pvp_mission_reward",
		"kill_mission",
		"kill_mission_reward"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("mission"))
			sendMissionHtmlMessage(activeChar, "data/html/mods/menu/Mission.htm");        
		
		if (command.equals("tvt_mission"))
			sendTvTMissionHtmlMessage(activeChar);        
		
		if (command.equals("tvt_mission_reward"))
			sendTvTMissionReward(activeChar);        
		
		if (command.equals("ctf_mission"))
			sendCTFMissionHtmlMessage(activeChar);        
		
		if (command.equals("ctf_mission_reward"))
			sendCTFMissionReward(activeChar);        
		
		if (command.equals("dm_mission"))
			sendDMMissionHtmlMessage(activeChar);        
		
		if (command.equals("dm_mission_reward"))
			sendDMMissionReward(activeChar);        
		
		if (command.equals("ktb_mission"))
			sendKTBMissionHtmlMessage(activeChar);        
		
		if (command.equals("ktb_mission_reward"))
			sendKTBMissionReward(activeChar);        
		
		if (command.equals("tournament_mission"))
			sendTournamentMissionHtmlMessage(activeChar);        
		
		if (command.equals("tournament_mission_reward"))
			sendTournamentMissionReward(activeChar);        

		if (command.equals("1x1_mission"))
			send1x1MissionHtmlMessage(activeChar);        
		
		if (command.equals("1x1_mission_reward"))
			send1x1MissionReward(activeChar);        

		if (command.equals("3x3_mission"))
			send3x3MissionHtmlMessage(activeChar);        
		
		if (command.equals("3x3_mission_reward"))
			send3x3MissionReward(activeChar);        

		if (command.equals("5x5_mission"))
			send5x5MissionHtmlMessage(activeChar);        
		
		if (command.equals("5x5_mission_reward"))
			send5x5MissionReward(activeChar);        

		if (command.equals("9x9_mission"))
			send9x9MissionHtmlMessage(activeChar);        
		
		if (command.equals("9x9_mission_reward"))
			send9x9MissionReward(activeChar);        
		
		if (command.equals("farm_mission"))
			sendFarmMissionHtmlMessage(activeChar);        
		
		if (command.equals("farm_mission_reward"))
			sendFarmMissionReward(activeChar);    
		
		if (command.equals("champion_mission"))
			sendChampionMissionHtmlMessage(activeChar);        
		
		if (command.equals("champion_mission_reward"))
			sendChampionMissionReward(activeChar);    
		
		if (command.equals("pvp_mission"))
			sendPvPMissionHtmlMessage(activeChar);        
		
		if (command.equals("pvp_mission_reward"))
			sendPvPMissionReward(activeChar);     
		
		if (command.equals("kill_mission"))
			sendRaidKillMissionHtmlMessage(activeChar);        
		
		if (command.equals("kill_mission_reward"))
			sendRaidKillMissionReward(activeChar);        
		
		return true;
	}
	
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	private void sendMissionHtmlMessage(L2PcInstance player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(file); 
		
		//TVT
		html.replace("%tvt_cont%", "" + Config.MISSION_TVT_COUNT);	
		if (player.isTvTCompleted())
			html.replace("%tvt%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getTvTCont() >= Config.MISSION_TVT_COUNT)
			html.replace("%tvt%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
		
		//CTF
		html.replace("%ctf_cont%", "" + Config.MISSION_CTF_COUNT);	
		html.replace("%cont%", "" + player.getTvTCont());	
		if (player.isCTFCompleted())
			html.replace("%ctf%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getCTFCont() >= Config.MISSION_CTF_COUNT)
			html.replace("%ctf%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%ctf%", "<font color=\"FF0000\">Not Completed</font>");
		
		//DM
		html.replace("%dm_cont%", "" + Config.MISSION_DM_COUNT);	
		if (player.isDMCompleted())
			html.replace("%dm%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getDMCont() >= Config.MISSION_DM_COUNT)
			html.replace("%dm%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%dm%", "<font color=\"FF0000\">Not Completed</font>");
		
		//KTB
		html.replace("%ktb_cont%", "" + Config.MISSION_KTB_COUNT);	
		if (player.isKTBCompleted())
			html.replace("%ktb%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getKTBCont() >= Config.MISSION_KTB_COUNT)
			html.replace("%ktb%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%ktb%", "<font color=\"FF0000\">Not Completed</font>");
		
		//TOURNAMENT
		html.replace("%tour_cont%", "" + Config.MISSION_TOURNAMENT_COUNT);	
		if (player.isTournamentCompleted())
			html.replace("%tournament%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getTournamentCont() >= Config.MISSION_TOURNAMENT_COUNT)
			html.replace("%tournament%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%tournament%", "<font color=\"FF0000\">Not Completed</font>");
		
		//1X1
		html.replace("%1x1_cont%", "" + Config.MISSION_1X1_COUNT);	
		if (player.is1x1Completed())
			html.replace("%1x1%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.get1x1Cont() >= Config.MISSION_1X1_COUNT)
			html.replace("%1x1%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%1x1%", "<font color=\"FF0000\">Not Completed</font>");
		
		//3X3
		html.replace("%3x3_cont%", "" + Config.MISSION_3X3_COUNT);	
		if (player.is3x3Completed())
			html.replace("%3x3%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.get3x3Cont() >= Config.MISSION_3X3_COUNT)
			html.replace("%3x3%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%3x3%", "<font color=\"FF0000\">Not Completed</font>");
		
		//5X5
		html.replace("%5x5_cont%", "" + Config.MISSION_5X5_COUNT);	
		if (player.is5x5Completed())
			html.replace("%5x5%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.get5x5Cont() >= Config.MISSION_5X5_COUNT)
			html.replace("%5x5%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%5x5%", "<font color=\"FF0000\">Not Completed</font>");
		
		//9X9
		html.replace("%9x9_cont%", "" + Config.MISSION_9X9_COUNT);	
		if (player.is9x9Completed())
			html.replace("%9x9%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.get9x9Cont() >= Config.MISSION_9X9_COUNT)
			html.replace("%9x9%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%9x9%", "<font color=\"FF0000\">Not Completed</font>");
		
		//FARM
		html.replace("%farm_cont%", "" + Config.MISSION_FARM_COUNT);	
		if (player.isFarmCompleted())
			html.replace("%farm%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getFarmCont() >= Config.MISSION_FARM_COUNT)
			html.replace("%farm%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%farm%", "<font color=\"FF0000\">Not Completed</font>");
		
		//PARTY FARM
		html.replace("%champion_cont%", "" + Config.MISSION_CHAMPION_COUNT);	
		if (player.isChampionCompleted())
			html.replace("%champion%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getChampionCont() >= Config.MISSION_CHAMPION_COUNT)
			html.replace("%champion%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%champion%", "<font color=\"FF0000\">Not Completed</font>");
		
		//PVP
		html.replace("%pvp_cont%", "" + Config.MISSION_PVP_COUNT);	
		if (player.isPVPCompleted())
			html.replace("%pvp%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getPVPCont() >= Config.MISSION_PVP_COUNT)
			html.replace("%pvp%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%pvp%", "<font color=\"FF0000\">Not Completed</font>");

		//BOSS
		if (player.isRaidKillCompleted())
			html.replace("%boss%", "<font color=\"2EEAF9\">Received</font>");	
		else if (player.getRaidKill_1() >= 1 && player.getRaidKill_2() >= 1 && player.getRaidKill_3() >= 1 && player.getRaidKill_4() >= 1 && player.getRaidKill_5() >= 1 && player.getRaidKill_6() >= 1)	       			
			html.replace("%boss%", "<font color=\"00FF00\">Completed</font>");
		else
			html.replace("%boss%", "<font color=\"FF0000\">Not Completed</font>");	
		
		html.replace("%reset%", "" + MissionReset.getInstance().NextEvent.getTime().toString());
		html.replace("%time%", sdf.format(new Date(System.currentTimeMillis())));
		player.sendPacket(html);
	}
	
	private void sendTvTMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkTvTHWid(player.getHWid()))
		{
			getTvTMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/TvT.htm"); 
			
			html.replace("%tvt_cont%", "" + Config.MISSION_TVT_COUNT);	
			html.replace("%cont%", "" + player.getTvTCont());	
			html.replace("%name%", "");	

			if (player.isTvTCompleted())
			{
				html.replace("%tvt%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getTvTCont() >= Config.MISSION_TVT_COUNT)
			{
				html.replace("%tvt%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_tvt_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}

	private void sendCTFMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkCTFHWid(player.getHWid()))
		{
			getCTFMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/CTF.htm"); 
			
			html.replace("%ctf_cont%", "" + Config.MISSION_CTF_COUNT);	
			html.replace("%cont%", "" + player.getCTFCont());	
			html.replace("%name%", "");	

			if (player.isCTFCompleted())
			{
				html.replace("%ctf%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getCTFCont() >= Config.MISSION_CTF_COUNT)
			{
				html.replace("%ctf%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_ctf_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%ctf%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}

	private void sendDMMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkDMHWid(player.getHWid()))
		{
			getDMMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/DM.htm"); 
			
			html.replace("%dm_cont%", "" + Config.MISSION_DM_COUNT);	
			html.replace("%cont%", "" + player.getDMCont());	
			html.replace("%name%", "");	

			if (player.isDMCompleted())
			{
				html.replace("%dm%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getDMCont() >= Config.MISSION_DM_COUNT)
			{
				html.replace("%dm%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_dm_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%dm%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}
	
	private void sendKTBMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkKTBHWid(player.getHWid()))
		{
			getKTBMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/KTB.htm"); 
			
			html.replace("%ktb_cont%", "" + Config.MISSION_KTB_COUNT);	
			html.replace("%cont%", "" + player.getKTBCont());	
			html.replace("%name%", "");	

			if (player.isKTBCompleted())
			{
				html.replace("%ktb%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getKTBCont() >= Config.MISSION_KTB_COUNT)
			{
				html.replace("%ktb%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_ktb_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%ktb%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}
	
	private void sendTournamentMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkTournamentHWid(player.getHWid()))
		{
			getTournamentMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/Tournament.htm"); 
			
			html.replace("%tournament_cont%", "" + Config.MISSION_TOURNAMENT_COUNT);	
			html.replace("%cont%", "" + player.getTournamentCont());	
			html.replace("%name%", "");	

			if (player.isTournamentCompleted())
			{
				html.replace("%tournament%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getTournamentCont() >= Config.MISSION_TOURNAMENT_COUNT)
			{
				html.replace("%tournament%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_tournament_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%tournament%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}

	private void send1x1MissionHtmlMessage(L2PcInstance player)
	{
		if (player.check1x1HWid(player.getHWid()))
		{
			get1x1MissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/1x1.htm"); 
			
			html.replace("%1x1_cont%", "" + Config.MISSION_1X1_COUNT);	
			html.replace("%cont%", "" + player.get1x1Cont());	
			html.replace("%name%", "");	

			if (player.is1x1Completed())
			{
				html.replace("%1x1%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.get1x1Cont() >= Config.MISSION_1X1_COUNT)
			{
				html.replace("%1x1%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_1x1_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%1x1%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}
	
	private void send3x3MissionHtmlMessage(L2PcInstance player)
	{
		if (player.check3x3HWid(player.getHWid()))
		{
			get3x3MissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/3x3.htm"); 
			
			html.replace("%3x3_cont%", "" + Config.MISSION_3X3_COUNT);	
			html.replace("%cont%", "" + player.get3x3Cont());	
			html.replace("%name%", "");	

			if (player.is3x3Completed())
			{
				html.replace("%3x3%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.get3x3Cont() >= Config.MISSION_3X3_COUNT)
			{
				html.replace("%3x3%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_3x3_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%3x3%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}

	private void send5x5MissionHtmlMessage(L2PcInstance player)
	{
		if (player.check5x5HWid(player.getHWid()))
		{
			get5x5MissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/5x5.htm"); 
			
			html.replace("%5x5_cont%", "" + Config.MISSION_5X5_COUNT);	
			html.replace("%cont%", "" + player.get5x5Cont());	
			html.replace("%name%", "");	

			if (player.is5x5Completed())
			{
				html.replace("%5x5%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.get5x5Cont() >= Config.MISSION_5X5_COUNT)
			{
				html.replace("%5x5%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_5x5_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%5x5%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}

	private void send9x9MissionHtmlMessage(L2PcInstance player)
	{
		if (player.check9x9HWid(player.getHWid()))
		{
			get9x9MissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/9x9.htm"); 
			
			html.replace("%9x9_cont%", "" + Config.MISSION_9X9_COUNT);	
			html.replace("%cont%", "" + player.get9x9Cont());	
			html.replace("%name%", "");	

			if (player.is9x9Completed())
			{
				html.replace("%9x9%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.get9x9Cont() >= Config.MISSION_9X9_COUNT)
			{
				html.replace("%9x9%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_9x9_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%9x9%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}

	private void sendFarmMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkFarmHWid(player.getHWid()))
		{
			getFarmMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/FarmMobs.htm"); 
			
			html.replace("%farm_cont%", "" + Config.MISSION_FARM_COUNT);	
			html.replace("%cont%", "" + player.getFarmCont());	
			html.replace("%name%", "");	

			if (player.isFarmCompleted())
			{
				html.replace("%farm%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getFarmCont() >= Config.MISSION_FARM_COUNT)
			{
				html.replace("%farm%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_farm_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%farm%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}
	
	private void sendChampionMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkChampionHWid(player.getHWid()))
		{
			getChampionMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/ChampionMobs.htm"); 
			
			html.replace("%champion_cont%", "" + Config.MISSION_CHAMPION_COUNT);	
			html.replace("%cont%", "" + player.getChampionCont());	
			html.replace("%name%", "");	

			if (player.isChampionCompleted())
			{
				html.replace("%champion%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getChampionCont() >= Config.MISSION_CHAMPION_COUNT)
			{
				html.replace("%champion%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_champion_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%champion%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}
	
	private void sendPvPMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkPVPHWid(player.getHWid()))
		{
			getPvPMissionInfo(player);  
		}
		else
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/PvP.htm"); 
			
			html.replace("%pvp_cont%", "" + Config.MISSION_PVP_COUNT);	
			html.replace("%cont%", "" + player.getPVPCont());	
			html.replace("%name%", "");	

			if (player.isPVPCompleted())
			{
				html.replace("%pvp%", "<font color=\"FF0000\">Received</font>");
				html.replace("%link%", "");	
			}
			else if (player.getPVPCont() >= Config.MISSION_PVP_COUNT)
			{
				html.replace("%pvp%", "<font color=\"2EEAF9\">Completed</font>");	
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_pvp_mission_reward\" value=\"Receive Reward!\">");	         
			}
			else
			{
				html.replace("%pvp%", "<font color=\"FF0000\">Not Completed</font>");
				html.replace("%link%", "");	
			}
			player.sendPacket(html);
		}
	}
	
	private void sendRaidKillMissionHtmlMessage(L2PcInstance player)
	{
		if (player.checkRaidKillHWid(player.getHWid()))
		{
			getRaidMissionInfo(player); 
		}
		else
		{
			final NpcTemplate template1 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_1);

			String bossName1 = template1.getName();
			if (bossName1.length() > 23)
				bossName1 = bossName1.substring(0, 23) + "...";

			final NpcTemplate template2 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_2);

			String bossName2 = template2.getName();
			if (bossName2.length() > 23)
				bossName2 = bossName2.substring(0, 23) + "...";

			final NpcTemplate template3 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_3);

			String bossName3 = template3.getName();
			if (bossName3.length() > 23)
				bossName3 = bossName3.substring(0, 23) + "...";

			final NpcTemplate template4 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_4);

			String bossName4 = template4.getName();
			if (bossName4.length() > 23)
				bossName4 = bossName4.substring(0, 23) + "...";

			final NpcTemplate template5 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_5);

			String bossName5 = template5.getName();
			if (bossName5.length() > 23)
				bossName5 = bossName5.substring(0, 23) + "...";

			final NpcTemplate template6 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_6);

			String bossName6 = template6.getName();
			if (bossName6.length() > 23)
				bossName6 = bossName6.substring(0, 23) + "...";

			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/missions/RaidKill.htm"); 
			
			html.replace("%name%", "");	
			html.replace("%raid_name_1%", bossName1);	
			html.replace("%raid_name_2%", bossName2);	
			html.replace("%raid_name_3%", bossName3);	
			html.replace("%raid_name_4%", bossName4);	
			html.replace("%raid_name_5%", bossName5);	
			html.replace("%raid_name_6%", bossName6);

			if (player.getRaidKill_1() >= 1)
				html.replace("%raid_1%", "<font color=\"5EA82E\">Killed</font>");	
			else
				html.replace("%raid_1%", "<font color=\"FF0000\">Not Killed</font>");	

			if (player.getRaidKill_2() >= 1)
				html.replace("%raid_2%", "<font color=\"5EA82E\">Killed</font>");	
			else
				html.replace("%raid_2%", "<font color=\"FF0000\">Not Killed</font>");	

			if (player.getRaidKill_3() >= 1)
				html.replace("%raid_3%", "<font color=\"5EA82E\">Killed</font>");	
			else
				html.replace("%raid_3%", "<font color=\"FF0000\">Not Killed</font>");	

			if (player.getRaidKill_4() >= 1)
				html.replace("%raid_4%", "<font color=\"5EA82E\">Killed</font>");	
			else
				html.replace("%raid_4%", "<font color=\"FF0000\">Not Killed</font>");	

			if (player.getRaidKill_5() >= 1)
				html.replace("%raid_5%", "<font color=\"5EA82E\">Killed</font>");	
			else
				html.replace("%raid_5%", "<font color=\"FF0000\">Not Killed</font>");	

			if (player.getRaidKill_6() >= 1)
				html.replace("%raid_6%", "<font color=\"5EA82E\">Killed</font>");	
			else
				html.replace("%raid_6%", "<font color=\"FF0000\">Not Killed</font>");	

			if (player.isRaidKillCompleted() || player.checkRaidKillHWid(player.getHWid()))
			{
				html.replace("%raidkill%", "<font color=\"FF0000\">Received</font>");	
				html.replace("%link%", "");		
			}
			else if (player.getRaidKill_1() >= 1 && player.getRaidKill_2() >= 1 && player.getRaidKill_3() >= 1 && player.getRaidKill_4() >= 1 && player.getRaidKill_5() >= 1 && player.getRaidKill_6() >= 1)
			{	       			
				html.replace("%raidkill%", "<font color=\"2EEAF9\">Completed</font>");
				html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_kill_mission_reward\" value=\"Receive Reward!\">");	
			}
			else
			{
				html.replace("%raidkill%", "<font color=\"FF0000\">Not Completed</font>");	
				html.replace("%link%", "");	 
			}
			player.sendPacket(html);
		}
	}
	
	private void sendTvTMissionReward(L2PcInstance player)
	{
        if (player.checkTvTHWid(player.getHWid()) || player.isTvTCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getTvTCont() >= Config.MISSION_TVT_COUNT && !player.isTvTCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tvt_completed=?,tvt_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission TvT Reward: " + e.getMessage(), e);
    		}	
			
			player.setTvTCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_TVT_REWARD_ID, Config.MISSION_TVT_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    		
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");        
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}
	
	private void sendCTFMissionReward(L2PcInstance player)
	{
        if (player.checkCTFHWid(player.getHWid()) || player.isCTFCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getCTFCont() >= Config.MISSION_CTF_COUNT && !player.isCTFCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET ctf_completed=?,ctf_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission CTF Reward: " + e.getMessage(), e);
    		}	
			
			player.setCTFCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_CTF_REWARD_ID, Config.MISSION_CTF_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    		
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}
	
	private void sendDMMissionReward(L2PcInstance player)
	{
        if (player.checkDMHWid(player.getHWid()) || player.isDMCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getDMCont() >= Config.MISSION_DM_COUNT && !player.isDMCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET dm_completed=?,dm_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission DM Reward: " + e.getMessage(), e);
    		}	
			
			player.setDMCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_DM_REWARD_ID, Config.MISSION_DM_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    		
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");
	}
	
	private void sendKTBMissionReward(L2PcInstance player)
	{
        if (player.checkKTBHWid(player.getHWid()) || player.isKTBCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getKTBCont() >= Config.MISSION_KTB_COUNT && !player.isKTBCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET ktb_completed=?,ktb_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission KTB Reward: " + e.getMessage(), e);
    		}	
			
			player.setKTBCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_KTB_REWARD_ID, Config.MISSION_KTB_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    		
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}
	
	private void sendTournamentMissionReward(L2PcInstance player)
	{
        if (player.checkTournamentHWid(player.getHWid()) || player.isTournamentCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getTournamentCont() >= Config.MISSION_TOURNAMENT_COUNT && !player.isTournamentCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tournament_completed=?,tourmanet_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission Tournament Reward: " + e.getMessage(), e);
    		}	
			
			player.setTournamentCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_TOURNAMENT_REWARD_ID, Config.MISSION_TOURNAMENT_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}

	private void send1x1MissionReward(L2PcInstance player)
	{
        if (player.check1x1HWid(player.getHWid()) || player.is1x1Completed())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.get1x1Cont() >= Config.MISSION_1X1_COUNT && !player.is1x1Completed())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET 1x1_completed=?,1x1_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission 1x1 Reward: " + e.getMessage(), e);
    		}	
			
			player.set1x1Completed(true);
        	
			player.addItem("Reward", Config.MISSION_1X1_REWARD_ID, Config.MISSION_1X1_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}
	
	private void send3x3MissionReward(L2PcInstance player)
	{
        if (player.check3x3HWid(player.getHWid()) || player.is3x3Completed())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.get3x3Cont() >= Config.MISSION_3X3_COUNT && !player.is3x3Completed())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET 3x3_completed=?,3x3_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission 3x3 Reward: " + e.getMessage(), e);
    		}	
			
			player.set3x3Completed(true);
        	
			player.addItem("Reward", Config.MISSION_3X3_REWARD_ID, Config.MISSION_3X3_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}

	private void send5x5MissionReward(L2PcInstance player)
	{
        if (player.check5x5HWid(player.getHWid()) || player.is5x5Completed())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.get5x5Cont() >= Config.MISSION_5X5_COUNT && !player.is5x5Completed())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET 5x5_completed=?,5x5_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission 5x5 Reward: " + e.getMessage(), e);
    		}	
			
			player.set5x5Completed(true);
        	
			player.addItem("Reward", Config.MISSION_5X5_REWARD_ID, Config.MISSION_5X5_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}

	private void send9x9MissionReward(L2PcInstance player)
	{
        if (player.check9x9HWid(player.getHWid()) || player.is9x9Completed())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.get9x9Cont() >= Config.MISSION_9X9_COUNT && !player.is9x9Completed())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET 9x9_completed=?,9x9_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission 9x9 Reward: " + e.getMessage(), e);
    		}	
			
			player.set9x9Completed(true);
        	
			player.addItem("Reward", Config.MISSION_9X9_REWARD_ID, Config.MISSION_9X9_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}
	
	private void sendFarmMissionReward(L2PcInstance player)
	{
        if (player.checkFarmHWid(player.getHWid()) || player.isFarmCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getFarmCont() >= Config.MISSION_FARM_COUNT && !player.isFarmCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET farm_completed=?,farm_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission Farm Reward: " + e.getMessage(), e);
    		}	
			
			player.setFarmCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_FARM_REWARD_ID, Config.MISSION_FARM_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}

	private void sendChampionMissionReward(L2PcInstance player)
	{
        if (player.checkChampionHWid(player.getHWid()) || player.isChampionCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getChampionCont() >= Config.MISSION_CHAMPION_COUNT && !player.isChampionCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET champion_completed=?,champion_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission Party Farm Reward: " + e.getMessage(), e);
    		}	
			
			player.setChampionCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_CHAMPION_REWARD_ID, Config.MISSION_CHAMPION_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    			
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");	
	}
	
	private void sendPvPMissionReward(L2PcInstance player)
	{
        if (player.checkPVPHWid(player.getHWid()) || player.isPVPCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
			return;
      	}			      
      	else if (player.getPVPCont() >= Config.MISSION_PVP_COUNT && !player.isPVPCompleted())
        {
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET pvp_completed=?,pvp_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;	
				
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission PvP Reward: " + e.getMessage(), e);
    		}	
			
			player.setPVPCompleted(true);
        	
			player.addItem("Reward", Config.MISSION_PVP_REWARD_ID, Config.MISSION_PVP_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    		
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
        else               
        	player.sendMessage("You have not completed this task.");
	}
	
	private void sendRaidKillMissionReward(L2PcInstance player)
	{
        if (player.checkRaidKillHWid(player.getHWid()) || player.isRaidKillCompleted())
      	{
        	player.sendMessage("You've already been rewarding.");
            return;
      	}
		else if ((player.getRaidKill_1() >= 1 && player.getRaidKill_2() >= 1 && player.getRaidKill_3() >= 1 && player.getRaidKill_4() >= 1 && player.getRaidKill_5() >= 1 && player.getRaidKill_6() >= 1) && !player.isRaidKillCompleted())
        {						
        	if (!player.checkMissions(player.getObjectId()))
        		player.updateMissions();

    		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
    		{
				PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET raid_kill_completed=?,raid_kill_hwid=? WHERE obj_Id=?");
				stmt.setInt(1, 1);
				stmt.setString(2, player.getHWid());
				stmt.setInt(3, player.getObjectId());
				stmt.execute();
				stmt.close();
				stmt = null;						
			}
    		catch (Exception e)
    		{
    			_log.log(Level.WARNING, "Mission Boss Reward: " + e.getMessage(), e);
    		}	
			
    		player.setRaidKillCompleted(true);
    		player.addItem("Reward", Config.MISSION_RAIDKILL_REWARD_ID, Config.MISSION_RAIDKILL_REWARD_AMOUNT, player, true);
			player.sendCustomMessage("Congratulations! You have completed a daily task.");
        	
			player.sendPacket(new PlaySound("ItemSound.quest_finish"));
			player.broadcastPacket(new SocialAction(player, 3));	    		
			sendMissionHtmlMessage(player, "data/html/mods/menu/Mission.htm");
        }
		else              
			player.sendMessage("You have not completed this task.");	
	}
	
	private void getTvTMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE tvt_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/TvT.htm"); 
				
				html.replace("%tvt_cont%", "" + Config.MISSION_TVT_COUNT);	
				html.replace("%cont%", "" + player.getTvTCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isTvTCompleted() || player.checkTvTHWid(player.getHWid()))
                {
                	html.replace("%tvt%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getTvTCont() >= Config.MISSION_TVT_COUNT)
                {
                	html.replace("%tvt%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_tvt_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void getCTFMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE ctf_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/CTF.htm"); 
				
				html.replace("%ctf_cont%", "" + Config.MISSION_CTF_COUNT);	
				html.replace("%cont%", "" + player.getCTFCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isCTFCompleted() || player.checkCTFHWid(player.getHWid()))
                {
                	html.replace("%ctf%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getCTFCont() >= Config.MISSION_CTF_COUNT)
                {
                	html.replace("%ctf%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_ctf_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%ctf%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void getDMMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE dm_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/DM.htm"); 
				
				html.replace("%dm_cont%", "" + Config.MISSION_DM_COUNT);	
				html.replace("%cont%", "" + player.getDMCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isDMCompleted() || player.checkDMHWid(player.getHWid()))
                {
                	html.replace("%dm%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getDMCont() >= Config.MISSION_DM_COUNT)
                {
                	html.replace("%dm%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_dm_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%dm%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void getKTBMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE ktb_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/KTB.htm"); 
				
				html.replace("%ktb_cont%", "" + Config.MISSION_KTB_COUNT);	
				html.replace("%cont%", "" + player.getKTBCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isKTBCompleted() || player.checkKTBHWid(player.getHWid()))
                {
                	html.replace("%ktb%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getKTBCont() >= Config.MISSION_KTB_COUNT)
                {
                	html.replace("%ktb%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_ktb_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%ktb%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void getTournamentMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE tournament_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/Tournament.htm"); 
				
				html.replace("%tournament_cont%", "" + Config.MISSION_TOURNAMENT_COUNT);	
				html.replace("%cont%", "" + player.getTournamentCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isTournamentCompleted() || player.checkTournamentHWid(player.getHWid()))
                {
                	html.replace("%tournament%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getTournamentCont() >= Config.MISSION_TOURNAMENT_COUNT)
                {
                	html.replace("%tournament%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_tournament_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%tournament%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void get1x1MissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE 1x1_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/1x1.htm"); 
				
				html.replace("%1x1_cont%", "" + Config.MISSION_1X1_COUNT);	
				html.replace("%cont%", "" + player.get1x1Cont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.is1x1Completed() || player.check1x1HWid(player.getHWid()))
                {
                	html.replace("%1x1%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.get1x1Cont() >= Config.MISSION_1X1_COUNT)
                {
                	html.replace("%1x1%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_1x1_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%1x1%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}

	private void get3x3MissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE 3x3_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/3x3.htm"); 
				
				html.replace("%3x3_cont%", "" + Config.MISSION_3X3_COUNT);	
				html.replace("%cont%", "" + player.get3x3Cont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.is3x3Completed() || player.check3x3HWid(player.getHWid()))
                {
                	html.replace("%3x3%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.get3x3Cont() >= Config.MISSION_3X3_COUNT)
                {
                	html.replace("%3x3%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_3x3_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%3x3%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}

	private void get5x5MissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE 5x5_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/5x5.htm"); 
				
				html.replace("%5x5_cont%", "" + Config.MISSION_5X5_COUNT);	
				html.replace("%cont%", "" + player.get5x5Cont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.is5x5Completed() || player.check5x5HWid(player.getHWid()))
                {
                	html.replace("%5x5%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.get5x5Cont() >= Config.MISSION_5X5_COUNT)
                {
                	html.replace("%5x5%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_5x5_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%5x5%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}

	private void get9x9MissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE 9x9_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/9x9.htm"); 
				
				html.replace("%9x9_cont%", "" + Config.MISSION_5X5_COUNT);	
				html.replace("%cont%", "" + player.get9x9Cont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.is9x9Completed() || player.check9x9HWid(player.getHWid()))
                {
                	html.replace("%9x9%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.get9x9Cont() >= Config.MISSION_5X5_COUNT)
                {
                	html.replace("%9x9%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_9x9_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%9x9%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void getFarmMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE farm_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/FarmMobs.htm"); 
				
				html.replace("%farm_cont%", "" + Config.MISSION_FARM_COUNT);	
				html.replace("%cont%", "" + player.getFarmCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isFarmCompleted() || player.checkFarmHWid(player.getHWid()))
                {
                	html.replace("%farm%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getFarmCont() >= Config.MISSION_FARM_COUNT)
                {
                	html.replace("%farm%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_farm_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%farm%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}

	private void getChampionMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE champion_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/ChampionMobs.htm"); 
				
				html.replace("%champion_cont%", "" + Config.MISSION_CHAMPION_COUNT);	
				html.replace("%cont%", "" + player.getChampionCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isChampionCompleted() || player.checkChampionHWid(player.getHWid()))
                {
                	html.replace("%champion%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getChampionCont() >= Config.MISSION_CHAMPION_COUNT)
                {
                	html.replace("%champion%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_champion_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%champion%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}

	private void getPvPMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE pvp_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				name = rset.getString("char_name");
  	               
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/PvP.htm"); 
				
				html.replace("%pvp_cont%", "" + Config.MISSION_PVP_COUNT);	
				html.replace("%cont%", "" + player.getPVPCont());	
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
               
                if (player.isPVPCompleted() || player.checkPVPHWid(player.getHWid()))
                {
                	html.replace("%pvp%", "<font color=\"2EEAF9\">Received</font>");
                	html.replace("%link%", "");	
                }
                else if (player.getPVPCont() >= Config.MISSION_PVP_COUNT)
                {
                	html.replace("%pvp%", "<font color=\"5EA82E\">Completed</font>");	
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_pvp_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%pvp%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                player.sendPacket(html);
			}
	
			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	private void getRaidMissionInfo(L2PcInstance player)
	{		
		String name = "*Null*";

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters_mission WHERE raid_kill_hwid=?");
			statement.setString(1, player.getHWid());
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				final NpcTemplate template1 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_1);
				if (template1 == null)
					continue;

				String bossName1 = template1.getName();
				if (bossName1.length() > 23)
					bossName1 = bossName1.substring(0, 23) + "...";

				final NpcTemplate template2 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_2);
				if (template2 == null)
					continue;

				String bossName2 = template2.getName();
				if (bossName2.length() > 23)
					bossName2 = bossName2.substring(0, 23) + "...";

				final NpcTemplate template3 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_3);
				if (template3 == null)
					continue;

				String bossName3 = template3.getName();
				if (bossName3.length() > 23)
					bossName3 = bossName3.substring(0, 23) + "...";

				final NpcTemplate template4 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_4);
				if (template4 == null)
					continue;

				String bossName4 = template4.getName();
				if (bossName4.length() > 23)
					bossName4 = bossName4.substring(0, 23) + "...";

				final NpcTemplate template5 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_5);
				if (template5 == null)
					continue;

				String bossName5 = template5.getName();
				if (bossName5.length() > 23)
					bossName5 = bossName5.substring(0, 23) + "...";

				final NpcTemplate template6 = NpcTable.getInstance().getTemplate(Config.RAIDKILL_ID_6);
				if (template6 == null)
					continue;

				String bossName6 = template6.getName();
				if (bossName6.length() > 23)
					bossName6 = bossName6.substring(0, 23) + "...";

				name = rset.getString("char_name");

				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/missions/RaidKill.htm"); 
				
				html.replace("%name%", "The Player: <font color=\"LEVEL\">" + name + "</font> completed the mission.");	
				html.replace("%raid_name_1%", bossName1);	
				html.replace("%raid_name_2%", bossName2);	
				html.replace("%raid_name_3%", bossName3);	
				html.replace("%raid_name_4%", bossName4);	
				html.replace("%raid_name_5%", bossName5);	
				html.replace("%raid_name_6%", bossName6);

				if (player.getRaidKill_1() >= 1)
					html.replace("%raid_1%", "<font color=\"5EA82E\">Killed</font>");	
				else
					html.replace("%raid_1%", "<font color=\"FF0000\">Not Killed</font>");	

				if (player.getRaidKill_2() >= 1)
					html.replace("%raid_2%", "<font color=\"5EA82E\">Killed</font>");	
				else
					html.replace("%raid_2%", "<font color=\"FF0000\">Not Killed</font>");	

				if (player.getRaidKill_3() >= 1)
					html.replace("%raid_3%", "<font color=\"5EA82E\">Killed</font>");	
				else
					html.replace("%raid_3%", "<font color=\"FF0000\">Not Killed</font>");	

				if (player.getRaidKill_4() >= 1)
					html.replace("%raid_4%", "<font color=\"5EA82E\">Killed</font>");	
				else
					html.replace("%raid_4%", "<font color=\"FF0000\">Not Killed</font>");	

				if (player.getRaidKill_5() >= 1)
					html.replace("%raid_5%", "<font color=\"5EA82E\">Killed</font>");	
				else
					html.replace("%raid_5%", "<font color=\"FF0000\">Not Killed</font>");	

				if (player.getRaidKill_6() >= 1)
					html.replace("%raid_6%", "<font color=\"5EA82E\">Killed</font>");	
				else
					html.replace("%raid_6%", "<font color=\"FF0000\">Not Killed</font>");	

				if (player.isRaidKillCompleted() || player.checkRaidKillHWid(player.getHWid()))
				{
					html.replace("%raidkill%", "<font color=\"FF0000\">Received</font>");	
					html.replace("%link%", "");		
				}
				else if (player.getRaidKill_1() >= 1 && player.getRaidKill_2() >= 1 && player.getRaidKill_3() >= 1 && player.getRaidKill_4() >= 1 && player.getRaidKill_5() >= 1 && player.getRaidKill_6() >= 1)
				{	       			
					html.replace("%raidkill%", "<font color=\"2EEAF9\">Completed</font>");
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h voiced_kill_mission_reward\" value=\"Receive Reward!\">");	
				}
				else
				{
					html.replace("%raidkill%", "<font color=\"FF0000\">Not Completed</font>");	
					html.replace("%link%", "");	 
				}
				player.sendPacket(html);
			}

			rset.close();
			statement.close();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "ChargebackHwid: " + e.getMessage(), e);
		}
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}