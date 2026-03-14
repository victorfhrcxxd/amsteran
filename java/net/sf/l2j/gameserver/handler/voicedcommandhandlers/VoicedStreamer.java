package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedStreamer implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = 
	{
		"Alucard",
		"Alucard_Hat",
		"Alucard_Sh_1",
		"Alucard_Sh_2",
		"Alucard_Sh_3",
		"Bradesco_Hat",
		"Bradesco_Sh_1",
		"Bradesco_Sh_2",
		"Bradesco_Sh_3"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("Alucard") && Config.ENABLE_ALUCARD_COMMAND)
			showAlucardHtm(activeChar);
		
		if (command.startsWith("Bradesco") && Config.ENABLE_BRADESCO_COMMAND)
			showBradescoHtm(activeChar);
		
		if (command.startsWith("Alucard_Hat") && Config.ENABLE_ALUCARD_COMMAND)
		{
			if (activeChar.getObjectId() != Config.ALUCARD_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 5000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 5000, activeChar, null);
				activeChar.getInventory().addItem("Acessory", 30119, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 5,000 Gold Coin's.");
		}
		
		if (command.startsWith("Alucard_Sh_1") && Config.ENABLE_ALUCARD_COMMAND)
		{
			if (activeChar.getObjectId() != Config.ALUCARD_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 1000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 1000, activeChar, null);
				activeChar.getInventory().addItem("Shield", 30700, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 1,000 Gold Coin's.");
		}
		
		if (command.startsWith("Alucard_Sh_2") && Config.ENABLE_ALUCARD_COMMAND)
		{
			if (activeChar.getObjectId() != Config.ALUCARD_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 1000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 1000, activeChar, null);
				activeChar.getInventory().addItem("Shield", 30701, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 1,000 Gold Coin's.");
		}
		
		if (command.startsWith("Alucard_Sh_3") && Config.ENABLE_ALUCARD_COMMAND)
		{
			if (activeChar.getObjectId() != Config.ALUCARD_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 1000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 1000, activeChar, null);
				activeChar.getInventory().addItem("Shield", 30702, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 1,000 Gold Coin's.");
		}
		
		if (command.startsWith("Bradesco_Hat") && Config.ENABLE_BRADESCO_COMMAND)
		{
			if (activeChar.getObjectId() != Config.BRADESCO_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 5000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 5000, activeChar, null);
				activeChar.getInventory().addItem("Acessory", 30120, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 5,000 Gold Coin's.");
		}
		
		if (command.startsWith("Bradesco_Sh_1") && Config.ENABLE_BRADESCO_COMMAND)
		{
			if (activeChar.getObjectId() != Config.BRADESCO_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 1000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 1000, activeChar, null);
				activeChar.getInventory().addItem("Shield", 30710, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 1,000 Gold Coin's.");
		}
		
		if (command.startsWith("Bradesco_Sh_2") && Config.ENABLE_BRADESCO_COMMAND)
		{
			if (activeChar.getObjectId() != Config.BRADESCO_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 1000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 1000, activeChar, null);
				activeChar.getInventory().addItem("Shield", 30711, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 1,000 Gold Coin's.");
		}
		
		if (command.startsWith("Bradesco_Sh_3") && Config.ENABLE_BRADESCO_COMMAND)
		{
			if (activeChar.getObjectId() != Config.BRADESCO_CHAR_ID)
				return false;

			if (activeChar.getInventory().getInventoryItemCount(9500, 0) >= 1000)
			{
				activeChar.getInventory().destroyItemByItemId("Gold Coin", 9500, 1000, activeChar, null);
				activeChar.getInventory().addItem("Shield", 30712, 1, activeChar, null);
				activeChar.getInventory().updateDatabase();
				activeChar.sendPacket(new ItemList(activeChar, true));
			}
			else
				activeChar.sendMessage("You do not have 1,000 Gold Coin's.");
		}
		
		return true;
	}

	public static void showAlucardHtm(L2PcInstance player) 
	{
		if (player.getObjectId() == Config.ALUCARD_CHAR_ID)
		{
			String htmFile = "data/html/mods/menu/stream/Alucard.htm";

			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile(htmFile);
			player.sendPacket(htm);
		}
		else
			player.sendMessage("You do not have permission for this command.");
	}

	public static void showBradescoHtm(L2PcInstance player) 
	{
		if (player.getObjectId() == Config.BRADESCO_CHAR_ID)
		{
			String htmFile = "data/html/mods/menu/stream/Bradesco.htm";

			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile(htmFile);
			player.sendPacket(htm);
		}
		else
			player.sendMessage("You do not have permission for this command.");
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}