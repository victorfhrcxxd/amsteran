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
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class L2PartyFarmZone extends L2SpawnZone
{
	public L2PartyFarmZone(int id)
	{
		super(id);
	}

	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.PARTY_FARM, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
			activeChar.updatePvPStatus();
			
			/*
			if (Config.LIMIT_HWID_ON_AREA && LimitHwidZone.getInstance().checkCount(activeChar)) 
			{
				ThreadPoolManager.getInstance().scheduleGeneral(new KickPlayer(activeChar), 1000);
				activeChar.sendPacket(new ExShowScreenMessage("Allowed only " + Config.MAX_BOX_ON_AREA + " same HwId Players on this area!", 5 * 1000));
			} 
			*/
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.PARTY_FARM, false);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
			PvpFlagTaskManager.getInstance().add(activeChar, 10000);
			
			// if (Config.LIMIT_HWID_ON_AREA)
			//	LimitHwidZone.getInstance().removeHwidCount(activeChar); 
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