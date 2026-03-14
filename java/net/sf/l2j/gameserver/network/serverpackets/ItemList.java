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

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;

public class ItemList extends L2GameServerPacket
{
	private final ItemInstance[] _items;
	private final boolean _showWindow;
	private final L2PcInstance _activeChar;
	
	public ItemList(L2PcInstance cha, boolean showWindow)
	{
		_activeChar = cha;
		_items = cha.getInventory().getItems();
		_showWindow = showWindow;
	}
	
	public ItemList(ItemInstance[] items, L2PcInstance cha, boolean showWindow)
	{
		_activeChar = cha;
		_items = items;
		_showWindow = showWindow;
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
		writeC(0x1b);
		writeH(_showWindow ? 0x01 : 0x00);
		writeH(_items.length);
		
		for (ItemInstance temp : _items)
		{
			if (temp == null || temp.getItem() == null)
				continue;
			
			Item item = temp.getItem();
			
			writeH(item.getType1());
			writeD(temp.getObjectId());
			writeD(temp.getItemId());
			writeD(temp.getCount());
			writeH(item.getType2());
			writeH(temp.getCustomType1());
						
			if (_activeChar.getFakeArmorObjectId() > 0 || _activeChar.getFakeWeaponObjectId() > 0)
			{
				if (temp.getObjectId() == _activeChar.getFakeArmorObjectId() || temp.getObjectId() == _activeChar.getFakeWeaponObjectId())
					writeH(0x01);
				else
					writeH(temp.isEquipped() ? 0x01 : 0x00);

				if (temp.getObjectId() == _activeChar.getFakeArmorObjectId())
					writeD(item.isFakeArmor() ? Item.SLOT_ALLDRESS : item.getBodyPart());
				else if (temp.getObjectId() == _activeChar.getFakeWeaponObjectId() && item.getBodyPart() == Item.SLOT_R_HAND)
					writeD(item.isFakeWeapon() ? Item.SLOT_R_HAND : item.getBodyPart());
				else if (temp.getObjectId() == _activeChar.getFakeWeaponObjectId() && item.getBodyPart() == Item.SLOT_LR_HAND)
					writeD(item.isFakeWeapon() ? Item.SLOT_LR_HAND : item.getBodyPart());
				else if (isBodypart(item) && temp.isEquipped() && _activeChar.getFakeArmorObjectId() > 0 || isHandpart(item) && temp.isEquipped() && _activeChar.getFakeWeaponObjectId() > 0)
					writeD(99);
				else
					writeD(item.getBodyPart());
			}
			else
			{
				writeH(temp.isEquipped() ? 0x01 : 0x00);
				writeD(item.getBodyPart());
			}
			
			writeH(temp.getEnchantLevel());
			writeH(temp.getCustomType2());
			writeD((temp.isAugmented()) ? temp.getAugmentation().getAugmentationId() : 0x00);
			writeD(temp.getMana());
		}
	}
}