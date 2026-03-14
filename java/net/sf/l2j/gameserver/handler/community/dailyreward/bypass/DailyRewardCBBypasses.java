package net.sf.l2j.gameserver.handler.community.dailyreward.bypass;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.handler.ICBBypassHandler;
import net.sf.l2j.gameserver.handler.community.dailyreward.DailyRewardCB;
import net.sf.l2j.gameserver.handler.community.dailyreward.DailyRewardCBManager;
import net.sf.l2j.gameserver.handler.community.dailyreward.data.DailyRewardCBData;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class DailyRewardCBBypasses implements ICBBypassHandler
{
	@Override
	public boolean handleBypass(String bypass, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(bypass, " ");
		st.nextToken();
		if (bypass.startsWith("bp_getDailyReward"))
		{
			int day = Integer.parseInt(st.nextToken());
			DailyRewardCB dr = DailyRewardCBData.getInstance().getDailyRewardByDay(day);
			DailyRewardCBManager.getInstance().tryToGetDailyReward(activeChar, dr);
			DailyRewardCBManager.getInstance().showBoard(activeChar, "index");
		}
		if (bypass.startsWith("bp_showDailyRewardsBoard"))
		{
			DailyRewardCBManager.getInstance().showBoard(activeChar, "index");
		}
		return false;
	}

	@Override
	public String[] getBypassHandlersList()
	{
		return new String[] { "bp_getDailyReward", "bp_showDailyRewardsBoard" };
	}
}