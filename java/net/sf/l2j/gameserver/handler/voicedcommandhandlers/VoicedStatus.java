package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.GMViewCharacterInfo;
import net.sf.l2j.gameserver.network.serverpackets.GMViewHennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.GMViewItemList;
import net.sf.l2j.gameserver.network.serverpackets.GMViewSkillInfo;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedStatus implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS = 
	{
		"status",
		"inventory"
	};

	@Override
	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target)
	{
		if (command.startsWith("status"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof L2PcInstance))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}

			L2Character targetCharacter = (L2Character) activeChar.getTarget();
			L2PcInstance targetPlayer = targetCharacter.getActingPlayer();

			activeChar.sendPacket(new GMViewCharacterInfo(targetPlayer));
			activeChar.sendPacket(new GMViewHennaInfo(targetPlayer));
			return true;
		}
		else if (command.startsWith("rank") && Config.ALLOW_RANKED_SYSTEM)
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof L2PcInstance))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			showRankHtm(activeChar);
		}
		else if (command.startsWith("inventory"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof L2PcInstance))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}

			L2Character targetCharacter = (L2Character) activeChar.getTarget();
			L2PcInstance targetPlayer = targetCharacter.getActingPlayer();

			activeChar.sendPacket(new GMViewItemList(targetPlayer));
			return true;
		}
		else if (command.startsWith("skills"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof L2PcInstance))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}

			L2Character targetCharacter = (L2Character) activeChar.getTarget();
			L2PcInstance targetPlayer = targetCharacter.getActingPlayer();

			activeChar.sendPacket(new GMViewSkillInfo(targetPlayer));
			return true;
		}

		return true;
	}

	public static void showRankHtm(L2PcInstance player) 
	{
		String htmFile = "data/html/mods/menu/RankManager.htm";

		L2PcInstance targetPlayer = (L2PcInstance) player.getTarget();
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile(htmFile);
		
		htm.replace("%name%", targetPlayer.getName());
		htm.replace("%class%", targetPlayer.getTemplate().getClassName());
		htm.replace("%color%", targetPlayer.StringToHexForVote(Integer.toHexString(targetPlayer.getAppearance().getNameColor()).toUpperCase()));
		htm.replace("%elopoints%", targetPlayer.getPcBangScore());
		
		//Unranked
		if (targetPlayer.getPcBangScore() <= 249) 
		{
			htm.replace("%namerank%", "Unranked");
			htm.replace("%rank%", "<img src=\"Ranked.unranked\" width=\"70\" height=\"74\">");
			htm.replace("%bracket%", "<img src=\"customi.deco_up\" width=\"256\" height=\"32\">");
		}
		//Iron
		if (targetPlayer.getPcBangScore() >= 250 && targetPlayer.getPcBangScore() <= 299) 
		{
			htm.replace("%namerank%", "<font color=\"4c4c4c\">Iron I</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.ferro1\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.ironbreaker\" width=\"256\" height=\"68\">");
		}
		
		if (targetPlayer.getPcBangScore() >= 300 && targetPlayer.getPcBangScore() <= 349) 
		{
			htm.replace("%namerank%", "<font color=\"4c4c4c\">Iron II</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.ferro2\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.ironbreaker\" width=\"256\" height=\"68\">");
		}
		
		if (targetPlayer.getPcBangScore() >= 350 && targetPlayer.getPcBangScore() <= 499) 
		{
			htm.replace("%namerank%", "<font color=\"4c4c4c\">Iron III</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.ferro3\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.ironbreaker\" width=\"256\" height=\"68\">");
		}
		//Bronze
		if (targetPlayer.getPcBangScore() >= 500 && targetPlayer.getPcBangScore() <= 599) 
		{
			htm.replace("%namerank%", "<font color=\"BA7204\">Bronze I</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.bronze1\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.bronzebreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 600 && targetPlayer.getPcBangScore() <= 749) 
		{
			htm.replace("%namerank%", "<font color=\"BA7204\">Bronze II</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.bronze2\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.bronzebreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 750 && targetPlayer.getPcBangScore() <= 999) 
		{
			htm.replace("%namerank%", "<font color=\"BA7204\">Bronze III</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.bronze3\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.bronzebreaker\" width=\"256\" height=\"67\">");
		}
		//Silver
		if (targetPlayer.getPcBangScore() >= 1000 && targetPlayer.getPcBangScore() <= 1099) 
		{
			htm.replace("%namerank%", "<font color=\"AAAAAA\">Silver I</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.prata1\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.silverbreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 1100 && targetPlayer.getPcBangScore() <= 1299) 
		{
			htm.replace("%namerank%", "<font color=\"AAAAAA\">Silver II</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.prata2\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.silverbreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 1300 && targetPlayer.getPcBangScore() <= 1499) 
		{
			htm.replace("%namerank%", "<font color=\"AAAAAA\">Silver III</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.prata3\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.silverbreaker\" width=\"256\" height=\"67\">");
		}
		//Gold
		if (targetPlayer.getPcBangScore() >= 1500 && targetPlayer.getPcBangScore() <= 1599) 
		{
			htm.replace("%namerank%", "<font color=\"F9EE00\">Gold I</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.ouro1\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.goldbreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 1600 && targetPlayer.getPcBangScore() <= 1799) 
		{
			htm.replace("%namerank%", "<font color=\"F9EE00\">Gold II</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.ouro2\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.goldbreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 1800 && targetPlayer.getPcBangScore() <= 1999) 
		{
			htm.replace("%namerank%", "<font color=\"F9EE00\">Gold III</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.ouro3\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.goldbreaker\" width=\"256\" height=\"67\">");
		}
		//Platinum
		if (targetPlayer.getPcBangScore() >= 2000 && targetPlayer.getPcBangScore() <= 2099) 
		{
			htm.replace("%namerank%", "<font color=\"41b7ff\">Platinum I</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.platina1\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.platbreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 2100 && targetPlayer.getPcBangScore() <= 2299) 
		{
			htm.replace("%namerank%", "<font color=\"41b7ff\">Platinum II</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.platina2\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.platbreaker\" width=\"256\" height=\"67\">");
		}
		if (targetPlayer.getPcBangScore() >= 2300 && targetPlayer.getPcBangScore() <= 2499) 
		{
			htm.replace("%namerank%", "<font color=\"41b7ff\">Platinum III</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.platina3\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.platbreaker\" width=\"256\" height=\"67\">");
		}
		//Diamond
		if (targetPlayer.getPcBangScore() >= 2500 && targetPlayer.getPcBangScore() <= 2599) 
		{
			htm.replace("%namerank%", "<font color=\"F69EE1\">Diamond I</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.diamante1\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.diabreaker\" width=\"256\" height=\"76\">");
		}
		if (targetPlayer.getPcBangScore() >= 2600 && targetPlayer.getPcBangScore() <= 2899) 
		{
			htm.replace("%namerank%", "<font color=\"F69EE1\">Diamond II</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.diamante2\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.diabreaker\" width=\"256\" height=\"76\">");
		}
		if (targetPlayer.getPcBangScore() >= 2900) 
		{
			htm.replace("%namerank%", "<font color=\"F69EE1\">Diamond III</font>");
			htm.replace("%rank%", "<img src=\"WzRanked.diamante3\" width=\"170\" height=\"100\">");
			htm.replace("%bracket%", "<img src=\"WzRanked.diabreaker\" width=\"256\" height=\"76\">");
		}
		
		//Human Male
		if (targetPlayer.getRace() == Race.Human && targetPlayer.getAppearance().getSex() == false) 
		{
			if (Config.LIST_HUMAN_MAGE_BASE.contains(targetPlayer.getBaseClass())) 
				htm.replace("%race%", "<img src=\"WzRanked.MMagic\" width=\"64\" height=\"64\">");
			else
				htm.replace("%race%", "<img src=\"WzRanked.MFighter\" width=\"64\" height=\"64\">");
		}
		
		//Human Female
		if (targetPlayer.getRace() == Race.Human && targetPlayer.getAppearance().getSex() == true) 
		{
			if (Config.LIST_HUMAN_MAGE_BASE.contains(targetPlayer.getBaseClass())) 
				htm.replace("%race%", "<img src=\"WzRanked.FMagic\" width=\"64\" height=\"64\">");
			else
				htm.replace("%race%", "<img src=\"WzRanked.FFighter\" width=\"64\" height=\"64\">");
		}

		//Orc Male
		if (targetPlayer.getRace() == Race.Orc && targetPlayer.getAppearance().getSex() == false) 
		{
			if (Config.LIST_ORC_SHAMAN_BASE.contains(targetPlayer.getBaseClass())) 
				htm.replace("%race%", "<img src=\"WzRanked.MShaman\" width=\"64\" height=\"64\">");
			else
				htm.replace("%race%", "<img src=\"WzRanked.MOrc\" width=\"64\" height=\"64\">");
		}
		
		//Orc Female
		if (targetPlayer.getRace() == Race.Orc && targetPlayer.getAppearance().getSex() == true) 
		{
			if (Config.LIST_ORC_SHAMAN_BASE.contains(targetPlayer.getBaseClass())) 
				htm.replace("%race%", "<img src=\"WzRanked.FShaman\" width=\"64\" height=\"64\">");
			else
				htm.replace("%race%", "<img src=\"WzRanked.FOrc\" width=\"64\" height=\"64\">");
		}
		
		if (targetPlayer.getRace() == Race.Elf && targetPlayer.getAppearance().getSex() == false) 
			htm.replace("%race%", "<img src=\"WzRanked.MElf\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.Elf && targetPlayer.getAppearance().getSex() == true) 
			htm.replace("%race%", "<img src=\"WzRanked.FElf\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.DarkElf && targetPlayer.getAppearance().getSex() == false) 
			htm.replace("%race%", "<img src=\"WzRanked.MDarkElf\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.DarkElf && targetPlayer.getAppearance().getSex() == true) 
			htm.replace("%race%", "<img src=\"WzRanked.FDarkElf\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.Orc && targetPlayer.getAppearance().getSex() == false) 
			htm.replace("%race%", "<img src=\"WzRanked.MOrc\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.Orc && targetPlayer.getAppearance().getSex() == true) 
			htm.replace("%race%", "<img src=\"WzRanked.FOrc\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.Dwarf && targetPlayer.getAppearance().getSex() == false) 
			htm.replace("%race%", "<img src=\"WzRanked.MDwarf\" width=\"64\" height=\"64\">");
		
		if (targetPlayer.getRace() == Race.Dwarf && targetPlayer.getAppearance().getSex() == true) 
			htm.replace("%race%", "<img src=\"WzRanked.FDwarf\" width=\"64\" height=\"64\">");

		player.sendPacket(htm);
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}