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
package net.sf.l2j.gameserver.model.zone.type;

import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;

/**
 * An arena
 * @author durgus
 */
public class L2FarmInstanceZone extends L2SpawnZone
{
	public L2FarmInstanceZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.FARM_INSTANCE_ZONE, true);
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.FARM_INSTANCE_ZONE, false);
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