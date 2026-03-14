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
 * this program. If not, see <http://eternity-world.ru/>.
 */
package net.sf.l2j.gameserver.model.actor.task;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.actor.instance.L2AgathionInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class AgathionHpTask implements Runnable
{
	private static final Logger _log = Logger.getLogger(AgathionHpTask.class.getName());

	protected final L2AgathionInstance _agathion;
	protected final L2PcInstance _player;
	protected final int _resHp;

	public AgathionHpTask(L2AgathionInstance agathion, L2PcInstance player, int reshp)
	{
		_agathion = agathion;
		_player = player;
		_resHp = reshp;
	}

	@Override
	public void run()
	{
		if (_player != null)
		{
			try
			{
				if (!_player.isInOlympiadMode() || !_player.isAlikeDead() || !_player.isDead())
				{
					if (_player.getCurrentHp() < _player.getMaxHp())
					{
						_player.setCurrentHp(_resHp + _player.getCurrentHp());
						StatusUpdate su = new StatusUpdate(_player);
						su.addAttribute(StatusUpdate.CUR_HP, (int) _player.getCurrentHp());
						_player.sendPacket(su);

						_player.broadcastPacket(new MagicSkillUse(_player, _player, 2037, 1, 5, 0));
						_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HP_RESTORED).addNumber(_resHp));
					}
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Agathion [NpcId: " + _agathion.getNpcId() + "] a hp task error has occurred", e);
			}
		}
	}
}