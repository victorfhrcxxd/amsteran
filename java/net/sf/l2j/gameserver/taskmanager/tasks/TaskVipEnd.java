package net.sf.l2j.gameserver.taskmanager.tasks;

import net.sf.l2j.gameserver.instancemanager.autofarm.AutofarmManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public class TaskVipEnd implements Runnable
{
	private L2PcInstance _player;
	
	public TaskVipEnd(L2PcInstance player)
	{
		_player = player;
	}
	
	@Override
	public void run()
	{
		if (_player == null)
			return;
		
		if (_player.isOnline())
		{
			_player.setVip(false);
			_player.setVipEndTime(0);
			_player.store();

			AutofarmManager.INSTANCE.onDeath(_player);
			
			_player.sendPacket(new ExShowScreenMessage("Your VIP status period is ended!", 5 * 1000));
			_player.sendMessage("Your VIP status period is ended!");
		}
	}
}