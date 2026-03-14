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

import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class L2ZakenZone extends L2ZoneType
{
	public L2ZakenZone(int id)
	{
		super(id);
	}

	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.ZAKEN, true);
	}

	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.ZAKEN, false);
	}

	public void onDieInside(L2Character character)
	{
	}

	public void onReviveInside(L2Character character)
	{
	}
}
