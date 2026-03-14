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
package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.custom.TimeInstanceManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.CheckNextEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedMenu implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = 
	{ 
		"menu",
		//"info",
		"setPartyRefuse", 
		"setTradeRefuse", 
		"setMessageRefuse",
		"setAutoGoldBar",
		"setBuffPrevent",
		"hideHeroAura",
		"hideEnchantGlow",
		"hideSkillsAnimation",
		"hideSkinsTextures",
		"hideMsg",
		"eventTime",
		"timeleft",
		"disableHelm",
		"removeskin"
	};

	public static SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z");
	 
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("menu"))
			showMenuHtml(activeChar);      
		
		if (command.equals("eventTime"))
			showEventTimeHtml(activeChar);        
		
		if (command.equals("timeleft"))
		{
			//if (Config.TIME_INSTANCE_SCREEN_MESSAGE)
			//	activeChar.sendPacket(new ExShowScreenMessage("You have " + TimeInstanceManager.getPlayerTime(activeChar) + " minutes left in Time Instance area.", 6000));  
			
			activeChar.sendMessage("You have " + TimeInstanceManager.getPlayerTime(activeChar) + " minutes left in Time Instance area.");
		}
		
		if (command.startsWith("info"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			
			showInfoHtml(activeChar, val);   
		}
		
		if (command.equals("setPartyRefuse"))
		{
			if (activeChar.isPartyInRefuse())
				activeChar.setIsPartyInRefuse(false);
			else
				activeChar.setIsPartyInRefuse(true);            
			showMenuHtml(activeChar);
		}    
		if (command.equals("setTradeRefuse"))
		{
			if (activeChar.getTradeRefusal())
				activeChar.setTradeRefusal(false);
			else
				activeChar.setTradeRefusal(true);
			showMenuHtml(activeChar);
		}        
		if (command.equals("setMessageRefuse"))
		{        
			if (activeChar.isInRefusalMode())
				activeChar.setInRefusalMode(false);
			else
				activeChar.setInRefusalMode(true);
			showMenuHtml(activeChar);
		}
		if (command.equals("setAutoGoldBar"))
		{  
			if (activeChar.isInAutoGoldBarMode())
				activeChar.setInAutoGoldBarMode(false);
			else
				activeChar.setInAutoGoldBarMode(true);
			showMenuHtml(activeChar);
		}
		if (command.equals("setBuffPrevent"))
		{
			if (activeChar.isBuffProtected())
				activeChar.setIsBuffProtected(false);
			else
				activeChar.setIsBuffProtected(true);            
			showMenuHtml(activeChar);
		}   
		if (command.equals("hideHeroAura"))
		{        
			if (activeChar.isDisableHeroAura())
			{
				activeChar.setDisableHeroAura(false);
				activeChar.broadcastUserInfoHiden();
			}
			else
			{
				activeChar.setDisableHeroAura(true);
				activeChar.broadcastUserInfoHiden();
			}
			showMenuHtml(activeChar);
		}
		if (command.equals("hideEnchantGlow"))
		{        
			if (activeChar.isDisableGlowWeapon())
			{
				activeChar.setDisableGlowWeapon(false);
				activeChar.broadcastUserInfoHiden();
			}
			else
			{
				activeChar.setDisableGlowWeapon(true);
				activeChar.broadcastUserInfoHiden();
			}
			showMenuHtml(activeChar);
		}
		if (command.equals("hideSkillsAnimation"))
		{        
			if (activeChar.isDisableSkillAnimation())
				activeChar.setDisableSkillAnimation(false);
			else
				activeChar.setDisableSkillAnimation(true);

			showMenuHtml(activeChar);
		}
		if (command.equals("hideMsg"))
		{ 
			if (activeChar.isHideMsg())
			{
				activeChar.setHideMsg(false);
				activeChar.broadcastUserInfoHiden();
			}
			else
			{
				activeChar.setHideMsg(true);
				activeChar.broadcastUserInfoHiden();
			}
			showMenuHtml(activeChar);
		}
		if (command.equals("hideSkinsTextures")) 
		{
			if (activeChar.isDressMeDisabled()) 
			{
				activeChar.setDressMeDisabled(false);
				activeChar.broadcastUserInfo();
				activeChar.broadcastUserInfoHiden();
			} 
			else 
			{
				activeChar.setDressMeDisabled(true);
				activeChar.broadcastUserInfo();
				activeChar.broadcastUserInfoHiden();
			} 
			showMenuHtml(activeChar);
		} 
		if (command.equals("disableHelm"))
		{
			if (activeChar.isDressMeHelmEnabled())
			{
				activeChar.setDressMeHelmEnabled(false);
				activeChar.broadcastUserInfo();
				activeChar.broadcastUserInfoHiden();
			} 
			else 
			{
				activeChar.setDressMeHelmEnabled(true);
				activeChar.broadcastUserInfo();
				activeChar.broadcastUserInfoHiden();
			} 
			showMenuHtml(activeChar);
		} 
		if (command.equals("removeskin")) 
		{
			if (activeChar.isDressMeEnabled())
			{
				activeChar.setDressMeEnabled(false);
				activeChar.broadcastUserInfo();
			}
			showMenuHtml(activeChar);
		}
		return true;
	}

	public static void showMenuHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Menu.htm"); 
		final String ON  = "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked";
		final String OFF = "back=L2UI.CheckBox fore=L2UI.CheckBox";
		html.replace("%partyRefusal%", activeChar.isPartyInRefuse() ? ON : OFF);
		html.replace("%tradeRefusal%", activeChar.getTradeRefusal() ? ON : OFF);
		html.replace("%messageRefusal%", activeChar.isInRefusalMode() ? ON : OFF);
		html.replace("%messageBuff%", activeChar.isBuffProtected() ? ON : OFF);
		html.replace("%hideHeroAura%", activeChar.isDisableHeroAura() ? ON : OFF);
		html.replace("%hideEnchantGlow%", activeChar.isDisableGlowWeapon() ? ON : OFF);
		html.replace("%hideSkillAnimation%", activeChar.isDisableSkillAnimation() ? ON : OFF);
	    html.replace("%hideSkinAnimation%", activeChar.isDressMeDisabled() ? ON : OFF);
	    html.replace("%hideHelmet%", activeChar.isDressMeHelmEnabled() ? ON : OFF);
		html.replace("%autofarm%", activeChar.isAutoFarm() ? ON : OFF);
		html.replace("%hideMsg%", activeChar.isHideMsg() ? ON : OFF);
		html.replace("%goldBarMsg%", activeChar.isInAutoGoldBarMode() ? ON : OFF);
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		html.replace("%date%", date.format(new Date(System.currentTimeMillis())));

		html.replace("%nextEvent%", CheckNextEvent.getInstance().getNextEventInfo());
		
		html.replace("%ktbTime%", CheckNextEvent.getInstance().getNextKTBTime());
		html.replace("%ctfTime%", CheckNextEvent.getInstance().getNextCTFTime());
		html.replace("%dmTime%", CheckNextEvent.getInstance().getNextDMTime());
		html.replace("%lmTime%", CheckNextEvent.getInstance().getNextLMTime());
		html.replace("%fosTime%", CheckNextEvent.getInstance().getNextFOSTime());
		html.replace("%farmTime%", CheckNextEvent.getInstance().getNextFarmTime());
		html.replace("%bonusTime%", CheckNextEvent.getInstance().getNextBonusTime());
		html.replace("%tourTime%", CheckNextEvent.getInstance().getNextTournamentTime());
		activeChar.sendPacket(html);
	}
	
	private static void showInfoHtml(L2PcInstance activeChar, int val)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/server/Page-"+ val +".htm"); 
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		html.replace("%rate_xp%", String.valueOf(Config.RATE_XP));
		html.replace("%rate_sp%", String.valueOf(Config.RATE_SP));
		html.replace("%rate_party_xp%", String.valueOf(Config.RATE_PARTY_XP));
		html.replace("%rate_adena%", String.valueOf(Config.RATE_DROP_ADENA));
		html.replace("%rate_party_sp%", String.valueOf(Config.RATE_PARTY_SP));
		html.replace("%rate_items%", String.valueOf(Config.RATE_DROP_ITEMS));
		html.replace("%rate_spoil%", String.valueOf(Config.RATE_DROP_SPOIL));
		html.replace("%rate_drop_manor%", String.valueOf(Config.RATE_DROP_MANOR));
		html.replace("%pet_rate_xp%", String.valueOf(Config.PET_XP_RATE));
		html.replace("%sineater_rate_xp%", String.valueOf(Config.SINEATER_XP_RATE));
		html.replace("%pet_food_rate%", String.valueOf(Config.PET_FOOD_RATE));
		activeChar.sendPacket(html);
	}

	public static void showEventTimeHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/EventTime.htm");

		if (TvTEvent.isStarted())
			html.replace("%tvtTime%", "Running");
		else
			html.replace("%tvtTime%", CheckNextEvent.getInstance().getNextTvTTime());
		
		html.replace("%ktbTime%", CheckNextEvent.getInstance().getNextKTBTime());
		html.replace("%ctfTime%", CheckNextEvent.getInstance().getNextCTFTime());
		html.replace("%dmTime%", CheckNextEvent.getInstance().getNextDMTime());
		html.replace("%lmTime%", CheckNextEvent.getInstance().getNextLMTime());
		html.replace("%fosTime%", CheckNextEvent.getInstance().getNextFOSTime());
		html.replace("%farmTime%", CheckNextEvent.getInstance().getNextFarmTime());
		html.replace("%tourTime%", CheckNextEvent.getInstance().getNextTournamentTime());
		html.replace("%bonusTime%", CheckNextEvent.getInstance().getNextBonusTime());

		activeChar.sendPacket(html);
	}
	
	public static void showAutoFarmHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/AutoFarm.htm"); 
		activeChar.sendPacket(html);
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}