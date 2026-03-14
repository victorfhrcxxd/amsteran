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
package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.handler.community.marketplace.MarketplaceCBBypasses;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;

public final class RequestShowBoard extends L2GameClientPacket
{
	@SuppressWarnings("unused")
	private int _unknown;

	@Override
	protected void readImpl()
	{
		_unknown = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
			return;

		MarketplaceCBBypasses.showMarketBoard(activeChar, 1, "*null*");        
	}
	
	/*
	private static void showMenuHtml(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/Menu.htm"); 
		html.replace("%partyRefusal%", activeChar.isPartyInRefuse() ? ACTIVED : DESATIVED);
		html.replace("%tradeRefusal%", activeChar.getTradeRefusal() ? ACTIVED : DESATIVED);
		html.replace("%messageRefusal%", activeChar.isInRefusalMode() ? ACTIVED : DESATIVED);    
		html.replace("%hideHeroAura%", activeChar.isDisableHeroAura() ? ACTIVED : DESATIVED);    
		html.replace("%hideEnchantGlow%", activeChar.isDisableGlowWeapon() ? ACTIVED : DESATIVED);    
		html.replace("%server_restarted%", String.valueOf(GameServer.dateTimeServerStarted.getTime()));
		html.replace("%server_os%", String.valueOf(System.getProperty("os.name")));
		html.replace("%server_free_mem%", String.valueOf((Runtime.getRuntime().maxMemory()-Runtime.getRuntime().totalMemory()+Runtime.getRuntime().freeMemory()) / 1048576));
		html.replace("%server_total_mem%", String.valueOf(Runtime.getRuntime().totalMemory() / 1048576));
		activeChar.sendPacket(html);
	}
	*/
}