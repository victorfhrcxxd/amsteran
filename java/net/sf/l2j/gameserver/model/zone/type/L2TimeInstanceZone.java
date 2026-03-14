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

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.instancemanager.custom.TimeInstanceManager;
import net.sf.l2j.gameserver.model.L2Party.MessageType;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.StopMove;
import net.sf.l2j.gameserver.taskmanager.TimeInstanceRemainTaskManager;
import net.sf.l2j.gameserver.taskmanager.TimeInstanceTeleportTaskManager;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class L2TimeInstanceZone extends L2SpawnZone
{	
	public L2TimeInstanceZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.TIME_INSTANCE_ZONE, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
			
			if (Config.TIME_INSTANCE_BLOCK_CLASS_LIST.contains(activeChar.getClassId().getId()))
			{
				activeChar.sendMessage("Your class is not allowed in Time Instance Zone.");
				activeChar.teleToLocation(81337, 148093, -3473, 0);
				return;
			}
			
			if (!TimeInstanceManager.checkPlayerTime(activeChar))
			{
				activeChar.setTimeInstanceAvaiable(false);
				activeChar.setIsInTimeInstance(false);
				activeChar.sendMessage("Your time in the Time Instance Zone has expired.");
				
				if (Config.TIME_INSTANCE_SCREEN_MESSAGE)
					activeChar.sendPacket(new ExShowScreenMessage("Your time in the Time Instance Zone has expired.", 6000));
				
				activeChar.setTimeInstanceMobs(0);
				
				activeChar.startAbnormalEffect(0x0800);
				activeChar.setIsParalyzed(true);
				activeChar.startParalyze();				
				activeChar.broadcastPacket(new StopMove(activeChar));
				
				new TimeInstanceTeleportTaskManager(activeChar);
			}
			else
			{
				activeChar.setIsInTimeInstance(true);
				activeChar.sendMessage("You have entered the Time Instance Zone.");
				
				if (Config.TIME_INSTANCE_SCREEN_MESSAGE)
					activeChar.sendPacket(new ExShowScreenMessage("You have " + TimeInstanceManager.getPlayerTime(activeChar) + " minutes left in Time Instance area.", 6000));
				
				if (!Config.TIME_INSTANCE_ALLOW_PARTY && activeChar.isInParty())
					activeChar.getParty().removePartyMember(activeChar, MessageType.Expelled);
				
				if (Config.TIME_INSTANCE_FLAG_ZONE)
				{
					if (activeChar.getPvpFlag() > 0)
						PvpFlagTaskManager.getInstance().remove(activeChar);
					
					activeChar.updatePvPFlag(1);
				}
				
				new TimeInstanceRemainTaskManager(activeChar);
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.TIME_INSTANCE_ZONE, false);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
			
			activeChar.setIsInTimeInstance(false);
			activeChar.sendMessage("You left the Time Instance Zone.");
			
			if (Config.TIME_INSTANCE_FLAG_ZONE)
				PvpFlagTaskManager.getInstance().add(activeChar, 20000);
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