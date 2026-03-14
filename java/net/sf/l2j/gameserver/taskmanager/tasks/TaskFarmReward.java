package net.sf.l2j.gameserver.taskmanager.tasks;

import java.util.Calendar;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.instancemanager.custom.VipRewardManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.sf.l2j.gameserver.taskmanager.models.Task;
import net.sf.l2j.gameserver.taskmanager.models.TaskTypes;

public class TaskFarmReward extends Task
{
	private static final Logger _log = Logger.getLogger(TaskFarmReward.class.getName());
	public static final String NAME = "farm_reward_players";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Calendar cal = Calendar.getInstance();
		
		if (cal.get(Calendar.HOUR_OF_DAY) == 23 && cal.get(Calendar.MINUTE) == 35)
		{
			VipRewardManager.clearDBTable();
			
			_log.info("Farm Reward: Table cleaned and restart the thread!");
		}
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "23:35:00", "");
	}
}