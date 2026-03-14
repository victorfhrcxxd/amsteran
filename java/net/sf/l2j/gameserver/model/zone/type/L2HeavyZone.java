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
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public class L2HeavyZone extends L2SpawnZone
{
	private boolean _enableScreenMessage;
	private String _screenMessageOnEnter;
	
	public L2HeavyZone(int id)
	{
		super(id);
		
		_enableScreenMessage = false;
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("enableScreenMessage"))
			_enableScreenMessage = Boolean.parseBoolean(value);
		else if (name.equals("screenMessageOnEnter"))
			_screenMessageOnEnter = value;
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.HEAVY_FARM_AREA, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
	
			if (_enableScreenMessage)
				activeChar.sendPacket(new ExShowScreenMessage(_screenMessageOnEnter, 4000));
			
			activeChar.broadcastUserInfo();
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.HEAVY_FARM_AREA, false);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;

			activeChar.broadcastUserInfo();
		}
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