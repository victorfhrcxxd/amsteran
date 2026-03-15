package phantom.ai.event;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import phantom.FakePlayer;
import phantom.FakePlayerManager;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmConfig;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmManager;
import net.sf.l2j.util.Rnd;

public class ChaoticFarmController
{
	private static final List<FakePlayer> _cfFakes = new CopyOnWriteArrayList<>();
	private static volatile boolean _requeueEnabled = false;

	public static void spawnAndEnqueue(int count)
	{
		_requeueEnabled = true;
		final int[] exitLoc = ChaoticFarmConfig.CF_EXIT_LOC;
		for (int i = 0; i < count; i++)
		{
			final FakePlayer fp = FakePlayerManager.spawnPlayer(
				exitLoc[0] + Rnd.get(-100, 100),
				exitLoc[1] + Rnd.get(-100, 100),
				exitLoc[2]);
			fp.setFakeChaoticFarm(true);
			fp.setFakeAi(new ChaoticFarmAI(fp));
			_cfFakes.add(fp);
			ChaoticFarmManager.getInstance().requestJoin(fp);
		}
	}

	public static void despawnAll()
	{
		_requeueEnabled = false;
		for (FakePlayer fp : _cfFakes)
		{
			if (fp != null)
			{
				ChaoticFarmManager.getInstance().onPlayerDisconnect(fp);
				fp.despawnPlayer();
			}
		}
		_cfFakes.clear();
	}

	public static void scheduleRequeue(FakePlayer fp)
	{
		if (!_requeueEnabled)
			return;

		final long delayMs = ChaoticFarmConfig.CF_FAKE_REQUEUE_DELAY_SECONDS * 1000L;
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			if (!_requeueEnabled)
				return;
			if (!fp.isFakeChaoticFarm())
				return;
			if (!fp.isOnline())
				return;
			if (ChaoticFarmManager.getInstance().isInRoom(fp))
				return;
			if (ChaoticFarmManager.getInstance().isInQueue(fp))
				return;
			ChaoticFarmManager.getInstance().requestJoin(fp);
		}, delayMs);
	}

	public static List<FakePlayer> getFakes()
	{
		return Collections.unmodifiableList(_cfFakes);
	}

	public static int getCount()
	{
		return _cfFakes.size();
	}
}
