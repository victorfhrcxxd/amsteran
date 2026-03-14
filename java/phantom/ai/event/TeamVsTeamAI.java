package phantom.ai.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.util.Rnd;

public class TeamVsTeamAI 
{
	public static List<FakePlayer> _tvtFakes  = new CopyOnWriteArrayList<>();
	public static List<Location> _tvtFakelocs = new ArrayList<>();
	public static int _tvtFakesCount = Rnd.get(FakePlayerConfig.TVT_FAKE_PLAYER_COUNT_MIN, FakePlayerConfig.TVT_FAKE_PLAYER_COUNT_MAX);
	
	public static boolean spawnPhantoms()
	{
		_tvtFakelocs = FakePlayerConfig.TVT_FAKE_PLAYER_LIST_LOCS;

		try
		{
			Location loc = null; 
			
			for (int i = 0; i <_tvtFakesCount; i++)
			{
				loc = _tvtFakelocs.get(Rnd.get(_tvtFakelocs.size()));
				FakePlayer fakeSoloPlayer = FakePlayerManager.spawnEventPlayer(loc.getX() + Rnd.get(210), loc.getY() + Rnd.get(210), loc.getZ());
				fakeSoloPlayer.setFakeEvent(true);
				fakeSoloPlayer.assignDefaultAI();
				
				_tvtFakes.add(fakeSoloPlayer);
			}
			
			for (FakePlayer fakePlayer : _tvtFakes)
			{
				TvTEvent.addParticipant(fakePlayer);
			}
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean unspawnPhantoms()
	{
		try
		{
			for (FakePlayer fakePlayer : _tvtFakes)
			{
				if (fakePlayer != null)
					fakePlayer.despawnPlayer();
			}
			_tvtFakes.clear();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}