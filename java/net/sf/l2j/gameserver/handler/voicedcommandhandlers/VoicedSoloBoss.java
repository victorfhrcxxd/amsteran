package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.SoloBossEvent;

public class VoicedSoloBoss implements IVoicedCommandHandler
{
	private final String[] VOICED_COMMANDS =
	{
		"soloboss"
	};
	
	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (activeChar == null)
			return false;
		
		if (command.equalsIgnoreCase("soloboss"))
		{
			final SoloBossEvent event = SoloBossEvent.getInstance();
			
			if (event.isRegistering())
			{
				if (event.isParticipant(activeChar))
					event.unregisterPlayer(activeChar);
				else
					event.registerPlayer(activeChar);
			}
			else if (event.isRunning())
			{
				activeChar.sendMessage("Solo Boss: O evento ja esta em andamento.");
			}
			else
			{
				activeChar.sendMessage("Solo Boss: O evento nao esta ativo no momento.");
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
