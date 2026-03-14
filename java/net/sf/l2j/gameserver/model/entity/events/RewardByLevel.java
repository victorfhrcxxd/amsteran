package net.sf.l2j.gameserver.model.entity.events;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class RewardByLevel
{
	public static void checkLevel(L2PcInstance player)
	{
		if (player.getLevel() >= 20 && player.getLevel() < 40 && !player.isSelectArmorD())
			rewardArmorLevel20(player);
		
		if (player.getLevel() >= 40 && player.getLevel() < 52 && !player.isSelectArmorC())
			rewardArmorLevel40(player);
		
		if (player.getLevel() >= 52 && !player.isSelectArmorB())
			rewardArmorLevel52(player);
	}
	
	public static void rewardArmorLevel20(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/armor/ArmorLevel-20.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardArmorLevel40(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/armor/ArmorLevel-40.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardArmorLevel52(L2PcInstance player)
	{		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/armor/ArmorLevel-52.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardWeaponLevel20(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/weapon/WeaponLevel-20.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardWeaponLevel40(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/weapon/WeaponLevel-40.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardWeaponLevel52(L2PcInstance player)
	{		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/weapon/WeaponLevel-52.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardTeleportLevel20(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/teleport/TeleportLevel-20.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardTeleportLevel40(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/teleport/TeleportLevel-40.htm"); 
		player.sendPacket(html);
	}
	
	public static void rewardTeleportLevel52(L2PcInstance player)
	{		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/RewardByLevel/teleport/TeleportLevel-52.htm"); 
		player.sendPacket(html);
	}
}