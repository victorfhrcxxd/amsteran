package phantom.ai.shop;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.ai.FakePlayerAI;
import phantom.ai.FakePlayerUtilsAI;
import phantom.ai.shop.holder.FakePrivateBuyHolder;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import net.sf.l2j.util.Rnd;

public class PrivateStoreBuyAI extends FakePlayerAI
{
	public PrivateStoreBuyAI(FakePlayer character)
	{
		super(character);
		setup();
	}
	
	@Override
	public void setup()
	{
		_fakePlayer.setIsRunning(true);
	}
	
	@Override
	public void thinkAndAct()
	{
		setBusyThinking(true);
		
		getFake().addItem("Adena", 57, 1000000000, getFake(), false); 		
		for (FakePrivateBuyHolder buyItem : FakePlayerConfig.FAKE_PLAYER_PRIVATE_BUY_LIST)
		{			
			if (Rnd.get(100) <= buyItem.getListChance())
				getFake().getBuyList().addItemByItemId(buyItem.getBuyId(), Rnd.get(buyItem.getCountMin(), buyItem.getCountMax()), Rnd.get(buyItem.getPriceMin(), buyItem.getPriceMax()));
		}

		getFake().getBuyList().setTitle(FakePlayerUtilsAI.getRandomPrivateBuyTitle());
		getFake().sitDown();
		getFake().setPrivateStoreType(L2PcInstance.STORE_PRIVATE_BUY);
		getFake().broadcastUserInfo();
		getFake().broadcastPacket(new PrivateStoreMsgBuy(getFake()));
	}
	
	public FakePlayer getFake()
	{
		return _fakePlayer;
	}
}