package net.sf.l2j.gameserver.model.entity.events.bonuzone;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.Earthquake;
import net.sf.l2j.gameserver.network.serverpackets.ExRedSky;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

public class BonusZoneManager implements Runnable
{
	protected static final Logger _log = Logger.getLogger(BonusZoneManager.class.getName());

	private int _tick;
	private EngineState _state;
	private BonusZone _event;

	protected enum EngineState 
	{
		AWAITING, ACTIVE, INACTIVE;
	}

	protected BonusZoneManager() 
	{
		_event = BonusZone.getInstance();
		if (Config.BONUS_ZONE_EVENT_ENABLED)
		{
			_state = EngineState.AWAITING;
			ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
			_log.info("Bonus Event: is active.");
		}
		else
		{
			_state = EngineState.INACTIVE;
			_log.info("Bonus Event: is disabled.");
		} 
	}

	public void run() 
	{
		if (_state == EngineState.AWAITING)
		{
			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			for (String time : Config.BONUS_ZONE_INTERVAL)
			{
				String[] splitTime = time.split(":");
				if (Integer.parseInt(splitTime[0]) == hour && Integer.parseInt(splitTime[1]) == minute)
					startEvent(); 
			} 
		}
		else if (_state == EngineState.ACTIVE) 
		{
			switch (_tick) 
			{
			case 7200:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 5400:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 3600:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 1800:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 600:
			case 900:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 300:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 60:
			case 180:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + (_tick / 60) + " minute(s) until finished!");
				break;
			case 30:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + _tick + " second(s) until finished!");
				break;
			case 3:
			case 4:
			case 5:
			case 10:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + _tick + " second(s) until finished!");
				break;
			case 2:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + _tick + " second(s) until finished!");
				break;
			case 1:
				Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: " + _tick + " second(s) until finished!");
				break;
			} 
			if (_tick == 0)
				endEvent(); 
			
			_tick--;
		} 
	}

	public void startEvent() 
	{
		if (_event.startBonusEvent()) 
		{
			Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: is Started!");
			Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: spawned Bonus Monsters!");
			
			Broadcast.toAllOnlinePlayers(new ExShowScreenMessage("Bonus Zone Event is Started!", 3000, SMPOS.TOP_CENTER, false));
			
			// RedSky and Earthquake
			Broadcast.toAllOnlinePlayers(new ExRedSky(10));
			Broadcast.toAllOnlinePlayers(new Earthquake(0, 0, 0, 14, 3));
			
			SpawnEventChampions();
			
			_state = EngineState.ACTIVE;
			_tick = Config.BONUS_ZONE_RUNNING_TIME * 60;
		} 
	}

	public void endEvent()
	{
		if (_event.endBonusEvent())
		{
			Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: is Finished!");
			Broadcast.gameAnnounceToOnlinePlayers("Bonus Zone Event: Bonus Monsters disappeared!");
			
			UspawnEventChampions();
			
			_state = EngineState.AWAITING;
		} 
	}

	public static List<L2Spawn> _eventChampions = new CopyOnWriteArrayList<>();
	
	public void SpawnEventChampions()
	{
		int[] coord;
		for (int i = 0; i < Config.BONUS_ZONE_MONSTERS_EVENT_LOCS_COUNT; i++)
		{
			coord = Config.BONUS_ZONE_MONSTERS_EVENT_LOCS[i];
			_eventChampions.add(spawnChampion(coord[0], coord[1], coord[2]));
		}
	}
	
	protected static L2Spawn spawnChampion(int xPos, int yPos, int zPos)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(Config.BONUS_ZONE_MONSTERS_EVENT_ID.get(Rnd.get(Config.BONUS_ZONE_MONSTERS_EVENT_ID.size())));
		
		try
		{
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(xPos);
			spawn.setLocy(yPos);
			spawn.setLocz(zPos);
			spawn.setHeading(0);
			spawn.setRespawnDelay(30);
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			spawn.init();
		
			spawn.getLastSpawn().setTitle("Bonus Monster");
			spawn.getLastSpawn().isAggressive();
			spawn.getLastSpawn().decayMe();
			spawn.getLastSpawn().spawnMe(spawn.getLastSpawn().getX(), spawn.getLastSpawn().getY(), spawn.getLastSpawn().getZ());

			return spawn;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	protected static void UspawnEventChampions()
	{
		for (L2Spawn s : _eventChampions)
		{
			if (s == null)
			{
				_eventChampions.remove(s);
				continue;
			}
			
			s.getLastSpawn().deleteMe();
			s.stopRespawn();
			SpawnTable.getInstance().deleteSpawn(s, true);
			_eventChampions.remove(s);
		}
	}
	
	public static BonusZoneManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final BonusZoneManager INSTANCE = new BonusZoneManager();
	}
}
