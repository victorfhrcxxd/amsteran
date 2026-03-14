package net.sf.l2j.gameserver.model.entity.events;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.util.Rnd;

public class BlessedJewels
{
	protected static final Logger _log = Logger.getLogger(BlessedJewels.class.getName());

	// Chance
	private static final int BLESSED_CHANCE = 40;

	// Boss Id
	private static final int SBLESSED_ANTHARAS = 29066;
	private static final int DBLESSED_ANTHARAS = 29067;
	private static final int TBLESSED_ANTHARAS = 29068;

	private static final int BLESSED_BAIUM = 29020;
	private static final int BLESSED_FRINTEZZA = 29047;
	private static final int BLESSED_QUEEN_ANT = 29001;
	private static final int BLESSED_VALAKAS = 29028;
	private static final int BLESSED_ZAKEN = 29022;
	
	// Blessed
	public final static void upgradeJewels(L2PcInstance player, L2Npc epicboss)
	{
		final int epicId = epicboss.getNpcId();
		
		// Queen Ant
		if (epicId == BLESSED_QUEEN_ANT)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(epicboss.getX(), epicboss.getY(), epicboss.getZ(), 3000, false, false))
					{
						String playerHwId = member.getHWid();
						if (_rewarded_hwid.contains(playerHwId)) 
						{
							member.sendMessage("Your character(s) rewarded already.");
							continue;
						}

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) < BLESSED_CHANCE)
						{
							if ((member.getInventory().getInventoryItemCount(9508, 0) >= 3) && (member.getInventory().getInventoryItemCount(6660, -1, true) >= 1))
							{
								member.getInventory().destroyItemByItemId("Blue Specter", 9508, 3, member, null);
								member.getInventory().destroyItemByItemId("Queen Ant Ring", 6660, 1, member, null);
								member.sendCustomMessage("3 Blue Specter and 1 Ring of Queen Ant was consumed.");
								member.addItem("Blessed - Queen Ant Ring", 9620, 1, member, true);
								member.getInventory().updateDatabase();
								member.sendPacket(new ItemList(member, true));
							}
							else
								member.sendMessage("You do not have the required items to create your blessed jewel.");
						}
					}
					else
						member.sendMessage("You are too far to get a blessed jewel from the boss.");
				} 
			}
			else
			{
				if (Rnd.get(100) < BLESSED_CHANCE)
				{
					if ((player.getInventory().getInventoryItemCount(9508, 0) >= 3) && (player.getInventory().getInventoryItemCount(6660, -1, true) >= 1))
					{
						player.getInventory().destroyItemByItemId("Blue Specter", 9508, 3, player, null);
						player.getInventory().destroyItemByItemId("Queen Ant Ring", 6660, 1, player, null);
						player.sendCustomMessage("3 Blue Specter and 1 Ring of Queen Ant was consumed.");
						player.addItem("Blessed - Queen Ant Ring", 9620, 1, player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(new ItemList(player, true));
					}
					else
						player.sendMessage("You do not have the required items to create your blessed jewel.");
				}
			}
		}
		
		// Zaken
		if (epicId == BLESSED_ZAKEN)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(epicboss.getX(), epicboss.getY(), epicboss.getZ(), 3000, false, false))
					{
						String playerIp = member.getHWid();
						if (_rewarded_hwid.contains(playerIp)) 
						{
							member.sendMessage("Your character(s) rewarded already.");
							continue;
						}

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) < BLESSED_CHANCE)
						{
							if ((member.getInventory().getInventoryItemCount(9508, 0) >= 3) && (member.getInventory().getInventoryItemCount(6659, -1, true) >= 1))
							{
								member.getInventory().destroyItemByItemId("Blue Specter", 9508, 3, member, null);
								member.getInventory().destroyItemByItemId("Zaken's Earring", 6659, 1, member, null);
								member.sendCustomMessage("3 Blue Specter and 1 Zaken's Earring was consumed.");
								member.addItem("Blessed - Zaken's Earring", 9619, 1, member, true);
								member.getInventory().updateDatabase();
								member.sendPacket(new ItemList(member, true));
							}
							else
								member.sendMessage("You do not have the required items to create your blessed jewel.");
						}
					}
					else
						member.sendMessage("You are too far to get a blessed jewel from the boss.");
				} 
			}
			else
			{
				if (Rnd.get(100) < BLESSED_CHANCE)
				{
					if ((player.getInventory().getInventoryItemCount(9508, 0) >= 3) && (player.getInventory().getInventoryItemCount(6659, -1, true) >= 1))
					{
						player.getInventory().destroyItemByItemId("Blue Specter", 9508, 3, player, null);
						player.getInventory().destroyItemByItemId("Zaken's Earring", 6659, 1, player, null);
						player.sendCustomMessage("3 Blue Specter and 1 Zaken's Earring was consumed.");
						player.addItem("Blessed - Zaken's Earring", 9619, 1, player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(new ItemList(player, true));
					}
					else
						player.sendMessage("You do not have the required items to create your blessed jewel.");
				}
			}
		}
		
		// Baium
		if (epicId == BLESSED_BAIUM)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(epicboss.getX(), epicboss.getY(), epicboss.getZ(), 3000, false, false))
					{
						String playerIp = member.getHWid();
						if (_rewarded_hwid.contains(playerIp)) 
						{
							member.sendMessage("Your character(s) rewarded already.");
							continue;
						}

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) < BLESSED_CHANCE)
						{
							if ((member.getInventory().getInventoryItemCount(9508, 0) >= 4) && (member.getInventory().getInventoryItemCount(6658, -1, true) >= 1))
							{
								member.getInventory().destroyItemByItemId("Blue Specter", 9508, 4, member, null);
								member.getInventory().destroyItemByItemId("Ring of Baium", 6658, 1, member, null);
								member.sendCustomMessage("4 Blue Specter and 1 Ring of Baium was consumed.");
								member.addItem("Blessed - Ring of Baium", 9618, 1, member, true);
								member.getInventory().updateDatabase();
								member.sendPacket(new ItemList(member, true));
							}
							else
								member.sendMessage("You do not have the required items to create your blessed jewel.");
						}
					}
					else
						member.sendMessage("You are too far to get a blessed jewel from the boss.");
				} 
			}
			else
			{
				if (Rnd.get(100) < BLESSED_CHANCE)
				{
					if ((player.getInventory().getInventoryItemCount(9508, 0) >= 4) && (player.getInventory().getInventoryItemCount(6658, -1, true) >= 1))
					{
						player.getInventory().destroyItemByItemId("Blue Specter", 9508, 4, player, null);
						player.getInventory().destroyItemByItemId("Ring of Baium", 6658, 1, player, null);
						player.sendCustomMessage("4 Blue Specter and 1 Ring of Baium was consumed.");
						player.addItem("Blessed - Ring of Baium", 9618, 1, player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(new ItemList(player, true));
					}
					else
						player.sendMessage("You do not have the required items to create your blessed jewel.");
				}
			}
		}
		
		// Frintezza
		if (epicId == BLESSED_FRINTEZZA)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(epicboss.getX(), epicboss.getY(), epicboss.getZ(), 3000, false, false))
					{
						String playerIp = member.getHWid();
						if (_rewarded_hwid.contains(playerIp)) 
						{
							member.sendMessage("Your character(s) rewarded already.");
							continue;
						}

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) < BLESSED_CHANCE)
						{
							if ((member.getInventory().getInventoryItemCount(9508, 0) >= 4) && (member.getInventory().getInventoryItemCount(8191, -1, true) >= 1))
							{
								member.getInventory().destroyItemByItemId("Blue Specter", 9508, 4, member, null);
								member.getInventory().destroyItemByItemId("Frintezza's Necklace", 8191, 1, member, null);
								member.sendCustomMessage("4 Blue Specter and 1 Frintezza's Necklace was consumed.");
								member.addItem("Blessed - Frintezza's Necklace", 9623, 1, member, true);
								member.getInventory().updateDatabase();
								member.sendPacket(new ItemList(member, true));
							}
							else
								member.sendMessage("You do not have the required items to create your blessed jewel.");
						}
					}
					else
						member.sendMessage("You are too far to get a blessed jewel from the boss.");
				} 
			}
			else
			{
				if (Rnd.get(100) < BLESSED_CHANCE)
				{
					if ((player.getInventory().getInventoryItemCount(9508, 0) >= 4) && (player.getInventory().getInventoryItemCount(8191, -1, true) >= 1))
					{
						player.getInventory().destroyItemByItemId("Blue Specter", 9508, 4, player, null);
						player.getInventory().destroyItemByItemId("Frintezza's Necklace", 8191, 1, player, null);
						player.sendCustomMessage("4 Blue Specter and 1 Frintezza's Necklace was consumed.");
						player.addItem("Blessed - Frintezza's Necklace", 9623, 1, player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(new ItemList(player, true));
					}
					else
						player.sendMessage("You do not have the required items to create your blessed jewel.");
				}
			}
		}
		
		// Valakas
		if (epicId == BLESSED_VALAKAS)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(epicboss.getX(), epicboss.getY(), epicboss.getZ(), 3000, false, false))
					{
						String playerIp = member.getHWid();
						if (_rewarded_hwid.contains(playerIp)) 
						{
							member.sendMessage("Your character(s) rewarded already.");
							continue;
						}

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) < BLESSED_CHANCE)
						{
							if ((member.getInventory().getInventoryItemCount(9508, 0) >= 6) && (member.getInventory().getInventoryItemCount(6657, -1, true) >= 1))
							{
								member.getInventory().destroyItemByItemId("Blue Specter", 9508, 6, member, null);
								member.getInventory().destroyItemByItemId("Necklace of Valakas", 6657, 1, member, null);
								member.sendCustomMessage("6 Blue Specter and 1 Necklace of Valakas was consumed.");
								member.addItem("Blessed - Necklace of Valakas", 9617, 1, member, true);
								member.getInventory().updateDatabase();
								member.sendPacket(new ItemList(member, true));
							}
							else
								member.sendMessage("You do not have the required items to create your blessed jewel.");
						}
					}
					else
						member.sendMessage("You are too far to get a blessed jewel from the boss.");
				} 
			}
			else
			{
				if (Rnd.get(100) < BLESSED_CHANCE)
				{
					if ((player.getInventory().getInventoryItemCount(9508, 0) >= 6) && (player.getInventory().getInventoryItemCount(6657, -1, true) >= 1))
					{
						player.getInventory().destroyItemByItemId("Blue Specter", 9508, 6, player, null);
						player.getInventory().destroyItemByItemId("Necklace of Valakas", 6657, 1, player, null);
						player.sendCustomMessage("6 Blue Specter and 1 Necklace of Valakas was consumed.");
						player.addItem("Blessed - Necklace of Valakas", 9617, 1, player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(new ItemList(player, true));
					}
					else
						player.sendMessage("You do not have the required items to create your blessed jewel.");
				}
			}
		}
		
		// Antharas
		if (epicId == SBLESSED_ANTHARAS || epicId == DBLESSED_ANTHARAS || epicId == TBLESSED_ANTHARAS)
		{
			if (player.getParty() != null)
			{
				List<String> _rewarded_hwid = new ArrayList<>();
				for (L2PcInstance member : player.getParty().getPartyMembers()) 
				{
					if (member.isInsideRadius(epicboss.getX(), epicboss.getY(), epicboss.getZ(), 3000, false, false))
					{
						String playerIp = member.getHWid();
						if (_rewarded_hwid.contains(playerIp)) 
						{
							member.sendMessage("Your character(s) rewarded already.");
							continue;
						}

						_rewarded_hwid.add(member.getHWid());
						if (Rnd.get(100) < BLESSED_CHANCE)
						{
							if ((member.getInventory().getInventoryItemCount(9508, 0) >= 6) && (member.getInventory().getInventoryItemCount(6656, -1, true) >= 1))
							{
								member.getInventory().destroyItemByItemId("Blue Specter", 9508, 6, member, null);
								member.getInventory().destroyItemByItemId("Earring of Antharas", 6656, 1, member, null);
								member.sendCustomMessage("6 Blue Specter and 1 Earring of Antharas was consumed.");
								member.addItem("Blessed - Earring of Antharas", 9616, 1, member, true);
								member.getInventory().updateDatabase();
								member.sendPacket(new ItemList(member, true));
							}
							else
								member.sendMessage("You do not have the required items to create your blessed jewel.");
						}
					}
					else
						member.sendMessage("You are too far to get a blessed jewel from the boss.");
				} 
			}
			else
			{
				if (Rnd.get(100) < BLESSED_CHANCE)
				{
					if ((player.getInventory().getInventoryItemCount(9508, 0) >= 6) && (player.getInventory().getInventoryItemCount(6656, -1, true) >= 1))
					{
						player.getInventory().destroyItemByItemId("Blue Specter", 9508, 6, player, null);
						player.getInventory().destroyItemByItemId("Earring of Antharas", 6656, 1, player, null);
						player.sendCustomMessage("6 Blue Specter and 1 Earring of Antharas was consumed.");
						player.addItem("Blessed - Earring of Antharas", 9616, 1, player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(new ItemList(player, true));
					}
					else
						player.sendMessage("You do not have the required items to create your blessed jewel.");
				}
			}
		}
	}
}