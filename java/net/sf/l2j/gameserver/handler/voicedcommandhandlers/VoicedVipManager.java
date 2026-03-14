package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.util.logging.Logger;

import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.taskmanager.AttackStanceTaskManager;

public class VoicedVipManager  implements IVoicedCommandHandler
{
	public static final Logger _log = Logger.getLogger(VoicedVipManager.class.getName());
	
	private static final String[] VOICED_COMMANDS = 
	{
		"vip",
		"vip_multisell",
		"vip_teleport"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("vip"))
		{ 
			if (!activeChar.isVip()) 
			{
				activeChar.sendMessage("You need to be a VIP member to use this command.");
				return false;
			} 
			showVipPanel(activeChar);
		}
		
		if (command.startsWith("vip_multisell"))
		{
			if (!activeChar.isVip()) 
			{
				activeChar.sendMessage("You need to be a VIP member to use this command.");
				return false;
			} 
			
			try
			{
				activeChar.setIsUsingCMultisell(true);
				MultisellData.getInstance().separateAndSend("consumables", activeChar, false, 0);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("This list does not exist.");
			}
		}
		if (command.startsWith("vip_teleport"))
		{
			if (activeChar.isVip())
			{
				if (activeChar.isCursedWeaponEquipped() || activeChar.isInArenaEvent() || OlympiadManager.getInstance().isRegistered(activeChar) || activeChar.getKarma() > 0 || activeChar.inObserverMode() || CTFEvent.isPlayerParticipant(activeChar.getObjectId()) || DMEvent.isPlayerParticipant(activeChar.getObjectId()) || LMEvent.isPlayerParticipant(activeChar.getObjectId()) || TvTEvent.isPlayerParticipant(activeChar.getObjectId()) || KTBEvent.isPlayerParticipant(activeChar.getObjectId()) || AttackStanceTaskManager.getInstance().get(activeChar))
				{
					activeChar.sendMessage("You can not teleport right now.");
					return false;
				}

				activeChar.teleToLocation(83397, 147996, -3400, 20);
			}
			else
				activeChar.sendMessage("You need to be a VIP member to use this command.");   
			
		}
		return true;
	}

	private static final String ACTIVED_IMG = "<img src=\"panel.online\" width=\"16\" height=\"16\">";
	private static final String DESATIVED_IMG = "<img src=\"panel.offline\" width=\"16\" height=\"16\">";
	private static final String ACTIVED_LETTER = "<font color=00FF00>Active</font>";
	private static final String DESATIVED_LETTER = "<font color=FF0000>Disabled</font>";

	public static void showVipPanel(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/VipManager.htm"); 
		html.replace("%autofarmImg%", activeChar.isAutoFarm() ? ACTIVED_IMG : DESATIVED_IMG);
		html.replace("%autofarmLetter%", activeChar.isAutoFarm() ? ACTIVED_LETTER : DESATIVED_LETTER);
		activeChar.sendPacket(html);
	}

	public static void showVipAutoFarmHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/VipAutoFarm.htm"); 
		activeChar.sendPacket(html);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}