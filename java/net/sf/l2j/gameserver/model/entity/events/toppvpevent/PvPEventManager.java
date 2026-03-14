package net.sf.l2j.gameserver.model.entity.events.toppvpevent;

import java.util.Calendar;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

public class PvPEventManager implements Runnable
{
	protected static final Logger _log = Logger.getLogger(PvPEventManager.class.getName());

	private enum EngineState
	{
		INACTIVE, AWAITING, REGISTRATION, ACTIVE
	}

	private EngineState _state;
	private int _tick;
	private PvPEvent _event;

	protected PvPEventManager() 
	{
		_event = PvPEvent.getInstance();
		if (Config.PVP_EVENT_ENABLED)
		{
			_state = EngineState.AWAITING;
			ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
			_log.info("PvPEvent: Event is active.");
		}
		else
		{
			_state = EngineState.INACTIVE;
			_log.info("PvPEvent: Event is disabled.");
		} 
	}

	public void run() 
	{
		if (_state == EngineState.AWAITING)
		{
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(11);
			int minute = calendar.get(12);
			for (String time : Config.PVP_EVENT_INTERVAL)
			{
				String[] splitTime = time.split(":");
				if (Integer.parseInt(splitTime[0]) == hour && Integer.parseInt(splitTime[1]) == minute)
				{
					startCountdown(); 
					break;
				}
			} 
		}
		else if (_state == EngineState.REGISTRATION)
		{
			switch (_tick) 
			{
			case 60:
			case 120:
			case 180:
			case 300:
			case 600:
			case 900:
			case 1800:
			case 3600:
			case 7200:
				Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) to start!");
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 10:
			case 30:
				Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + _tick + " second(s) to start!");
				break;
			} 
			if (_tick == 0)
				startEvent(); 
			_tick--;
		}
		else if (_state == EngineState.ACTIVE) 
		{
			switch (_tick) 
			{
			    case 7200:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
		    	case 5400:
			    	Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
			    	Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
			    case 3600:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
			    case 1800:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				break;
			    case 600:
			    case 900:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
			    case 300:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
			    case 60:
			    case 180:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + (_tick / 60) + " minute(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
			    case 30:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + _tick + " second(s) until the event is finished!");
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event check the current ranking by .pvpEvent!");
				    break;
			    case 3:
			    case 4:
			    case 5:
			    case 10:
			    	Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + _tick + " second(s) until the event is finished!");
				    break;
		    	case 2:
			    	Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + _tick + " second(s) until the event is finished!");
				    break;
			    case 1:
				    Broadcast.gameAnnounceToOnlinePlayers("PvP Event " + _tick + " second(s) until the event is finished!");
				    break;
			} 
			if (_tick == 0)
				endEvent(); 
			
			_tick--;
		} 
	}

	public void startCountdown() 
	{
		Broadcast.gameAnnounceToOnlinePlayers("PvP Event Started!");
		Broadcast.gameAnnounceToOnlinePlayers("PvP Event Commands .joinpvp");
		_state = EngineState.REGISTRATION;
		_tick = Config.PVP_EVENT_REGISTER_TIME * 60;
	}

	public void startEvent() 
	{
		if (_event.startPvPEvent()) 
		{
			teleportPlayers();
			
			Broadcast.gameAnnounceToOnlinePlayers("PvP Event is enabled, go to PvP Zone.");
			_state = EngineState.ACTIVE;
			_tick = Config.PVP_EVENT_RUNNING_TIME * 60;
		} 
	}

	public void endEvent()
	{
		if (_event.endPvPEvent())
		{
			_event.rewardFinish();
			
			Broadcast.gameAnnounceToOnlinePlayers("PvP Event is finished, thank you for participating.");
			_state = EngineState.AWAITING;
		} 
	}

	public boolean teleportPlayers() 
	{
		for (L2PcInstance player : L2World.getInstance().getAllPlayers().values()) 
		{
			if (player.isPvPEvent())
			{
				RandomTeleport(player);
				player.setPvPEvent(false);
			}
			else if (!player.isPvPEvent())
			{
				teleportInviteBox(player);
			}
		} 
		return true;
	}
	
	public static boolean teleport_pvpEventBox = false;
	
	public void teleportInviteBox(L2PcInstance player) 
	{
		teleport_pvpEventBox = true;
		
		ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.EVENT.getId());
		confirm.addString("You want teleport to PvP Event?");
		confirm.addTime(15000);
		confirm.addRequesterId(player.getObjectId());
		player.sendPacket(confirm);
	}
	
	public static void RandomTeleport(L2PcInstance activeChar)
	{
		switch (Rnd.get(5))
		{
		    case 0:
		    {
		    	int x = 10666 + Rnd.get(100);
		    	int y = -24668 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3645, 0);
		    	break;
		    }
		    case 1:
		    {
		    	int x = 9739 + Rnd.get(100);
		    	int y = -22229 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3700, 0);
		    	break;
		    }
		    case 2:
		    {
		    	int x = 8708 + Rnd.get(100);
		    	int y = -23199 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3715, 0);
		    	break;
		    }
		    case 3:
		    {
		    	int x = 5653 + Rnd.get(100);
		    	int y = -23566 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3726, 0);
		    	break;
		    }
		    case 4:
		    {
		    	int x = 11150 + Rnd.get(100);
		    	int y = -22746 + Rnd.get(100);
		    	activeChar.teleToLocation(x, y, -3607, 0);
		    	break;
		    }
	    }
	}
	
	public static PvPEventManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final PvPEventManager INSTANCE = new PvPEventManager();
	}
}
