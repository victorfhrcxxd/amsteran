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

import phantom.FakePlayer;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class L2PvPZone extends L2ZoneType
{
	private boolean _checkClasses;
	private static List<String> _classes = new ArrayList<>();
	private int dismountDelay = 5;
	
	public L2PvPZone(int id)
	{
		super(id);
		_checkClasses = false;
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("checkClasses"))
			_checkClasses = Boolean.parseBoolean(value);
		else if (name.equals("Classes"))
		{
			String[] propertySplit = value.split(",");
			for (String i : propertySplit)
				 _classes.add(i);
		}
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.ZONE_PVP, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;

			if (_checkClasses)
			{
				if (_classes != null && _classes.contains(""+ activeChar.getClassId().getId()))
				{
					activeChar.teleToLocation(83597, 147888, -3405, 0);
					activeChar.sendMessage("Your class is not allowed in this zone.");
					return;
				}
			}
			
			if (activeChar.getMountType() == 2)
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN));
				activeChar.enteredNoLanding(dismountDelay);
				activeChar.teleToLocation(83597, 147888, -3405, 0);
			}
		}
		
		// Auto Flag FakePlayers
		if (character instanceof FakePlayer)
		{
			final FakePlayer fakePlayer = (FakePlayer) character;
			
			fakePlayer.updatePvPStatus();
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.ZONE_PVP, false);
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