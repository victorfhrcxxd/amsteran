package net.sf.l2j.gameserver.handler.itemhandlers.custom.hero;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class HeroCoinDays implements IItemHandler
{
	private final int HERO_7DAYS = 7;
	private final int HERO_50DAYS = 30;
	private final int HERO_LIFETIME = 36500;
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		int itemId = item.getItemId();
		
		if (itemId == 10001)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on olympiad games.");
				return;
			}

			if (activeChar.isHero())
			{
				activeChar.sendMessage("You are already a hero!");
				return;
			}

			if (activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				activeChar.broadcastPacket(new SocialAction(activeChar, 16));
				activeChar.setHero(true);
				activeChar.setTimedHero(true);
				activeChar.setEndTime("timedHero", HERO_50DAYS);
				
				activeChar.sendMessage("You became a heroic member for "+ HERO_50DAYS +" days.");
	
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (itemId == 10002)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on olympiad games.");
				return;
			}

			if (activeChar.isHero())
			{
				activeChar.sendMessage("You are already a hero!");
				return;
			}

			if (activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				activeChar.broadcastPacket(new SocialAction(activeChar, 16));
				activeChar.setHero(true);
				activeChar.setTimedHero(true);
				activeChar.setEndTime("timedHero", HERO_LIFETIME);
				
				activeChar.sendMessage("You became a lifetime heroic member.");
	
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (itemId == 10501)
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on olympiad games.");
				return;
			}

			if (activeChar.isHero())
			{
				activeChar.sendMessage("You are already a hero!");
				return;
			}

			if (activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				activeChar.broadcastPacket(new SocialAction(activeChar, 16));
				activeChar.setHero(true);
				activeChar.setTimedHero(true);
				activeChar.setEndTime("timedHero", HERO_7DAYS);
				
				activeChar.sendMessage("You became a lifetime heroic member.");
	
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
	}
}