package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;
import net.sf.l2j.util.Rnd;

public class L2BuySkillInstance extends L2NpcInstance
{
	public L2BuySkillInstance(int objectId, NpcTemplate template)
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
			int item = Config.BUY_SKILL_ITEM;
			int count = Config.BUY_SKILL_PRICE;

			if (player.getSkillBought() >= Config.BUY_SKILL_MAX_SLOTS)
			{
				player.sendMessage("You cannot learn more skills.");
				return;
			}

			if (player.getClassIndex() != 0)
			{
				player.sendMessage("Skill is only available for base class.");
				return;
			}

			if (player.getInventory().getItemByItemId(item) == null || player.getInventory().getItemByItemId(item).getCount() < count)
			{
				player.sendMessage("Incorrect item count.");
				return;
			}

			L2Skill skill = SkillTable.getInstance().getInfo(id, player.getSkillLevel(id));
			if (skill != null)
			{
				player.sendMessage("You already have this skill.");
				return;
			}

			player.destroyItemByItemId("Coin", item, count, this, true);
			player.addSkill(SkillTable.getInstance().getInfo(id, SkillTable.getInstance().getMaxLevel(id)), true); // DA A SKILL
			player.sendSkillList();
			showMessageWindow(player);
			player.setSkillBought(player.getSkillBought() + 1);
			player.store();
		}

		if (command.startsWith("remove"))
		{
			String skills = command.substring(7);
			int skillId = Integer.parseInt(skills);

			L2Skill skill = SkillTable.getInstance().getInfo(skillId, player.getSkillLevel(skillId));
			if (skill == null)
			{
				player.sendMessage("You dont have this skill.");
				return;
			}

			player.removeSkill(SkillTable.getInstance().getInfo(skillId, player.getSkillLevel(skillId))); // REMOVE A SKILL
			player.sendSkillList();
			showMessageWindow(player);
			player.setSkillBought(player.getSkillBought() - 1);
			player.store();
		}

		super.onBypassFeedback(player, command);
	}

	private void showMessageWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(1);
		html.setFile("data/html/mods/skillManager/Main.htm");
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}
}