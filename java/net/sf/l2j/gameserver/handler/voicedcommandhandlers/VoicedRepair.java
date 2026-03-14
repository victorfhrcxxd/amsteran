package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class VoicedRepair implements IVoicedCommandHandler 
{
	private static String[] VOICED_COMMANDS =
	{
		"showrepair",
		"repair"
	};

	public boolean useVoicedCommand(String command, L2PcInstance activeChar, String target) 
	{
		if (command.startsWith("repair")) 
			showRepairWindow(activeChar);

		if (command.startsWith("showrepair"))
		{
			if (command.equals("showrepair")) 
			{
				activeChar.sendMessage("Please first select character to be repaired.");
				return false;
			} 

			StringTokenizer st = new StringTokenizer(command);
			st.nextToken();
			String repairChar = st.nextToken();
			
			if (repairChar == null || repairChar.equals(""))
			{
				activeChar.sendMessage("Please first select character to be repaired.");
				return false;
			} 
			
			if (checkAcc(activeChar, repairChar)) 
			{
				if (checkChar(activeChar, repairChar)) 
				{
					activeChar.sendMessage("You cannot repair your self.");
					return false;
				} 
				
				repairBadCharacter(repairChar);
				NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/mods/menu/Repaired.htm");
				activeChar.sendPacket(html);
			}
			else 
			{
				activeChar.sendMessage("Something went wrong. Please contact with the server's administrator.");
				return false;
			} 
		} 
		return true;
	}

	private static void showRepairWindow(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Repair.htm");
		html.replace("%acc_chars%", getCharList(activeChar));
		activeChar.sendPacket(html);
	}

	private static String getCharList(L2PcInstance activeChar) 
	{
		String result = "";
		String repCharAcc = activeChar.getAccountName();
		Connection con = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT char_name FROM characters WHERE account_name=?");
			statement.setString(1, repCharAcc);
			ResultSet rset = statement.executeQuery();
			while (rset.next()) 
			{
				if (activeChar.getName().compareTo(rset.getString(1)) != 0)
					result = result + rset.getString(1) + ";"; 
			} 
			rset.close();
			statement.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return result;
		}
		finally 
		{
			try 
			{
				if (con != null)
					con.close(); 
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			} 
		} 
		return result;
	}

	private static boolean checkAcc(L2PcInstance activeChar, String repairChar) 
	{
		boolean result = false;
		String repCharAcc = "";
		Connection con = null;
		try 
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT account_name FROM characters WHERE char_name=?");
			statement.setString(1, repairChar);
			ResultSet rset = statement.executeQuery();
			
			if (rset.next())
				repCharAcc = rset.getString(1); 
			
			rset.close();
			statement.close();
			try 
			{
				if (con != null)
					con.close(); 

			} 
			catch (SQLException e) 
			{
				e.printStackTrace();
			} 
			
			if (activeChar.getAccountName().compareTo(repCharAcc) != 0)
				return result; 
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return result;
		} 
		finally 
		{
			try 
			{
				if (con != null)
					con.close(); 

			} 
			catch (SQLException e)
			{
				e.printStackTrace();
			} 
		} 
		result = true;
		return result;
	}

	private static boolean checkChar(L2PcInstance activeChar, String repairChar)
	{
		boolean result = false;

		if (activeChar.getName().compareTo(repairChar) == 0)
			result = true; 

		return result;
	}

	private static void repairBadCharacter(String charName) 
	{
		Connection con = null;
		try 
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT obj_Id FROM characters WHERE char_name=?");
			statement.setString(1, charName);
			ResultSet rset = statement.executeQuery();
			int objId = 0;

			if (rset.next())
				objId = rset.getInt(1); 

			rset.close();
			statement.close();
			if (objId == 0)
			{
				con.close();
				return;
			} 
			statement = con.prepareStatement("UPDATE characters SET x=82698, y=148638, z=-3468 WHERE obj_Id=?");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			
			statement = con.prepareStatement("UPDATE items SET loc=\"INVENTORY\" WHERE owner_id=? AND loc=\"PAPERDOLL\"");
			statement.setInt(1, objId);
			statement.execute();
			statement.close();
			return;
		}
		catch (Exception e)
		{
			System.out.println("GameServer: could not repair character:" + e);
		}
		finally
		{
			try 
			{
				if (con != null)
					con.close();
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			} 
		} 
	}

	public String[] getVoicedCommandList() 
	{
		return VOICED_COMMANDS;
	}
}