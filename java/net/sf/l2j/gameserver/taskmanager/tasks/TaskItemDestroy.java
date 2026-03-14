package net.sf.l2j.gameserver.taskmanager.tasks;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class TaskItemDestroy implements Runnable
{
	private L2PcInstance _player;
	
	public TaskItemDestroy(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		if (_player == null)
			return;
		
		_player.removeTimedItens();
	}
}
