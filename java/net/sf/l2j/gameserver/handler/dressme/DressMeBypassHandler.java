package net.sf.l2j.gameserver.handler.dressme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.custom.DressMeData;
import net.sf.l2j.gameserver.datatables.custom.IconTable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.SkinPackage;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.model.item.type.WeaponType;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;

public class DressMeBypassHandler
{
	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	
	public static void handleCommand(L2PcInstance player, String command)
	{
		if (player == null || !Config.ALLOW_DRESS_ME_SYSTEM)
			return;
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("DressMe can't be used in Olympiad.");
			return;
		}
		
		if (Config.ALLOW_DRESS_ME_VIP && !player.isVip())
		{
			player.sendMessage("DressMe is only available for VIP players.");
			return;
		}
		
		try
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip "dressme"
			
			if (!st.hasMoreTokens())
			{
				showDressMeMainPage(player);
				return;
			}
			
			int page = 1;
			try
			{
				page = Integer.parseInt(st.nextToken());
			}
			catch (NumberFormatException e)
			{
				showDressMeMainPage(player);
				return;
			}
			
			if (!st.hasMoreTokens())
			{
				showDressMeMainPage(player);
				return;
			}
			
			String next = st.nextToken();
			
			if (next.startsWith("skinlist"))
			{
				String type = st.hasMoreTokens() ? st.nextToken() : "armor";
				showSkinList(player, type, page);
			}
			else if (next.startsWith("myskinlist"))
			{
				String type = "all";
				if (st.hasMoreTokens())
				{
					String t = st.nextToken();
					if (t.equalsIgnoreCase("armor") || t.equalsIgnoreCase("weapon") || t.equalsIgnoreCase("shield"))
						type = t.toLowerCase();
				}
				showMySkinList(player, page, type);
			}
			else if (next.equals("clean"))
			{
				handleClean(player, st, page);
			}
			else if (next.equals("cleanall"))
			{
				handleCleanAll(player);
			}
			else if (next.startsWith("buyskin"))
			{
				handleBuySkin(player, st, page);
			}
			else if (next.startsWith("tryskin"))
			{
				handleTrySkin(player, st, page);
			}
			else if (next.startsWith("setskin"))
			{
				handleSetSkin(player, st, page);
			}
			else
			{
				showDressMeMainPage(player);
			}
		}
		catch (Exception e)
		{
			player.sendMessage("An error occurred. Please try again.");
			showDressMeMainPage(player);
		}
	}
	
	public static void showDressMeMainPage(L2PcInstance player)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("data/html/mods/dressme/index.htm");
		htm.replace("%time%", sdf.format(new Date(System.currentTimeMillis())));
		htm.replace("%dat%", (new SimpleDateFormat("dd/MM/yyyy")).format(new Date(System.currentTimeMillis())));
		player.sendPacket(htm);
	}
	
	private static void handleClean(L2PcInstance player, StringTokenizer st, int page)
	{
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		String type = st.nextToken();
		boolean removed = false;
		switch (type.toLowerCase())
		{
			case "armor":
				if (player.getArmorSkinOption() != 0) { player.setArmorSkinOption(0); removed = true; }
				break;
			case "weapon":
				if (player.getWeaponSkinOption() != 0) { player.setWeaponSkinOption(0); removed = true; }
				break;
			case "hair":
				if (player.getHairSkinOption() != 0) { player.setHairSkinOption(0); removed = true; }
				break;
			case "face":
				if (player.getFaceSkinOption() != 0) { player.setFaceSkinOption(0); removed = true; }
				break;
			case "shield":
				if (player.getShieldSkinOption() != 0) { player.setShieldSkinOption(0); removed = true; }
				break;
		}
		
		if (removed)
		{
			player.broadcastUserInfo();
			player.sendPacket(new ItemList(player, false));
			player.storeDressMeData();
			player.sendMessage(type.substring(0, 1).toUpperCase() + type.substring(1) + " skin removed.");
		}
		else
			player.sendMessage("No active " + type + " skin to remove.");
		
		// If called from myskinlist context, return to myskinlist; otherwise main page
		if (st.hasMoreTokens())
			showMySkinList(player, page, "all");
		else
			showDressMeMainPage(player);
	}
	
	private static void handleCleanAll(L2PcInstance player)
	{
		boolean removed = false;
		
		if (player.getArmorSkinOption() != 0) { player.setArmorSkinOption(0); removed = true; }
		if (player.getWeaponSkinOption() != 0) { player.setWeaponSkinOption(0); removed = true; }
		if (player.getHairSkinOption() != 0) { player.setHairSkinOption(0); removed = true; }
		if (player.getFaceSkinOption() != 0) { player.setFaceSkinOption(0); removed = true; }
		if (player.getShieldSkinOption() != 0) { player.setShieldSkinOption(0); removed = true; }
		
		if (removed)
		{
			player.broadcastUserInfo();
			player.sendPacket(new ItemList(player, false));
			player.storeDressMeData();
			player.sendMessage("All skins removed.");
		}
		else
			player.sendMessage("No active skins to remove.");
		
		showDressMeMainPage(player);
	}
	
	private static void handleBuySkin(L2PcInstance player, StringTokenizer st, int page)
	{
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		int skinId;
		try
		{
			skinId = Integer.parseInt(st.nextToken());
		}
		catch (NumberFormatException e)
		{
			player.sendMessage("Invalid skin ID.");
			showDressMeMainPage(player);
			return;
		}
		
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		String type = st.nextToken();
		
		SkinPackage skinPackage = null;
		switch (type.toLowerCase())
		{
			case "armor":
				skinPackage = DressMeData.getInstance().getArmorSkinsPackage(skinId);
				if (skinPackage != null && player.hasArmorSkin(skinId))
				{
					player.sendMessage("You already own this skin.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
				break;
			case "weapon":
				skinPackage = DressMeData.getInstance().getWeaponSkinsPackage(skinId);
				if (skinPackage != null && player.hasWeaponSkin(skinId))
				{
					player.sendMessage("You already own this skin.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
				break;
			case "hair":
				skinPackage = DressMeData.getInstance().getHairSkinsPackage(skinId);
				if (skinPackage != null && player.hasHairSkin(skinId))
				{
					player.sendMessage("You already own this skin.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
				break;
			case "face":
				skinPackage = DressMeData.getInstance().getFaceSkinsPackage(skinId);
				if (skinPackage != null && player.hasFaceSkin(skinId))
				{
					player.sendMessage("You already own this skin.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
				break;
			case "shield":
				skinPackage = DressMeData.getInstance().getShieldSkinsPackage(skinId);
				if (skinPackage != null && player.hasShieldSkin(skinId))
				{
					player.sendMessage("You already own this skin.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
				break;
		}
		
		if (skinPackage == null)
		{
			player.sendMessage("Skin not found.");
			showSkinList(player, type, page);
			return;
		}
		
		// Check weapon type compatibility
		if (type.equalsIgnoreCase("weapon"))
		{
			if (player.getActiveWeaponItem() == null)
			{
				player.sendMessage("You must have a weapon equipped to buy a weapon skin.");
				player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
				showSkinList(player, type, page);
				return;
			}
			if (!isWeaponTypeCompatible(player, skinPackage))
			{
				final Item skinItem = ItemTable.getInstance().getTemplate(skinPackage.getWeaponId());
				final String skinTypeName = (skinItem instanceof Weapon) ? getWeaponTypeName(((Weapon) skinItem).getItemType()) : "Unknown";
				final String equippedTypeName = getWeaponTypeName(player.getActiveWeaponItem().getItemType());
				player.sendMessage("This skin is for " + skinTypeName + " but you have a " + equippedTypeName + " equipped.");
				player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
				showSkinList(player, type, page);
				return;
			}
		}
		
		// Check price
		int priceId = skinPackage.getPriceId();
		int priceCount = skinPackage.getPriceCount();
		
		if (priceId > 0 && priceCount > 0)
		{
			if (player.getInventory().getItemByItemId(priceId) == null ||
				player.getInventory().getItemByItemId(priceId).getCount() < priceCount)
			{
				player.sendMessage("You don't have enough " + getItemNameById(priceId) + ".");
				player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
				showSkinList(player, type, page);
				return;
			}
			
			player.getInventory().destroyItemByItemId("DressMe", priceId, priceCount, player, null);
		}
		
		// Add skin to player
		switch (type.toLowerCase())
		{
			case "armor":
				player.buyArmorSkin(skinId);
				final int hairSkinId = DressMeData.getInstance().getCorrespondingHairSkinId(skinId);
				if (hairSkinId > 0)
				{
					boolean wasNew = !player.hasHairSkin(hairSkinId);
					player.buyHairSkin(hairSkinId);
					if (wasNew)
					{
						SkinPackage hairSkin = DressMeData.getInstance().getHairSkinsPackage(hairSkinId);
						if (hairSkin != null)
							player.sendMessage("You also received " + hairSkin.getName() + "!");
					}
				}
				break;
			case "weapon":
				player.buyWeaponSkin(skinId);
				break;
			case "hair":
				player.buyHairSkin(skinId);
				break;
			case "face":
				player.buyFaceSkin(skinId);
				break;
			case "shield":
				player.buyShieldSkin(skinId);
				break;
		}
		
		player.storeDressMeData();
		player.sendMessage("You have successfully purchased " + skinPackage.getName() + "!");
		player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
		showSkinList(player, type, page);
	}
	
	private static void handleTrySkin(L2PcInstance player, StringTokenizer st, int page)
	{
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		int skinId;
		try
		{
			skinId = Integer.parseInt(st.nextToken());
		}
		catch (NumberFormatException e)
		{
			player.sendMessage("Invalid skin ID.");
			showDressMeMainPage(player);
			return;
		}
		
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		String type = st.nextToken();
		
		if (player.isTryingSkin())
		{
			player.sendMessage("You are already trying a skin.");
			player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
			showSkinList(player, type, page);
			return;
		}
		
		// Check weapon type compatibility for try
		if (type.equalsIgnoreCase("weapon"))
		{
			SkinPackage wsp = DressMeData.getInstance().getWeaponSkinsPackage(skinId);
			if (wsp != null)
			{
				if (player.getActiveWeaponItem() == null)
				{
					player.sendMessage("You must have a weapon equipped to try a weapon skin.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
				if (!isWeaponTypeCompatible(player, wsp))
				{
					final Item skinItem = ItemTable.getInstance().getTemplate(wsp.getWeaponId());
					final String skinTypeName = (skinItem instanceof Weapon) ? getWeaponTypeName(((Weapon) skinItem).getItemType()) : "Unknown";
					final String equippedTypeName = getWeaponTypeName(player.getActiveWeaponItem().getItemType());
					player.sendMessage("This skin is for " + skinTypeName + " but you have a " + equippedTypeName + " equipped.");
					player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
					showSkinList(player, type, page);
					return;
				}
			}
		}
		
		player.setIsTryingSkin(true);
		
		final int oldArmorSkin = player.getArmorSkinOption();
		final int oldWeaponSkin = player.getWeaponSkinOption();
		final int oldHairSkin = player.getHairSkinOption();
		final int oldFaceSkin = player.getFaceSkinOption();
		final int oldShieldSkin = player.getShieldSkinOption();
		
		boolean hatTemp = false;
		int testedHairId = 0;
		
		switch (type.toLowerCase())
		{
			case "armor":
				player.setArmorSkinOption(skinId);
				testedHairId = DressMeData.getInstance().getCorrespondingHairSkinId(skinId);
				if (testedHairId > 0)
				{
					if (!player.hasHairSkin(testedHairId))
					{
						player.buyHairSkin(testedHairId);
						hatTemp = true;
					}
					player.setHairSkinOption(testedHairId);
				}
				break;
			case "weapon":
				player.setWeaponSkinOption(skinId);
				break;
			case "hair":
				player.setHairSkinOption(skinId);
				break;
			case "face":
				player.setFaceSkinOption(skinId);
				break;
			case "shield":
				player.setShieldSkinOption(skinId);
				break;
		}
		
		final int TRY_DURATION = 5;
		player.sendMessage("Trying skin for " + TRY_DURATION + " seconds...");
		player.broadcastUserInfo();
		player.sendPacket(new ItemList(player, false));
		showSkinList(player, type, page);
		
		// Countdown timer messages
		for (int i = TRY_DURATION - 1; i >= 1; i--)
		{
			final int sec = i;
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
				player.sendMessage("Skin preview ends in " + sec + " second" + (sec > 1 ? "s" : "") + "."),
				(TRY_DURATION - sec) * 1000);
		}
		
		final boolean finalHatTemp = hatTemp;
		final int finalTestedHairId = testedHairId;
		final String finalType = type;
		
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			switch (finalType.toLowerCase())
			{
				case "armor":
					if (finalHatTemp)
						player.getHairSkins().removeIf(id -> id == finalTestedHairId);
					player.setArmorSkinOption(oldArmorSkin);
					player.setHairSkinOption(oldHairSkin);
					break;
				case "weapon":
					player.setWeaponSkinOption(oldWeaponSkin);
					break;
				case "hair":
					player.setHairSkinOption(oldHairSkin);
					break;
				case "face":
					player.setFaceSkinOption(oldFaceSkin);
					break;
				case "shield":
					player.setShieldSkinOption(oldShieldSkin);
					break;
			}
			player.sendMessage("Skin preview ended.");
			player.broadcastUserInfo();
			player.sendPacket(new ItemList(player, false));
			player.setIsTryingSkin(false);
		}, TRY_DURATION * 1000);
	}
	
	private static void handleSetSkin(L2PcInstance player, StringTokenizer st, int page)
	{
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		int id;
		try
		{
			id = Integer.parseInt(st.nextToken());
		}
		catch (NumberFormatException e)
		{
			player.sendMessage("Invalid skin ID.");
			showDressMeMainPage(player);
			return;
		}
		
		if (!st.hasMoreTokens())
		{
			showDressMeMainPage(player);
			return;
		}
		
		String type = st.nextToken();
		boolean hasSkin = false;
		
		switch (type.toLowerCase())
		{
			case "armor":
				hasSkin = player.hasArmorSkin(id);
				if (hasSkin) player.setArmorSkinOption(id);
				break;
			case "weapon":
				hasSkin = player.hasWeaponSkin(id);
				if (hasSkin)
				{
					SkinPackage weaponSkin = DressMeData.getInstance().getWeaponSkinsPackage(id);
					if (player.getActiveWeaponItem() == null)
					{
						player.sendMessage("You must have a weapon equipped to use a weapon skin.");
						player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
						showMySkinList(player, page, "all");
						return;
					}
					if (weaponSkin != null && !isWeaponTypeCompatible(player, weaponSkin))
					{
						final Item skinItem = ItemTable.getInstance().getTemplate(weaponSkin.getWeaponId());
						final String skinTypeName = (skinItem instanceof Weapon) ? getWeaponTypeName(((Weapon) skinItem).getItemType()) : "Unknown";
						final String equippedTypeName = getWeaponTypeName(player.getActiveWeaponItem().getItemType());
						player.sendMessage("This skin is for " + skinTypeName + " but you have a " + equippedTypeName + " equipped.");
						player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
						showMySkinList(player, page, "all");
						return;
					}
					player.setWeaponSkinOption(id);
				}
				break;
			case "hair":
				hasSkin = player.hasHairSkin(id);
				if (hasSkin) player.setHairSkinOption(id);
				break;
			case "face":
				hasSkin = player.hasFaceSkin(id);
				if (hasSkin) player.setFaceSkinOption(id);
				break;
			case "shield":
				hasSkin = player.hasShieldSkin(id);
				if (hasSkin) player.setShieldSkinOption(id);
				break;
		}
		
		if (!hasSkin)
		{
			player.sendMessage("You don't own this skin.");
			player.sendPacket(new PlaySound("ItemSound3.sys_impossible"));
		}
		else
		{
			player.sendMessage("Skin applied successfully!");
			player.broadcastUserInfo();
			player.sendPacket(new ItemList(player, false));
			player.storeDressMeData();
		}
		
		showMySkinList(player, page, "all");
	}
	
	public static void showSkinList(L2PcInstance player, String type, int page)
	{
		if (player == null)
			return;
		if (type == null || type.isEmpty())
			type = "armor";
		if (page < 1)
			page = 1;
		
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/dressme/allskins.htm");
		html.replace("%time%", sdf.format(new Date(System.currentTimeMillis())));
		html.replace("%dat%", (new SimpleDateFormat("dd/MM/yyyy")).format(new Date(System.currentTimeMillis())));
		
		final String rowBuyTemplate = loadTemplate("data/html/mods/dressme/row_buy.htm");
		final String rowOwnedTemplate = loadTemplate("data/html/mods/dressme/row_owned.htm");
		
		final int ITEMS_PER_PAGE = 8;
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		final StringBuilder sb = new StringBuilder();
		
		List<SkinPackage> tempList = new ArrayList<>();
		switch (type.toLowerCase())
		{
			case "armor":
				tempList = new ArrayList<>(DressMeData.getInstance().getArmorSkinOptions().values());
				break;
			case "weapon":
				tempList = new ArrayList<>(DressMeData.getInstance().getWeaponSkinOptions().values());
				break;
			case "hair":
				tempList = new ArrayList<>(DressMeData.getInstance().getHairSkinOptions().values());
				break;
			case "face":
				tempList = new ArrayList<>(DressMeData.getInstance().getFaceSkinOptions().values());
				break;
			case "shield":
				tempList = new ArrayList<>(DressMeData.getInstance().getShieldSkinOptions().values());
				break;
		}
		
		for (SkinPackage sp : tempList)
		{
			if (sp == null)
				continue;
			
			if (shown == ITEMS_PER_PAGE)
			{
				hasMore = true;
				break;
			}
			
			if (myPage != page)
			{
				i++;
				if (i == ITEMS_PER_PAGE)
				{
					myPage++;
					i = 0;
				}
				continue;
			}
			
			int itemId = getItemIdForSkin(sp, type);
			if (itemId <= 0)
				continue;
			
			String itemIcon = IconTable.getIcon(itemId);
			
			boolean ownsSkin = false;
			switch (type.toLowerCase())
			{
				case "armor": ownsSkin = player.hasArmorSkin(sp.getId()); break;
				case "weapon": ownsSkin = player.hasWeaponSkin(sp.getId()); break;
				case "hair": ownsSkin = player.hasHairSkin(sp.getId()); break;
				case "face": ownsSkin = player.hasFaceSkin(sp.getId()); break;
				case "shield": ownsSkin = player.hasShieldSkin(sp.getId()); break;
			}
			
			if (ownsSkin)
			{
				String row = rowOwnedTemplate
					.replace("%icon%", itemIcon)
					.replace("%name%", sp.getName());
				sb.append(row);
			}
			else
			{
				String row = rowBuyTemplate
					.replace("%icon%", itemIcon)
					.replace("%name%", sp.getName())
					.replace("%priceItem%", getItemNameById(sp.getPriceId()))
					.replace("%priceCount%", String.valueOf(sp.getPriceCount()))
					.replace("%page%", String.valueOf(page))
					.replace("%id%", String.valueOf(sp.getId()))
					.replace("%type%", type)
					.replace("%itemId%", String.valueOf(itemId));
				sb.append(row);
			}
			
			shown++;
		}
		
		if (shown == 0 && page == 1)
			sb.append("<center>No skins available for this category.</center>");
		
		final StringBuilder pg = new StringBuilder();
		pg.append("<table width=300><tr>");
		pg.append("<td align=center width=70>").append(page > 1 ? "<button value=\"< PREV\" action=\"bypass -h dressme " + (page - 1) + " skinlist " + type + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "").append("</td>");
		pg.append("<td align=center width=140>Page: ").append(page).append("</td>");
		pg.append("<td align=center width=70>").append(hasMore ? "<button value=\"NEXT >\" action=\"bypass -h dressme " + (page + 1) + " skinlist " + type + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "").append("</td>");
		pg.append("</tr></table>");
		
		html.replace("%itemList%", sb.toString());
		html.replace("%pagination%", pg.toString());
		player.sendPacket(html);
	}
	
	public static void showMySkinList(L2PcInstance player, int page, String filterType)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/dressme/myskins.htm");
		html.replace("%time%", sdf.format(new Date(System.currentTimeMillis())));
		html.replace("%dat%", (new SimpleDateFormat("dd/MM/yyyy")).format(new Date(System.currentTimeMillis())));
		
		final String rowTemplate = loadTemplate("data/html/mods/dressme/row_myskins.htm");
		
		final int ITEMS_PER_PAGE = 8;
		int myPage = 1;
		int i = 0;
		int shown = 0;
		boolean hasMore = false;
		
		final StringBuilder sb = new StringBuilder();
		
		List<SkinPackage> armors = new ArrayList<>(DressMeData.getInstance().getArmorSkinOptions().values()).stream()
			.filter(s -> player.hasArmorSkin(s.getId())).collect(Collectors.toList());
		List<SkinPackage> weapons = new ArrayList<>(DressMeData.getInstance().getWeaponSkinOptions().values()).stream()
			.filter(s -> player.hasWeaponSkin(s.getId())).collect(Collectors.toList());
		List<SkinPackage> shields = new ArrayList<>(DressMeData.getInstance().getShieldSkinOptions().values()).stream()
			.filter(s -> player.hasShieldSkin(s.getId())).collect(Collectors.toList());
		
		List<SkinPackage> filteredList = new ArrayList<>();
		if (filterType.equalsIgnoreCase("armor"))
			filteredList.addAll(armors);
		else if (filterType.equalsIgnoreCase("weapon"))
			filteredList.addAll(weapons);
		else if (filterType.equalsIgnoreCase("shield"))
			filteredList.addAll(shields);
		else
		{
			filteredList.addAll(armors);
			filteredList.addAll(weapons);
			filteredList.addAll(shields);
		}
		
		for (SkinPackage sp : filteredList)
		{
			if (sp == null)
				continue;
			
			if (shown == ITEMS_PER_PAGE)
			{
				hasMore = true;
				break;
			}
			
			if (myPage != page)
			{
				i++;
				if (i == ITEMS_PER_PAGE)
				{
					myPage++;
					i = 0;
				}
				continue;
			}
			
			int itemId = getItemIdForSkin(sp, sp.getType());
			String itemIcon = IconTable.getIcon(itemId);
			
			String row = rowTemplate
				.replace("%icon%", itemIcon)
				.replace("%name%", sp.getName())
				.replace("%page%", String.valueOf(page))
				.replace("%id%", String.valueOf(sp.getId()))
				.replace("%type%", sp.getType());
			sb.append(row);
			
			shown++;
		}
		
		if (shown == 0 && page == 1)
		{
			String msg = filterType.equalsIgnoreCase("all") ? "You don't own any skins yet." : "You don't own any " + filterType + " skins yet.";
			sb.append("<center>").append(msg).append("</center>");
		}
		
		final StringBuilder pg = new StringBuilder();
		pg.append("<br><table width=300><tr>");
		pg.append("<td align=center><button value=\"All\" action=\"bypass -h dressme 1 myskinlist all\" width=90 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2></td>");
		pg.append("<td align=center><button value=\"Armor\" action=\"bypass -h dressme 1 myskinlist armor\" width=90 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2></td>");
		pg.append("<td align=center><button value=\"Weapon\" action=\"bypass -h dressme 1 myskinlist weapon\" width=90 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2></td>");
		pg.append("</tr></table>");
		pg.append("<table width=300><tr>");
		pg.append("<td align=center width=70>").append(page > 1 ? "<button value=\"< PREV\" action=\"bypass -h dressme " + (page - 1) + " myskinlist " + filterType + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "").append("</td>");
		pg.append("<td align=center width=140>Page: ").append(page).append("</td>");
		pg.append("<td align=center width=70>").append(hasMore ? "<button value=\"NEXT >\" action=\"bypass -h dressme " + (page + 1) + " myskinlist " + filterType + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "").append("</td>");
		pg.append("</tr></table>");
		
		html.replace("%itemList%", sb.toString());
		html.replace("%pagination%", pg.toString());
		player.sendPacket(html);
	}
	
	private static String loadTemplate(String path)
	{
		final StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(new File(path))))
		{
			String line;
			while ((line = br.readLine()) != null)
				sb.append(line).append("\n");
		}
		catch (Exception e)
		{
			return "";
		}
		return sb.toString();
	}
	
	private static int getItemIdForSkin(SkinPackage sp, String type)
	{
		switch (type.toLowerCase())
		{
			case "armor": return sp.getChestId();
			case "weapon": return sp.getWeaponId();
			case "hair": return sp.getHairId();
			case "face": return sp.getFaceId();
			case "shield": return sp.getShieldId();
			default: return 0;
		}
	}
	
	public static String getItemNameById(int itemId)
	{
		if (itemId == 0)
			return "NoName";
		Item item = ItemTable.getInstance().getTemplate(itemId);
		if (item == null)
			return "NoName";
		return item.getName() != null ? item.getName() : "NoName";
	}
	
	/**
	 * Checks if the player's equipped weapon type matches the skin weapon type.
	 * @return true if compatible, false otherwise.
	 */
	private static boolean isWeaponTypeCompatible(L2PcInstance player, SkinPackage skinPackage)
	{
		final Weapon equippedWeapon = player.getActiveWeaponItem();
		if (equippedWeapon == null)
			return false;
		
		final Item skinItem = ItemTable.getInstance().getTemplate(skinPackage.getWeaponId());
		if (skinItem == null || !(skinItem instanceof Weapon))
			return false;
		
		final WeaponType equippedType = equippedWeapon.getItemType();
		final WeaponType skinType = ((Weapon) skinItem).getItemType();
		
		return equippedType == skinType;
	}
	
	private static String getWeaponTypeName(WeaponType type)
	{
		switch (type)
		{
			case SWORD: return "Sword";
			case BLUNT: return "Blunt";
			case DAGGER: return "Dagger";
			case BOW: return "Bow";
			case POLE: return "Pole";
			case FIST: return "Fist";
			case DUAL: return "Dual Sword";
			case DUALFIST: return "Dual Fist";
			case BIGSWORD: return "Big Sword";
			case BIGBLUNT: return "Big Blunt";
			default: return type.name();
		}
	}
}
