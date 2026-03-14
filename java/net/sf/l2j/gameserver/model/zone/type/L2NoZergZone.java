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

import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedZerg;
import net.sf.l2j.gameserver.instancemanager.custom.ZergManager;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.L2SpawnZone;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public class L2NoZergZone extends L2SpawnZone
{
	private int _maxClanMembers;
	private int _maxAllyMembers;
	private int _minPartyMembers;
	private boolean _showRules;
	private boolean _checkParty;
	private boolean _checkClan;
	private boolean _checkAlly;
	
	public L2NoZergZone(int id)
	{
		super(id);
		
		_maxClanMembers = 0;
		_maxAllyMembers = 0;
		_minPartyMembers = 0;
		_showRules = false;
		_checkParty = false;
		_checkClan = false;
		_checkAlly = false;
	}

	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("MaxClanMembers"))
			_maxClanMembers = Integer.parseInt(value);
		else if (name.equals("MaxAllyMembers"))
			_maxAllyMembers = Integer.parseInt(value);
		else if (name.equals("MinPartyMembers"))
			_minPartyMembers = Integer.parseInt(value);
		else if (name.equals("showRules"))
			_showRules = Boolean.parseBoolean(value);
		else if (name.equals("checkParty"))
			_checkParty = Boolean.parseBoolean(value);
		else if (name.equals("checkClan"))
			_checkClan = Boolean.parseBoolean(value);
		else if (name.equals("checkAlly"))
			_checkAlly = Boolean.parseBoolean(value);
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		character.setInsideZone(ZoneId.NO_ZERG, true);
		
		if (character instanceof L2PcInstance)
		{
			final L2PcInstance activeChar = (L2PcInstance) character;

			if (_checkParty)
			{
				if (!activeChar.isInParty() || activeChar.getParty().getMemberCount() < _minPartyMembers)
				{
					activeChar.sendPacket(new ExShowScreenMessage("Your party does not have " + _minPartyMembers + " members to enter on this zone!", 6 * 1000));
					activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);
				}
			}
			
			if (_showRules)
				VoicedZerg.showZergHtml(activeChar, 0);

			if (_checkClan)
				MaxClanMembersOnArea(activeChar);

			if (_checkAlly)
				MaxAllyMembersOnArea(activeChar);

			if (_checkParty)
				checkPartyMembers(activeChar); 
		}
	}
	
	public boolean MaxClanMembersOnArea(L2PcInstance activeChar)
	{
		return ZergManager.getInstance().checkClanArea(activeChar, _maxClanMembers, true);
	}
	
	public boolean MaxAllyMembersOnArea(L2PcInstance activeChar)
	{
		return ZergManager.getInstance().checkAllyArea(activeChar, _maxAllyMembers, L2World.getInstance().getAllPlayers().values(), true);
	}
	
	public void checkPartyMembers(L2PcInstance player) 
	{
		L2Party party = player.getParty();
		
		if (party == null)
			return; 
		
		for (L2PcInstance member : party.getPartyMembers()) 
		{
			if (member == null)
				continue;
			
			if (!member.isOnline())
				continue;
			
			if (member.getClan() != player.getClan() && member.getClan().getAllyId() != player.getClan().getAllyId())
			{
				player.sendMessage("Only clan member party are allowed.");
				player.getParty().removePartyMember(player, L2Party.MessageType.Left);
				break;
			}
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		character.setInsideZone(ZoneId.NO_ZERG, false);
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