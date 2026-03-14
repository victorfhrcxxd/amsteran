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
package net.sf.l2j.gameserver.model.holder;

/**
 * Holder for item id-count.
 * @author UnAfraid
 */
public class ItemHolder
{
	private int _id;
	private int _count;
	private int _enchantMin = 0;
	private int _enchantMax = 0;
	private int _enchantSuccess = 0;
	
	public ItemHolder(int id, int count)
	{
		_id = id;
		_count = count;
	}

	public ItemHolder(int id, int value, int enchantMin, int enchantMax, int enchantSuc) 
	{
		_id = id;
		_count = value;
		_enchantMin = enchantMin;
		_enchantMax = enchantMax;
		_enchantSuccess = enchantSuc;
	}
	 
	/**
	 * @return the item/object identifier.
	 */
	public int getId()
	{
		return _id;
	}
	
	/**
	 * @return the item count.
	 */
	public int getCount()
	{
		return _count;
	}
	
	public int getMinEnchant()
	{
		return _enchantMin;
	}
	
	public int getMaxEnchant()
	{
		return _enchantMax;
	}

	public int getEnchantSuccess() 
	{
		return _enchantSuccess;
	}
	
	/**
	 * @param id : The new value to set.
	 */
	public void setId(int id)
	{
		_id = id;
	}
	
	/**
	 * @param count : The new value to set.
	 */
	public void setCount(int count)
	{
		_count = count;
	}

	public void setMinEnchant(int enchant)
	{
		_enchantMin = enchant;
	}

	public void setMaxEnchant(int enchant)
	{
		_enchantMax = enchant;
	}

	public void SetEnchantSuccess(int enchantSuc) 
	{
		_enchantSuccess = enchantSuc;
	}
	 
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": Id: " + _id + " Count: " + _count;
	}
}