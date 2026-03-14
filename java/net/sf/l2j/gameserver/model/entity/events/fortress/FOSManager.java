/*
 * Copyright (C) 2004-2015 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.entity.events.fortress;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.util.Broadcast;

/**
 * @author HorridoJoho
 */
public class FOSManager
{
	protected static final Logger _log = Logger.getLogger(FOSManager.class.getName());
	
	/** Task for event cycles<br> */
	private CTFStartTask _task;
	
	/**
	 * New instance only by getInstance()<br>
	 */
	protected FOSManager()
	{
		if (FOSConfig.FOS_EVENT_ENABLED)
		{
			// Cannot start if both teams have same name
			if (FOSConfig.FOS_EVENT_TEAM_1_NAME != FOSConfig.FOS_EVENT_TEAM_2_NAME)
			{
				FOSEvent.init();
				
				scheduleEventStart();
				_log.info("Fortress Engine: is Started.");
			}
			else
			{
				_log.info("Fortress Engine: is uninitiated. Cannot start if both teams have same name!");
			}
		}
		else
		{
			_log.info("Fortress Engine: is disabled.");
		}
	}
	
	/**
	 * Initialize new/Returns the one and only instance<br>
	 * <br>
	 * @return CTFManager<br>
	 */
	public static FOSManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	/**
	 * Starts CTFStartTask
	 */
	public void scheduleEventStart()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : FOSConfig.FOS_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			if (nextStartTime != null)
			{
				_task = new CTFStartTask(nextStartTime.getTimeInMillis());
				ThreadPoolManager.getInstance().executeTask(_task);
			}
		}
		catch (Exception e)
		{
			_log.warning("FOSEventEngine[FOSManager.scheduleEventStart()]: Error figuring out a start time. Check FOSEventInterval in config file.");
		}
	}
	
	/**
	 * Method to start participation
	 */
	public void startReg()
	{
		if (!FOSEvent.startParticipation())
		{
			Broadcast.gameAnnounceToOnlinePlayers("Fortress: Event was cancelled.");
			_log.warning("FOSEventEngine[FOSManager.run()]: Error spawning event npc for participation.");
			
			scheduleEventStart();
		}
		else
		{
			Broadcast.gameAnnounceToOnlinePlayers("Fortress: Joinable in " + FOSConfig.FOS_NPC_LOC_NAME + "!");
			
			if (Config.ALLOW_EVENT_COMMANDS)
			     Broadcast.gameAnnounceToOnlinePlayers("Fortress: Command: .fosjoin / .fosleave / .fosinfo");
			
			// schedule registration end
			_task.setStartTime(System.currentTimeMillis() + (60000L * FOSConfig.FOS_EVENT_PARTICIPATION_TIME));
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}
	
	/**
	 * Method to start the fight
	 */
	public void startEvent()
	{
		if (!FOSEvent.startFight())
		{
			Broadcast.gameAnnounceToOnlinePlayers("Fortress: Event cancelled due to lack of Participation.");
			_log.info("FOSEventEngine[FOSManager.run()]: Lack of registration, abort event.");
			
			scheduleEventStart();
		}
		else
		{
			FOSEvent.sysMsgToAllParticipants("Teleporting in " + FOSConfig.FOS_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
			_task.setStartTime(System.currentTimeMillis() + (60000L * FOSConfig.FOS_EVENT_RUNNING_TIME));
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}
	
	/**
	 * Method to end the event and reward
	 */
	public void endEvent()
	{
		Broadcast.gameAnnounceToOnlinePlayers(FOSEvent.calculateRewards());
		FOSEvent.sysMsgToAllParticipants("Teleporting back town in " + FOSConfig.FOS_EVENT_START_LEAVE_TELEPORT_DELAY + " second(s).");
		FOSEvent.stopFight();
		
		scheduleEventStart();
	}
	
	public void skipDelay()
	{
		if (_task.nextRun.cancel(false))
		{
			_task.setStartTime(System.currentTimeMillis());
			ThreadPoolManager.getInstance().executeTask(_task);
		}
	}
	
	/**
	 * Class for CTF cycles
	 */
	class CTFStartTask implements Runnable
	{
		private long _startTime;
		public ScheduledFuture<?> nextRun;
		
		public CTFStartTask(long startTime)
		{
			_startTime = startTime;
		}
		
		public void setStartTime(long startTime)
		{
			_startTime = startTime;
		}
		
		@Override
		public void run()
		{
			int delay = (int) Math.round((_startTime - System.currentTimeMillis()) / 1000.0);
			
			if (delay > 0)
			{
				announce(delay);
			}
			
			int nextMsg = 0;
			if (delay > 3600)
			{
				nextMsg = delay - 3600;
			}
			else if (delay > 1800)
			{
				nextMsg = delay - 1800;
			}
			else if (delay > 900)
			{
				nextMsg = delay - 900;
			}
			else if (delay > 600)
			{
				nextMsg = delay - 600;
			}
			else if (delay > 300)
			{
				nextMsg = delay - 300;
			}
			else if (delay > 60)
			{
				nextMsg = delay - 60;
			}
			else if (delay > 5)
			{
				nextMsg = delay - 5;
			}
			else if (delay > 0)
			{
				nextMsg = delay;
			}
			else
			{
				// start
				if (FOSEvent.isInactive())
				{
					startReg();
				}
				else if (FOSEvent.isParticipating())
				{
					startEvent();
				}
				else
				{
					endEvent();
				}
			}
			
			if (delay > 0)
			{
				nextRun = ThreadPoolManager.getInstance().scheduleGeneral(this, nextMsg * 1000);
			}
		}
		
		private void announce(long time)
		{
			if ((time >= 3600) && ((time % 3600) == 0))
			{
				if (FOSEvent.isParticipating())
				{
					Broadcast.gameAnnounceToOnlinePlayers("Fortress: " + (time / 60 / 60) + " hour(s) until registration is closed!");
				}
				else if (FOSEvent.isStarted())
				{
					FOSEvent.sysMsgToAllParticipants("" + (time / 60 / 60) + " hour(s) until event is finished!");
				}
			}
			else if (time >= 60)
			{
				if (FOSEvent.isParticipating())
				{
					Broadcast.gameAnnounceToOnlinePlayers("Fortress: " + (time / 60) + " minute(s) until registration is closed!");
				}
				else if (FOSEvent.isStarted())
				{
					FOSEvent.sysMsgToAllParticipants("" + (time / 60) + " minute(s) until the event is finished!");
				}
			}
			else
			{
				if (FOSEvent.isParticipating())
				{
					Broadcast.gameAnnounceToOnlinePlayers("Fortress: " + time + " second(s) until registration is closed!");
				}
				else if (FOSEvent.isStarted())
				{
					FOSEvent.sysMsgToAllParticipants("" + time + " second(s) until the event is finished!");
				}
			}
		}
	}
	
	private static class SingletonHolder
	{
		protected static final FOSManager _instance = new FOSManager();
	}
}