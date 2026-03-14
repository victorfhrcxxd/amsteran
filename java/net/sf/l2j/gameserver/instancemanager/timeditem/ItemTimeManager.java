package net.sf.l2j.gameserver.instancemanager.timeditem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class ItemTimeManager
{
	public final Map<Integer, Info> _timedItems = new ConcurrentHashMap<>();
	private static Logger _log = Logger.getLogger(ItemTimeManager.class.getName());
	private ItemInstance item;

	public class Info
	{
		int _charId;
		int _itemId;
		long _activationTime;
	}

	public static final ItemTimeManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final ItemTimeManager _instance = new ItemTimeManager();
	}

	public ItemTimeManager()
	{
		restore();
		_startControlTask.schedule(60000);
	}

	public long getItemEndDate(int item)
	{
		long endDate = 0;
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT charId,time FROM character_timed_items WHERE itemId=?");
			statement.setInt(1, item);
			ResultSet rset = statement.executeQuery();
			while (rset.next())
			{
    			endDate = rset.getLong("time");
    		}
			statement.close();
		}
		catch (Exception e)
		{
		}

		return endDate;
	}
	
	public boolean getActiveTimed(L2PcInstance pl, boolean trade)
	{
		for (Info i : _timedItems.values())
		{
			if ((i != null) && (i._charId == pl.getObjectId()))
			{
				ItemInstance item = pl.getInventory().getItemByObjectId(i._itemId);
				if (item != null)
				{
					if (System.currentTimeMillis() < i._activationTime)
						return true;
				}
			}
		}
		return false;
	}

	public synchronized void destroy(ItemInstance item)
	{
		Info inf = _timedItems.get(item.getObjectId());
		if (inf != null)
		{
			_timedItems.remove(inf._itemId);
			try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_timed_items WHERE charId=? AND itemId=?");)
			{
				statement.setInt(1, inf._charId);
				statement.setInt(2, inf._itemId);
				statement.execute();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public boolean isActive(ItemInstance item)
	{
		for (Info i : _timedItems.values())
		{
			if (i._itemId == item.getObjectId())
				return true;
		}
		return false;
	}

	private void restore()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("SELECT charId, itemId, time FROM character_timed_items");)
		{
			try (ResultSet rs = statement.executeQuery();)
			{
				while (rs.next())
				{
					Info inf = new Info();
					inf._activationTime = rs.getLong("time");
					inf._charId = rs.getInt("charId");
					inf._itemId = rs.getInt("itemId");
					_timedItems.put(inf._itemId, inf);
				}
			}
			_log.info("TimedItems: loaded " + _timedItems.size() + " items ");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public synchronized void setTimed(ItemInstance item, int minutes)
	{
		Info inf = _timedItems.get(item.getObjectId());
		if (inf != null)
			inf._charId = item.getOwnerId();
		else
		{
			inf = new Info();
			inf._activationTime = (System.currentTimeMillis() / 1000) + (minutes * 60);
			inf._charId = item.getOwnerId();
			inf._itemId = item.getObjectId();
			_timedItems.put(inf._itemId, inf);
		}
		saveToDb(inf);
	}

	private static void saveToDb(Info temp)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("UPDATE character_timed_items SET charId=? where itemId=?");)
		{
			statement.setInt(1, temp._charId);
			statement.setInt(2, temp._itemId);
			if (statement.executeUpdate() == 0)
			{
				try (PreparedStatement statement2 = con.prepareStatement("INSERT INTO character_timed_items (charId, itemId, time) VALUES (?, ?, ?)");)
				{
					statement2.setInt(1, temp._charId);
					statement2.setInt(2, temp._itemId);
					statement2.setLong(3, temp._activationTime);
					statement2.execute();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void delete(Info temp)
	{
		_timedItems.remove(temp._itemId);
		try (Connection con = L2DatabaseFactory.getInstance().getConnection(); PreparedStatement statement = con.prepareStatement("DELETE FROM character_timed_items WHERE charId=? AND itemId=?");)
		{
			statement.setInt(1, temp._charId);
			statement.setInt(2, temp._itemId);
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		L2PcInstance pl = L2World.getInstance().getPlayer(temp._charId);
		if (pl != null)
		{
			item = pl.getInventory().getItemByObjectId(temp._itemId);
			int itemId = item.getItemId();
			if (item != null)
			{
				if (item.isEquipped())
					pl.getInventory().unEquipItemInSlot(item.getLocationSlot());

				pl.getInventory().destroyItem("timeLost", item, pl, pl);
				pl.sendPacket(new ItemList(pl, false));
			}
			pl.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(itemId));
		}
		else
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection();)
			{
				if (temp._charId != 0)
				{
					try (PreparedStatement statement = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND object_id=?");)
					{
						statement.setInt(1, temp._charId);
						statement.setInt(2, temp._itemId);
						statement.execute();
					}
				}
				else
				{
					for (L2Object o : L2World.getInstance().getAllVisibleObjects().values())
					{
						if (o.getObjectId() == temp._itemId)
						{
							L2World.getInstance().removeObject(o);
							break;
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private final ItemTimeTask _startControlTask = new ItemTimeTask()
	{
		@Override
		protected void onElapsed()
		{
			for (Info temp : _timedItems.values())
			{
				if (temp._activationTime < (System.currentTimeMillis() / 1000))                  
					delete(temp);
			}
			schedule(60000);
		}
	};      
}