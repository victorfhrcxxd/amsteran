/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.handler.itemhandlers.custom.special;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.L2Augmentation;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

public class AugmentScrolls implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance) playable;
		
		if (activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("This item cannot be used on olympiad games.");
			return;
		}
		
		ItemInstance rhand = activeChar.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		if (rhand == null)
		{
			activeChar.sendMessage("You need to equip a weapon to use the augment scroll.");
			return;
		}
		
		if (rhand.isAugmented())
		{
			activeChar.sendMessage("Your weapon is already augmented.");
			return;
		}
		
		if (rhand.getItem().getCrystalType().getId() == 0 || rhand.getItem().getCrystalType().getId() == 1 || rhand.getItem().getCrystalType().getId() == 2)
		{
			activeChar.sendMessage("You can't augment under " + rhand.getItem().getCrystalType() + " grade weapon!");
			return;
		}

		int itemId = item.getItemId();
		
		if (itemId == 9800) // Item Skill: +1STR, Active Might Lv.10
			AugmentMight(activeChar);
		
		if (itemId == 9801) // Item Skill: +1STR, Active Duel Might Lv.10
			AugmentDuelMight(activeChar);
		
		if (itemId == 9802) // Item Skill: +1STR, Active Shield Lv.10
			AugmentShieldStr(activeChar);
		
		if (itemId == 9803) // Item Skill: +1CON, Active Shield Lv.10
			AugmentShieldCon(activeChar);
		
		if (itemId == 9804) // Item Skill: +1INT, Active Shield Lv.10
			AugmentShieldInt(activeChar);
		
		if (itemId == 9805) // Item Skill: +1MEN, Active Shield Lv.10
			AugmentShieldMen(activeChar);

		if (itemId == 9806) // Item Skill: +INT, Active Empower Lv.10
			AugmentEmpower(activeChar);
		
		if (itemId == 9807) // Item Skill: +INT, Active Wild Magic Lv.10
			AugmentWildMagic(activeChar);

		if (itemId == 9808) // Item Skill: +1STR, Active Magic Barrie Lv.10
			AugmentMagicBarrierStr(activeChar);
		
		if (itemId == 9809) // Item Skill: +1CON, Active Magic Barrie Lv.10
			AugmentMagicBarrierCon(activeChar);
		
		if (itemId == 9810) // Item Skill: +1INT, Active Magic Barrie Lv.10
			AugmentMagicBarrierInt(activeChar);
		
		if (itemId == 9811) // Item Skill: +1MEN, Active Magic Barrie Lv.10
			AugmentMagicBarrierMen(activeChar);

		if (itemId == 9812) // Item Skill: +1STR, Agility Lv.10
			AugmentAgility(activeChar);

		if (itemId == 9813) // Item Skill: +1STR, Guidance Lv.10
			AugmentGuidance(activeChar);

		if (itemId == 9814) // Item Skill: +1STR, Focus Lv.10
			AugmentFocus(activeChar);

		if (itemId == 9815) // Item Skill: +1INT, Spell Refresh Lv.3
			AugmentSpellRefresh(activeChar);
		
		if (itemId == 9816) // Item Skill: +MEN, Heal Empower Lv.3
			AugmentHealEmpower(activeChar);
		
		if (itemId == 9817) // Item Skill: +CON, Reflect Damage Lv.3
			AugmentReflectDamage(activeChar);
	}
	
	// Confirm Box
	// Str Might
	public void AugmentMight(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptMightAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Might Lv. 10\\n - Increase STR +1"));
	}
	
	//Str Might
	public void AugmentDuelMight(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptDuelMightAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Duel Might Lv. 10\\n - Increase STR +1"));
	}

	//Shield Skills
	//Str Shield
	public void AugmentShieldStr(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptShieldStrAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Shield Lv. 10\\n - Increase STR +1"));
	}
	
	//Con Shield
	public void AugmentShieldCon(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptShieldConAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Shield Lv. 10\\n - Increase CON +1"));
	}
	
	//Int Shield
	public void AugmentShieldInt(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptShieldIntAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Shield Lv. 10\\n - Increase INT +1"));
	}

	//Men Shield
	public void AugmentShieldMen(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptShieldMenAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Shield Lv. 10\\n - Increase MEN +1"));
	}
	
	// Int Empower
	public void AugmentEmpower(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptEmpowerAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Empower Lv. 10\\n - Increase INT +1"));
	}
	
	// Int Wild Magic
	public void AugmentWildMagic(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptWildMagicAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Wild Magic Lv. 10\\n - Increase INT +1"));
	}

	//Str Magic Barrier
	public void AugmentMagicBarrierStr(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptMagicBarrierStrAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Magic Barrier Lv. 10\\n - Increase STR +1"));
	}
	
	//Con Magic Barrier
	public void AugmentMagicBarrierCon(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptMagicBarrierConAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Magic Barrier Lv. 10\\n - Increase CON +1"));
	}
	
	//Int Magic Barrier
	public void AugmentMagicBarrierInt(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptMagicBarrierIntAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Magic Barrier Lv. 10\\n - Increase INT +1"));
	}

	//Men Magic Barrier
	public void AugmentMagicBarrierMen(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		
		player.willAcceptMagicBarrierMenAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Magic Barrier Lv. 10\\n - Increase MEN +1"));
	}
	
	// Str Agility
	public void AugmentAgility(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptAgilityAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Agility Lv. 10\\n - Increase STR +1"));
	}
	
	// Str Guidance
	public void AugmentGuidance(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptGuidanceAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Guidance Lv. 10\\n - Increase STR +1"));
	}
	
	// Str Focus
	public void AugmentFocus(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptFocusAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Focus Lv. 10\\n - Increase STR +1"));
	}
	
	// Int Spell Refresh
	public void AugmentSpellRefresh(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptSpellRefreshAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Spell Refresh Lv. 3\\n - Increase INT +1"));
	}
	
	// Men Spell Refresh
	public void AugmentHealEmpower(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptHealEmpowerAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Heal Empower Lv. 3\\n - Increase MEN +1"));
	}

	// Con Reflect Damage
	public void AugmentReflectDamage(L2PcInstance player)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		player.willAcceptReflectDamageAugment(true);
		player.setAugmentScroll(this);
		player.sendPacket(new ConfirmDlg("Do you want to augment " + ItemTable.getInstance().getTemplate(rhand.getItemId()).getName() + "?\\n \\n - Increase Active Reflect Damage Lv. 3\\n - Increase CON +1"));
	}
	
	//Apply Augment
	public void AcceptMight(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1070923776, 3132, 10);
		player.getInventory().destroyItemByItemId("Consume", 9800, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}

	public void AcceptDuelMight(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071448064, 3134, 10);
		player.getInventory().destroyItemByItemId("Consume", 9801, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Shield Str
	public void AcceptShieldStr(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071185920, 3135, 10);
		player.getInventory().destroyItemByItemId("Consume", 9802, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Shield Con
	public void AcceptShieldCon(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1070989312, 3135, 10);
		player.getInventory().destroyItemByItemId("Consume", 9803, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}

	// Shield Int
	public void AcceptShieldInt(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071054848, 3135, 10);
		player.getInventory().destroyItemByItemId("Consume", 9804, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Shield Men
	public void AcceptShieldMen(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071120384, 3135, 10);
		player.getInventory().destroyItemByItemId("Consume", 9805, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Empower Int
	public void AcceptEmpower(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071316992, 3133, 10);
		player.getInventory().destroyItemByItemId("Consume", 9806, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Wild Magic Int
	public void AcceptWildMagic(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071579136, 3142, 10);
		player.getInventory().destroyItemByItemId("Consume", 9807, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}

	// Magic Barrier Str
	public void AcceptMagicBarrierStr(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071710208, 3136, 10);
		player.getInventory().destroyItemByItemId("Consume", 9808, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Magic Barrier Con
	public void AcceptMagicBarrierCon(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071775744, 3136, 10);
		player.getInventory().destroyItemByItemId("Consume", 9809, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}

	// Magic Barrier Int
	public void AcceptMagicBarrierInt(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071841280, 3136, 10);
		player.getInventory().destroyItemByItemId("Consume", 9810, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Magic Barrier Men
	public void AcceptMagicBarrierMen(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071906816, 3136, 10);
		player.getInventory().destroyItemByItemId("Consume", 9811, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Agility
	public void AcceptAgility(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071972352, 3139, 10);
		player.getInventory().destroyItemByItemId("Consume", 9812, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Guidance
	public void AcceptGuidance(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1072234496, 3140, 10);
		player.getInventory().destroyItemByItemId("Consume", 9813, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Focus
	public void AcceptFocus(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1072496640, 3141, 10);
		player.getInventory().destroyItemByItemId("Consume", 9814, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Spell Refresh
	public void AcceptSpellRefresh(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1072103424, 3200, 3);
		player.getInventory().destroyItemByItemId("Consume", 9815, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Heal Empower
	public void AcceptHealEmpower(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071382528, 3138, 3);
		player.getInventory().destroyItemByItemId("Consume", 9816, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	// Reflect Damage
	public void AcceptReflectDamage(L2PcInstance player, int answer)
	{
		player.setAugmentScroll(null);
		if (answer == 0)
		{
			player.sendMessage("Augment scroll was cancelled!");
			return;
		}
		Augments(player, 1071251456, 3204, 3);
		player.getInventory().destroyItemByItemId("Consume", 9817, 1, player, null);
		player.getInventory().updateDatabase();
		player.sendPacket(new ItemList(player, true));
		setAugmentFalse(player);
	}
	
	public void Augments(L2PcInstance player, int attributes, int idaugment, int levelaugment)
	{
		ItemInstance rhand = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);

		if (rhand == null)
		{
			player.sendMessage("You need to equip a weapon to use the augment scroll.");
			return;
		}
		
		if (rhand.getItem().getCrystalType().getId() == 0 || rhand.getItem().getCrystalType().getId() == 1 || rhand.getItem().getCrystalType().getId() == 2)
		{
			player.sendMessage("You can't augment under " + rhand.getItem().getCrystalType() + " grade weapon!");
			return;
		}
		
		if (rhand.isHeroItem())
		{
			player.sendMessage("You can't add augment on " + rhand.getItemName() + "!");
			return;
		}
		
		if (!rhand.isAugmented())
		{
			player.sendMessage("Your " + rhand.getItemName() + " has been augmented!");
			AugmentWeaponDatabase(player, attributes, idaugment, levelaugment);
		}
		else
		{
			player.sendMessage("Your weapon already contains an augment!");
			return;
		}
	}

	public void AugmentWeaponDatabase(L2PcInstance player, int attributes, int id, int level)
	{
		ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
		L2Augmentation augmentation = new L2Augmentation(attributes, id, level);
		augmentation.applyBonus(player);
		
		item.setAugmentation(augmentation);
		player.disarmWeapons();

		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("REPLACE INTO augmentations VALUES(?,?,?,?)");
			statement.setInt(1, item.getObjectId());
			statement.setInt(2, attributes);
			statement.setInt(3, id);
			statement.setInt(4, level);
			InventoryUpdate iu = new InventoryUpdate();
			player.sendPacket(iu);
			statement.execute();
			statement.close();
		}
		catch (SQLException e)
		{
			System.out.println(e);
		}
	}
	
	public void setAugmentFalse(L2PcInstance player)
	{
		player.willAcceptMightAugment(false);
		player.willAcceptDuelMightAugment(false);
		player.willAcceptShieldStrAugment(false);
		player.willAcceptShieldConAugment(false);
		player.willAcceptShieldIntAugment(false);
		player.willAcceptShieldMenAugment(false);
		player.willAcceptEmpowerAugment(false);
		player.willAcceptWildMagicAugment(false);
		player.willAcceptMagicBarrierStrAugment(false);
		player.willAcceptMagicBarrierConAugment(false);
		player.willAcceptMagicBarrierIntAugment(false);
		player.willAcceptMagicBarrierMenAugment(false);
		player.willAcceptAgilityAugment(false);
		player.willAcceptGuidanceAugment(false);
		player.willAcceptFocusAugment(false);
		player.willAcceptSpellRefreshAugment(false);
		player.willAcceptHealEmpowerAugment(false);
		player.willAcceptReflectDamageAugment(false);
	}
}