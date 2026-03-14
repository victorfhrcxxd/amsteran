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
package net.sf.l2j.gameserver.model;

import net.sf.l2j.commons.utils.ArraysUtil;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.type.CrystalType;

public class L2EnchantScroll
{
	private final CrystalType _grade;
	private final boolean _universalScrol;
	private final boolean _weapon;
	private final boolean _crystalize;
	private final int _breaks;
	private final int _decrease;
	
	private final float[] _chance;

	private final String _message;
	private final int[] _enchants;
	
	public L2EnchantScroll(CrystalType grade, boolean universalScrol, boolean weapon, boolean crystalize, int breaks, int decrease, float[] chance, String message, int[] enchants)
	{
		_grade = grade;
		_universalScrol = universalScrol;
		_weapon = weapon;
		_crystalize = crystalize;
		_breaks = breaks;
		_decrease = decrease;
		_chance = chance;
		_message = message;
		_enchants = enchants;
	}

	public final float getChance(ItemInstance enchantItem)
	{
		int level = enchantItem.getEnchantLevel();
		if (enchantItem.getItem().getBodyPart() == Item.SLOT_FULL_ARMOR && level != 0)
			level--;
		
		if (level >= _chance.length)
			return 0;
			
		return _chance[level];
	}

	public final boolean isCrystalize()
	{
		return _crystalize;
	}

	public final boolean isUniversal()
	{
		return _universalScrol;
	}

	public final int getBreak()
	{
		return _breaks;
	}

	public final int getDecrease()
	{
		return _decrease;
	}

	public final String getAnnounceMessage()
	{
		return _message;
	}
	
	public final int[] getAnnounceEnchants()
	{
		return _enchants;
	}
	
	public final boolean announceTheEnchant(ItemInstance item)
	{
		return item != null && _message != null && ArraysUtil.contains(_enchants, item.getEnchantLevel());
	}
	
	public final boolean isValid(ItemInstance enchantItem)
	{
		// check for crystal type
		if (_grade != enchantItem.getItem().getCrystalType() && !(_universalScrol && enchantItem.getItem().getCrystalType().getId() != 0))
			return false;
		
		// check enchant max level
		if (enchantItem.getEnchantLevel() >= _chance.length)
			return false;
		
		// checking scroll type
		switch (enchantItem.getItem().getType2())
		{
			case Item.TYPE2_WEAPON:
				if (!_weapon)
					return false;
				break;
			
			case Item.TYPE2_SHIELD_ARMOR:
			case Item.TYPE2_ACCESSORY:
				if (_weapon)
					return false;
				break;
			
			default:
				return false;
		}
		
		return true;
	}
}