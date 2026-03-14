package net.sf.l2j.gameserver.taskmanager.tasks;

import java.util.Calendar;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.instancemanager.custom.GloryRewardManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager.ExecutedTask;
import net.sf.l2j.gameserver.taskmanager.models.Task;
import net.sf.l2j.gameserver.taskmanager.models.TaskTypes;
import net.sf.l2j.gameserver.util.Broadcast;

public class TaskGloryReward extends Task
{
	private static final Logger _log = Logger.getLogger(TaskGloryReward.class.getName());
	public static final String NAME = "glory_reward_players";
	
	@Override
	public String getName()
	{
		return NAME;
	}
	
	@Override
	public void onTimeElapsed(ExecutedTask task)
	{
		Calendar cal = Calendar.getInstance();
		
		if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && cal.get(Calendar.HOUR_OF_DAY) == 12)
		{
			Broadcast.announceToOnlinePlayers("Ranked weekly season is over!");
			Broadcast.announceToOnlinePlayers("Your Glory Badge's has been delivered!");
			
			for (L2PcInstance player : L2World.getInstance().getAllPlayers().values())
				GloryRewardManager.claimDailyReward(player);
			
			_log.info("Glory Reward: Reward was been delivered!");
		}
	}
	
	@Override
	public void initializate()
	{
		super.initializate();
		TaskManager.addUniqueTask(NAME, TaskTypes.TYPE_GLOBAL_TASK, "1", "12:00:00", "");
	}
}