package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.OfflineFarmManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedOfflineFarm implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"offlinefarm",
		"farmoff",
		"offlinefarm_zone",
		"offlinefarm_confirm",
		"offlinefarm_renew",
		"offlinefarm_start"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.equals("offlinefarm"))
		{
			showOfflineFarmMenu(activeChar);
		}
		else if (command.equals("farmoff"))
		{
			// Direct activate: if farm is already active, stop it; otherwise start + disconnect
			if (activeChar.isOfflineFarm())
			{
				OfflineFarmManager.getInstance().stopOfflineFarm(activeChar);
				showOfflineFarmMenu(activeChar);
			}
			else
			{
				OfflineFarmManager.getInstance().startOfflineFarm(activeChar);
			}
			return true;
		}
		else if (command.startsWith("offlinefarm_zone"))
		{
			try
			{
				String[] parts = command.split(" ");
				if (parts.length > 1)
				{
					int zone = Integer.parseInt(parts[1]);
					if (zone == 1 || zone == 2)
					{
						activeChar.setOfflineFarmType(zone);
						activeChar.sendMessage("Zona de farm " + zone + " selecionada.");
					}
				}
			}
			catch (Exception e)
			{
				// ignore
			}
			showOfflineFarmMenu(activeChar);
		}
		else if (command.equals("offlinefarm_confirm"))
		{
			showConfirmPage(activeChar);
		}
		else if (command.equals("offlinefarm_renew"))
		{
			OfflineFarmManager.getInstance().purchaseTime(activeChar);
			showOfflineFarmMenu(activeChar);
		}
		else if (command.equals("offlinefarm_start"))
		{
			if (activeChar.isOfflineFarm())
			{
				OfflineFarmManager.getInstance().stopOfflineFarm(activeChar);
				showOfflineFarmMenu(activeChar);
			}
			else
			{
				// startOfflineFarm will disconnect the client
				OfflineFarmManager.getInstance().startOfflineFarm(activeChar);
			}
			return true;
		}

		return true;
	}

	public static void showConfirmPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/OfflineFarmConfirm.htm");
		html.replace("%price%", String.valueOf(Config.OFFLINE_FARM_PRICE_COUNT));
		html.replace("%duration%", String.valueOf(Config.OFFLINE_FARM_DURATION));
		activeChar.sendPacket(html);
	}

	public static void showOfflineFarmMenu(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/OfflineFarm.htm");

		// Zone checkboxes
		int farmType = activeChar.getOfflineFarmType();
		String zone1Check = farmType == 1 ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox";
		String zone2Check = farmType == 2 ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox";

		html.replace("%zone1%", zone1Check);
		html.replace("%zone2%", zone2Check);
		html.replace("%time%", OfflineFarmManager.getInstance().getRemainingTime(activeChar));
		html.replace("%status%", activeChar.isOfflineFarm() ? "<font color=00FF00>Farm & Events</font>" : "<font color=FF0000>Inativo</font>");
		html.replace("%button%", activeChar.isOfflineFarm() ? "PARAR" : "ATIVAR");
		html.replace("%price%", String.valueOf(Config.OFFLINE_FARM_PRICE_COUNT));

		activeChar.sendPacket(html);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}
