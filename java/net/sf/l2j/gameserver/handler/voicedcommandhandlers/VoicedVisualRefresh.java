package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class VoicedVisualRefresh implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"visualrefresh"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (activeChar == null)
			return false;

		if (command.equals("visualrefresh"))
		{
			boolean enabled = !activeChar.isVisualRefreshOnTeleport();
			activeChar.setVisualRefreshOnTeleport(enabled);

			if (enabled)
				activeChar.sendMessage("Visual refresh on teleport: ENABLED. Your client view will be refreshed after each teleport.");
			else
				activeChar.sendMessage("Visual refresh on teleport: DISABLED.");
		}

		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
