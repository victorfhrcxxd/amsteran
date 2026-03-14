/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package phantom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import phantom.ai.shop.holder.FakePrivateBuyHolder;
import phantom.ai.shop.holder.FakePrivateSellHolder;
import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.gameserver.model.Location;

public class FakePlayerConfig
{
	protected static final Logger _log = Logger.getLogger(FakePlayerConfig.class.getName());
	
	private static final String PHANTOM_FILE = "./config/phantom/FakePlayers.properties";

	//Social
	public static int FAKE_CHANCE_TO_TALK_SOCIAL;
	public static int FAKE_CHANCE_TO_TALK_DIED;
	public static int FAKE_CHANCE_TO_TALK_KILLED;
	public static int FAKE_SOCIAL_CHANCE;
	public static int FAKE_SIT_CHANCE;
	
	//NPC
	public static int[] FAKE_PLAYER_ALLOWED_NPC_TO_WALK;
	
	public static boolean FAKE_PLAYERS_DEBUG;
	
	public static int FAKE_PLAYER_ROAMING_MAX_WH_CHECKS;
	public static int FAKE_PLAYER_ROAMING_MAX_SHOP_CHECKS;
	public static int FAKE_PLAYER_ROAMING_MAX_TELEPORT_CHECKS;
	public static int FAKE_PLAYER_ROAMING_MAX_BUFFER_CHECKS;
	public static int FAKE_PLAYER_ROAMING_MAX_PLAYER_CHECKS;
	public static int FAKE_PLAYER_ROAMING_MAX_PL_STORE_CHECKS;
	
	public static int FAKE_PLAYER_WH_CHECK_CHANCE;
	public static int FAKE_PLAYER_SHOP_CHECK_CHANCE;
	public static int FAKE_PLAYER_TELEPORT_CHECK_CHANCE;
	public static int FAKE_PLAYER_BUFFER_CHECK_CHANCE;
	public static int FAKE_PLAYER_RELAX_CHECK_CHANCE;
	public static int FAKE_PLAYER_WALK_CHECK_CHANCE;
	public static int FAKE_PLAYER_PLAYER_CHECK_CHANCE;
	public static int FAKE_PLAYER_PL_STORE_CHECK_CHANCE;
	
	//Private
	public static List<FakePrivateBuyHolder> FAKE_PLAYER_PRIVATE_BUY_LIST = new ArrayList<>();
	public static List<FakePrivateSellHolder> FAKE_PLAYER_PRIVATE_SELL_LIST = new ArrayList<>();
	
	//Protection
	public static boolean CHECK_FAKE_PLAYERS_AREA;
	public static int CHECK_FAKE_PLAYERS_START_TIME;
	public static int CHECK_FAKE_PLAYERS_RESTART_TIME;
	
	//Consumables
	public static int FAKE_PLAYER_ARROW;
	public static int FAKE_PLAYER_SOULSHOT;
	public static int FAKE_PLAYER_BLESSED_SOULSHOT;
	
	//Timer
	public static int DESPAWN_CITIZEN_RANDOM_TIME_1;
	public static int DESPAWN_CITIZEN_RANDOM_TIME_2;
	
	public static int DESPAWN_PVP_RANDOM_TIME_1;
	public static int DESPAWN_PVP_RANDOM_TIME_2;
	
	//Colors
	public static String FAKE_PLAYER_COLOR_NAME;
	public static String FAKE_PLAYER_COLOR_TITLE;
	
	//Title
	public static String FAKE_PLAYER_FIXED_TITLE;
	
	//Clan
	public static String CLAN_ID;
	public static List<Integer> LIST_CLAN_ID;
	
	//Enchant
	public static int MIN_ENCHANT_ARMOR;
	public static int MAX_ENCHANT_ARMOR;
	
	public static int MIN_ENCHANT_WEAPON;
	public static int MAX_ENCHANT_WEAPON;
	
	public static int MIN_ENCHANT_JEWEL;
	public static int MAX_ENCHANT_JEWEL;
	
	//Armors Robe
	public static String PHANTOM_ROB_ARMOR_1;
	public static List<Integer> LIST_PHANTOM_ROB_ARMOR_1 = new ArrayList<>();

	public static String PHANTOM_ROB_ARMOR_2;
	public static List<Integer> LIST_PHANTOM_ROB_ARMOR_2 = new ArrayList<>();
	
	public static String PHANTOM_ROB_ARMOR_3;
	public static List<Integer> LIST_PHANTOM_ROB_ARMOR_3 = new ArrayList<>();
	
	public static String PHANTOM_ROB_ARMOR_4;
	public static List<Integer> LIST_PHANTOM_ROB_ARMOR_4 = new ArrayList<>();
	
	//Armors Heavy
	public static String PHANTOM_HEAVY_ARMOR_1;
	public static List<Integer> LIST_PHANTOM_HEAVY_ARMOR_1 = new ArrayList<>();

	public static String PHANTOM_HEAVY_ARMOR_2;
	public static List<Integer> LIST_PHANTOM_HEAVY_ARMOR_2 = new ArrayList<>();
	
	public static String PHANTOM_HEAVY_ARMOR_3;
	public static List<Integer> LIST_PHANTOM_HEAVY_ARMOR_3 = new ArrayList<>();
	
	public static String PHANTOM_HEAVY_ARMOR_4;
	public static List<Integer> LIST_PHANTOM_HEAVY_ARMOR_4 = new ArrayList<>();
	
	//Armors Light
	public static String PHANTOM_LIGHT_ARMOR_1;
	public static List<Integer> LIST_PHANTOM_LIGHT_ARMOR_1 = new ArrayList<>();

	public static String PHANTOM_LIGHT_ARMOR_2;
	public static List<Integer> LIST_PHANTOM_LIGHT_ARMOR_2 = new ArrayList<>();
	
	public static String PHANTOM_LIGHT_ARMOR_3;
	public static List<Integer> LIST_PHANTOM_LIGHT_ARMOR_3 = new ArrayList<>();
	
	public static String PHANTOM_LIGHT_ARMOR_4;
	public static List<Integer> LIST_PHANTOM_LIGHT_ARMOR_4 = new ArrayList<>();
	
	//BOW
	public static String FAKE_BOW_ID;
	public static List<Integer> LIST_FAKE_BOW;
	
	//DAGGER
	public static String FAKE_DAGGER_ID;
	public static List<Integer> LIST_FAKE_DAGGER;
	
	//SWORD
	public static String FAKE_SWORD_ID;
	public static List<Integer> LIST_FAKE_SWORD;
	
	//SPEAR
	public static String FAKE_SPEAR_ID;
	public static List<Integer> LIST_FAKE_SPEAR;
	
	//DUAL
	public static String FAKE_DUAL_ID;
	public static List<Integer> LIST_FAKE_DUAL;
	
	//FIST
	public static String FAKE_FIST_ID;
	public static List<Integer> LIST_FAKE_FIST;
	
	//BIGSWORD
	public static String FAKE_BIGSWORD_ID;
	public static List<Integer> LIST_FAKE_BIG_SWORD;
	
	//MAGIC
	public static String FAKE_MAGIC_ID;
	public static List<Integer> LIST_FAKE_MAGIC;
	
	//SHIELD
	public static String FAKE_SHIELD_ID;
	public static List<Integer> LIST_FAKE_SHIELD;
	
	//JEWELS
	public static int[] PHANTOM_JEWELS_1;
	public static List<int[]> LIST_PHANTOM_JEWELS_1 = new ArrayList<>();
	
	public static int[] PHANTOM_JEWELS_2;
	public static List<int[]> LIST_PHANTOM_JEWELS_2 = new ArrayList<>();
	
	//Accessory
	public static boolean ALLOW_FAKE_PLAYERS_ACCESSORY;
	public static String FAKE_ACCESSORY_ID;
	public static List<Integer> LIST_FAKE_ACCESSORY;
	public static boolean ALLOW_FAKE_PLAYERS_ACCESSORY_BY_CLASS;
	
	//Buffer
	public static String NUKER_BUFFER;
	public static List<Integer> NUKER_BUFFER_LIST = new ArrayList<>();
	
	public static String ARCHER_BUFFER;
	public static List<Integer> ARCHER_BUFFER_LIST = new ArrayList<>();
	
	public static String WARRIOR_BUFFER;
	public static List<Integer> WARRIOR_BUFFER_LIST = new ArrayList<>();
	
	public static String TANKER_BUFFER;
	public static List<Integer> TANKER_BUFFER_LIST = new ArrayList<>();
	
	public static String DAGGER_BUFFER;
	public static List<Integer> DAGGER_BUFFER_LIST = new ArrayList<>();
	
	public static String RANDOM_BUFFER;
	public static List<Integer> RANDOM_BUFFER_LIST = new ArrayList<>();
	
	//Henna
	public static boolean ALLOW_FAKE_PLAYERS_HENNA;
	
	public static String NUKER_HENNA;
	public static List<Integer> NUKER_HENNA_LIST = new ArrayList<>();
	
	public static String ARCHER_HENNA;
	public static List<Integer> ARCHER_HENNA_LIST = new ArrayList<>();
	
	public static String WARRIOR_HENNA;
	public static List<Integer> WARRIOR_HENNA_LIST = new ArrayList<>();
	
	public static String DAGGER_HENNA;
	public static List<Integer> DAGGER_HENNA_LIST = new ArrayList<>();
	
	public static String TANKER_HENNA;
	public static List<Integer> TANKER_HENNA_LIST = new ArrayList<>();
	
	//Potion Skills
	public static String FAKE_POTIONS;
	public static ArrayList<Integer> FAKE_POTIONS_SKILLS = new ArrayList<>();
	
	//Events
	public static boolean ALLOW_FAKE_PLAYER_TVT;
	public static int TVT_FAKE_PLAYER_COUNT_MIN;
	public static int TVT_FAKE_PLAYER_COUNT_MAX;
	public static List<Location> TVT_FAKE_PLAYER_LIST_LOCS = new ArrayList<>();
	
	public static boolean ALLOW_FAKE_PLAYER_CTF;
	public static int CTF_FAKE_PLAYER_COUNT_MIN;
	public static int CTF_FAKE_PLAYER_COUNT_MAX;
	public static List<Location> CTF_FAKE_PLAYER_LIST_LOCS = new ArrayList<>();
	
	public static boolean ALLOW_FAKE_PLAYER_DM;
	public static int DM_FAKE_PLAYER_COUNT_MIN;
	public static int DM_FAKE_PLAYER_COUNT_MAX;
	public static List<Location> DM_FAKE_PLAYER_LIST_LOCS = new ArrayList<>();
	
	public static boolean ALLOW_FAKE_PLAYER_LM;
	public static int LM_FAKE_PLAYER_COUNT_MIN;
	public static int LM_FAKE_PLAYER_COUNT_MAX;
	public static List<Location> LM_FAKE_PLAYER_LIST_LOCS = new ArrayList<>();
	
	public static boolean ALLOW_FAKE_PLAYER_KTB;
	public static int KTB_FAKE_PLAYER_COUNT_MIN;
	public static int KTB_FAKE_PLAYER_COUNT_MAX;
	public static List<Location> KTB_FAKE_PLAYER_LIST_LOCS = new ArrayList<>();
	
	public static boolean ALLOW_FAKE_PLAYER_TOURNAMENT;
	public static int TOURNAMENT_FAKE_COUNT_MIN;
	public static int TOURNAMENT_FAKE_COUNT_MAX;
	public static List<Location> FAKE_TOURNAMENT_LIST_LOCS = new ArrayList<>();
	
	public static boolean ALLOW_FAKE_PLAYER_AUTO_SPAWN;
	public static int AUTO_SPAWN_FAKE_COUNT_MIN;
	public static int AUTO_SPAWN_FAKE_COUNT_MAX;
	public static List<Location> FAKE_AUTO_SPAWN_LIST_LOCS = new ArrayList<>();
	
	public static void init()
	{
		ExProperties phantom = load(PHANTOM_FILE);
	
		//Social
		FAKE_CHANCE_TO_TALK_SOCIAL = phantom.getProperty("FakeTalkChance", 3000);
		FAKE_CHANCE_TO_TALK_DIED = phantom.getProperty("FakeTalkChanceDied", 3000);
		FAKE_CHANCE_TO_TALK_KILLED = phantom.getProperty("FakeTalkChanceKilled", 3000);
		FAKE_SOCIAL_CHANCE = phantom.getProperty("FakeSocialChance", 3000);
		FAKE_SIT_CHANCE = phantom.getProperty("FakeSitChance", 10);
		FAKE_PLAYER_ALLOWED_NPC_TO_WALK = phantom.getProperty("FakeRoamingNpcs", new int[]{});
		
		//Interactions
		FAKE_PLAYERS_DEBUG = phantom.getProperty("FakePlayerDebug", false);
		
		FAKE_PLAYER_ROAMING_MAX_WH_CHECKS = phantom.getProperty("FakeRoamingMaxWhChecks", 2);
		FAKE_PLAYER_ROAMING_MAX_SHOP_CHECKS = phantom.getProperty("FakeRoamingMaxShopChecks", 2);
		FAKE_PLAYER_ROAMING_MAX_TELEPORT_CHECKS = phantom.getProperty("FakeRoamingMaxTeleportChecks", 2);
		FAKE_PLAYER_ROAMING_MAX_BUFFER_CHECKS = phantom.getProperty("FakeRoamingMaxBufferChecks", 2);
		FAKE_PLAYER_ROAMING_MAX_PLAYER_CHECKS = phantom.getProperty("FakeRoamingMaxPlayerChecks", 2);
		FAKE_PLAYER_ROAMING_MAX_PL_STORE_CHECKS = phantom.getProperty("FakeRoamingMaxPlayerStoreChecks", 2);
		
		FAKE_PLAYER_WH_CHECK_CHANCE = phantom.getProperty("FakeWarehouseChecksChance", 2);
		FAKE_PLAYER_SHOP_CHECK_CHANCE = phantom.getProperty("FakeShopChecksChance", 2);
		FAKE_PLAYER_TELEPORT_CHECK_CHANCE = phantom.getProperty("FakeTeleportChecksChance", 2);
		FAKE_PLAYER_BUFFER_CHECK_CHANCE = phantom.getProperty("FakeBufferChecksChance", 2);
		FAKE_PLAYER_RELAX_CHECK_CHANCE = phantom.getProperty("FakeRelaxChecksChance", 2);
		FAKE_PLAYER_WALK_CHECK_CHANCE = phantom.getProperty("FakeWalkAroundChecksChance", 2);
		FAKE_PLAYER_PLAYER_CHECK_CHANCE = phantom.getProperty("FakePlayerAroundChecksChance", 2);
		FAKE_PLAYER_PL_STORE_CHECK_CHANCE = phantom.getProperty("FakePlayerStoreAroundChecksChance", 2);
		
		//Private Store
		FAKE_PLAYER_PRIVATE_BUY_LIST = parseBuyList(phantom, "FakePrivateBuyList");
		FAKE_PLAYER_PRIVATE_SELL_LIST = parseSellList(phantom, "FakePrivateSellList");
		
		//Protection
		CHECK_FAKE_PLAYERS_AREA = phantom.getProperty("AllowFakePlayerCheck", false);
		CHECK_FAKE_PLAYERS_START_TIME = phantom.getProperty("FakeCheckStartTime", 1);
		CHECK_FAKE_PLAYERS_RESTART_TIME = phantom.getProperty("FakeCheckRestartTime", 1);
		
		//Consumables
		FAKE_PLAYER_ARROW = phantom.getProperty("FakePlayerArrow", 0);
		FAKE_PLAYER_SOULSHOT = phantom.getProperty("FakePlayerSoulShot", 0);
		FAKE_PLAYER_BLESSED_SOULSHOT = phantom.getProperty("FakePlayerBlessedSoulShot", 0);
		
		//Timer
		DESPAWN_CITIZEN_RANDOM_TIME_1 = phantom.getProperty("FakeCitizenDespawnMinTime", 1);
		DESPAWN_CITIZEN_RANDOM_TIME_2 = phantom.getProperty("FakeCitizenDespawnMaxTime", 1);
		
		DESPAWN_PVP_RANDOM_TIME_1 = phantom.getProperty("FakePvpDespawnMinTime", 1);
		DESPAWN_PVP_RANDOM_TIME_2 = phantom.getProperty("FakePvpDespawnMaxTime", 1);
		
		//Color
		FAKE_PLAYER_COLOR_NAME = phantom.getProperty("FakePlayerColorName", "");
		FAKE_PLAYER_COLOR_TITLE = phantom.getProperty("FakePlayerColorTitle", "");
		
		//Title
		FAKE_PLAYER_FIXED_TITLE = phantom.getProperty("FakePlayerTitle", "");
		
		//Clan
		CLAN_ID = phantom.getProperty("FakeClanIDList", "");
		LIST_CLAN_ID = new ArrayList<>();
		for (String itemId : CLAN_ID.split(","))
			LIST_CLAN_ID.add(Integer.parseInt(itemId));
		
		//Enchant
		MIN_ENCHANT_ARMOR = phantom.getProperty("MinEnchantAmor", 0);
		MAX_ENCHANT_ARMOR = phantom.getProperty("MaxEnchantAmor", 0);
		
		MIN_ENCHANT_WEAPON = phantom.getProperty("MinEnchantWeapon", 0);
		MAX_ENCHANT_WEAPON = phantom.getProperty("MaxEnchantWeapon", 0);
		
		MIN_ENCHANT_JEWEL = phantom.getProperty("MinEnchantJewel", 0);
		MAX_ENCHANT_JEWEL = phantom.getProperty("MaxEnchantJewel", 0);
		
		//Armors Robe
		PHANTOM_ROB_ARMOR_1 = phantom.getProperty("ListRobeArmor1", "0");
		LIST_PHANTOM_ROB_ARMOR_1 = new ArrayList<>();
		for (String listid : PHANTOM_ROB_ARMOR_1.split(","))
		{
			LIST_PHANTOM_ROB_ARMOR_1.add(Integer.parseInt(listid));
		}
		
		PHANTOM_ROB_ARMOR_2 = phantom.getProperty("ListRobeArmor2", "0");
		LIST_PHANTOM_ROB_ARMOR_2 = new ArrayList<>();
		for (String listid : PHANTOM_ROB_ARMOR_2.split(","))
		{
			LIST_PHANTOM_ROB_ARMOR_2.add(Integer.parseInt(listid));
		}
		
		PHANTOM_ROB_ARMOR_3 = phantom.getProperty("ListRobeArmor3", "0");
		LIST_PHANTOM_ROB_ARMOR_3 = new ArrayList<>();
		for (String listid : PHANTOM_ROB_ARMOR_3.split(","))
		{
			LIST_PHANTOM_ROB_ARMOR_3.add(Integer.parseInt(listid));
		}
		
		PHANTOM_ROB_ARMOR_4 = phantom.getProperty("ListRobeArmor4", "0");
		LIST_PHANTOM_ROB_ARMOR_4 = new ArrayList<>();
		for (String listid : PHANTOM_ROB_ARMOR_4.split(","))
		{
			LIST_PHANTOM_ROB_ARMOR_4.add(Integer.parseInt(listid));
		}
		
		//Armors Heavy
		PHANTOM_HEAVY_ARMOR_1 = phantom.getProperty("ListHeavyArmor1", "0");
		LIST_PHANTOM_HEAVY_ARMOR_1 = new ArrayList<>();
		for (String listid : PHANTOM_HEAVY_ARMOR_1.split(","))
		{
			LIST_PHANTOM_HEAVY_ARMOR_1.add(Integer.parseInt(listid));
		}
		
		PHANTOM_HEAVY_ARMOR_2 = phantom.getProperty("ListHeavyArmor2", "0");
		LIST_PHANTOM_HEAVY_ARMOR_2 = new ArrayList<>();
		for (String listid : PHANTOM_HEAVY_ARMOR_2.split(","))
		{
			LIST_PHANTOM_HEAVY_ARMOR_2.add(Integer.parseInt(listid));
		}
		
		PHANTOM_HEAVY_ARMOR_3 = phantom.getProperty("ListHeavyArmor3", "0");
		LIST_PHANTOM_HEAVY_ARMOR_3 = new ArrayList<>();
		for (String listid : PHANTOM_HEAVY_ARMOR_3.split(","))
		{
			LIST_PHANTOM_HEAVY_ARMOR_3.add(Integer.parseInt(listid));
		}
		
		PHANTOM_HEAVY_ARMOR_4 = phantom.getProperty("ListHeavyArmor4", "0");
		LIST_PHANTOM_HEAVY_ARMOR_4 = new ArrayList<>();
		for (String listid : PHANTOM_HEAVY_ARMOR_4.split(","))
		{
			LIST_PHANTOM_HEAVY_ARMOR_4.add(Integer.parseInt(listid));
		}
		
		//Armors Light
		PHANTOM_LIGHT_ARMOR_1 = phantom.getProperty("ListLightArmor1", "0");
		LIST_PHANTOM_LIGHT_ARMOR_1 = new ArrayList<>();
		for (String listid : PHANTOM_LIGHT_ARMOR_1.split(","))
		{
			LIST_PHANTOM_LIGHT_ARMOR_1.add(Integer.parseInt(listid));
		}
		
		PHANTOM_LIGHT_ARMOR_2 = phantom.getProperty("ListLightArmor2", "0");
		LIST_PHANTOM_LIGHT_ARMOR_2 = new ArrayList<>();
		for (String listid : PHANTOM_LIGHT_ARMOR_2.split(","))
		{
			LIST_PHANTOM_LIGHT_ARMOR_2.add(Integer.parseInt(listid));
		}
		
		PHANTOM_LIGHT_ARMOR_3 = phantom.getProperty("ListLightArmor3", "0");
		LIST_PHANTOM_LIGHT_ARMOR_3 = new ArrayList<>();
		for (String listid : PHANTOM_LIGHT_ARMOR_3.split(","))
		{
			LIST_PHANTOM_LIGHT_ARMOR_3.add(Integer.parseInt(listid));
		}
		
		PHANTOM_LIGHT_ARMOR_4 = phantom.getProperty("ListLightArmor4", "0");
		LIST_PHANTOM_LIGHT_ARMOR_4 = new ArrayList<>();
		for (String listid : PHANTOM_LIGHT_ARMOR_4.split(","))
		{
			LIST_PHANTOM_LIGHT_ARMOR_4.add(Integer.parseInt(listid));
		}
		
		//Bow Fake
		FAKE_BOW_ID = phantom.getProperty("FakeBowIDList", "");
		LIST_FAKE_BOW = new ArrayList<>();
		for (String itemId : FAKE_BOW_ID.split(","))
		{
			LIST_FAKE_BOW.add(Integer.parseInt(itemId));
		}

		//Dagger Fake
		FAKE_DAGGER_ID = phantom.getProperty("FakeDaggerIDList", "");
		LIST_FAKE_DAGGER = new ArrayList<>();
		for (String itemId : FAKE_DAGGER_ID.split(","))
		{
			LIST_FAKE_DAGGER.add(Integer.parseInt(itemId));
		}
		
		//Sword Fake
		FAKE_SWORD_ID = phantom.getProperty("FakeSwordIDList", "");
		LIST_FAKE_SWORD = new ArrayList<>();
		for (String itemId : FAKE_SWORD_ID.split(","))
		{
			LIST_FAKE_SWORD.add(Integer.parseInt(itemId));
		}
		
		//Spear Fake
		FAKE_SPEAR_ID = phantom.getProperty("FakeSpearIDList", "");
		LIST_FAKE_SPEAR = new ArrayList<>();
		for (String itemId : FAKE_SPEAR_ID.split(","))
		{
			LIST_FAKE_SPEAR.add(Integer.parseInt(itemId));
		}
		
		//Dual Fake
		FAKE_DUAL_ID = phantom.getProperty("FakeDualIDList", "");
		LIST_FAKE_DUAL = new ArrayList<>();
		for (String itemId : FAKE_DUAL_ID.split(","))
		{
			LIST_FAKE_DUAL.add(Integer.parseInt(itemId));
		}
		
		//Fist Fake
		FAKE_FIST_ID = phantom.getProperty("FakeFistIDList", "");
		LIST_FAKE_FIST = new ArrayList<>();
		for (String itemId : FAKE_FIST_ID.split(","))
		{
			LIST_FAKE_FIST.add(Integer.parseInt(itemId));
		}
		
		//BigSword Fake
		FAKE_BIGSWORD_ID = phantom.getProperty("FakeBigSwordIDList", "");
		LIST_FAKE_BIG_SWORD = new ArrayList<>();
		for (String itemId : FAKE_BIGSWORD_ID.split(","))
		{
			LIST_FAKE_BIG_SWORD.add(Integer.parseInt(itemId));
		}
		
		//Magic Fake
		FAKE_MAGIC_ID = phantom.getProperty("FakeMagicWeaponIDList", "");
		LIST_FAKE_MAGIC = new ArrayList<>();
		for (String itemId : FAKE_MAGIC_ID.split(","))
		{
			LIST_FAKE_MAGIC.add(Integer.parseInt(itemId));
		}
		
		//Shield Fake
		FAKE_SHIELD_ID = phantom.getProperty("FakeShieldIDList", "");
		LIST_FAKE_SHIELD = new ArrayList<>();
		for (String itemId : FAKE_SHIELD_ID.split(","))
		{
			LIST_FAKE_SHIELD.add(Integer.parseInt(itemId));
		}
		
		//Jewels Set
		String[] propertySplit = phantom.getProperty("JewelSetList1", "0,0").split(";");
		LIST_PHANTOM_JEWELS_1.clear();
		for (String reward : propertySplit)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2) 
			{
				_log.warning("JewelSetList1[FakePlayerConfig.load()]: invalid config property -> JewelSetList1 \"" + reward + "\"");
			} 
			else 
			{
				try
				{
					LIST_PHANTOM_JEWELS_1.add(new int[] { Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1]) });
				}
				catch (NumberFormatException nfe)
				{
					if (!reward.isEmpty())
					{
						_log.warning("JewelSetList1[FakePlayerConfig.load()]: invalid config property -> JewelSetList1 \"" + reward + "\"");
					}
				}
			}
		}
		propertySplit = phantom.getProperty("JewelSetList2", "0,0").split(";");
		LIST_PHANTOM_JEWELS_2.clear();
		for (String reward : propertySplit)
		{
			String[] rewardSplit = reward.split(",");
			if (rewardSplit.length != 2) 
			{
				_log.warning("JewelSetList2[FakePlayerConfig.load()]: invalid config property -> JewelSetList2 \"" + reward + "\"");
			} 
			else 
			{
				try
				{
					LIST_PHANTOM_JEWELS_2.add(new int[] { Integer.parseInt(rewardSplit[0]), Integer.parseInt(rewardSplit[1]) });
				}
				catch (NumberFormatException nfe)
				{
					if (!reward.isEmpty()) 
					{
						_log.warning("JewelSetList2[FakePlayerConfig.load()]: invalid config property -> JewelSetList2 \"" + reward + "\"");
					}
				}
			}
		}

		//Accessory Fake
		ALLOW_FAKE_PLAYERS_ACCESSORY = phantom.getProperty("AllowFakePlayerAccesory", false);
		FAKE_ACCESSORY_ID = phantom.getProperty("FakeAccessoryIDList", "");
		LIST_FAKE_ACCESSORY = new ArrayList<>();
		for (String itemId : FAKE_ACCESSORY_ID.split(","))
		{
			LIST_FAKE_ACCESSORY.add(Integer.parseInt(itemId));
		}
		ALLOW_FAKE_PLAYERS_ACCESSORY_BY_CLASS = phantom.getProperty("AllowFakePlayerAccesoryByClass", false);
		
		//Buffer List
		NUKER_BUFFER = phantom.getProperty("NukerBufferList", "0");
		NUKER_BUFFER_LIST = new ArrayList<>();
		for (String listid : NUKER_BUFFER.split(","))
		{
			NUKER_BUFFER_LIST.add(Integer.parseInt(listid));
		}

		ARCHER_BUFFER = phantom.getProperty("ArcherBufferList", "0");
		ARCHER_BUFFER_LIST = new ArrayList<>();
		for (String listid : ARCHER_BUFFER.split(","))
		{
			ARCHER_BUFFER_LIST.add(Integer.parseInt(listid));
		}

		DAGGER_BUFFER = phantom.getProperty("DaggerBufferList", "0");
		DAGGER_BUFFER_LIST = new ArrayList<>();
		for (String listid : DAGGER_BUFFER.split(","))
		{
			DAGGER_BUFFER_LIST.add(Integer.parseInt(listid));
		}

		WARRIOR_BUFFER = phantom.getProperty("WarriorBufferList", "0");
		WARRIOR_BUFFER_LIST = new ArrayList<>();
		for (String listid : WARRIOR_BUFFER.split(","))
		{
			WARRIOR_BUFFER_LIST.add(Integer.parseInt(listid));
		}

		TANKER_BUFFER = phantom.getProperty("TankerBufferList", "0");
		TANKER_BUFFER_LIST = new ArrayList<>();
		for (String listid : TANKER_BUFFER.split(","))
		{
			TANKER_BUFFER_LIST.add(Integer.parseInt(listid));
		}
		
		RANDOM_BUFFER = phantom.getProperty("RandomBufferList", "0");
		RANDOM_BUFFER_LIST = new ArrayList<>();
		for (String listid : RANDOM_BUFFER.split(","))
		{
			RANDOM_BUFFER_LIST.add(Integer.parseInt(listid));
		}
		
		//Henna List
		ALLOW_FAKE_PLAYERS_HENNA = phantom.getProperty("AllowFakePlayerHenna", false);
		NUKER_HENNA = phantom.getProperty("NukerHennaList", "0");
		NUKER_HENNA_LIST = new ArrayList<>();
		for (String listid : NUKER_HENNA.split(","))
		{
			NUKER_HENNA_LIST.add(Integer.parseInt(listid));
		}

		ARCHER_HENNA = phantom.getProperty("ArcherHennaList", "0");
		ARCHER_HENNA_LIST = new ArrayList<>();
		for (String listid : ARCHER_HENNA.split(","))
		{
			ARCHER_HENNA_LIST.add(Integer.parseInt(listid));
		}

		DAGGER_HENNA = phantom.getProperty("DaggerHennaList", "0");
		DAGGER_HENNA_LIST = new ArrayList<>();
		for (String listid : DAGGER_HENNA.split(","))
		{
			DAGGER_HENNA_LIST.add(Integer.parseInt(listid));
		}

		TANKER_HENNA = phantom.getProperty("TankerHennaList", "0");
		TANKER_HENNA_LIST = new ArrayList<>();
		for (String listid : TANKER_HENNA.split(","))
		{
			TANKER_HENNA_LIST.add(Integer.parseInt(listid));
		}
		
		WARRIOR_HENNA = phantom.getProperty("WarriorHennaList", "0");
		WARRIOR_HENNA_LIST = new ArrayList<>();
		for (String listid : WARRIOR_HENNA.split(","))
		{
			WARRIOR_HENNA_LIST.add(Integer.parseInt(listid));
		}

		//Potion Skills
		FAKE_POTIONS = phantom.getProperty("PotionSkills", "0");
		FAKE_POTIONS_SKILLS = new ArrayList<>();
		for (String id : FAKE_POTIONS.trim().split(","))
		{
			FAKE_POTIONS_SKILLS.add(Integer.parseInt(id.trim()));
		}
		
		//Events
		ALLOW_FAKE_PLAYER_TVT = phantom.getProperty("TvTAllowFakePlayer", false);
		TVT_FAKE_PLAYER_COUNT_MIN = phantom.getProperty("TvTFakePlayerCountMin", 5);
		TVT_FAKE_PLAYER_COUNT_MAX = phantom.getProperty("TvTFakePlayerCountMax", 5);
		String[] returnTvTLocations = phantom.getProperty("TvTFakePlayerSpawnLocs", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnTvTLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			TVT_FAKE_PLAYER_LIST_LOCS.add(locToAdd);
		}
		
		ALLOW_FAKE_PLAYER_CTF = phantom.getProperty("CTFAllowFakePlayer", false);
		CTF_FAKE_PLAYER_COUNT_MIN = phantom.getProperty("CTFFakePlayerCountMin", 5);
		CTF_FAKE_PLAYER_COUNT_MAX = phantom.getProperty("CTFFakePlayerCountMax", 5);
		String[] returnCTFLocations = phantom.getProperty("CTFFakePlayerSpawnLocs", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnCTFLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			CTF_FAKE_PLAYER_LIST_LOCS.add(locToAdd);
		}
		
		ALLOW_FAKE_PLAYER_DM = phantom.getProperty("DMAllowFakePlayer", false);
		DM_FAKE_PLAYER_COUNT_MIN = phantom.getProperty("DMFakePlayerCountMin", 5);
		DM_FAKE_PLAYER_COUNT_MAX = phantom.getProperty("DMFakePlayerCountMax", 5);
		String[] returnDMLocations = phantom.getProperty("DMFakePlayerSpawnLocs", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnDMLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			DM_FAKE_PLAYER_LIST_LOCS.add(locToAdd);
		}

		ALLOW_FAKE_PLAYER_LM = phantom.getProperty("LMAllowFakePlayer", false);
		LM_FAKE_PLAYER_COUNT_MIN = phantom.getProperty("LMFakePlayerCountMin", 5);
		LM_FAKE_PLAYER_COUNT_MAX = phantom.getProperty("LMFakePlayerCountMax", 5);
		String[] returnLMLocations = phantom.getProperty("LMFakePlayerSpawnLocs", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnLMLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			LM_FAKE_PLAYER_LIST_LOCS.add(locToAdd);
		}

		ALLOW_FAKE_PLAYER_KTB = phantom.getProperty("KTBAllowFakePlayer", false);
		KTB_FAKE_PLAYER_COUNT_MIN = phantom.getProperty("KTBFakePlayerCountMin", 5);
		KTB_FAKE_PLAYER_COUNT_MAX = phantom.getProperty("KTBFakePlayerCountMax", 5);
		String[] returnKTBLocations = phantom.getProperty("KTBFakePlayerSpawnLocs", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnKTBLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			KTB_FAKE_PLAYER_LIST_LOCS.add(locToAdd);
		}
		
		ALLOW_FAKE_PLAYER_TOURNAMENT = phantom.getProperty("TournamentAllowFakePlayer", false);
		TOURNAMENT_FAKE_COUNT_MIN = phantom.getProperty("TournamentFakesCountMin", 5);
		TOURNAMENT_FAKE_COUNT_MAX = phantom.getProperty("TournamentFakesCountMax", 5);
		String[] returnLocations = phantom.getProperty("SpawnLocationsTour", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			FAKE_TOURNAMENT_LIST_LOCS.add(locToAdd);
		}
		
		ALLOW_FAKE_PLAYER_AUTO_SPAWN = phantom.getProperty("AutoSpawnAllowFakePlayer", false);
		AUTO_SPAWN_FAKE_COUNT_MIN = phantom.getProperty("AutoSpawnFakesCountMin", 5);
		AUTO_SPAWN_FAKE_COUNT_MAX = phantom.getProperty("AutoSpawnFakesCountMax", 5);
		String[] returnAutoLocations = phantom.getProperty("AutoSpawnLocations", "82698,148638,-3473;82698,148638,-3473").split(";");
		for (String location : returnAutoLocations)
		{
			String[] coords = location.split(",");
			int x = Integer.parseInt(coords[0]);
			int y = Integer.parseInt(coords[1]);
			int z = Integer.parseInt(coords[2]);
			Location locToAdd = new Location(x, y, z);
			FAKE_AUTO_SPAWN_LIST_LOCS.add(locToAdd);
		}
	}
	
	public static List<FakePrivateBuyHolder> parseBuyList(ExProperties propertie, String configName)
	{
		List<FakePrivateBuyHolder> auxReturn = new ArrayList<>();
		
		String aux = propertie.getProperty(configName).trim();
		for (String randomReward : aux.split(";"))
		{
			final String[] infos = randomReward.split(",");

			if (infos.length > 5)
				auxReturn.add(new FakePrivateBuyHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2]), Integer.valueOf(infos[3]), Integer.valueOf(infos[4]), Integer.valueOf(infos[5])));
			else
				auxReturn.add(new FakePrivateBuyHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2]), Integer.valueOf(infos[3]), Integer.valueOf(infos[4])));
		}
		return auxReturn;
	}
	
	public static List<FakePrivateSellHolder> parseSellList(ExProperties propertie, String configName)
	{
		List<FakePrivateSellHolder> auxReturn = new ArrayList<>();
		
		String aux = propertie.getProperty(configName).trim();
		for (String randomReward : aux.split(";"))
		{
			final String[] infos = randomReward.split(",");

			if (infos.length > 3)
				auxReturn.add(new FakePrivateSellHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2]), Integer.valueOf(infos[3])));
			else
				auxReturn.add(new FakePrivateSellHolder(Integer.valueOf(infos[0]), Integer.valueOf(infos[1]), Integer.valueOf(infos[2])));
		}
		return auxReturn;
	}
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}

	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();

		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}

		return result;
	}
}