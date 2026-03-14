package phantom.ai.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.util.Rnd;

public class KillTheBossAI 
{
	public static List<FakePlayer> _ktbFakes  = new CopyOnWriteArrayList<>();
	public static List<Location> _ktbFakelocs = new ArrayList<>();
	public static int _ktbFakesCount = Rnd.get(FakePlayerConfig.KTB_FAKE_PLAYER_COUNT_MIN, FakePlayerConfig.KTB_FAKE_PLAYER_COUNT_MAX);
	
	public static boolean spawnPhantoms()
	{
		_ktbFakelocs = FakePlayerConfig.KTB_FAKE_PLAYER_LIST_LOCS;

		try
		{
			Location loc = null; 
			
			for (int i = 0; i <_ktbFakesCount; i++)
			{
				loc = _ktbFakelocs.get(Rnd.get(_ktbFakelocs.size()));
				FakePlayer fakeSoloPlayer = FakePlayerManager.spawnEventPlayer(loc.getX() + Rnd.get(210), loc.getY() + Rnd.get(210), loc.getZ());
				fakeSoloPlayer.setFakeKTBEvent(true);
				fakeSoloPlayer.assignDefaultAI();
				
				_ktbFakes.add(fakeSoloPlayer);
			}
			
			for (FakePlayer fakePlayer : _ktbFakes)
			{
				KTBEvent.addParticipant(fakePlayer);
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
			for (FakePlayer fakePlayer : _ktbFakes)
			{
				if (fakePlayer != null)
					fakePlayer.despawnPlayer();
			}
			_ktbFakes.clear();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}