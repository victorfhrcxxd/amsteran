package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedBossSpawn implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = 
	{ 
		"raidinfo",
		"blessed_info"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("raidinfo"))
			showNewRaidInfo(activeChar, 0);
		
		if (command.startsWith("blessed_info"))
			showBlessedInfo(activeChar);

		return true;
	}

	public static void showNewRaidInfo(L2PcInstance activeChar, int val)
	{
		String name = "data/html/mods/menu/RaidInfo.htm";
		if (val != 0)
			name = "data/html/mods/menu/RaidInfo-" + val + ".htm";

		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(name); 
		activeChar.sendPacket(html);
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static void showBlessedInfo(L2PcInstance activeChar)
	{
		String name = "data/html/mods/menu/BlessedInfo.htm";

		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(name); 
		activeChar.sendPacket(html);
		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}