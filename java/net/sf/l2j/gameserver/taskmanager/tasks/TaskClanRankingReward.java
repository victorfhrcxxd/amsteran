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
package net.sf.l2j.gameserver.taskmanager.tasks;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.entity.events.clanranking.ClanRankingManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.sf.l2j.gameserver.taskmanager.models.Task;
import net.sf.l2j.gameserver.taskmanager.models.TaskTypes;

public class TaskClanRankingReward extends Task
{
	private static final Logger _log = Logger.getLogger(TaskClanRankingReward.class.getName());
	public static final String NAME = "clan_ranking_points_reset";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Calendar cal = Calendar.getInstance();
		
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		{
			ClanRankingManager.claimReward();
			ClanRankingManager.cleanUp();
			
			_log.info("Clan Ranking Points Global Task: launched.");
		}
	}
	
	public static String getTimeToDate()
	{
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 7);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 6);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 5);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 4);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 3);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 2);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 1);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTime().toString();
	}
	
	public static String getNextHourToDate()
	{
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
		{
			c.add(Calendar.DAY_OF_WEEK, 7);
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
		}
		return c.getTime().toString();
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "00:00:00", "");
	}
}