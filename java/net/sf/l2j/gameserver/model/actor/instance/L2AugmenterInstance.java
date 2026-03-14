package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.AugmentationData;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Augmentation;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;
import net.sf.l2j.util.Rnd;

public class L2AugmenterInstance extends L2NpcInstance
{
	public L2AugmenterInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onAction(L2PcInstance player)
	{
		if (this != player.getTarget()) 
		{
			player.setTarget(this);
			player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel() - getLevel()));
			player.sendPacket(new ValidateLocation(this));
		}
		else if (isInsideRadius(player, INTERACTION_DISTANCE, false, false)) 
		{
			SocialAction sa = new SocialAction(this, Rnd.get(8));
			broadcastPacket(sa);
			player.setCurrentFolkNPC(this);
			showMessageWindow(player);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
		else 
		{
			player.getAI().setIntention(CtrlIntention.INTERACT, this);
			player.sendPacket(ActionFailed.STATIC_PACKET);
		}
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (command.startsWith("learn"))
		{
			String[] args = command.substring(6).split(" ");
			
			int id = Integer.parseInt(args[0]);
			int count = Config.AUGMENT_SKILL_PRICE; // we dont care about count from html (just override)
			
			if (player.getAugsBought() >= Config.DONATION_MAX_AUGS)
			{
				player.sendMessage("You cannot learn more skills.");
				return;
			}
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND) == null)
			{
				player.sendMessage("You need to equip a weapon to learn a skill.");
				return;
			}
			if (!player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).isWeapon())
			{
				player.sendMessage("You need to equip a weapon to learn a skill.");
				return;
			}
			if (player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND).getAugmentation() != null)
			{
				player.sendMessage("Your weapon is already augmented.");
				return;
			}
			
			if (player.getInventory().getItemByItemId(Config.DONATE_TICKET) == null || player.getInventory().getItemByItemId(Config.DONATE_TICKET).getCount() < count)
			{
				player.sendMessage("Incorrect item count.");
				return;
			}
			
			player.destroyItemByItemId("Price", Config.DONATE_TICKET, count, this, true);
			
			ItemInstance wep = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
			player.disarmWeapons();
			
			L2Augmentation aug = AugmentationData.getInstance().generateAugmentationWithSkill(id, SkillTable.getInstance().getMaxLevel(id));
			if (wep.isAugmented())
			{
				wep.removeAugmentation();
				InventoryUpdate iu = new InventoryUpdate();
				iu.addModifiedItem(wep);
				player.sendPacket(iu);
			}
			wep.setAugmentation(aug);
			InventoryUpdate iuu = new InventoryUpdate();
			iuu.addModifiedItem(wep);
			player.sendPacket(iuu);
			StatusUpdate su = new StatusUpdate(player);
			su.addAttribute(StatusUpdate.CUR_LOAD, player.getCurrentLoad());
			player.sendPacket(su);
			showChatWindow(player, 0);
			player.setAugsBought(player.getAugsBought() + 1);
			player.store();
		}

		super.onBypassFeedback(player, command);
	}

	private void showMessageWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/mods/AugmentSeller/Main.htm");
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
}