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
package net.sf.l2j.gameserver.handler.itemhandlers.custom.special;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.Experience;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public class LevelItem implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;
		
		if (activeChar.isAllSkillsDisabled())
		{
			activeChar.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		byte lvl = 81;
		if (lvl >= 1 && lvl <= Experience.MAX_LEVEL)
		{
			long pXp = activeChar.getExp();
			long tXp = Experience.LEVEL[lvl];
			
			if (pXp > tXp)
				activeChar.removeExpAndSp(pXp - tXp, 0);
			else if (pXp < tXp)
				activeChar.addExpAndSp(tXp - pXp, 0);
		}
	}
}