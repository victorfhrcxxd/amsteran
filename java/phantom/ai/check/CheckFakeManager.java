/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package phantom.ai.check;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import phantom.task.ThreadPool;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class CheckFakeManager
{
	private CheckFakeManager()
	{
		if (FakePlayerConfig.CHECK_FAKE_PLAYERS_AREA)
			ThreadPool.scheduleAtFixedRate(new CheckFakeTask(), FakePlayerConfig.CHECK_FAKE_PLAYERS_START_TIME * 60 * 1000, FakePlayerConfig.CHECK_FAKE_PLAYERS_RESTART_TIME * 60 * 1000);
	}

	private class CheckFakeTask implements Runnable
	{
		@Override
		public void run()
		{
			loadData();
		}
	}

	public void loadData()
	{
		for (FakePlayer fakePlayer : FakePlayerManager.getFakePlayers())
		{
			if (fakePlayer.isFakeFarm() && fakePlayer.isInsideZone(ZoneId.ZONE_PVP))
				fakePlayer.teleToLocation(fakePlayer.getLastX(), fakePlayer.getLastY(), fakePlayer.getLastZ(), 0);

			if (fakePlayer.isFakePvp() && !fakePlayer.isInsideZone(ZoneId.ZONE_PVP))
				fakePlayer.teleToLocation(fakePlayer.getLastX(), fakePlayer.getLastY(), fakePlayer.getLastZ(), 0);
		}
	}

	public static CheckFakeManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final CheckFakeManager _instance = new CheckFakeManager();
	}
}