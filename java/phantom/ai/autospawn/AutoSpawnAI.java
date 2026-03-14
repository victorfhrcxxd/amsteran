package phantom.ai.autospawn;

import java.util.ArrayList;
import java.util.List;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import phantom.ai.walker.CitizenAI;
import phantom.task.ThreadPool;

import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.util.Rnd;

public class AutoSpawnAI 
{
	public static List<Location> locs = new ArrayList<>();
	public static int fakesCount = Rnd.get(FakePlayerConfig.AUTO_SPAWN_FAKE_COUNT_MIN, FakePlayerConfig.AUTO_SPAWN_FAKE_COUNT_MAX);
	
	private AutoSpawnAI()
	{
		if (FakePlayerConfig.ALLOW_FAKE_PLAYER_AUTO_SPAWN)
			ThreadPool.schedule(new spawnPhantoms(), 1 * 60 * 1000);
	}

	private class spawnPhantoms implements Runnable
	{
		@Override
		public void run()
		{
			loadData();
		}
	}

	public void loadData()
	{
		locs = FakePlayerConfig.FAKE_AUTO_SPAWN_LIST_LOCS;

		try
		{
			Location loc = null; 
			for (int i = 0; i < fakesCount; i++)
			{
				loc = locs.get(Rnd.get(locs.size() - 1));
			    FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPlayer(loc.getX() + Rnd.get(250), loc.getY() + Rnd.get(250), loc.getZ());
			    fakeSoloPlayer.setFakeAi(new CitizenAI(fakeSoloPlayer));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static AutoSpawnAI getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder
	{
		protected static final AutoSpawnAI _instance = new AutoSpawnAI();
	}
}