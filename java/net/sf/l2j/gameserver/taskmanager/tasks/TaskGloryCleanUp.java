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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.logging.Logger;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.sf.l2j.gameserver.taskmanager.models.Task;
import net.sf.l2j.gameserver.taskmanager.models.TaskTypes;

public final class TaskGloryCleanUp extends Task
{
	private static final Logger _log = Logger.getLogger(TaskGloryCleanUp.class.getName());
	public static final String NAME = "glory_clean_players";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Calendar cal = Calendar.getInstance();

		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && cal.get(Calendar.HOUR_OF_DAY) == 22 && cal.get(Calendar.MINUTE) == 30)
		{
			try (Connection con = L2DatabaseFactory.getInstance().getConnection())
			{
				PreparedStatement statement = con.prepareStatement("DELETE FROM glory_reward_manager");
				statement.execute();
				statement.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			_log.info("Glory Reward: Table was been cleaned!");
		}
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "22:30:00", "");
	}
}