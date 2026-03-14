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
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class L2ChaoticSelfZone extends L2SpawnZone
{
	private boolean _enableScreenMessage;
	private String _screenMessageOnEnter;
	private int dismountDelay = 5;
	
	public L2ChaoticSelfZone(int id)
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
		character.setInsideZone(ZoneId.FLAG_AREA_SELF, true);

		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
			
			if (_enableScreenMessage)
				activeChar.sendPacket(new ExShowScreenMessage(_screenMessageOnEnter, 4000));

			if (Config.ENABLE_CHAOTIC_COLOR_NAME)
			{
				activeChar.setVisibleNameColor(Integer.decode("0x" + Config.CHAOTIC_COLOR_NAME));
				activeChar.broadcastUserInfo();
			}

			if (activeChar.getMountType() == 2)
			{
				activeChar.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_WYVERN));
				activeChar.enteredNoLanding(dismountDelay);
				activeChar.teleToLocation(83597, 147888, -3405, 0);
			}
			
			if (!activeChar.inObserverMode())
			{
				if (activeChar.getPvpFlag() > 0)
					PvpFlagTaskManager.getInstance().remove(activeChar);
				
				activeChar.updatePvPFlag(1);
				
				if (!activeChar.isGM())
					activeChar.getAppearance().setVisible();
			}
		}
	}

	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.FLAG_AREA_SELF, false);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;
			
			if (Config.ENABLE_CHAOTIC_COLOR_NAME)
			{
				activeChar.setVisibleNameColor(activeChar.getAppearance().getNameColor());
				activeChar.broadcastUserInfo();
			}
			
			if (!activeChar.inObserverMode())
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