package phantom.ai.shop;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.ai.FakePlayerAI;
import phantom.ai.FakePlayerUtilsAI;
import phantom.ai.shop.holder.FakePrivateSellHolder;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import net.sf.l2j.util.Rnd;

public class PrivateStoreSellAI extends FakePlayerAI
{
	public PrivateStoreSellAI(FakePlayer character)
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

		for (FakePrivateSellHolder sellItem : FakePlayerConfig.FAKE_PLAYER_PRIVATE_SELL_LIST)
		{
			if (Rnd.get(100) <= sellItem.getListChance())
				getFake().addItem("List", sellItem.getSellId(), Rnd.get(sellItem.getCountMin(), sellItem.getCountMax()), getFake(), false); 		

			for (ItemInstance itemDrop : getFake().getInventory().getItems())
			{
				if (itemDrop.isEquipped())
					continue;

				getFake().getSellList().addItem(itemDrop.getObjectId(), itemDrop.getCount(), itemDrop.getItem().getReferencePrice() * 2);
			}
		}
		
		getFake().getSellList().setTitle(FakePlayerUtilsAI.getRandomPrivateSellTitle());
		getFake().getSellList().setPackaged(L2PcInstance.STORE_PRIVATE_SELL == L2PcInstance.STORE_PRIVATE_PACKAGE_SELL);
		getFake().sitDown();
		getFake().setPrivateStoreType(L2PcInstance.STORE_PRIVATE_SELL);
		getFake().broadcastUserInfo();
		getFake().broadcastPacket(new PrivateStoreMsgSell(getFake()));
	}
	
	public FakePlayer getFake()
	{
		return _fakePlayer;
	}
}