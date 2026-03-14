package net.sf.l2j.gameserver.taskmanager;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.Duel;

public class TimeInstanceTeleportTaskManager implements Runnable
{
	private L2PcInstance _playerInstance = null;
	
	/**
	 * Initialize the teleporter and start the delayed task.
	 * @param playerInstance
	 */
	public TimeInstanceTeleportTaskManager(L2PcInstance playerInstance)
	{
		_playerInstance = playerInstance;
		
		ThreadPoolManager.getInstance().scheduleGeneral(this, 3000);
	}
	
	/**
	 * The task method to teleport the player 1. Unsummon pet if there is one 2. Revive and full heal the player 3. Teleport the player 4. Broadcast status and user info
	 */
	@Override
	public void run()
	{
		if (_playerInstance == null)
			return;
		
		L2Summon summon = _playerInstance.getPet();
		
		if (summon != null)
			summon.unSummon(_playerInstance);
		
		if (_playerInstance.isInDuel())
			_playerInstance.setDuelState(Duel.DUELSTATE_INTERRUPTED);
		
		_playerInstance.stopAbnormalEffect(0x0800);
		_playerInstance.setIsParalyzed(false);
		_playerInstance.stopParalyze(false);
		
		_playerInstance.doRevive();
		
		_playerInstance.setCurrentCp(_playerInstance.getMaxCp());
		_playerInstance.setCurrentHp(_playerInstance.getMaxHp());
		_playerInstance.setCurrentMp(_playerInstance.getMaxMp());
		
		_playerInstance.teleToLocation(81337, 148093, -3473, 0);
		
		_playerInstance.broadcastStatusUpdate();
		_playerInstance.broadcastUserInfo();
	}
}