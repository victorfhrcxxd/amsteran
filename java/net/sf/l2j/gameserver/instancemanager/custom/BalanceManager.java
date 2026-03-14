/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.instancemanager.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.base.ClassId;

/**
 * @author Anarchy
 *
 */
public class BalanceManager
{
	private Map<Integer, Map<Integer, Integer>> values;
	
	public static BalanceManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	protected BalanceManager()
	{
		values = new HashMap<>();
		
		load();
	}
	
	private void load()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement stm = con.prepareStatement("SELECT * FROM balance");
			ResultSet rset = stm.executeQuery();
			
			while (rset.next())
			{
				int from = rset.getInt("from_class");
				int to = rset.getInt("to_class");
				int mod = rset.getInt("mod_val");
				
				if (values.containsKey(from))
					values.get(from).put(to, mod);
				else
				{
					Map<Integer, Integer> temp = new HashMap<>();
					temp.put(to, mod);
					values.put(from, temp);
				}
			}
			
			rset.close();
			stm.close();
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addMod(int from, int to, int mod)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			if (values.containsKey(from) && values.get(from).containsKey(to))
			{
				PreparedStatement stm = con.prepareStatement("UPDATE balance SET mod_val=? WHERE from_class=? AND to_class=?");
				stm.setInt(1, mod);
				stm.setInt(2, from);
				stm.setInt(3, to);
				stm.execute();
				stm.close();
			}
			else
			{
				PreparedStatement stm = con.prepareStatement("INSERT INTO balance VALUES (?,?,?)");
				stm.setInt(1, from);
				stm.setInt(2, to);
				stm.setInt(3, mod);
				stm.execute();
				stm.close();
			}
			
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (values.containsKey(from))
			values.get(from).put(to, mod);
		else
		{
			Map<Integer, Integer> temp = new HashMap<>();
			temp.put(to, mod);
			values.put(from, temp);
		}
	}
	
	public int getMod(int from, int to)
	{
		int actualFrom = from;
		int actualTo = to;
		
		if (!values.containsKey(from))
		{
			for (ClassId ci : ClassId.values())
			{
				if (values.containsKey(ci.getId()) && ci.getParent().getId() == from)
				{
					actualFrom = ci.getId();
					break;
				}
			}
		}
		
		if (values.get(actualFrom) != null && !values.get(actualFrom).containsKey(to))
		{
			for (ClassId ci : ClassId.values())
			{
				if (values.get(actualFrom).containsKey(ci.getId()) && ci.getParent().getId() == to)
				{
					actualTo = ci.getId();
					break;
				}
			}
		}
		
		if (values.containsKey(actualFrom))
		{
			if (values.get(actualFrom).containsKey(actualTo))
			{
				return values.get(actualFrom).get(actualTo);
			}
		}
		
		return 0;
	}
	
	private static final class SingletonHolder
	{
		protected static final BalanceManager instance = new BalanceManager();
	}
}