/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.handler.community.dailyreward.bypass.DailyRewardCBBypasses;
import net.sf.l2j.gameserver.handler.community.marketplace.MarketplaceCBBypasses;
import net.sf.l2j.gameserver.handler.community.raidinfo.RaidInfoCBBypasses;
import net.sf.l2j.gameserver.handler.community.ranking.RankingCBBypasses;

/**
 * @author Anarchy
 */
public class CBBypassHandler
{
	private final Map<Integer, ICBBypassHandler> _datatable = new HashMap<>();

	public static CBBypassHandler getInstance()
	{
		return SingletonHolder._instance;
	}

	private CBBypassHandler()
	{
		registerBypassHandler(new DailyRewardCBBypasses());
		registerBypassHandler(new RaidInfoCBBypasses());
		registerBypassHandler(new RankingCBBypasses());
		registerBypassHandler(new MarketplaceCBBypasses());
	}

	public void registerBypassHandler(ICBBypassHandler handler)
	{
		String[] ids = handler.getBypassHandlersList();
		for (int i = 0; i < ids.length; i++)
		{
			_datatable.put(ids[i].hashCode(), handler);
		}
	}

	public ICBBypassHandler getBypassHandler(String bypass)
	{
		String command = bypass;

		if (bypass.indexOf(" ") != -1)
			command = bypass.substring(0, bypass.indexOf(" "));

		return _datatable.get(command.hashCode());
	}

	public int size()
	{
		return _datatable.size();
	}

	private static class SingletonHolder
	{
		protected static final CBBypassHandler _instance = new CBBypassHandler();
	}
}