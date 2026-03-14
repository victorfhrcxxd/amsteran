package phantom.ai.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.util.Rnd;

public class LastManAI 
{
	public static List<FakePlayer> _lmFakes  = new CopyOnWriteArrayList<>();
	public static List<Location> _lmFakelocs = new ArrayList<>();
	public static int _lmFakesCount = Rnd.get(FakePlayerConfig.LM_FAKE_PLAYER_COUNT_MIN, FakePlayerConfig.LM_FAKE_PLAYER_COUNT_MAX);
	
	public static boolean spawnPhantoms()
	{
		_lmFakelocs = FakePlayerConfig.LM_FAKE_PLAYER_LIST_LOCS;

		try
		{
			Location loc = null; 
			
			for (int i = 0; i <_lmFakesCount; i++)
			{
				loc = _lmFakelocs.get(Rnd.get(_lmFakelocs.size()));
				FakePlayer fakeSoloPlayer = FakePlayerManager.spawnEventPlayer(loc.getX() + Rnd.get(210), loc.getY() + Rnd.get(210), loc.getZ());
				fakeSoloPlayer.setFakeEvent(true);
				fakeSoloPlayer.assignDefaultAI();
				
				_lmFakes.add(fakeSoloPlayer);
			}
			
			for (FakePlayer fakePlayer : _lmFakes)
			{
				LMEvent.addParticipant(fakePlayer);
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
			for (FakePlayer fakePlayer : _lmFakes)
			{
				if (fakePlayer != null)
					fakePlayer.despawnPlayer();
			}
			_lmFakes.clear();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}