package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.custom.DollsTable;
import net.sf.l2j.gameserver.datatables.custom.IconTable;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.Doll;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.MyTargetSelected;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.ValidateLocation;
import net.sf.l2j.util.Rnd;

public class L2DollManagerInstance extends L2NpcInstance
{
	public L2DollManagerInstance(int objectId, NpcTemplate template)
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
			showDollList(player);
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
		StringTokenizer st = new StringTokenizer(command, " ");
		String currentCommand = st.nextToken();

		if (currentCommand.startsWith("doll_list"))
		{
			showDollList(player);
		}
		else if (currentCommand.startsWith("doll_upgrade"))
		{
			if (st.hasMoreTokens())
			{
				int dollItemId = Integer.parseInt(st.nextToken());
				upgradeDoll(player, dollItemId);
			}
		}
		else if (currentCommand.startsWith("doll_confirm"))
		{
			if (st.hasMoreTokens())
			{
				int dollItemId = Integer.parseInt(st.nextToken());
				showConfirm(player, dollItemId);
			}
		}
	}

	private void showDollList(L2PcInstance player)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Doll Manager</title></head><body>");
		sb.append("<center>");
		sb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
		sb.append("<br><font color=\"LEVEL\">Doll Upgrade System</font><br>");
		sb.append("<img src=\"L2UI.SquareGray\" width=250 height=1><br>");
		sb.append("</center>");

		boolean hasDolls = false;

		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item == null)
				continue;

			Doll doll = DollsTable.getInstance().getDollById(item.getItemId());
			if (doll == null)
				continue;

			hasDolls = true;
			Item itemTemplate = ItemTable.getInstance().getTemplate(item.getItemId());
			String itemName = itemTemplate != null ? itemTemplate.getName() : doll.getName();

			sb.append("<center>");
			sb.append("<table width=280 bgcolor=000000>");
			sb.append("<tr>");
			sb.append("<td width=40><button width=32 height=32 back=" + IconTable.getIcon(item.getItemId()) + " fore=" + IconTable.getIcon(item.getItemId()) + "></td>");
			sb.append("<td width=160>");
			sb.append("<font color=\"LEVEL\">" + itemName + "</font><br1>");
			sb.append("<font color=\"B09878\">" + doll.getName() + " Lv." + doll.getLevel() + "</font>");
			sb.append("</td>");
			sb.append("<td width=80>");

			if (doll.canUpgrade())
			{
				Item priceItem = ItemTable.getInstance().getTemplate(doll.getUpgradePriceId());
				String priceName = priceItem != null ? priceItem.getName() : "Item";
				sb.append("<button value=\"Upgrade\" action=\"bypass -h npc_%objectId%_doll_confirm " + item.getItemId() + "\" width=70 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			}
			else
			{
				sb.append("<font color=\"FF6600\">Max Level</font>");
			}

			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			sb.append("<img src=\"L2UI.SquareGray\" width=250 height=1>");
			sb.append("</center>");
		}

		if (!hasDolls)
		{
			sb.append("<center><br><font color=\"FF0000\">You don't have any dolls in your inventory.</font></center>");
		}

		sb.append("<br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center>");
		sb.append("</body></html>");

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setHtml(sb.toString());
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}

	private void showConfirm(L2PcInstance player, int dollItemId)
	{
		Doll doll = DollsTable.getInstance().getDollById(dollItemId);
		if (doll == null || !doll.canUpgrade())
		{
			player.sendMessage("This doll cannot be upgraded.");
			return;
		}

		if (player.getInventory().getItemByItemId(dollItemId) == null)
		{
			player.sendMessage("You don't have this doll.");
			return;
		}

		Doll nextDoll = DollsTable.getInstance().getDollById(doll.getUpgradeId());
		Item priceItem = ItemTable.getInstance().getTemplate(doll.getUpgradePriceId());
		Item currentItem = ItemTable.getInstance().getTemplate(dollItemId);
		Item nextItem = ItemTable.getInstance().getTemplate(doll.getUpgradeId());

		String currentName = currentItem != null ? currentItem.getName() : doll.getName();
		String nextName = nextItem != null ? nextItem.getName() : (nextDoll != null ? nextDoll.getName() : "Unknown");
		String priceName = priceItem != null ? priceItem.getName() : "Item";
		int nextLevel = nextDoll != null ? nextDoll.getLevel() : doll.getLevel() + 1;

		StringBuilder sb = new StringBuilder();
		sb.append("<html><head><title>Doll Manager</title></head><body>");
		sb.append("<center>");
		sb.append("<img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>");
		sb.append("<br><font color=\"LEVEL\">Confirm Upgrade</font><br>");
		sb.append("<img src=\"L2UI.SquareGray\" width=250 height=1><br><br>");
		sb.append("</center>");

		sb.append("<center>");
		sb.append("<table width=280 bgcolor=000000>");
		int chance = doll.getUpgradeChance() > 0 ? doll.getUpgradeChance() : Config.DOLL_UPGRADE_CHANCE;

		sb.append("<tr><td><center><font color=\"B09878\">Current Doll:</font></center></td></tr>");
		sb.append("<tr><td><center>");
		sb.append("<button width=32 height=32 back=" + IconTable.getIcon(dollItemId) + " fore=" + IconTable.getIcon(dollItemId) + ">");
		sb.append("</center></td></tr>");
		sb.append("<tr><td><center><font color=\"LEVEL\">" + currentName + " Lv." + doll.getLevel() + "</font></center></td></tr>");
		sb.append("</table>");
		sb.append("<br><font color=\"00FF00\">▼</font><br>");
		sb.append("<table width=280 bgcolor=000000>");
		sb.append("<tr><td><center><font color=\"B09878\">Upgraded Doll:</font></center></td></tr>");
		sb.append("<tr><td><center>");
		sb.append("<button width=32 height=32 back=" + IconTable.getIcon(doll.getUpgradeId()) + " fore=" + IconTable.getIcon(doll.getUpgradeId()) + ">");
		sb.append("</center></td></tr>");
		sb.append("<tr><td><center><font color=\"00FF00\">" + nextName + " Lv." + nextLevel + "</font></center></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table width=280 bgcolor=000000>");
		sb.append("<tr><td><center><font color=\"B09878\">Cost:</font></center></td></tr>");
		sb.append("<tr><td><center><font color=\"FF6600\">" + doll.getUpgradePriceCount() + "x " + priceName + "</font></center></td></tr>");
		sb.append("<tr><td><center><font color=\"B09878\">Success Rate:</font> <font color=\"" + (chance >= 100 ? "00FF00" : (chance >= 50 ? "LEVEL" : "FF0000")) + "\">" + chance + "%</font></center></td></tr>");
		if (Config.DOLL_UPGRADE_DESTROY_ON_FAIL)
			sb.append("<tr><td><center><font color=\"FF0000\">Warning: Doll will be destroyed on failure!</font></center></td></tr>");
		sb.append("</table>");
		sb.append("<br>");
		sb.append("<table width=200>");
		sb.append("<tr>");
		sb.append("<td><button value=\"Upgrade\" action=\"bypass -h npc_%objectId%_doll_upgrade " + dollItemId + "\" width=90 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("<td><button value=\"Back\" action=\"bypass -h npc_%objectId%_doll_list\" width=90 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>");
		sb.append("</tr>");
		sb.append("</table>");
		sb.append("</center>");

		sb.append("<br><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32></center>");
		sb.append("</body></html>");

		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setHtml(sb.toString());
		html.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(html);
	}

	private void upgradeDoll(L2PcInstance player, int dollItemId)
	{
		Doll doll = DollsTable.getInstance().getDollById(dollItemId);
		if (doll == null || !doll.canUpgrade())
		{
			player.sendMessage("This doll cannot be upgraded.");
			showDollList(player);
			return;
		}

		ItemInstance dollItem = player.getInventory().getItemByItemId(dollItemId);
		if (dollItem == null)
		{
			player.sendMessage("You don't have this doll.");
			showDollList(player);
			return;
		}

		int priceId = doll.getUpgradePriceId();
		int priceCount = doll.getUpgradePriceCount();

		ItemInstance priceItem = player.getInventory().getItemByItemId(priceId);
		if (priceItem == null || priceItem.getCount() < priceCount)
		{
			Item priceTemplate = ItemTable.getInstance().getTemplate(priceId);
			String priceName = priceTemplate != null ? priceTemplate.getName() : "required items";
			player.sendMessage("You don't have enough " + priceName + ". Required: " + priceCount + ".");
			showDollList(player);
			return;
		}

		// Consume price items
		player.getInventory().destroyItemByItemId("Doll Upgrade", priceId, priceCount, player, this);

		// Roll upgrade chance
		int chance = doll.getUpgradeChance() > 0 ? doll.getUpgradeChance() : Config.DOLL_UPGRADE_CHANCE;
		boolean success = Rnd.get(100) < chance;

		if (success)
		{
			// Consume old doll and give upgraded doll
			player.getInventory().destroyItemByItemId("Doll Upgrade", dollItemId, 1, player, this);
			player.getInventory().addItem("Doll Upgrade", doll.getUpgradeId(), 1, player, this);

			// Refresh doll skills
			DollsTable.refreshAllDollSkills(player);

			Doll nextDoll = DollsTable.getInstance().getDollById(doll.getUpgradeId());
			Item nextItem = ItemTable.getInstance().getTemplate(doll.getUpgradeId());
			String nextName = nextItem != null ? nextItem.getName() : (nextDoll != null ? nextDoll.getName() : "Doll");

			player.sendMessage("Upgrade successful! You received: " + nextName);
			player.broadcastPacket(new MagicSkillUse(player, player, 2025, 1, 500, 0));
		}
		else
		{
			if (Config.DOLL_UPGRADE_DESTROY_ON_FAIL)
			{
				player.getInventory().destroyItemByItemId("Doll Upgrade", dollItemId, 1, player, this);
				DollsTable.refreshAllDollSkills(player);
				player.sendMessage("Upgrade failed! Your doll has been destroyed.");
			}
			else
			{
				player.sendMessage("Upgrade failed! Your doll was kept, but the materials were consumed.");
			}
			player.broadcastPacket(new MagicSkillUse(player, player, 2024, 1, 500, 0));
		}

		showDollList(player);
	}

	@Override
	public String getHtmlPath(int npcId, int val)
	{
		return "data/html/mods/dolls/" + npcId + (val == 0 ? "" : "-" + val) + ".htm";
	}
}
