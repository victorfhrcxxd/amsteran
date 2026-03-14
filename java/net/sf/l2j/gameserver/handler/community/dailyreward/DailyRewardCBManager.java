package net.sf.l2j.gameserver.handler.community.dailyreward;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.communitybbs.Manager.BaseBBSManager;
import net.sf.l2j.gameserver.handler.community.dailyreward.data.DailyRewardCBData;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.util.variables.Mysql;
import net.sf.l2j.gameserver.util.variables.sub.PlayerVariables;
import phantom.task.ThreadPool;

public class DailyRewardCBManager
{
	private static final Logger _log = Logger.getLogger(DailyRewardCBManager.class.getName());
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	private static class SingleTonHolder
	{
		protected static final DailyRewardCBManager _instance = new DailyRewardCBManager();
	}

	public static DailyRewardCBManager getInstance()
	{
		return SingleTonHolder._instance;
	}

	public DailyRewardCBManager()
	{
		loadData();
		scheduleDailyRewardReset();
		DailyRewardCBData.getInstance();
	}

	public void loadData()
	{
		restoreRewardedPlayersObjId();
		restoreRewardedPlayersHWID();
	}

	public void showBoard(L2PcInstance player, String file)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/dailyreward/" + file + ".htm");
		content = content.replace("%rewards%", generateDailyRewardsHtml(player));
		content = content.replace("%rewardrestart%", getNextResetTime());

		BaseBBSManager.separateAndSend(content, player);
	}

	public String generateDailyRewardsHtml(L2PcInstance player)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<table bgcolor=000000 border=1>");

		int line = 0;

		sb.append("<tr>");
		int totalReward = DailyRewardCBData.getInstance().getAllDailyRewads().size();
		int rewardCount = 0;
		for (DailyRewardCB dr : DailyRewardCBData.getInstance().getAllDailyRewads())
		{
			if (line < 7)
			{
				sb.append("<td align=center width=80>");
				sb.append("<table>");
				sb.append("<tr>");
				sb.append("<td align=center width=72>");
				sb.append("Day " + dr.getDay() + " - <font color=LEVEL>(" + dr.getAmountTxt() + ")</font>");
				sb.append("<button  action=\"bypass bp_getDailyReward " + dr.getDay() + "\" width=32 height=32 back=\"icon.skill0000\" fore=\"" + dr.getIcon() + "\">");
				sb.append(getReceivedStatus(player, dr));
				sb.append("</td>");
				sb.append("</tr>");
				sb.append("</table>");
				sb.append("</td>");
				line++;
				rewardCount++;
			}
			if (line >= 7)
			{
				line = 0;
				sb.append("</tr>");
				if (rewardCount < totalReward)
					sb.append("<tr>");
			}
		}
		sb.append("</table>");

		return sb.toString();
	}

	public String getReceivedStatus(L2PcInstance player, DailyRewardCB dr)
	{
		if (dr.getPlayersReceivdList() == null)
			dr.getPlayersReceivdList().addAll(new ArrayList<>());

		if (dr.getPlayersReceivdList().contains(player.getObjectId()) || dr.getHwidReceivedList().contains(player.getHWid()))
			return "<font color=00FDFF>(Received)</font><br>";
		else if (getDailyRewardDays(player) < dr.getDay())
			return "<font color=5BDF35>(Soon)</font><br>";
		
		return "<font color=LEVEL>(REWARD)</font><br>";
	}

	public void saveRewardedPlayersObjId()
	{
		Mysql.set("DELETE  FROM daily_rewarded_players");
		for (DailyRewardCB dr : DailyRewardCBData.getInstance().getAllDailyRewads())
		{
			for (int objId : dr.getPlayersReceivdList())
			{
				Mysql.set("REPLACE INTO daily_rewarded_players (day, obj_id) VALUES (?,?)", dr.getDay(), objId);
			}
		}	
	}
	
	public void saveRewardedPlayersHWID()
	{
		Mysql.set("DELETE FROM daily_rewarded_players_hwid");
		for (DailyRewardCB dr : DailyRewardCBData.getInstance().getAllDailyRewads())
		{
			for (String hwid : dr.getHwidReceivedList())
			{
				Mysql.set("REPLACE INTO daily_rewarded_players_hwid (day, hwid) VALUES (?,?)", dr.getDay(), hwid);
			}
		}	
	}

	public void restoreRewardedPlayersObjId()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			DailyRewardCB reward = null;
			PreparedStatement st = con.prepareStatement("SELECT * FROM daily_rewarded_players");
			ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				reward = DailyRewardCBData.getInstance().getDailyRewardByDay(rs.getInt("day"));
				if (reward.getPlayersReceivdList() == null)
				{
					reward.setPlayersReceivdList(new TreeSet<>());
				}
				reward.getPlayersReceivdList().add(rs.getInt("obj_id"));
			}
			st.close();
		}
		catch (Exception e)
		{
			_log.warning("[Daily Reward Manager]: Error could not restore rewarded players:" + e);
			e.printStackTrace();
		}
	}
	
	public void restoreRewardedPlayersHWID()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			DailyRewardCB reward = null;
			PreparedStatement st = con.prepareStatement("SELECT * FROM daily_rewarded_players_hwid");
			ResultSet rs = st.executeQuery();
			while (rs.next())
			{
				reward = DailyRewardCBData.getInstance().getDailyRewardByDay(rs.getInt("day"));
				if (reward.getHwidReceivedList() == null)
				{
					reward.setHwidReceivedList(new TreeSet<>());
				}
				reward.getHwidReceivedList().add(rs.getString("hwid"));
			}
			st.close();
		}
		catch (Exception e)
		{
			_log.warning("[Daily Reward Manager]: Error could not restore rewarded players:" + e);
			e.printStackTrace();
		}
	}

	public boolean canAddDaysForPlayer(L2PcInstance player)
	{
		if (player.getVariables().get("CanAddDaysForPlayer") == null)
			PlayerVariables.setVar(player, "CanAddDaysForPlayer", "true", -1);
		
		return player.getVariables().get("CanAddDaysForPlayer").getValueBoolean();
	}

	public void checkResetLastReward(L2PcInstance player)
	{
		if (getDailyRewardDays(player) >= DailyRewardCBData.getInstance().getAllDailyRewads().size())
			PlayerVariables.setVar(player, "DailyRewards", 1, -1);
	}

	public void onPlayerEnter(L2PcInstance player)
	{
		if (canAddDaysForPlayer(player))
		{
			addDailyRewardDay(player);
			checkResetLastReward(player);
			PlayerVariables.changeValue(player, "CanAddDaysForPlayer", "false");
		}
		
		if (getRewardsToReceiveCount(player) > 0)
			player.sendMessage("Welcome, " + player.getName() + " you have " + getRewardsToReceiveCount(player) + " Daily Rewards to receive, check our .menu!");
	}

	public int getRewardsToReceiveCount(L2PcInstance player)
	{
		int rewarded = 0;
		for (Map.Entry<Integer, DailyRewardCB> entry : DailyRewardCBData.getInstance().getDailyRewards().entrySet())
		{
			if (entry.getValue().getPlayersReceivdList().contains(player.getObjectId()))
			{
				rewarded++;
			}
		}
		return (getDailyRewardDays(player) - rewarded);
	}

	public void addReward(L2PcInstance player, DailyRewardCB dr)
	{
		ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), dr.getItemId());
		item.setEnchantLevel(dr.getEnchantLevel());

		if (item.isStackable())
			player.addItem("DailyReward", dr.getItemId(), dr.getAmount(), player, true);
		else
			player.addItem("DailyReward", item, player, true);
	}

	public void tryToGetDailyReward(L2PcInstance player, DailyRewardCB dr)
	{
		if (!dr.getPlayersReceivdList().contains(player.getObjectId()))
		{
			if(dr.getHwidReceivedList().contains(player.getHWid()))
			{
				player.sendMessage("You Already received this reward in another character.");
				return;
			}
			if (getDailyRewardDays(player) >= dr.getDay())
			{
				addReward(player, dr);
				player.sendMessage("Congratulations, you received Day " + dr.getDay() + " reward!");
				dr.getPlayersReceivdList().add(player.getObjectId());
				dr.getHwidReceivedList().add(player.getHWid());
			}
			else
			{
				player.sendMessage("Reward not available yet!");
				return;
			}
		}
		else
			player.sendMessage("You already received this reward! ");
	}

	public void addDailyRewardDay(L2PcInstance player)
	{
		if (player.getVariables().get("DailyRewards") == null)
		{
			PlayerVariables.setVar(player, "DailyRewards", 0, -1);
		}
		int days = getDailyRewardDays(player) + 1;

		PlayerVariables.changeValue(player, "DailyRewards", String.valueOf(days));
	}

	public int getDailyRewardDays(L2PcInstance player)
	{
		if (player.getVariables().get("DailyRewards") == null)
		{
			PlayerVariables.setVar(player, "DailyRewards", 1, -1);
			return 1;
		}
		return Integer.parseInt(player.getVariables().get("DailyRewards").getValue());
	}

	public String getNextResetTime()
	{
		if (dailyRewardResetTime().getTime() != null)
			return format.format(dailyRewardResetTime().getTime());
		
		return "Error";
	}

	public Calendar dailyRewardResetTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0L;
			long timeL = 0L;
			int count = 0;
			Calendar resetTime = null;
			for (String timeOfDay : Config.DAILY_REWARD_RESET_TIME)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(13, 0);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(5, 1);
				}
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				if (count == 0)
				{
					flush2 = timeL;
					resetTime = testStartTime;
				}
				if (timeL < flush2)
				{
					flush2 = timeL;
					resetTime = testStartTime;
				}
				count++;
			}
			return resetTime;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void scheduleDailyRewardReset()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0L;
			long timeL = 0L;
			int count = 0;
			@SuppressWarnings("unused")
			Calendar resetTime = null;
			for (String timeOfDay : Config.DAILY_REWARD_RESET_TIME)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(13, 0);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(5, 1);
				}
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				if (count == 0)
				{
					flush2 = timeL;
					resetTime = testStartTime;
				}
				if (timeL < flush2)
				{
					flush2 = timeL;
					resetTime = testStartTime;
				}
				count++;
			}
			ThreadPool.schedule(new DailyRewardReset(), flush2);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	class DailyRewardReset implements Runnable
	{
		@Override
		public void run()
		{
			deleteAllVarsOfName("CanAddDaysForPlayer");
		}
	}

	public void deleteAllVarsOfName(String name)
	{
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			offline = con.prepareStatement("DELETE FROM character_memo_alt WHERE name=?");
			offline.setString(1, name);
			offline.execute();

			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
	}
}