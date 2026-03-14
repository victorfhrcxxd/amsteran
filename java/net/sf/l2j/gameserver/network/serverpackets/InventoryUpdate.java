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
package net.sf.l2j.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInfo;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;

/**
 * @author Advi
 */
public class InventoryUpdate extends L2GameServerPacket
{
	private List<ItemInfo> _items;
	
	public InventoryUpdate()
	{
		_items = new ArrayList<>();
	}
	
	public InventoryUpdate(List<ItemInfo> items)
	{
		_items = items;
	}
	
	public void addItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item));
	}
	
	public void addNewItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 1));
	}
	
	public void addModifiedItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 2));
	}
	
	public void addRemovedItem(ItemInstance item)
	{
		if (item != null)
			_items.add(new ItemInfo(item, 3));
	}
	
	public void addItems(List<ItemInstance> items)
	{
		if (items != null)
			for (ItemInstance item : items)
				if (item != null)
					_items.add(new ItemInfo(item));
	}
	
	private static boolean isBodypart(Item item)
	{
		if (item.getBodyPart() == Item.SLOT_ALLDRESS || item.getBodyPart() == Item.SLOT_HEAD || item.getBodyPart() == Item.SLOT_FULL_ARMOR || item.getBodyPart() == Item.SLOT_CHEST || item.getBodyPart() == Item.SLOT_LEGS || item.getBodyPart() == Item.SLOT_GLOVES || item.getBodyPart() == Item.SLOT_FEET)
			return true;

		return false;
	}
	
	private static boolean isHandpart(Item item)
	{
		if (item.getBodyPart() == Item.SLOT_R_HAND || item.getBodyPart() == Item.SLOT_LR_HAND)
			return true;

		return false;
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x27);
		writeH(_items.size());
		
		for (ItemInfo temp : _items)
		{
			if (temp == null || temp.getItem() == null)
				continue;
			
			L2PcInstance activeChar = L2World.getInstance().getPlayer(temp.getOwnerId());
			
			Item item = temp.getItem();
			
			writeH(temp.getChange());
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(item.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
			
			if (activeChar != null)
			{
				if (activeChar.getFakeArmorObjectId() > 0 || activeChar.getFakeWeaponObjectId() > 0)
				{
					if (temp.getObjectId() == activeChar.getFakeArmorObjectId() || temp.getObjectId() == activeChar.getFakeWeaponObjectId())
						writeH(0x01);
					else
						writeH(temp.getEquipped());

					if (temp.getObjectId() == activeChar.getFakeArmorObjectId())
						writeD(item.isFakeArmor() ? Item.SLOT_ALLDRESS : item.getBodyPart());
					else if (temp.getObjectId() == activeChar.getFakeWeaponObjectId() && item.getBodyPart() == Item.SLOT_R_HAND)
						writeD(item.isFakeWeapon() ? Item.SLOT_R_HAND : item.getBodyPart());
					else if (temp.getObjectId() == activeChar.getFakeWeaponObjectId() && item.getBodyPart() == Item.SLOT_LR_HAND)
						writeD(item.isFakeWeapon() ? Item.SLOT_LR_HAND : item.getBodyPart());
					else if (isBodypart(item) && temp.getEquipped() == 1 && activeChar.getFakeArmorObjectId() > 0 || isHandpart(item) && temp.getEquipped() == 1 && activeChar.getFakeWeaponObjectId() > 0)
						writeD(99);
					else
						writeD(item.getBodyPart());
				}
				else
				{
					writeH(temp.getEquipped());
					writeD(item.getBodyPart());
				}
			}
			else
			{
				writeH(temp.getEquipped());
				writeD(item.getBodyPart());
			}

			writeH(temp.getEnchant());
			writeH(temp.getCustomType2());
			writeD(temp.getAugmentationBoni());
			writeD(temp.getMana());
		}
		_items.clear();
		_items = null;
	}
}