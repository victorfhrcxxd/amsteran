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

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Party.MessageType;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2ZoneType;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

/**
 * The only zone where 'Build Headquarters' is allowed.
 * @author Tryskell, reverted version of Gnat's NoHqZone
 */
public class L2HideZone extends L2ZoneType
{
	public L2HideZone(final int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance activeChar = (L2PcInstance)character;
			
			activeChar.setInsideZone(ZoneId.HIDE, true);
			
			activeChar.updatePvPStatus();
			
			if (activeChar.isInParty())
				activeChar.getParty().removePartyMember(activeChar, MessageType.Left); 
		}
	}
	
	@Override
	protected void onExit(final L2Character character)
	{
		if (character instanceof L2PcInstance)
		{
			L2PcInstance activeChar = (L2PcInstance)character;
			
			activeChar.setInsideZone(ZoneId.HIDE, false);
			
			PvpFlagTaskManager.getInstance().add(activeChar, Config.PVP_NORMAL_TIME);

			// Set pvp flag
			if (activeChar.getPvpFlag() == 0)
				activeChar.updatePvPFlag(1);
		}
	}
	
	@Override
	public void onDieInside(final L2Character character)
	{
	}
	
	@Override
	public void onReviveInside(final L2Character character)
	{
	}
}