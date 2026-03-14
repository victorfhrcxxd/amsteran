package net.sf.l2j.gameserver.handler.community.marketplace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.communitybbs.Manager.BaseBBSManager;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.custom.AuctionTable;
import net.sf.l2j.gameserver.datatables.custom.IconTable;
import net.sf.l2j.gameserver.handler.ICBBypassHandler;
import net.sf.l2j.gameserver.handler.community.marketplace.htm.HtmlBuilder;
import net.sf.l2j.gameserver.handler.community.marketplace.htm.HtmlBuilder.HtmlType;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.AuctionHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;
import net.sf.l2j.util.StringUtil;

public class MarketplaceCBBypasses implements ICBBypassHandler
{
	@Override
	public boolean handleBypass(String bypass, L2PcInstance activeChar)
	{
		if (bypass.startsWith("bp_showMarketPlace"))
		{
			String[] data = bypass.substring(19).split(" - ");
			int page = Integer.parseInt(data[0]);
			String search = data[1];

			showMarketBoard(activeChar, page, search);
		}		
		else if (bypass.startsWith("bp_showByGrade"))
		{
			String[] grade = bypass.substring(15).split(" - ");
			int pageGrade = Integer.parseInt(grade[0]);
			String searchGrade = grade[1];

			showMarketBoardByGrade(activeChar, pageGrade, searchGrade);
		}
		else if (bypass.startsWith("bp_showByCategory"))
		{
			String[] category = bypass.substring(18).split(" - ");
			int pageCategory = Integer.parseInt(category[0]);
			String searchCategory = category[1];

			showMarketBoardByCategory(activeChar, pageCategory, searchCategory);
		}
		else if (bypass.startsWith("bp_buy"))
		{
			int auctionId = Integer.parseInt(bypass.substring(7));
			AuctionHolder item = AuctionTable.getInstance().getItem(auctionId);
 
			if (item == null)
			{
				activeChar.sendMessage("Invalid choice. Please try again.");
				return false;
			}
 
			showBuyBoard(activeChar, item);
		}
		else if (bypass.startsWith("bp_confirmBuy"))
		{
			int auctionId = Integer.parseInt(bypass.substring(14));
			AuctionHolder item = AuctionTable.getInstance().getItem(auctionId);
 
			ThreadPoolManager.getInstance().scheduleGeneral(() ->
			{
				if (item == null)
				{
					activeChar.sendMessage("Invalid choice. Please try again.");
					return;
				}

				if (activeChar.getInventory().getItemByItemId(item.getCostId()) == null || activeChar.getInventory().getItemByItemId(item.getCostId()).getCount() < item.getCostCount())
				{
					activeChar.sendMessage("Incorrect item count.");
					showBuyBoard(activeChar, item);
					return;
				}

				activeChar.destroyItemByItemId("auction", item.getCostId(), item.getCostCount(), activeChar, true);

				L2PcInstance owner = L2World.getInstance().getPlayer(item.getOwnerId());
				if (owner != null && owner.isOnline())
				{
					owner.addItem("Auction", item.getCostId(), item.getCostCount(), null, true);
					owner.sendMessage("You have sold an item in the marketplace.");
				}
				else
				{
					addItemToOffline(item.getOwnerId(), item.getCostId(), item.getCostCount());
				}

				ItemInstance i = activeChar.addItem("auction", item.getItemId(), item.getCount(), activeChar, true);
				i.setEnchantLevel(item.getEnchant());
				activeChar.sendPacket(new InventoryUpdate());
				activeChar.sendMessage("You have purchased an item from the marketplace.");

				// Warning
				//MarketBuy.Log(activeChar.getName(), ItemTable.getInstance().getTemplate(item.getItemId()).getName(), item.getEnchant(), item.getCount());
				//AdminData.getInstance().broadcastMessageToGMs("[WARN]" + activeChar.getName() + " BUY [" + ItemTable.getInstance().getTemplate(item.getItemId()).getName() + "] COUNT [" + item.getCount() + "]");

				if (item.getEnchant() >= 1)
					Broadcast.gameAnnounceToOnlinePlayers("Market: " + activeChar.getName() + " bought: " + item.getCount() + " - " + ItemTable.getInstance().getTemplate(item.getItemId()).getName() + " +" + item.getEnchant() + ".");
				else
					Broadcast.gameAnnounceToOnlinePlayers("Market: " + activeChar.getName() + " bought: " + item.getCount() + " - " + ItemTable.getInstance().getTemplate(item.getItemId()).getName() + ".");

				// Auction
				AuctionTable.getInstance().deleteItem(item);
				showMarketBoard(activeChar, 1, "*null*");
			}, Rnd.get(100, 500));
		}
		else if (bypass.startsWith("bp_addpanel"))
		{
			int page = Integer.parseInt(bypass.substring(12));
 
			showAddBoard(activeChar, page);
		}
		else if (bypass.startsWith("bp_additem"))
		{
			int itemId = Integer.parseInt(bypass.substring(11));
 
			if (activeChar.getInventory().getItemByObjectId(itemId) == null)
			{
				activeChar.sendMessage("Invalid item. Please try again.");
				return false;
			}
 
			showAddBoard2(activeChar, itemId);
		}
		else if (bypass.startsWith("bp_addit2"))
		{
			try
			{
				String[] data = bypass.substring(10).split(" ");
				int itemId = Integer.parseInt(data[0]);
				String costitemtype = data[1];
				int costCount = Integer.parseInt(data[2]);
				int itemAmount = Integer.parseInt(data[3]);
				int feeId = Config.MARKETPLACE_FEE[0];
				int feeAmount = Config.MARKETPLACE_FEE[1];
				/*
	        	if ((activeChar.getMarketPlaceTimer() - 1500) > System.currentTimeMillis() && Config.MIN_PLAY_TIME_TO_USE_MARKETPLACE)
				{
					if (((activeChar.getMarketPlaceTimer() - System.currentTimeMillis()) / 1000) >= 60)
					{
						activeChar.sendMessage("You need " + Config.PLAY_TIME_TO_USE_MARKETPLACE + " minutes online to use marketplace. Finish in " + (activeChar.getMarketPlaceTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s).");
						activeChar.sendPacket(new ExShowScreenMessage("You can use marketplace in " + (activeChar.getMarketPlaceTimer() - System.currentTimeMillis()) / (1000 * 60) + " minute(s)", 5 * 1000));
					}
					else
					{
						activeChar.sendMessage("You need " + Config.PLAY_TIME_TO_USE_MARKETPLACE + " minutes online to use marketplace. Finish in " + (activeChar.getMarketPlaceTimer() - System.currentTimeMillis()) / 1000 + " second(s).");
						activeChar.sendPacket(new ExShowScreenMessage("You can use marketplace in " + (activeChar.getMarketPlaceTimer() - System.currentTimeMillis()) / 1000 + " second(s)", 5 * 1000));
					}
					return false;
				}
				if (activeChar.getLevel() <= Config.MIN_LEVEL_TO_SELL_ON_MARKETPLACE)
				{
					activeChar.sendMessage("You need to be at least " + Config.MIN_LEVEL_TO_SELL_ON_MARKETPLACE + " level in order to use marketplace.");
					return false;
				}
				*/
				if (activeChar.getInventory().getInventoryItemCount(feeId, -1) < feeAmount)
				{
					activeChar.sendMessage("You don't have " + StringUtil.concat(String.valueOf(feeAmount)) + " - " + ItemTable.getInstance().getTemplate(feeId).getName() +" to pay the fee.");
					return false;
				}
				if (activeChar.getInventory().getItemByObjectId(itemId) == null)
				{
					activeChar.sendMessage("Invalid item. Please try again.");
					return false;
				}
				if (activeChar.getInventory().getItemByObjectId(itemId).getCount() < itemAmount)
				{
					activeChar.sendMessage("Invalid item. Please try again.");
					return false;
				}
				if (!activeChar.getInventory().getItemByObjectId(itemId).isTradable())
				{
					activeChar.sendMessage("Invalid item. Please try again.");
					return false;
				}
 
				int costId = 0;
				if (costitemtype.equals("T-Donate"))
				{
					costId = 9511;
				}
				
				// Warning
				//MarketSell.Log(activeChar.getName(), activeChar.getInventory().getItemByObjectId(itemId).getItemName(), activeChar.getInventory().getItemByObjectId(itemId).getEnchantLevel(), activeChar.getInventory().getItemByObjectId(itemId).getCount(), activeChar.getInventory().getItemByObjectId(itemId).getObjectId());
				//AdminData.getInstance().broadcastMessageToGMs("[WARN]" + activeChar.getName() + " ADD [" + activeChar.getInventory().getItemByObjectId(itemId).getItemName() + "] COUNT [" + activeChar.getInventory().getItemByObjectId(itemId).getCount() + "]");

				if (activeChar.getInventory().getItemByObjectId(itemId).getEnchantLevel() >= 1)
					Broadcast.gameAnnounceToOnlinePlayers("Market: " + activeChar.getName() + " added: " + itemAmount + " - " + activeChar.getInventory().getItemByObjectId(itemId).getItemName() + " +" + activeChar.getInventory().getItemByObjectId(itemId).getEnchantLevel() + ".");
				else
					Broadcast.gameAnnounceToOnlinePlayers("Market: " + activeChar.getName() + " added: " + itemAmount + " - " + activeChar.getInventory().getItemByObjectId(itemId).getItemName() + ".");

				// Auction
				AuctionTable.getInstance().addItem(new AuctionHolder(AuctionTable.getInstance().getNextAuctionId(), activeChar.getObjectId(), activeChar.getInventory().getItemByObjectId(itemId).getItemId(), itemAmount, activeChar.getInventory().getItemByObjectId(itemId).getEnchantLevel(), costId, costCount));
 
				activeChar.destroyItemByItemId("Auction Fee", feeId, feeAmount, activeChar, true);
				activeChar.destroyItem("Auction Item", itemId, itemAmount, activeChar, true);
				activeChar.sendPacket(new InventoryUpdate());
				activeChar.sendMessage("You have added an item for sale in the Marketplace.");
				showAddBoard(activeChar, 1);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Invalid input. Please try again.");
				return false;
			}
		}
		else if (bypass.startsWith("bp_myitems"))
		{
			int page = Integer.parseInt(bypass.substring(11));
			showMyItemsBoard(activeChar, page);
		}
		else if (bypass.startsWith("bp_removeMarket"))
		{
			int auctionId = Integer.parseInt(bypass.substring(16));
			AuctionHolder item = AuctionTable.getInstance().getItem(auctionId);
 
			if (item == null)
			{
				activeChar.sendMessage("Invalid choice. Please try again.");
				return false;
			}
 
			AuctionTable.getInstance().deleteItem(item);
 
			ItemInstance i = activeChar.addItem("auction", item.getItemId(), item.getCount(), activeChar, true);
			i.setEnchantLevel(item.getEnchant());
			activeChar.sendPacket(new InventoryUpdate());
			activeChar.sendMessage("You have removed an item from the Auction Shop.");
			showMyItemsBoard(activeChar, 1);
		}
		return false;
	}

	public static void showMarketBoard(L2PcInstance player, int page, String search)
	{
		boolean src = !search.equals("*null*");
		 
		HashMap<Integer, ArrayList<AuctionHolder>> items = new HashMap<>();
		int curr = 1;
		int counter = 0;
 
		ArrayList<AuctionHolder> temp = new ArrayList<>();
		for (Map.Entry<Integer, AuctionHolder> entry : AuctionTable.getInstance().getItems().entrySet())
		{
			if (entry.getValue().getOwnerId() != player.getObjectId() && (!src || (src && ItemTable.getInstance().getTemplate(entry.getValue().getItemId()).getName().contains(search))))
			{
				temp.add(entry.getValue());
 
				counter++;
 
				if (counter == 9)
				{
					items.put(curr, temp);
					temp = new ArrayList<>();
					curr++;
					counter = 0;
				}
			}
		}
		items.put(curr, temp);
 
		if (!items.containsKey(page))
		{
			player.sendMessage("Invalid page. Please try again.");
			return;
		}
 
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");
		hb.append("<table width=570><tr><td>");
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table bgcolor=000000><tr><td>");
		
		hb.append("<table width=130 height=230><tr><td>");
		hb.append("<table width=130><tr><td><center><font color=F28034>Item Name:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><edit var=srch width=110 height=13></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"Search\" action=\"bypass bp_showMarketPlace 1 - $srch\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		
		hb.append("<tr><td height=5></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><font color=F28034>Grade:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><combobox width=110 height=15 var=gde list=NONE;D;C;B;A;S;></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"APPLY\" action=\"bypass bp_showByGrade 1 - $gde\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		
		hb.append("<tr><td height=5></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><font color=F28034>Category:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><combobox width=110 height=15 var=ctg list=ALL;WEAPON;ARMOR;JEWELS;ACCESSORY;MISC;SCROLL;></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"APPLY\" action=\"bypass bp_showByCategory 1 - $ctg\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		hb.append("</table><br></td>");
		
		hb.append("<td>");
		hb.append("<img src=L2UI.SquareGray width=500 height=1>");
		hb.append("<table width=480 height=340 bgcolor=000000>");
		hb.append("<tr><td><center>");
		hb.append("<table width=380><tr><td>");
		hb.append("<table width=500>");
		hb.append("<tr>");
		hb.append("<td width=40><font color=F28034>Item</font></td>");
		hb.append("<td width=230></td>");
		hb.append("<td width=40 align=center><font color=F28034>Grade</font></td>");
		hb.append("<td width=80 align=center><font color=F28034>Count</font></td>");
		hb.append("<td width=80 align=right><font color=F28034>Sale Price</font></td>");
		hb.append("<td width=40></td>");
		hb.append("</tr>");
		
		for (AuctionHolder item : items.get(page))
		{
			String gradeIcon = "N/A";
			
			switch (ItemTable.getInstance().getTemplate(item.getItemId()).getCrystalType())
			{
				default:
				case NONE:
					gradeIcon = "N/A";
					break; 
				case D:
					gradeIcon = "<img src=symbol.grade_d width=16 height=16>";
					break;
				case C:
					gradeIcon = "<img src=symbol.grade_c width=16 height=16>";
					break;
				case B:
					gradeIcon = "<img src=symbol.grade_b width=16 height=16>";
					break;
				case A:
					gradeIcon = "<img src=symbol.grade_a width=16 height=16>";
					break;
				case S:
					gradeIcon = "<img src=symbol.grade_s width=16 height=16>";
					break;
			}
			
			String priceIcon = "N/A";
			
			switch (ItemTable.getInstance().getTemplate(item.getCostId()).getItemId())
			{
			    default:
			    case 0:
				    priceIcon = "N/A";
				    break; 
			    case 57:
			    	priceIcon = "<img src=MarketIcons.AdenaMarket width=16 height=16>";
				    break;
			    case 9511:
			    	priceIcon = "<img src=MarketIcons.DonateMarket width=16 height=16>";
				    break;
			}
			
			hb.append("<tr>");
			hb.append("<td width=32 height=37><img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32></td>");
			
			if (item.getEnchant() > 0)
				hb.append("<td width=230 height=25><a action=\"bypass bp_buy "+item.getAuctionId()+"\">"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+" +"+item.getEnchant()+"</a></td>");
			else
				hb.append("<td width=230 height=25><a action=\"bypass bp_buy "+item.getAuctionId()+"\">"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</a></td>");
			
			hb.append("<td width=40 height=25 align=center>"+gradeIcon+"</td>");
			hb.append("<td width=80 height=20 align=center>"+item.getCount()+"</td>");
			hb.append("<td width=80 align=right><font color=LEVEL>"+StringUtil.formatNumber(item.getCostCount())+"</font></td><br1>");
			hb.append("<td width=40 height=25 align=left><font color=LEVEL>"+priceIcon+"</font></td><br1>");
			hb.append("</tr>");
		}
		
		hb.append("</table>");
		hb.append("</td></tr></table>");
		hb.append("</center></td></tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=500 height=1>");
		hb.append("<table width=300>");
		hb.append("<tr>");
		if (items.keySet().size() > 1)
		{
			hb.append("<td width=100></td>");
			if (page > 1)
				hb.append("<td width=30 align=center valign=center><button value=\"PREV\" action=\"bypass bp_showMarketPlace " + (page-1) + " - " + search + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");

			hb.append("<td width=30 align=center valign=center>Page 1</td>");
			
			if (items.keySet().size() > page)
				hb.append("<td width=30 align=center valign=center><button value=\"NEXT\" action=\"bypass bp_showMarketPlace " + (page+1) + " - " + search + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		}
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("</td>");
		hb.append("</tr></table><img src=L2UI.SquareGray width=620 height=1></td></tr></table>");
		hb.append("<br>");
		
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
	
	private void showMarketBoardByGrade(L2PcInstance player, int page, String search)
	{
		boolean src = !search.equals("*null*");
		 
		HashMap<Integer, ArrayList<AuctionHolder>> items = new HashMap<>();
		int curr = 1;
		int counter = 0;
 
		ArrayList<AuctionHolder> temp = new ArrayList<>();
		for (Map.Entry<Integer, AuctionHolder> entry : AuctionTable.getInstance().getItems().entrySet())
		{
			if (entry.getValue().getOwnerId() != player.getObjectId() && (!src || (src && ItemTable.getInstance().getTemplate(entry.getValue().getItemId()).getCrystalType().toString().equals(search))))
			{
				temp.add(entry.getValue());
 
				counter++;
 
				if (counter == 9)
				{
					items.put(curr, temp);
					temp = new ArrayList<>();
					curr++;
					counter = 0;
				}
			}
		}
		items.put(curr, temp);
 
		if (!items.containsKey(page))
		{
			player.sendMessage("Invalid page. Please try again.");
			return;
		}
 
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");
		hb.append("<table width=570><tr><td>");
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table bgcolor=000000><tr><td>");
		
		hb.append("<table width=130 height=230><tr><td>");
		hb.append("<table width=130><tr><td><center><font color=F28034>Item Name:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><edit var=srch width=110 height=13></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"Search\" action=\"bypass bp_showMarketPlace 1 - $srch\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		
		hb.append("<tr><td height=5></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><font color=F28034>Grade:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><combobox width=110 height=15 var=gde list=NONE;D;C;B;A;S;></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"APPLY\" action=\"bypass bp_showByGrade 1 - $gde\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		
		hb.append("<tr><td height=5></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><font color=F28034>Category:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><combobox width=110 height=15 var=ctg list=ALL;WEAPON;ARMOR;JEWELS;ACCESSORY;MISC;SCROLL;></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"APPLY\" action=\"bypass bp_showByCategory 1 - $ctg\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		hb.append("</table><br></td>");
		
		hb.append("<td>");
		hb.append("<img src=L2UI.SquareGray width=500 height=1>");
		hb.append("<table width=480 height=340 bgcolor=000000>");
		hb.append("<tr><td><center>");
		hb.append("<table width=380><tr><td>");
		hb.append("<table width=500>");
		hb.append("<tr>");
		hb.append("<td width=40><font color=F28034>Item</font></td>");
		hb.append("<td width=230></td>");
		hb.append("<td width=40 align=center><font color=F28034>Grade</font></td>");
		hb.append("<td width=80 align=center><font color=F28034>Count</font></td>");
		hb.append("<td width=80 align=right><font color=F28034>Sale Price</font></td>");
		hb.append("<td width=40></td>");
		hb.append("</tr>");
		
		for (AuctionHolder item : items.get(page))
		{
			String gradeIcon = "N/A";
			
			switch (ItemTable.getInstance().getTemplate(item.getItemId()).getCrystalType())
			{
				default:
				case NONE:
					gradeIcon = "N/A";
					break; 
				case D:
					gradeIcon = "<img src=symbol.grade_d width=16 height=16>";
					break;
				case C:
					gradeIcon = "<img src=symbol.grade_c width=16 height=16>";
					break;
				case B:
					gradeIcon = "<img src=symbol.grade_b width=16 height=16>";
					break;
				case A:
					gradeIcon = "<img src=symbol.grade_a width=16 height=16>";
					break;
				case S:
					gradeIcon = "<img src=symbol.grade_s width=16 height=16>";
					break;
			}
			
			String priceIcon = "N/A";
			
			switch (ItemTable.getInstance().getTemplate(item.getCostId()).getItemId())
			{
			    default:
			    case 0:
				    priceIcon = "N/A";
				    break; 
			    case 57:
			    	priceIcon = "<img src=MarketIcons.AdenaMarket width=16 height=16>";
				    break;
			    case 9511:
			    	priceIcon = "<img src=MarketIcons.DonateMarket width=16 height=16>";
				    break;
			}
			
			hb.append("<tr>");
			hb.append("<td width=32 height=37><img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32></td>");
			
			if (item.getEnchant() > 0)
				hb.append("<td width=230 height=25><a action=\"bypass bp_buy "+item.getAuctionId()+"\">"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+" +"+item.getEnchant()+"</a></td>");
			else
				hb.append("<td width=230 height=25><a action=\"bypass bp_buy "+item.getAuctionId()+"\">"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</a></td>");
			
			hb.append("<td width=40 height=25 align=center>"+gradeIcon+"</td>");
			hb.append("<td width=80 height=20 align=center>"+item.getCount()+"</td>");
			hb.append("<td width=80 align=right><font color=LEVEL>"+StringUtil.formatNumber(item.getCostCount())+"</font></td><br1>");
			hb.append("<td width=40 height=25 align=left><font color=LEVEL>"+priceIcon+"</font></td><br1>");
			hb.append("</tr>");
		}
		
		hb.append("</table>");
		hb.append("</td></tr></table>");
		hb.append("</center></td></tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=580 height=1>");
		hb.append("<table width=300>");
		hb.append("<tr>");
		if (items.keySet().size() > 1)
		{
			hb.append("<td width=100></td>");
			if (page > 1)
				hb.append("<td width=30 align=center valign=center><button value=\"PREV\" action=\"bypass bp_showByGrade " + (page-1) + " - " + search + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");

			hb.append("<td width=30 align=center valign=center>Page 1</td>");
			
			if (items.keySet().size() > page)
				hb.append("<td width=30 align=center valign=center><button value=\"NEXT\" action=\"bypass bp_showByGrade " + (page+1) + " - " + search + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		}
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("</td>");
		hb.append("</tr></table><img src=L2UI.SquareGray width=620 height=1></td></tr></table>");
		hb.append("<br>");
		
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
	
	private void showMarketBoardByCategory(L2PcInstance player, int page, String search)
	{
		boolean src = !search.equals("*null*");
		 
		HashMap<Integer, ArrayList<AuctionHolder>> items = new HashMap<>();
		int curr = 1;
		int counter = 0;
 
		ArrayList<AuctionHolder> temp = new ArrayList<>();
		for (Map.Entry<Integer, AuctionHolder> entry : AuctionTable.getInstance().getItems().entrySet())
		{
			if (entry.getValue().getOwnerId() != player.getObjectId() && (!src || (src && ItemTable.getInstance().getTemplate(entry.getValue().getItemId()).searchItemByType(search))))
			{
				temp.add(entry.getValue());
 
				counter++;
 
				if (counter == 9)
				{
					items.put(curr, temp);
					temp = new ArrayList<>();
					curr++;
					counter = 0;
				}
			}
		}
		items.put(curr, temp);
 
		if (!items.containsKey(page))
		{
			player.sendMessage("Invalid page. Please try again.");
			return;
		}
 
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");
		hb.append("<table width=570><tr><td>");
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table bgcolor=000000><tr><td>");
		
		hb.append("<table width=130 height=230><tr><td>");
		hb.append("<table width=130><tr><td><center><font color=F28034>Item Name:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><edit var=srch width=110 height=13></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"Search\" action=\"bypass bp_showMarketPlace 1 - $srch\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		
		hb.append("<tr><td height=5></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><font color=F28034>Grade:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><combobox width=110 height=15 var=gde list=NONE;D;C;B;A;S;></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"APPLY\" action=\"bypass bp_showByGrade 1 - $gde\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		
		hb.append("<tr><td height=5></td></tr>");
		hb.append("<tr><td><table width=130><tr><td><center><font color=F28034>Category:</font></center></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><combobox width=110 height=15 var=ctg list=ALL;WEAPON;ARMOR;JEWELS;ACCESSORY;MISC;SCROLL;></td></tr></table></td></tr>");
		hb.append("<tr><td><table width=130><tr><td align=center><button value=\"APPLY\" action=\"bypass bp_showByCategory 1 - $ctg\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td></tr></table></td></tr>");
		hb.append("</table><br></td>");
		
		hb.append("<td>");
		hb.append("<img src=L2UI.SquareGray width=500 height=1>");
		hb.append("<table width=480 height=340 bgcolor=000000>");
		hb.append("<tr><td><center>");
		hb.append("<table width=380><tr><td>");
		hb.append("<table width=500>");
		hb.append("<tr>");
		hb.append("<td width=40><font color=F28034>Item</font></td>");
		hb.append("<td width=230></td>");
		hb.append("<td width=40 align=center><font color=F28034>Grade</font></td>");
		hb.append("<td width=80 align=center><font color=F28034>Count</font></td>");
		hb.append("<td width=80 align=right><font color=F28034>Sale Price</font></td>");
		hb.append("<td width=40></td>");
		hb.append("</tr>");
		
		for (AuctionHolder item : items.get(page))
		{
			String gradeIcon = "N/A";
			
			switch (ItemTable.getInstance().getTemplate(item.getItemId()).getCrystalType())
			{
				default:
				case NONE:
					gradeIcon = "N/A";
					break; 
				case D:
					gradeIcon = "<img src=symbol.grade_d width=16 height=16>";
					break;
				case C:
					gradeIcon = "<img src=symbol.grade_c width=16 height=16>";
					break;
				case B:
					gradeIcon = "<img src=symbol.grade_b width=16 height=16>";
					break;
				case A:
					gradeIcon = "<img src=symbol.grade_a width=16 height=16>";
					break;
				case S:
					gradeIcon = "<img src=symbol.grade_s width=16 height=16>";
					break;
			}
			
			String priceIcon = "N/A";
			
			switch (ItemTable.getInstance().getTemplate(item.getCostId()).getItemId())
			{
			    default:
			    case 0:
				    priceIcon = "N/A";
				    break; 
			    case 57:
			    	priceIcon = "<img src=MarketIcons.AdenaMarket width=16 height=16>";
				    break;
			    case 9511:
			    	priceIcon = "<img src=MarketIcons.DonateMarket width=16 height=16>";
				    break;
			}
			
			hb.append("<tr>");
			hb.append("<td width=32 height=37><img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32></td>");
			
			if (item.getEnchant() > 0)
				hb.append("<td width=230 height=25><a action=\"bypass bp_buy "+item.getAuctionId()+"\">"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+" +"+item.getEnchant()+"</a></td>");
			else
				hb.append("<td width=230 height=25><a action=\"bypass bp_buy "+item.getAuctionId()+"\">"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</a></td>");
			
			hb.append("<td width=40 height=25 align=center>"+gradeIcon+"</td>");
			hb.append("<td width=80 height=20 align=center>"+item.getCount()+"</td>");
			hb.append("<td width=80 align=right><font color=LEVEL>"+StringUtil.formatNumber(item.getCostCount())+"</font></td><br1>");
			hb.append("<td width=40 height=25 align=left><font color=LEVEL>"+priceIcon+"</font></td><br1>");
			hb.append("</tr>");
		}
		
		hb.append("</table>");
		hb.append("</td></tr></table>");
		hb.append("</center></td></tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=580 height=1>");
		hb.append("<table width=300>");
		hb.append("<tr>");
		if (items.keySet().size() > 1)
		{
			hb.append("<td width=100></td>");
			if (page > 1)
				hb.append("<td width=30 align=center valign=center><button value=\"PREV\" action=\"bypass bp_showByCategory " + (page-1) + " - " + search + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");

			hb.append("<td width=30 align=center valign=center>Page 1</td>");
			
			if (items.keySet().size() > page)
				hb.append("<td width=30 align=center valign=center><button value=\"NEXT\" action=\"bypass bp_showByCategory " + (page+1) + " - " + search + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		}
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("</td>");
		hb.append("</tr></table><img src=L2UI.SquareGray width=620 height=1></td></tr></table>");
		hb.append("<br>");
		
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
	
	private void showAddBoard(L2PcInstance player, int page)
	{
		HashMap<Integer, ArrayList<ItemInstance>> items = new HashMap<>();
		int curr = 1;
		int counter = 0;
 
		ArrayList<ItemInstance> temp = new ArrayList<>();
		for (ItemInstance item : player.getInventory().getItems())
		{
			if (item.getItemId() != 9511 && item.isTradable() && !item.isEquipped())
			{
				temp.add(item);
 
				counter++;
 
				if (counter == 9)
				{
					items.put(curr, temp);
					temp = new ArrayList<>();
					curr++;
					counter = 0;
				}
			}
		}
		items.put(curr, temp);
 
		if (!items.containsKey(page))
		{
			player.sendMessage("Invalid page. Please try again.");
			return;
		}
 
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");

		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table width=610 bgcolor=000000>");
		hb.append("<tr>");
		hb.append("<td><font color=F28034>Item</font></td>");
		hb.append("<td width=220></td>");
		hb.append("<td width=90 align=center><font color=F28034></font></td>");
		hb.append("<td width=140 align=center><font color=F28034></font></td>");
		hb.append("<td width=50 align=center><font color=F28034></font></td>");
		hb.append("</tr>");

		for (ItemInstance item : items.get(page))
		{
			hb.append("<tr>");
		    hb.append("<td width=32 height=37><img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32></td>");

			if (item.getEnchantLevel() > 0)
				hb.append("<td width=220 height=23>"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+" +"+item.getEnchantLevel()+"</td>");
			else
				hb.append("<td width=220 height=23>"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</td>");

			hb.append("<td width=90 height=23 align=center></td>");
			hb.append("<td width=140 align=center></td><br1>");
			hb.append("<td width=50 height=22><button value=\"SELECT\" action=\"bypass bp_additem "+item.getObjectId()+"\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
			hb.append("</tr>");
		}
		hb.append("</table>");
		
		hb.append("<img src=L2UI.SquareGray width=520 height=1>");
		hb.append("<table>");
		hb.append("<tr>");
		if (items.keySet().size() > 1)
		{
			if (page > 1)
				hb.append("<td align=center valign=center><button value=\"PREV\" action=\"bypass bp_addpanel " + (page-1) + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");

			hb.append("<td width=50 align=center valign=center>Page " + page + "</td>");
			
			if (items.keySet().size() > page)
				hb.append("<td align=center valign=center><button value=\"NEXT\" action=\"bypass bp_addpanel " + (page+1) + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		}
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
 
	private void showAddBoard2(L2PcInstance player, int itemId)
	{
		ItemInstance item = player.getInventory().getItemByObjectId(itemId);
		
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");
		
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table width=610 bgcolor=000000>");
		hb.append("<tr>");
		hb.append("<td align=center>");
		hb.append("<img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32>");

		if (item.getEnchantLevel() > 0)
			hb.append("<br>Item: "+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+" +"+item.getEnchantLevel());
		else
			hb.append("<br>Item: "+ItemTable.getInstance().getTemplate(item.getItemId()).getName());

		if (item.isStackable())
		{
			hb.append("<br>Set amount of items to sell:");
			hb.append("<br1><edit var=amm type=number width=170 height=15>");
		}

		hb.append("<br>A fee of <font color=LEVEL>" + getMarketPlaceFee() + "</font> is charged.");
		hb.append("<br1>Select price:");
		hb.append("<br1><combobox width=120 height=17 var=ebox list=T-Donate>");
		hb.append("<br><edit var=count type=number width=170 height=15>");
		hb.append("<br><button value=\"ADD ITEM\" action=\"bypass bp_addit2 "+itemId+" $ebox $count "+(item.isStackable() ? "$amm" : "1")+"\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">");
		hb.append("</td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
	
	public static String getMarketPlaceFee()
	{
		int feeId = Config.MARKETPLACE_FEE[0];
		int feeCount = Config.MARKETPLACE_FEE[1];

		return StringUtil.concat(String.valueOf(feeCount), " - ", ItemTable.getInstance().getTemplate(feeId).getName());
	}
	
	private void showMyItemsBoard(L2PcInstance player, int page)
	{
		Map<Integer, ArrayList<AuctionHolder>> items = new ConcurrentHashMap<>();
		
		int curr = 1;
		int counter = 0;
 
		ArrayList<AuctionHolder> temp = new ArrayList<>();
		for (Map.Entry<Integer, AuctionHolder> entry : AuctionTable.getInstance().getItems().entrySet())
		{
			if (entry.getValue().getOwnerId() == player.getObjectId())
			{
				temp.add(entry.getValue());
 
				counter++;
 
				if (counter == 9)
				{
					items.put(curr, temp);
					temp = new ArrayList<>();
					curr++;
					counter = 0;
				}
			}
		}
		items.put(curr, temp);
 
		if (!items.containsKey(page))
		{
			player.sendMessage("Invalid page. Please try again.");
			return;
		}
 
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");
		
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table width=610 bgcolor=000000>");
		hb.append("<tr>");
		hb.append("<td><font color=F28034>Item</font></td>");
		hb.append("<td width=220></td>");
		hb.append("<td width=90 align=center><font color=F28034>Count</font></td>");
		hb.append("<td width=140 align=center><font color=F28034>Sale Price</font></td>");
		hb.append("<td width=50 align=center><font color=F28034></font></td>");
		hb.append("</tr>");

		for (AuctionHolder item : items.get(page))
		{
			hb.append("<tr>");
			hb.append("<td width=32 height=37><img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32></td>");
			
			if (item.getEnchant() > 0)
				hb.append("<td width=220 height=23>"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+" +"+item.getEnchant()+"</td>");
			else
				hb.append("<td width=220 height=23>"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</td>");

			hb.append("<td width=90 height=23 align=center>"+item.getCount()+"</td>");
			hb.append("<td width=140 align=center>"+StringUtil.formatNumber(item.getCostCount())+"</td><br1>");
			hb.append("<td width=50 height=22><button value=\"REMOVE\" action=\"bypass bp_removeMarket "+item.getAuctionId()+"\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
			hb.append("</tr>");
		}
		
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table>");
		hb.append("<tr>");
		if (items.keySet().size() > 1)
		{
			if (page > 1)
				hb.append("<td align=center valign=center><button value=\"PREV\" action=\"bypass bp_myitems "+(page-1)+"\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");

			hb.append("<td width=50 align=center valign=center>Page " + page + "</td>");
			
			if (items.keySet().size() > page)
				hb.append("<td align=center valign=center><button value=\"NEXT\" action=\"bypass bp_myitems "+(page+1)+"\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		}
		hb.append("</tr>");
		hb.append("</table>");
		
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
	
	private void showBuyBoard(L2PcInstance player, AuctionHolder item)
	{
		HtmlBuilder hb = new HtmlBuilder(HtmlType.COMUNITY);
		hb.append("<html><body>");
		hb.append("<br><br>");
		hb.append("<center><table><tr>");
		hb.append("<td><button value=\"Auction List\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"Create Auction\" action=\"bypass bp_addpanel 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("<td><button value=\"My Auction\" action=\"bypass bp_myitems 1\" width=114 height=31 back=\"L2UI_CH3.bigbutton2_over\" fore=\"L2UI_CH3.bigbutton2\"></td>");
		hb.append("</tr></table>");
		hb.append("<br>");
		
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table width=600 bgcolor=000000>");
		hb.append("<tr>");
		hb.append("<td align=center>");
		
		if (item.getEnchant() > 0)
			hb.append("<img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32> <br>Item: <font color=LEVEL>"+item.getCount()+"</font> - <font color=e6dcbe>"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</font> +<font color=LEVEL>"+item.getEnchant()+"</font> <br>Price: <font color=LEVEL>"+StringUtil.formatNumber(item.getCostCount())+" "+ItemTable.getInstance().getTemplate(item.getCostId()).getName()+"(s)</font>");
		else
			hb.append("<img src=\""+IconTable.getIcon(item.getItemId())+"\" width=32 height=32> <br>Item: <font color=LEVEL>"+item.getCount()+"</font> - <font color=e6dcbe>"+ItemTable.getInstance().getTemplate(item.getItemId()).getName()+"</font> <br>Price: <font color=LEVEL>"+StringUtil.formatNumber(item.getCostCount())+" "+ItemTable.getInstance().getTemplate(item.getCostId()).getName()+"(s)</font>");

		hb.append("</td>");
		hb.append("</tr>");
		hb.append("</table>");
		hb.append("<img src=L2UI.SquareGray width=620 height=1>");
		hb.append("<table>");
		hb.append("<tr>");
		hb.append("<td><button value=\"BUY\" action=\"bypass bp_confirmBuy "+item.getAuctionId()+"\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		hb.append("<td><button value=\"CANCEL\" action=\"bypass bp_showMarketPlace 1 - *null*\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\"></td>");
		hb.append("</tr>");
		hb.append("</table>");
		
		hb.append("</center></body></html>");
		BaseBBSManager.separateAndSend(hb.toString(), player);
	}
	
	private static void addItemToOffline(int playerId, int itemId, int count)
	{
		Item item = ItemTable.getInstance().getTemplate(itemId);
		
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			try (PreparedStatement ps = con.prepareStatement("SELECT count FROM items WHERE owner_id=? AND item_id=?"))
			{
				ps.setInt(1, playerId);
				ps.setInt(2, itemId);
				try (ResultSet rs = ps.executeQuery())
				{
					if (rs.next() && item.isStackable())
					{
						try (PreparedStatement update = con.prepareStatement("UPDATE items SET count=? WHERE owner_id=? AND item_id=?"))
						{
							update.setInt(1, rs.getInt("count") + count);
							update.setInt(2, playerId);
							update.setInt(3, itemId);
							update.execute();
						}
					}
					else
					{
						try (PreparedStatement insert = con.prepareStatement("INSERT INTO items (owner_id,item_id,count,loc,loc_data,enchant_level,object_id,custom_type1,custom_type2,mana_left,time) VALUES (?,?,?,?,?,?,?,?,?,?,?)"))
						{
							insert.setInt(1, playerId);
							insert.setInt(2, IdFactory.getInstance().getNextId());
							insert.setInt(3, itemId);
							insert.setInt(4, count);
							insert.setInt(5, 0);
							insert.setString(6, "INVENTORY");
							insert.setInt(7, 0);
							insert.setInt(8, 0);
							insert.setInt(9, 0);
							insert.setInt(10, -60);
							insert.setLong(11, System.currentTimeMillis());
							insert.execute();
							
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public String[] getBypassHandlersList()
	{
		return new String[] { "bp_showMarketPlace", "bp_showByGrade", "bp_showByCategory", "bp_buy", "bp_confirmBuy", "bp_addpanel", "bp_additem", "bp_addit2", "bp_myitems", "bp_removeMarket" };
	}
}