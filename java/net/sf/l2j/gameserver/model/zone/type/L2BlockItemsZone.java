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
 * this program. If not, see <[url="http://www.gnu.org/licenses/>."]http://www.gnu.org/licenses/>.[/url]
 */
package net.sf.l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class L2BlockItemsZone extends L2SpawnZone
{
	private static List<String> _items = new ArrayList<>();
	
	public L2BlockItemsZone(int id)
	{
		super(id);
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("itemsId"))
		{
			String[] propertySplit = value.split(",");
			for (String i : propertySplit)
				_items.add(i);
		}
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.BLOCK_ITEM, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;

			for (ItemInstance item : activeChar.getInventory()._items)
			{
				if (item.isEquipable() && item.isEquipped() && !checkItem(item))
				{
					int slot = activeChar.getInventory().getSlotFromItem(item);
					activeChar.getInventory().unEquipItemInBodySlotAndRecord(slot);
					activeChar.sendMessage(item.getItemName() + " unequiped because is not allowed inside this zone.");
				}
			}
		}
	}

	public static boolean checkItem (ItemInstance item)
	{
		if (_items != null && _items.contains(""+item.getItemId()))
			return false;
		
		return true;
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.BLOCK_ITEM, false);
	}

	@Override
	public void onDieInside(L2Character character)
	{

	}

	@Override
	public void onReviveInside(L2Character character)
	{
		
	}
}