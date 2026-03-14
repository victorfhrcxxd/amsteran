package net.sf.l2j.gameserver.handler.itemhandlers.custom.hero;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskHeroEnd;

public class HeroCoinHours implements IItemHandler
{
	public static int HERO_COIN_2HOURS = 120;
	public static int HERO_COIN_6HOURS = 300;
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		int itemId = item.getItemId();

		if (itemId == 10000)
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
				activeChar.setEndTimebyHour("timedHero", HERO_COIN_6HOURS);
				
				activeChar.setTimedHeroEndTime(System.currentTimeMillis() + (HERO_COIN_6HOURS * 60 * 1000));
				activeChar.store();
				
				activeChar.sendMessage("You became a heroic member for 6 hours.");
				ThreadPoolManager.getInstance().scheduleGeneral(new TaskHeroEnd(activeChar), HERO_COIN_6HOURS * 60 * 1000);
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (itemId == 10503)
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
				activeChar.setEndTimebyHour("timedHero", HERO_COIN_2HOURS);
				
				activeChar.setTimedHeroEndTime(System.currentTimeMillis() + (HERO_COIN_2HOURS * 60 * 1000));
				activeChar.store();
				
				activeChar.sendMessage("You became a heroic member for 2 hours.");
				ThreadPoolManager.getInstance().scheduleGeneral(new TaskHeroEnd(activeChar), HERO_COIN_2HOURS * 60 * 1000);
				
				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
	}
}