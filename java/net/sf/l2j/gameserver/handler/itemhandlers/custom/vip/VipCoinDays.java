package net.sf.l2j.gameserver.handler.itemhandlers.custom.vip;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;

public class VipCoinDays implements IItemHandler
{
	private final int VIP_2DAYS = 2;
	private final int VIP_7DAYS = 7;
	private final int VIP_50DAYS = 30;
	private final int VIP_LIFETIME = 36500;

	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance)playable;

		int itemId = item.getItemId();

		if (itemId == 10004)
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
				activeChar.setEndTime("vip", VIP_50DAYS);
				activeChar.sendMessage("You became VIP member per " + VIP_50DAYS + " day's.");

				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}

		if (itemId == 10005)
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
				activeChar.setEndTime("vip", VIP_LIFETIME);
				activeChar.sendMessage("You became a lifetime VIP member.");

				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}

		if (itemId == 10500)
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
				activeChar.setEndTime("vip", VIP_7DAYS);
				activeChar.sendMessage("You became a lifetime VIP member.");

				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}

		if (itemId == 10502)
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
				activeChar.setEndTime("vip", VIP_2DAYS);
				activeChar.sendMessage("You became VIP member per " + VIP_2DAYS + " day's.");

				activeChar.broadcastUserInfo();
				activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			}
		}
	}
}