package net.sf.l2j.gameserver.model.entity.events;

import phantom.FakePlayer;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.util.Rnd;

public class RankedSystem 
{
	public static void PvpRank(L2PcInstance player, L2Character target)
	{
		if (PvpProtection.getInstance().protectionExists(player, ((L2PcInstance)target)))
		{
			player.sendCustomMessage("You did not get any elo point from this kill.");
			return;
		}
		PvpProtection.getInstance().checkKill(player, ((L2PcInstance)target));

		if (((L2PcInstance)target).getPcBangScore() <= 18)
		{
			int Ncscore = Rnd.get(1, 5);
			
			if (player != null && target != null && target instanceof L2PcInstance)
			{
				if (PvpProtection.getInstance().protectionExists(player, ((L2PcInstance)target)))
					return;

				if (player.isInsideZone(ZoneId.ZONE_PVP) || player.isInsideZone(ZoneId.FLAG_AREA) || player.isInsideZone(ZoneId.HEAVY_FARM_AREA) || player.isInsideZone(ZoneId.NO_ZERG) || player.isInsideZone(ZoneId.CHANGE_PVP) || player.isInsideZone(ZoneId.BOSS_AREA) || player.isInsideZone(ZoneId.SIEGE) || player.isInsideZone(ZoneId.PVP))
				{
					player.addPcBangScore(Ncscore);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT).addNumber(Ncscore));
					player.updatePcBangWnd(Ncscore, true, false);
				}
			}
		}
		else
		{
			int score = Rnd.get(15, 20);

			if (player != null && target != null && target instanceof L2PcInstance)
			{
				if (PvpProtection.getInstance().protectionExists(player, ((L2PcInstance)target)))
					return;

				if (player.isInsideZone(ZoneId.ZONE_PVP) || player.isInsideZone(ZoneId.FLAG_AREA) || player.isInsideZone(ZoneId.HEAVY_FARM_AREA) || player.isInsideZone(ZoneId.NO_ZERG) || player.isInsideZone(ZoneId.CHANGE_PVP) || player.isInsideZone(ZoneId.BOSS_AREA) || player.isInsideZone(ZoneId.SIEGE) || player.isInsideZone(ZoneId.PVP))
				{
					player.addPcBangScore(score);
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT).addNumber(score));
					player.updatePcBangWnd(score, true, false);

					((L2PcInstance)target).reducePcBangScore(score);
					target.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.USING_S1_PCPOINT).addNumber(score));
				}
			}
		}
	}

	public static void RankReward(L2PcInstance player)
	{
		if (player instanceof FakePlayer)
			return;
		
		if (player.getPcBangScore() >= 250 && player.getInventory().getItemByItemId(Config.RANKED_REWARD_IRON) == null && player.getWarehouse().getItemByItemId(Config.RANKED_REWARD_IRON) == null)
			player.addItem("Iron", Config.RANKED_REWARD_IRON, 1, null, true);
		
		if (player.getPcBangScore() >= 500 && player.getInventory().getItemByItemId(Config.RANKED_REWARD_BRONZE) == null && player.getWarehouse().getItemByItemId(Config.RANKED_REWARD_BRONZE) == null)
			player.addItem("Bronze", Config.RANKED_REWARD_BRONZE, 1, null, true);
		
		if (player.getPcBangScore() >= 1000 && player.getInventory().getItemByItemId(Config.RANKED_REWARD_SILVER) == null && player.getWarehouse().getItemByItemId(Config.RANKED_REWARD_SILVER) == null)
			player.addItem("Silver", Config.RANKED_REWARD_SILVER, 1, null, true);
		
		if (player.getPcBangScore() >= 1500 && player.getInventory().getItemByItemId(Config.RANKED_REWARD_GOLD) == null && player.getWarehouse().getItemByItemId(Config.RANKED_REWARD_GOLD) == null)
			player.addItem("Gold", Config.RANKED_REWARD_GOLD, 1, null, true);
		
		if (player.getPcBangScore() >= 2000 && player.getInventory().getItemByItemId(Config.RANKED_REWARD_PLATINUM) == null && player.getWarehouse().getItemByItemId(Config.RANKED_REWARD_PLATINUM) == null)
			player.addItem("Platinum", Config.RANKED_REWARD_PLATINUM, 1, null, true);
		
		if (player.getPcBangScore() >= 2500 && player.getInventory().getItemByItemId(Config.RANKED_REWARD_DIAMOND) == null && player.getWarehouse().getItemByItemId(Config.RANKED_REWARD_DIAMOND) == null && player.getInventory().getItemByItemId(19055) == null && player.getWarehouse().getItemByItemId(19055) == null)
			player.addItem("Beret Diamond", Config.RANKED_REWARD_DIAMOND, 1, null, true);

		if (Config.ALLOW_RANKED_SYSTEM_SKILL)
		{
			if (player.getPcBangScore() >= 2500)
			{
				boolean hasRight = false;

				for (L2Skill skill : player.getAllSkills())
				{
					if (skill.getId() == 9500)
					{
						hasRight = true;
						break;
					}
				}

				if (!hasRight)
				{
					player.sendCustomMessage("You received the Ranked Spirit Cubic Skill.");
					player.addSkill(SkillTable.getInstance().getInfo(9500, 1), true); // Cubic
					player.sendSkillList();
					return;
				}
			}
		}
	}
}