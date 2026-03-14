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

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2AgathionInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class AgathionSkillTask implements Runnable
{
	private static final Logger _log = Logger.getLogger(AgathionHpTask.class.getName());
	
	protected final L2AgathionInstance _agathion;
	protected final L2PcInstance _player;
	protected final int _skill;
	
	public AgathionSkillTask(L2AgathionInstance agathion, L2PcInstance player, int skill)
	{
		_agathion = agathion;
		_player = player;
		_skill = skill;
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
					L2Skill skill = SkillTable.getInstance().getInfo(_skill, 1);
					if (skill != null)
					{
						_agathion.abortCast();
						_agathion.setTarget(_player);
						_agathion.doCast(skill);
						_player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.PET_USES_S1).addSkillName(skill));
					}
				}
			}
			catch (Exception e)
			{
				_log.log(Level.SEVERE, "Agathion [NpcId: " + _agathion.getNpcId() + "] a skill task error has occurred", e);
			}
		}
	}
}