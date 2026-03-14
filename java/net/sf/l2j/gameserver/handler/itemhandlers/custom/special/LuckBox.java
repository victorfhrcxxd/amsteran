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

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.util.Rnd;

public class LuckBox implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		switch (Rnd.get(5))
		{
		    case 0:
		    {
			    activeChar.sendMessage("Your box is empty.");   
			    playable.destroyItem("Consume", item.getObjectId(), 1, null, true);
			    MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
			    activeChar.broadcastPacket(MSU);
			    break;
		    }
		    case 1:
		    {
			    activeChar.sendMessage("Your box is empty.");   
			    playable.destroyItem("Consume", item.getObjectId(), 1, null, true);
			    MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
			    activeChar.broadcastPacket(MSU);
			    break;
		    }
		    case 2:
		    {
			    activeChar.sendMessage("Your box is empty.");   
			    playable.destroyItem("Consume", item.getObjectId(), 1, null, true);
			    MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
			    activeChar.broadcastPacket(MSU);
			    break;
		    }
		    case 3:
		    {
			    activeChar.sendMessage("Your box is empty.");   
			    playable.destroyItem("Consume", item.getObjectId(), 1, null, true);
			    MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
			    activeChar.broadcastPacket(MSU);
			    break;
		    }
		    case 4:
		    {
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.LUCK_BOX_REWARDS.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	activeChar.addItem("Reward", ids.get(rnd), Config.LUCK_BOX_REWARDS.get(ids.get(rnd)), null, true);
		    	activeChar.destroyItem("Consume", item.getObjectId(), 1, null, true);

		    	MagicSkillUse MSU = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
		    	activeChar.broadcastPacket(MSU);
		    	break;
		    }
		}
	}
}