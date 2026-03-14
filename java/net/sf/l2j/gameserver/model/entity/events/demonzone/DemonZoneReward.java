package net.sf.l2j.gameserver.model.entity.events.demonzone;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.RewardHolder;
import net.sf.l2j.util.Rnd;

public class DemonZoneReward
{
	protected static final Logger _log = Logger.getLogger(DemonZoneReward.class.getName());

	protected DemonZoneReward()
	{
	}

	public final static void addPartyZoneRewardEvent(L2Character killer, L2Npc monster)
	{
		if (killer instanceof L2Playable)
		{
			L2PcInstance player = killer.getActingPlayer();

			RandomRewardEvent(player);
		}
	}
	
	public static void RandomRewardEvent(L2PcInstance player)
	{
		for (RewardHolder reward : Config.DEMON_ZONE_EVENT_REWARDS)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(player.getX(), player.getY(), player.getZ(), 2500, false, false))
					{
						String playerHwId = member.getHWid();
						if (_rewarded_hwid.contains(playerHwId)) 
							continue;

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) <= reward.getRewardChance())
						{
							if (member.isVip())
								member.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getRewardMin(), reward.getRewardMax()) * Config.VIP_DROP_RATE, member, true);
							else
								member.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getRewardMin(), reward.getRewardMax()), member, true);
						}
					}
					else
						member.sendMessage("You are too far to get rewarded.");
				} 
			}
			else
			{
				if (Rnd.get(100) <= reward.getRewardChance())
				{
					if (player.isVip())
						player.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getRewardMin(), reward.getRewardMax()) * Config.VIP_DROP_RATE, player, true);
					else
						player.addItem("Random Reward", reward.getRewardId(), Rnd.get(reward.getRewardMin(), reward.getRewardMax()), player, true);
				}
			}
		}
	}

	public static final DemonZoneReward getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final DemonZoneReward _instance = new DemonZoneReward();
	}
}