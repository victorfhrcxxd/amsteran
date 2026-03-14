package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class VoicedJoinPvpEvent implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = 
	{
		"joinpvp",
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("joinpvp")) 
		{
			if (activeChar.isPvPEvent())
			{
				activeChar.sendMessage("You already registered!");
				return false;
			}
			else
			{
				activeChar.setPvPEvent(true);
				activeChar.sendMessage("You has been registered!");
			}
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
