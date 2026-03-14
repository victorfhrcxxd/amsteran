package net.sf.l2j.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.logging.Level;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Couple;
import net.sf.l2j.gameserver.model.entity.events.MissionReset;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SellList;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.TutorialCloseHtml;
import net.sf.l2j.gameserver.network.serverpackets.TutorialShowHtml;
import net.sf.l2j.gameserver.util.Broadcast;

public class L2ServerManagerInstance extends L2NpcInstance
{
	public L2ServerManagerInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	public String getHtmlPath(int npcId, int val) 
	{
		String filename;
		if (val == 0)
			filename = "data/html/mods/serverManager/" + npcId + ".htm";
		else 
			filename = "data/html/mods/serverManager/" + npcId + "-" + val + ".htm"; 
		
		if (HtmCache.getInstance().isLoadable(filename))
			return filename; 
		
		return "data/html/npcdefault.htm";
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("Auction"))
			sendAuctionHtmlMessage(player, "data/html/mods/AuctionerManager.htm");
		else if (command.startsWith("Mission"))
			sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");
		else if (command.startsWith("Wedding"))
		{
			if (!Config.ALLOW_WEDDING)
				sendHtmlMessage(player, "data/html/mods/Wedding_disabled.htm");
			else
			{
				// Married people got access to another menu
				if (player.isMarried())
					sendHtmlMessage(player, "data/html/mods/Wedding_start2.htm");
				// "Under marriage acceptance" people go to this one
				else if (player.isUnderMarryRequest())
					sendHtmlMessage(player, "data/html/mods/Wedding_waitforpartner.htm");
				// And normal players go here :)
				else
					sendHtmlMessage(player, "data/html/mods/Wedding_start.htm");
			}
		}
		else if (command.startsWith("AskWedding"))
		{
			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			
			if (st.hasMoreTokens())
			{
				final L2PcInstance ptarget = L2World.getInstance().getPlayer(st.nextToken());
				if (ptarget == null)
				{
					sendHtmlMessage(player, "data/html/mods/Wedding_notfound.htm");
					return;
				}
				
				// check conditions
				if (!weddingConditions(player, ptarget))
					return;
				
				// block the wedding manager until an answer is given.
				player.setUnderMarryRequest(true);
				ptarget.setUnderMarryRequest(true);
				
				// memorize the requesterId for future use, and send a popup to the target
				ptarget.setRequesterId(player.getObjectId());
				ptarget.sendPacket(new ConfirmDlg(1983).addString(player.getName() + " asked you to marry. Do you want to start a new relationship?"));
			}
			else
				sendHtmlMessage(player, "data/html/mods/Wedding_notfound.htm");
		}
		else if (command.startsWith("Divorce"))
		{
			player.sendMessage("You are now divorced.");
			player.getInventory().destroyItemByItemId("Cupid Bow", 9140, 1, player, null);
			player.getWarehouse().destroyItemByItemId("Cupid Bow", 9140, 1, player, null);
			
			// Find the partner using the couple information
			final L2PcInstance partner = L2World.getInstance().getPlayer(Couple.getPartnerId(player.getObjectId()));
			if (partner != null)
			{
				partner.sendMessage("Your beloved has decided to divorce.");
				partner.getInventory().destroyItemByItemId("Cupid Bow", 9140, 1, partner, null);
				partner.getWarehouse().destroyItemByItemId("Cupid Bow", 9140, 1, partner, null);
			}
			
			CoupleManager.getInstance().deleteCouple(player.getCoupleId());
		}
		else if (command.startsWith("GoToLove"))
		{
			// Find the partner using the couple information
			final L2PcInstance partner = L2World.getInstance().getPlayer(Couple.getPartnerId(player.getObjectId()));
			if (partner == null)
			{
				player.sendMessage("Your partner is not online.");
				return;
			}
			
			// Simple checks to avoid exploits
			if (partner.isDead() || partner.isInJail() || partner.isInOlympiadMode() || partner.isInDuel() || partner.isFestivalParticipant() || (partner.isInParty() && partner.getParty().isInDimensionalRift()) || partner.inObserverMode() || partner.isInsideZone(ZoneId.BOSS_AREA) || partner.isInsideZone(ZoneId.SIEGE) || partner.isInsideZone(ZoneId.NO_SUMMON_FRIEND))
			{
				player.sendMessage("Due to the current partner's status, the teleportation failed.");
				return;
			}
			
			if (partner.getClan() != null && CastleManager.getInstance().getCastleByOwner(partner.getClan()) != null && CastleManager.getInstance().getCastleByOwner(partner.getClan()).getSiege().isInProgress() || partner.isInsideZone(ZoneId.SIEGE) || partner.isInsideZone(ZoneId.CAST_ON_ARTIFACT))
			{
				player.sendMessage("As your partner is in siege, you can't go to him/her.");
				return;
			}
			
			// If all checks are successfully passed, teleport the player to the partner
			player.teleToLocation(partner.getX(), partner.getY(), partner.getZ(), 20);
		}
		else if (command.startsWith("TvT_Mission"))
		{
			if (player.checkTvTHWid(player.getHWid()))
			{
				getTvTMissionInfo(player);  
			}
			else
			{
				String filename = "data/html/mods/serverManager/missions/TvT.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_tvt_mission_reward\" value=\"Receive Reward!\">");	         
				}
				else
				{
					html.replace("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
					html.replace("%link%", "");	
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("DM_Mission"))
		{
			if (player.checkDMHWid(player.getHWid()))
			{
				getDMMissionInfo(player);  
			}
			else
			{
				String filename = "data/html/mods/serverManager/missions/DM.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_dm_mission_reward\" value=\"Receive Reward!\">");	         
				}
				else
				{
					html.replace("%dm%", "<font color=\"FF0000\">Not Completed</font>");
					html.replace("%link%", "");	
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("KTB_Mission"))
		{
			if (player.checkKTBHWid(player.getHWid()))
			{
				getKTBMissionInfo(player);  
			}
			else
			{
				String filename = "data/html/mods/serverManager/missions/KTB.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_ktb_mission_reward\" value=\"Receive Reward!\">");	         
				}
				else
				{
					html.replace("%ktb%", "<font color=\"FF0000\">Not Completed</font>");
					html.replace("%link%", "");	
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("Tournament_Mission"))
		{
			if (player.checkTournamentHWid(player.getHWid()))
			{
				getTournamentMissionInfo(player);  
			}
			else
			{
				String filename = "data/html/mods/serverManager/missions/Tournament.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_tournament_mission_reward\" value=\"Receive Reward!\">");	         
				}
				else
				{
					html.replace("%tournament%", "<font color=\"FF0000\">Not Completed</font>");
					html.replace("%link%", "");	
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("PvP_Mission"))
		{
			if (player.checkPVPHWid(player.getHWid()))
			{
				getPvPMissionInfo(player);  
			}
			else
			{
				String filename = "data/html/mods/serverManager/missions/PvP.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_pvp_mission_reward\" value=\"Receive Reward!\">");	         
				}
				else
				{
					html.replace("%pvp%", "<font color=\"FF0000\">Not Completed</font>");
					html.replace("%link%", "");	
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("RaidBoss_Mission"))
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

				String filename = "data/html/mods/serverManager/missions/RaidKill.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_boss_mission_reward\" value=\"Receive Reward!\">");	
				}
				else
				{
					html.replace("%raidkill%", "<font color=\"FF0000\">Not Completed</font>");	
					html.replace("%link%", "");	 
				}
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
			}
		}
		else if (command.startsWith("tvt_mission_reward"))
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
				sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");
	        }
	        else               
	        	player.sendMessage("You have not completed this task.");			        	
		}
		else if (command.startsWith("dm_mission_reward"))
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
				sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");
	        }
	        else               
	        	player.sendMessage("You have not completed this task.");			        	
		}
		else if (command.startsWith("ktb_mission_reward"))
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
				sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");
	        }
	        else               
	        	player.sendMessage("You have not completed this task.");			        	
		}
		else if (command.startsWith("tournament_mission_reward"))
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
				sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");
	        }
	        else               
	        	player.sendMessage("You have not completed this task.");			        	
		}
		else if (command.startsWith("pvp_mission_reward"))
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
				sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");
	        }
	        else               
	        	player.sendMessage("You have not completed this task.");			        	
		}
		else if (command.startsWith("boss_mission_reward"))
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
				sendMissionHtmlMessage(player, "data/html/mods/MissionManager.htm");       
	        }
    		else              
    			player.sendMessage("You have not completed this task.");	
		}
		else if (command.equalsIgnoreCase("Sell"))
			player.sendPacket(new SellList(player));
		else if (command.startsWith("Cobalt"))
			sendCobaltHtmlMessage(player);
		else if (command.startsWith("Carmine"))
			sendCarmineHtmlMessage(player);
		else if (command.equalsIgnoreCase("Soon"))
			player.sendMessage("Available soon.");
		else
			super.onBypassFeedback(player, command);
	}
	
	/**
	 * Are both partners wearing formal wear ? If Formal Wear check is disabled, returns True in any case.<BR>
	 * @param p1 L2PcInstance
	 * @param p2 L2PcInstance
	 * @return boolean
	 */
	private static boolean wearsFormalWear(L2PcInstance p1, L2PcInstance p2)
	{
		ItemInstance fw1 = p1.getChestArmorInstance();
		if (fw1 == null || fw1.getItemId() != 6408)
			return false;
		
		ItemInstance fw2 = p2.getChestArmorInstance();
		if (fw2 == null || fw2.getItemId() != 6408)
			return false;
		
		return true;
	}
	
	private boolean weddingConditions(L2PcInstance player, L2PcInstance ptarget)
	{
		// Check if player target himself
		if (ptarget.getObjectId() == player.getObjectId())
		{
			sendHtmlMessage(player, "data/html/mods/Wedding_error_wrongtarget.htm");
			return false;
		}
		
		// Sex check
		if (ptarget.getAppearance().getSex() == player.getAppearance().getSex() && !Config.WEDDING_SAMESEX)
		{
			sendHtmlMessage(player, "data/html/mods/Wedding_error_sex.htm");
			return false;
		}
		
		// Check if player has the target on friendlist
		if (!player.getFriendList().contains(ptarget.getObjectId()))
		{
			sendHtmlMessage(player, "data/html/mods/Wedding_error_friendlist.htm");
			return false;
		}
		
		// Target mustn't be already married
		if (ptarget.isMarried())
		{
			sendHtmlMessage(player, "data/html/mods/Wedding_error_alreadymarried.htm");
			return false;
		}
		
		// Check for Formal Wear
		if (Config.WEDDING_FORMALWEAR)
			if (!wearsFormalWear(player, ptarget))
			{
				sendHtmlMessage(player, "data/html/mods/Wedding_error_noformal.htm");
				return false;
			}
		
		// Check and reduce wedding price
		if (player.getAdena() < Config.WEDDING_PRICE || ptarget.getAdena() < Config.WEDDING_PRICE)
		{
			sendHtmlMessage(player, "data/html/mods/Wedding_error_adena.htm");
			return false;
		}
		
		return true;
	}
	
	public static void justMarried(L2PcInstance player, L2PcInstance ptarget)
	{
		// Unlock the wedding manager for both users, and set them as married
		player.setUnderMarryRequest(false);
		ptarget.setUnderMarryRequest(false);
		
		player.setMarried(true);
		ptarget.setMarried(true);
		
		// reduce adenas amount according to configs
		player.reduceAdena("Wedding", Config.WEDDING_PRICE, player.getCurrentFolkNPC(), true);
		ptarget.reduceAdena("Wedding", Config.WEDDING_PRICE, player.getCurrentFolkNPC(), true);
		
		// Flag players as married
		Couple couple = CoupleManager.getInstance().getCouple(player.getCoupleId());
		couple.marry();
		
		// Messages to the couple
		player.sendMessage("Congratulations, you are now married with " + ptarget.getName() + " !");
		ptarget.sendMessage("Congratulations, you are now married with " + player.getName() + " !");
		
		// Give the bow
		player.addItem("Cupid Bow", 9140, 1, player, true);
		player.sendSkillList();
		ptarget.addItem("Cupid Bow", 9140, 1, ptarget, true);
		ptarget.sendSkillList();
		
		// Wedding march
		player.broadcastPacket(new MagicSkillUse(player, player, 2230, 1, 1, 0));
		ptarget.broadcastPacket(new MagicSkillUse(ptarget, ptarget, 2230, 1, 1, 0));
		
		// Fireworks
		L2Skill skill = FrequentSkill.LARGE_FIREWORK.getSkill();
		player.doCast(skill);
		ptarget.doCast(skill);
		
		Broadcast.gameAnnounceToOnlinePlayers("Congratulations to " + player.getName() + " and " + ptarget.getName() + "! They have been married.");
	}
	
	private void sendAuctionHtmlMessage(L2PcInstance player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(file);
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	private void sendCobaltHtmlMessage(L2PcInstance player)
	{
		player.sendPacket(new TutorialShowHtml(HtmCache.getInstance().getHtmForce("data/html/mods/serverManager/Collectors/CobaltPack.htm")));
	}
	
	private void sendCarmineHtmlMessage(L2PcInstance player)
	{
		player.sendPacket(new TutorialShowHtml(HtmCache.getInstance().getHtmForce("data/html/mods/serverManager/Collectors/CarminePack.htm")));
	}
	
	private static final int[] TITANIUM_ARMOR =
	{
		9300,//Heavy
		9370,
		9302,
		9303,
		9304,
		9305,//Light
		9371,
		9307,
		9308,
		9309,
		9310,//Robe
		9372,
		9312,
		9313,
		9314
	};
	
	private static final int[] JEWELS_BOSS =
	{
		6660,
		6658,
		6659,
		6656,
		6657,
		8191
	};
	
	private static final int[] COBALT_SKINS =
	{
		30030,
		30031,
		30032,
		30036
	};
	
	private static final int[] TATTOS_SHIRTS =
	{
		9640,
		9641,
		9606,
		9607,
		9608,
		9609
	};
	
	private static final int[] CARMINE_SKINS =
	{
		30033,
		30034,
		30035,
		30037
	};
	
	private static final int[] COBALT_WEAPONS =
	{
		30511,
		30512,
		30513,
		30514,
		30515,
		30516,
		30517,
		30518,
		30519,
		30520,
		30521,
		30533
	};
	
	private static final int[] CARMINE_WEAPONS =
	{
		30522,
		30523,
		30524,
		30525,
		30526,
		30527,
		30528,
		30529,
		30530,
		30531,
		30532,
		30534
	};
	
	public static void CollectorsBypass(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("buy_cobalt"))
		{
			if (!activeChar.getInventory().validateCapacity(50))
			{
				activeChar.sendCustomMessage("Your inventory is full.");
				activeChar.sendCustomMessage("You must have at least 50 free slots.");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				return;
			}
			
			if (!activeChar.isInventoryUnder80(true) || activeChar.getWeightPenalty() > 0)
			{
				activeChar.sendCustomMessage("Your inventory is too heavy.");
				activeChar.sendCustomMessage("You must decrease your weight below 80%.");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				return;
			}

			if (activeChar.getInventory().getInventoryItemCount(9852, 0) >= 1)
			{
				for (int i : TITANIUM_ARMOR)
				{
					activeChar.getInventory().addEnchantedItem("Titanium Pack", i, 1, 25, activeChar, null);
				}

				for (int i : JEWELS_BOSS)
				{
					activeChar.getInventory().addEnchantedItem("Jewels Boss", i, 1, 25, activeChar, null);
				}

				for (int i : COBALT_SKINS)
				{
					activeChar.addItem("Cobalt Skin", i, 1, null, true);
				}

				for (int i : COBALT_WEAPONS)
				{
					activeChar.getInventory().addEnchantedItem("Weapons Pack", i, 1, 25, activeChar, null);
				}

				for (int i : TATTOS_SHIRTS)
				{
					activeChar.getInventory().addItem("Tattoos Shirts", i, 1, activeChar, null);
				}

				activeChar.getInventory().destroyItemByItemId("Cobalt Box", 9852, 1, activeChar, null);
				
				activeChar.addItem("Hero Coin", 10002, 1, null, true);
				activeChar.addItem("Vip Coin", 10005, 1, null, true);
				activeChar.addItem("Wyvern Necklace", 8663, 1, null, true);
				activeChar.addItem("Giant Codex", 6622, 200, null, true);
				activeChar.addItem("Life Stone", 8762, 250, null, true);
				activeChar.addItem("CP Potion", 8639, 1000, null, true);
				activeChar.addItem("HP Potion", 8627, 1000, null, true);
				activeChar.sendPacket(new ItemList(activeChar, true));

				//Broadcast.ServerAnnounce("Player " + activeChar.getName() + " became a Cobalt Collector Knight ::");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			}
			else
			{
				activeChar.sendCustomMessage("You don't have a cobalt chest.");
				activeChar.sendCustomMessage("Visit www.l2wz.org for more information.");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				return;
			}
		}
		
		if (command.startsWith("buy_carmine"))
		{
			if (!activeChar.getInventory().validateCapacity(50))
			{
				activeChar.sendCustomMessage("Your inventory is full.");
				activeChar.sendCustomMessage("You must have at least 50 free slots.");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				return;
			}
			
			if (!activeChar.isInventoryUnder80(true) || activeChar.getWeightPenalty() > 0)
			{
				activeChar.sendCustomMessage("Your inventory is too heavy.");
				activeChar.sendCustomMessage("You must decrease your weight below 80%.");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				return;
			}
			
			if (activeChar.getInventory().getInventoryItemCount(9851, 0) >= 1)
			{
				for (int i : TITANIUM_ARMOR)
				{
					activeChar.getInventory().addEnchantedItem("Titanium Pack", i, 1, 25, activeChar, null);
				}

				for (int i : JEWELS_BOSS)
				{
					activeChar.getInventory().addEnchantedItem("Jewels Boss", i, 1, 25, activeChar, null);
				}

				for (int i : CARMINE_SKINS)
				{
					activeChar.addItem("Carmine Skin", i, 1, null, true);
				}

				for (int i : CARMINE_WEAPONS)
				{
					activeChar.getInventory().addEnchantedItem("Weapons Pack", i, 1, 25, activeChar, null);
				}

				for (int i : TATTOS_SHIRTS)
				{
					activeChar.getInventory().addItem("Tattoos Shirts", i, 1, activeChar, null);
				}

				activeChar.getInventory().destroyItemByItemId("Carmine Box", 9851, 1, activeChar, null);
				
				activeChar.addItem("Hero Coin", 10002, 1, null, true);
				activeChar.addItem("Vip Coin", 10005, 1, null, true);
				activeChar.addItem("Wyvern Necklace", 8663, 1, null, true);
				activeChar.addItem("Giant Codex", 6622, 200, null, true);
				activeChar.addItem("Life Stone", 8762, 250, null, true);
				activeChar.addItem("CP Potion", 8639, 1000, null, true);
				activeChar.addItem("HP Potion", 8627, 1000, null, true);
				activeChar.sendPacket(new ItemList(activeChar, true));

				//Broadcast.ServerAnnounce("Player " + activeChar.getName() + " became a Carmine Collector Knight ::");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
			}
			else
			{
				activeChar.sendCustomMessage("You don't have a cobalt chest.");
				activeChar.sendCustomMessage("Visit www.l2wz.org for more information.");
				activeChar.sendPacket(TutorialCloseHtml.STATIC_PACKET);
				return;
			}
		}
	}
	
	public static final void onTutorialLink(L2PcInstance player, String request)
	{
		CollectorsBypass(request, player);
	}
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	private void sendMissionHtmlMessage(L2PcInstance player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(file);
	
		//TVT
		if (player.isTvTCompleted())
			html.replace("%tvt%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getTvTCont() >= Config.MISSION_TVT_COUNT)
			html.replace("%tvt%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
		
		//DM
		if (player.isDMCompleted())
			html.replace("%dm%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getDMCont() >= Config.MISSION_DM_COUNT)
			html.replace("%dm%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%dm%", "<font color=\"FF0000\">Not Completed</font>");
		
		//KTB
		if (player.isKTBCompleted())
			html.replace("%ktb%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getKTBCont() >= Config.MISSION_KTB_COUNT)
			html.replace("%ktb%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%ktb%", "<font color=\"FF0000\">Not Completed</font>");
		
		//TOURNAMENT
		if (player.isTournamentCompleted())
			html.replace("%tournament%", "<font color=\"2EEAF9\">Received</font>");
		else if (player.getTournamentCont() >= Config.MISSION_TOURNAMENT_COUNT)
			html.replace("%tournament%", "<font color=\"00FF00\">Completed</font>");	
		else
			html.replace("%tournament%", "<font color=\"FF0000\">Not Completed</font>");
		
		//PVP
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
		
		html.replace("%objectId%", getObjectId());
		player.sendPacket(html);
	}
	
	private void sendHtmlMessage(L2PcInstance player, String file)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(file);
		html.replace("%objectId%", getObjectId());
		html.replace("%adenasCost%", Config.WEDDING_PRICE);
		html.replace("%needOrNot%", Config.WEDDING_FORMALWEAR ? "will" : "won't");
		player.sendPacket(html);
	}
	
	// --------------------
	// Missions Mod
	// --------------------
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
  	               
				String filename = "data/html/mods/serverManager/missions/TvT.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_tvt_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%tvt%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                html.replace("%objectId%", getObjectId());
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
  	               
				String filename = "data/html/mods/serverManager/missions/DM.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_dm_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%dm%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                html.replace("%objectId%", getObjectId());
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
  	               
				String filename = "data/html/mods/serverManager/missions/KTB.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_ktb_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%ktb%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                html.replace("%objectId%", getObjectId());
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
  	               
				String filename = "data/html/mods/serverManager/missions/Tournament.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_tournament_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%tournament%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                html.replace("%objectId%", getObjectId());
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
  	               
				String filename = "data/html/mods/serverManager/missions/PvP.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
                	html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_pvp_mission_reward\" value=\"Receive Reward!\">");	         
                }
                else
                {
                	html.replace("%pvp%", "<font color=\"FF0000\">Not Completed</font>");
                	html.replace("%link%", "");	
                }
                html.replace("%objectId%", getObjectId());
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

				String filename = "data/html/mods/serverManager/missions/RaidKill.htm";
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile(filename);
				
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
					html.replace("%link%", "<button width=\"240\" height=\"27\" back=\"buttons_bs.bs_64x27_1\" fore=\"buttons_bs.bs_64x27_2\" action=\"bypass -h npc_%objectId%_boss_mission_reward\" value=\"Receive Reward!\">");	
				}
				else
				{
					html.replace("%raidkill%", "<font color=\"FF0000\">Not Completed</font>");	
					html.replace("%link%", "");	 
				}
				html.replace("%objectId%", getObjectId());
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
}