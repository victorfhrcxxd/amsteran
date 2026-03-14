package net.sf.l2j.gameserver.handler.itemhandlers.custom.vip;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskVipEnd;

public class VipCoinHours implements IItemHandler
{
	public static int VIP_PASS_2HOURS = 120;
	public static int VIP_PASS_12HOURS = 720;
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		int itemId = item.getItemId();

		if (itemId == 9655) // Vip Coin - 2 Hours
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on olympiad games.");
				return;
			}
			
			if (activeChar.isVip())
			{
				activeChar.sendMessage("You are already a VIP member.");
				return;
			}
			
			if (activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				activeChar.broadcastPacket(new SocialAction(activeChar, 15));
				activeChar.setVip(true);
				activeChar.setEndTimebyHour("vip", VIP_PASS_2HOURS);
				
				activeChar.setVipEndTime(System.currentTimeMillis() + (VIP_PASS_2HOURS * 60 * 1000));
				activeChar.store();
				
				activeChar.sendMessage("You became VIP member per 2 hour's.");

				ThreadPoolManager.getInstance().scheduleGeneral(new TaskVipEnd(activeChar), VIP_PASS_2HOURS * 60 * 1000);

				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
		
		if (itemId == 10003) // Vip Coin - 12 Hours
		{
			if (activeChar.isInOlympiadMode())
			{
				activeChar.sendMessage("This item cannot be used on olympiad games.");
				return;
			}
			
			if (activeChar.isVip())
			{
				activeChar.sendMessage("You are already a VIP member.");
				return;
			}
			
			if (activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
			{
				activeChar.broadcastPacket(new SocialAction(activeChar, 15));
				activeChar.setVip(true);
				activeChar.setEndTimebyHour("vip", VIP_PASS_12HOURS);
				
				activeChar.setVipEndTime(System.currentTimeMillis() + (VIP_PASS_12HOURS * 60 * 1000));
				activeChar.store();
				
				activeChar.sendMessage("You became VIP member per 12 hour's.");

				ThreadPoolManager.getInstance().scheduleGeneral(new TaskVipEnd(activeChar), VIP_PASS_12HOURS * 60 * 1000);

				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
	}
}