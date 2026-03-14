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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SiegeInfo;

public class L2SiegeCustomNpcInstance extends L2NpcInstance
{
	public L2SiegeCustomNpcInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		int castleId = 0;

		if (getNpcId() == 50200)
			castleId = 1; // Gludio

		if (getNpcId() == 50201)
			castleId = 2; // Dion

		if (getNpcId() == 50202)
			castleId = 3; // Giran

		if (getNpcId() == 50203)
			castleId = 4; // Oren

		if (getNpcId() == 50204)
			castleId = 5; // Aden

		if (getNpcId() == 50205)
			castleId = 6; // Innadril

		if (getNpcId() == 50206)
			castleId = 7; // Goddard

		if (getNpcId() == 50207)
			castleId = 8; // Rune

		if (getNpcId() == 50208)
			castleId = 9; // Schuttgart

		Castle castle = CastleManager.getInstance().getCastleById(castleId);
		if (castle != null && castleId != 0)
		{
			if (castle.getSiege().isInProgress())
			{
				NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
				html.setFile("data/html/siege/" + getNpcId() + "-busy.htm");
				html.replace("%castlename%", getCastle().getName());
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			else
				player.sendPacket(new SiegeInfo(castle));
		}
	}
}