package phantom.ai.event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.util.Rnd;

public class CaptureTheFlagAI 
{
	public static List<FakePlayer> _ctfFakes  = new CopyOnWriteArrayList<>();
	public static List<Location> _ctfFakelocs = new ArrayList<>();
	public static int _ctfFakesCount = Rnd.get(FakePlayerConfig.CTF_FAKE_PLAYER_COUNT_MIN, FakePlayerConfig.CTF_FAKE_PLAYER_COUNT_MAX);
	
	public static boolean spawnPhantoms()
	{
		_ctfFakelocs = FakePlayerConfig.CTF_FAKE_PLAYER_LIST_LOCS;

		try
		{
			Location loc = null; 
			
			for (int i = 0; i <_ctfFakesCount; i++)
			{
				loc = _ctfFakelocs.get(Rnd.get(_ctfFakelocs.size()));
				FakePlayer fakeSoloPlayer = FakePlayerManager.spawnEventPlayer(loc.getX() + Rnd.get(210), loc.getY() + Rnd.get(210), loc.getZ());
				fakeSoloPlayer.setFakeEvent(true);
				fakeSoloPlayer.assignDefaultAI();
				
				_ctfFakes.add(fakeSoloPlayer);
			}
			
			for (FakePlayer fakePlayer : _ctfFakes)
			{
				CTFEvent.addParticipant(fakePlayer);
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
			for (FakePlayer fakePlayer : _ctfFakes)
			{
				if (fakePlayer != null)
					fakePlayer.despawnPlayer();
			}
			_ctfFakes.clear();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}