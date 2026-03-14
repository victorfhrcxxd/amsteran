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
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.util.Rnd;

public class LuckBag implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		/*
		if (activeChar.getInventory().getItemByItemId(9594) != null || activeChar.getInventory().getItemByItemId(9595) != null || activeChar.getInventory().getItemByItemId(9596) != null)
		{
			activeChar.sendMessage("You can open a bag when your current rune runs out of time.");
			return;
		}
		*/
		
		if (Rnd.get(100) < 40)
		{
    		activeChar.addItem("Reward", 9500, Rnd.get(5000, 20000), null, true);
    		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

    		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
    		activeChar.broadcastPacket(MSU);
		}
		else if (Rnd.get(100) < 30)
		{
    		activeChar.addItem("Reward", 6622, 1, null, true);
    		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

    		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
    		activeChar.broadcastPacket(MSU);
		}
		else if (Rnd.get(100) < 30)
		{
    		activeChar.addItem("Reward", 8762, 1, null, true);
    		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

    		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
    		activeChar.broadcastPacket(MSU);
		}
		else if (Rnd.get(100) < 25)
		{
    		activeChar.addItem("Reward", 6577, 1, null, true);
    		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

    		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
    		activeChar.broadcastPacket(MSU);
		}
		else if (Rnd.get(100) < 25)
		{
    		activeChar.addItem("Reward", 6578, 1, null, true);
    		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

    		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
    		activeChar.broadcastPacket(MSU);
		}
		else if (Rnd.get(100) < 25)
		{
    		activeChar.addItem("Reward", 3470, 1, null, true);
    		activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

    		MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
    		activeChar.broadcastPacket(MSU);
		}
	}
}