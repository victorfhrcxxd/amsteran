package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedDonate implements IVoicedCommandHandler
{
	
	private static final String[] VOICED_COMMANDS = 
	{
		"donate",
		"infoDonate",
		"multisell"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("donate"))
			showDonateHtml(activeChar);       

		else if (command.startsWith("infoDonate"))
		{
			StringTokenizer stringTokenizer = new StringTokenizer(command, " ");
			
			if ((stringTokenizer.countTokens() > 2) || (stringTokenizer.countTokens() < 2))
				return false;

			stringTokenizer.nextToken();
			String actualCommand = "Page-" + stringTokenizer.nextToken() + ".htm";
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/mods/menu/Donate/" + actualCommand);
			activeChar.sendPacket(html);
			return true;    
		}
		else if (command.startsWith("multisell"))
		{
			try
			{
				activeChar.setIsUsingCMultisell(true);
				MultisellData.getInstance().separateAndSend(command.substring(9).trim(), activeChar, false, 0);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("This list does not exist.");
			}
		}
		else if (command.startsWith("exc_multisell"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // Get actual command
			
			try
			{
				if (st.countTokens() < 1)
					return false;
				
				activeChar.setIsUsingCMultisell(true);
				MultisellData.getInstance().separateAndSend(st.nextToken(), activeChar, true, 0);
			}
			catch(Exception e)
			{
				activeChar.sendMessage("This list does not exist.");
			}
		}
		return true;
	}

	private static void showDonateHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Donate.htm");  
		activeChar.sendPacket(html);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}