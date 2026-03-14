package net.sf.l2j.gameserver.taskmanager.tasks;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public class TaskHeroEnd implements Runnable
{
	private L2PcInstance _player;
	
	public TaskHeroEnd(L2PcInstance player)
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
			_player.setHero(false);
			_player.setTimedHero(false);
			_player.setTimedHeroEndTime(0);

			_player.sendPacket(new ExShowScreenMessage("Your heroic status period is over!", 5 * 1000));
			_player.sendMessage("Your heroic status period is over!");
			_player.getInventory().destroyItemByItemId("Wing", 6842, 1, _player, null);
			_player.store();
			
			_player.broadcastUserInfo();
			_player.sendPacket(new EtcStatusUpdate(_player));
		}
	}
}