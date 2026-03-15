package net.sf.l2j.gameserver.model.entity.events;

import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

public class Hide
{
	private static final Logger _log = Logger.getLogger(Hide.class.getName());
	private static Hide _instance;
	private final int delay = Config.HIDE_EVENT_ITEM_TIME * 60 * 1000;
	
	private final static int itemId = 9503;
	private final static int itemCount = 1;
	
	public static boolean running = false;
	private static int x;
	private static int y;
	private static int z = 0;

	static ItemInstance item = null;

	public static int getX()
	{
		return x;
	}

	public static int getY()
	{
		return y;
	}

	public static int getZ()
	{
		return z;
	}

	public static int getItemId()
	{
		return itemId;
	}

	public static int getItemCount()
	{
		return itemCount;
	}

	public void startEvent()
	{
		running = true;
		
		int s = Rnd.get(Config.HIDE_EVENT_ITEM_LOCS.length);
		x = Config.HIDE_EVENT_ITEM_LOCS[s][0];
		y = Config.HIDE_EVENT_ITEM_LOCS[s][1];
		z = Config.HIDE_EVENT_ITEM_LOCS[s][2];
		
		SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_WAS_DROPPED_IN_THE_S1_REGION);
		sm.addZoneName(getX(), getY(), getZ()); 
		sm.addItemName(itemId);

		item = new ItemInstance(IdFactory.getInstance().getNextId(), itemId);
		L2World.getInstance().storeObject(item);
		item.setCount(itemCount);
		item.setHide(true);
		item.getPosition().setWorldPosition(x, y ,z);
		item.getPosition().setWorldRegion(L2World.getInstance().getRegion(item.getPosition().getWorldPosition()));
		item.getPosition().getWorldRegion().addVisibleObject(item);
		item.setProtected(false);
		item.setIsVisible(true);
		L2World.getInstance().addVisibleObject(item, item.getPosition().getWorldRegion());

		Broadcast.toAllOnlinePlayers(sm);
		ThreadPoolManager.getInstance().scheduleGeneral(new Check(), Config.HIDE_EVENT_DISSAPEAR_TIME * 60 * 1000);
	}

	public void checkAfterTime()
	{
		if (running == false)
			return;
		
		if (item.isHide())
			item.setHide(false);
		
		item.decayMe();
		L2World.getInstance().removeObject(item);
		cleanEvent(); 
	}

	public static void cleanEvent()
	{
		x = 0;
		y = 0;
		z = 0;
		running = false;
		
		if(item != null)
		{
			item.decayMe();
			L2World.getInstance().removeObject(item);
		}
		
		item = null;
	}

	private Hide()
	{
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Event(), delay, delay);
		_log.info("Hide: Loaded.");
	}

	public static Hide getInstance()
	{
		if(_instance == null)
			_instance = new Hide();
		
		return _instance;
	}

	public class Check implements Runnable
	{
		public void run()
		{
			checkAfterTime();
		}
	}

	public class Event implements Runnable
	{
		public void run()
		{
			startEvent();	
		}
	}
}