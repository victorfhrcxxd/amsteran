package net.sf.l2j.gameserver.instancemanager.custom;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

public class VipRewardManager
{
	private static final Logger _log = Logger.getLogger(VipRewardManager.class.getName());

	protected VipRewardManager()
	{
		_log.info("VipRewardManager: Loaded.");
	}

	protected static void loadSystemThread()
	{
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		long spawnMillis = c.getTimeInMillis() - System.currentTimeMillis();

		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()
		{
			@Override
			public void run()
			{
				clearDBTable();
				loadSystemThread();
				_log.info("[VipRewardManager] Table cleaned, thread restarted.");
			}
		}, spawnMillis);
	}

	public static void clearDBTable()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("DELETE FROM reward_vip_manager");
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void claimVipReward(L2PcInstance player)
	{
		/*
		if (Config.ALLOW_VIP_REWARD)
		{
			if (checkIfIPorHWIDExistInDB(player, "hwid"))
			{
				if (checkForLatestHWIDReward(player, "hwid")) // Rewarded
				{
					if (checkForLatestHWIDReward(player, "hwid"))
						player.sendMessage("You cannot receive any more vip days at this time.");
						//player.sendMessage("Join again in " + Cd(player, "hwid", false) + " to get your daily reward.");
				}
				else // Not rewarded
				{ 
					if (checkIfIPorHWIDExistInDB(player, "hwid"))
						updateLastReward(player, "hwid");

					giveVip(player);
				}
			}
			else // Insert new Parent and reward
			{
				insertNewParentOfPlayerIPHWID(player);
				giveVip(player);
			}
		}
		*/
	}

	private static void giveVip(L2PcInstance player)
	{
		if (!player.isVip())
		{
			player.setVip(true);
			player.setEndTime("vip", 3);
			player.sendMessage("You became VIP member per 3 day's.");
			MagicSkillUse MSU = new MagicSkillUse(player, player, 2024, 1, 1, 0);
			player.broadcastPacket(MSU);
		}
	}

	private static boolean checkForLatestHWIDReward(L2PcInstance activeChar, String mode)
	{
		return Long.parseLong(Cd(activeChar, mode, true)) > System.currentTimeMillis();
	}

	private static void updateLastReward(L2PcInstance player, String mode)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("UPDATE reward_vip_manager SET expire_time=? WHERE "+mode+"=?");
			statement.setLong(1, System.currentTimeMillis());
			statement.setString(2,  player.getHWid()/*(mode.equals("ip") ? player.getClient().getConnection().getInetAddress().getHostAddress() : player.getHWid())*/);
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static String Cd(L2PcInstance player, String mode, boolean returnInTimestamp)
	{
		long CdMs = 0;
		long voteDelay = 1440 * 60000L;
		
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT expire_time FROM reward_vip_manager WHERE "+mode+"=?");
			statement.setString(1,  player.getHWid()/*(mode.equals("ip") ? player.getClient().getConnection().getInetAddress().getHostAddress() : player.getHWid())*/);
			
			ResultSet rset = statement.executeQuery();
			
			while (rset.next())
			{
				CdMs = rset.getLong("expire_time");
			}
			
			if((CdMs + voteDelay) < System.currentTimeMillis())
				CdMs = System.currentTimeMillis() - voteDelay;
			
			rset.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		
		if(returnInTimestamp)
			return String.valueOf(CdMs+voteDelay);
		
		Date resultdate = new Date(CdMs + voteDelay);
		return sdf.format(resultdate);
	}
	
	private static boolean checkIfIPorHWIDExistInDB(L2PcInstance player, String mode)
	{
		boolean flag = false;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT * FROM reward_vip_manager WHERE "+mode+"=?");
			statement.setString(1,  player.getHWid()/*(mode.equals("ip") ? player.getClient().getConnection().getInetAddress().getHostAddress() : player.getHWid())*/);
			
			ResultSet rset = statement.executeQuery();
			
			if (rset.next())
				flag = true;

			rset.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(statement != null)
					statement.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return flag;
	}
	
	private static void insertNewParentOfPlayerIPHWID(L2PcInstance player)
	{
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			//statement = con.prepareStatement("INSERT INTO reward_manager (ip,hwid,expire_time) VALUES (?,?,?)");
			statement = con.prepareStatement("INSERT INTO reward_vip_manager (hwid,expire_time) VALUES (?,?)");
			//statement.setString(1, player.getClient().getConnection().getInetAddress().getHostAddress());
			statement.setString(1, player.getHWid());
			statement.setLong(2, System.currentTimeMillis());
			statement.execute();
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (statement != null)
					statement.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static VipRewardManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final VipRewardManager _instance = new VipRewardManager();
	}
}