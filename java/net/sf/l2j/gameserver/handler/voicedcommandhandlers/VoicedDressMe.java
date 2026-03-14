package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.handler.dressme.DressMeBypassHandler;

public class VoicedDressMe implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"dressme"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String params)
	{
		if (!Config.ALLOW_DRESS_ME_SYSTEM)
		{
			activeChar.sendMessage("DressMe system is disabled.");
			return false;
		}
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("DressMe can't be used in Olympiad.");
			return false;
		}
		
		if (Config.ALLOW_DRESS_ME_VIP && !activeChar.isVip())
		{
			activeChar.sendMessage("DressMe is only available for VIP players.");
			return false;
		}
		
		DressMeBypassHandler.showDressMeMainPage(activeChar);
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
