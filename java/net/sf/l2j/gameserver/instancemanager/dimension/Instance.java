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
package net.sf.l2j.gameserver.instancemanager.dimension;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.actor.instance.L2DoorInstance;

public class Instance
{
	private int id;
	private List<L2DoorInstance> doors;
	
	public Instance(int id)
	{
		this.id = id;
		doors = new ArrayList<>();
	}
	
	public void openDoors()
	{
		for (L2DoorInstance door : doors)
			door.openMe();
	}
	
	public void closeDoors()
	{
		for (L2DoorInstance door : doors)
			door.closeMe();
	}
	
	public void addDoor(L2DoorInstance door)
	{
		doors.add(door);
	}
	
	public List<L2DoorInstance> getDoors()
	{
		return doors;
	}
	
	public int getId()
	{
		return id;
	}
}