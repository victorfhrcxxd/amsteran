package net.sf.l2j.gameserver.taskmanager;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.instancemanager.custom.TimeInstanceManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public class TimeInstanceRemainTaskManager implements Runnable
{
	private L2PcInstance _playerInstance = null;
	
	/**
	 * Initialize the teleporter and start the delayed task.
	 * @param playerInstance
	 */
	public TimeInstanceRemainTaskManager(L2PcInstance playerInstance)
	{
		_playerInstance = playerInstance;
		
		ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(this, 5*1000, 1000);
	}
	
	@Override
	public void run()
	{
		if (_playerInstance == null)
			return;
		
		TimeInstanceManager.broadcastTimer(_playerInstance);
	}
}