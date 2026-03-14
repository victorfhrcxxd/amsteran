package net.sf.l2j.gameserver.instancemanager.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class GloryRewardManager 
{
	public static void claimDailyReward(L2PcInstance player)
	{		
		Calendar cal = Calendar.getInstance();

		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && cal.get(Calendar.HOUR_OF_DAY) >= 12 && cal.get(Calendar.HOUR_OF_DAY) <= 21)
		{
			if (checkIfCharIDExistInDB(player))
				return;

			// Reward Player
			insertCharId(player);
			giveReward(player);
		}
	}
	
	private static boolean checkIfCharIDExistInDB(L2PcInstance player)
	{
		boolean flag = false;
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("SELECT * FROM glory_reward_manager WHERE obj_Id=?");
			statement.setInt(1, player.getObjectId());
			
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
				if (statement != null)
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

	private static void insertCharId(L2PcInstance player)
	{
		PreparedStatement statement = null;
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			statement = con.prepareStatement("INSERT INTO glory_reward_manager (obj_Id) VALUES (?)");
			statement.setInt(1, player.getObjectId());
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
				if(statement != null)
					statement.close();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void giveReward(L2PcInstance player)
	{
		if (player.getPcBangScore() >= 250 && player.getPcBangScore() <= 499)
		{
			player.addItem("Glory Badge", 9504, 100, null, true);
			player.reducePcBangScore(100);
		}
		
		if (player.getPcBangScore() >= 500 && player.getPcBangScore() <= 999)
		{
			player.addItem("Glory Badge", 9504, 500, null, true);
			player.reducePcBangScore(200);
		}
		
		if (player.getPcBangScore() >= 1000 && player.getPcBangScore() <= 1499)
		{
			player.addItem("Glory Badge", 9504, 1000, null, true);
			player.reducePcBangScore(300);
		}
		
		if (player.getPcBangScore() >= 1500 && player.getPcBangScore() <= 1999)
		{
			player.addItem("Glory Badge", 9504, 2000, null, true);
			player.reducePcBangScore(400);
		}
		
		if (player.getPcBangScore() >= 2000 && player.getPcBangScore() <= 2499)
		{
			player.addItem("Glory Badge", 9504, 3000, null, true);
			player.reducePcBangScore(500);
		}
		
		if (player.getPcBangScore() >= 2500)
		{
			player.addItem("Glory Badge", 9504, 5000, null, true);
			player.reducePcBangScore(600);
		}
	}
}