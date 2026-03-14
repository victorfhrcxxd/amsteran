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

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;

/**
 * @author Anarchy
 *
 */
public class InstanceHolder
{
	private int _id;
	private List<L2DoorInstance> _doors;
	
	public InstanceHolder(int id)
	{
		_id = id;
		_doors = new ArrayList<>();
	}
	
	public void openDoors()
	{
		for (L2DoorInstance door : _doors)
			door.openMe();
	}
	
	public void closeDoors()
	{
		for (L2DoorInstance door : _doors)
			door.closeMe();
	}
	
	public void addDoor(L2DoorInstance door)
	{
		_doors.add(door);
	}
	
	public List<L2DoorInstance> getDoors()
	{
		return _doors;
	}
	
	public int getId()
	{
		return _id;
	}
}