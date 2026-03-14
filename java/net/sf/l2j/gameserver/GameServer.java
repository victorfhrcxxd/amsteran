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
package net.sf.l2j.gameserver;

import hwid.Hwid;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.Server;
import net.sf.l2j.Team;
import net.sf.l2j.commons.mmocore.SelectorConfig;
import net.sf.l2j.commons.mmocore.SelectorThread;
import net.sf.l2j.gameserver.cache.CrestCache;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.cache.ImagesCache;
import net.sf.l2j.gameserver.communitybbs.Manager.ForumsBBSManager;
import net.sf.l2j.gameserver.datatables.AccessLevels;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.datatables.ArmorSetsTable;
import net.sf.l2j.gameserver.datatables.AugmentationData;
import net.sf.l2j.gameserver.datatables.BookmarkTable;
import net.sf.l2j.gameserver.datatables.BufferTable;
import net.sf.l2j.gameserver.datatables.BuyListTable;
import net.sf.l2j.gameserver.datatables.CharNameTable;
import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.datatables.ClanTable;
import net.sf.l2j.gameserver.datatables.DoorTable;
import net.sf.l2j.gameserver.datatables.FenceTable;
import net.sf.l2j.gameserver.datatables.FishTable;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.HelperBuffTable;
import net.sf.l2j.gameserver.datatables.HennaTable;
import net.sf.l2j.gameserver.datatables.HerbDropTable;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.MultisellData;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.NpcWalkerRoutesTable;
import net.sf.l2j.gameserver.datatables.PetDataTable;
import net.sf.l2j.gameserver.datatables.RecipeTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTreeTable;
import net.sf.l2j.gameserver.datatables.SoulCrystalData;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.datatables.SpellbookTable;
import net.sf.l2j.gameserver.datatables.StaticObjects;
import net.sf.l2j.gameserver.datatables.SummonItemsData;
import net.sf.l2j.gameserver.datatables.TeleportLocationTable;
import net.sf.l2j.gameserver.datatables.custom.AntiBotTable;
import net.sf.l2j.gameserver.datatables.custom.EnchantTable;
import net.sf.l2j.gameserver.datatables.custom.FakePcsTable;
import net.sf.l2j.gameserver.datatables.custom.DollsTable;
import net.sf.l2j.gameserver.datatables.custom.DressMeData;
import net.sf.l2j.gameserver.datatables.custom.IconTable;
import net.sf.l2j.gameserver.datatables.custom.OfflineTradersTable;
import net.sf.l2j.gameserver.datatables.custom.RaidSpawnTable;
import net.sf.l2j.gameserver.datatables.custom.SkinTable;
import net.sf.l2j.gameserver.datatables.custom.SkipTable;
import net.sf.l2j.gameserver.geoengine.GeoData;
import net.sf.l2j.gameserver.geoengine.PathFinding;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.ChatHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.SkillHandler;
import net.sf.l2j.gameserver.handler.TutorialHandler;
import net.sf.l2j.gameserver.handler.UserCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.handler.community.dailyreward.DailyRewardCBManager;
import net.sf.l2j.gameserver.handler.password.PasswordChanger;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.instancemanager.AuctionManager;
import net.sf.l2j.gameserver.instancemanager.AutoSpawnManager;
import net.sf.l2j.gameserver.instancemanager.BoatManager;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.CastleManorManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.instancemanager.ClassDamageManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.instancemanager.DayNightSpawnManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.FishingChampionshipManager;
import net.sf.l2j.gameserver.instancemanager.FourSepulchersManager;
import net.sf.l2j.gameserver.instancemanager.GrandBossManager;
import net.sf.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import net.sf.l2j.gameserver.instancemanager.MercTicketManager;
import net.sf.l2j.gameserver.instancemanager.OfflineFarmManager;
import net.sf.l2j.gameserver.instancemanager.MovieMakerManager;
import net.sf.l2j.gameserver.instancemanager.OlyClassDamageManager;
import net.sf.l2j.gameserver.instancemanager.PetitionManager;
import net.sf.l2j.gameserver.instancemanager.QuestManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossInfoManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossPointsManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.SevenSignsFestival;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.ZoneManager;
import net.sf.l2j.gameserver.instancemanager.custom.BalanceManager;
import net.sf.l2j.gameserver.instancemanager.custom.CharacterKillingManager;
import net.sf.l2j.gameserver.instancemanager.custom.DailyRewardManager;
import net.sf.l2j.gameserver.instancemanager.custom.HwidManager;
import net.sf.l2j.gameserver.instancemanager.custom.IPManager;
import net.sf.l2j.gameserver.instancemanager.custom.TimeInstanceManager;
import net.sf.l2j.gameserver.instancemanager.custom.ZergManager;
import net.sf.l2j.gameserver.instancemanager.games.MonsterRace;
import net.sf.l2j.gameserver.instancemanager.timeditem.ItemTimeManager;
import net.sf.l2j.gameserver.model.L2Manor;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2FenceInstance;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.entity.events.ColorSystem;
import net.sf.l2j.gameserver.model.entity.events.Hide;
import net.sf.l2j.gameserver.model.entity.events.MissionReset;
import net.sf.l2j.gameserver.model.entity.events.PCBangPoint;
import net.sf.l2j.gameserver.model.entity.events.PvpProtection;
import net.sf.l2j.gameserver.model.entity.events.SoloBossEvent;
import net.sf.l2j.gameserver.model.entity.events.TopKillerRoundSystem;
import net.sf.l2j.gameserver.model.entity.events.toppvpevent.PvPEventManager;
import net.sf.l2j.gameserver.model.entity.events.bonuzone.BonusZoneManager;
import net.sf.l2j.gameserver.model.entity.events.bonuzone.BonusZoneReward;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFConfig;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFManager;
import net.sf.l2j.gameserver.model.entity.events.clanranking.ClanRankingConfig;
import net.sf.l2j.gameserver.model.entity.events.clanranking.ClanRankingManager;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMConfig;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMManager;
import net.sf.l2j.gameserver.model.entity.events.demonzone.DemonZoneManager;
import net.sf.l2j.gameserver.model.entity.events.demonzone.DemonZoneReward;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSConfig;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSManager;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBConfig;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBManager;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMConfig;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMManager;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTConfig;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTManager;
import net.sf.l2j.gameserver.model.entity.events.partyzone.PartyZoneManager;
import net.sf.l2j.gameserver.model.entity.events.partyzone.PartyZoneReward;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTConfig;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTManager;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaConfig;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaEvent;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaTask;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadGameManager;
import net.sf.l2j.gameserver.model.partymatching.PartyMatchRoomList;
import net.sf.l2j.gameserver.model.partymatching.PartyMatchWaitingList;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.L2GamePacketHandler;
import net.sf.l2j.gameserver.script.EventDroplist;
import net.sf.l2j.gameserver.script.faenor.FaenorScriptEngine;
import net.sf.l2j.gameserver.scripting.L2ScriptEngineManager;
import net.sf.l2j.gameserver.taskmanager.ItemsAutoDestroyTaskManager;
import net.sf.l2j.gameserver.taskmanager.KnownListUpdateTaskManager;
import net.sf.l2j.gameserver.taskmanager.TaskManager;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;
import net.sf.l2j.util.DeadLockDetector;
import net.sf.l2j.util.IPv4Filter;
import net.sf.l2j.util.Util;
import phantom.FakePlayerConfig;
import phantom.FakePlayerManager;
import phantom.task.ThreadPool;

public class GameServer
{
	private static final Logger _log = Logger.getLogger(GameServer.class.getName());
	
	private final SelectorThread<L2GameClient> _selectorThread;
	private final L2GamePacketHandler _gamePacketHandler;
	private final DeadLockDetector _deadDetectThread;
	public static GameServer gameServer;
	private final LoginServerThread _loginThread;
	public static final Calendar dateTimeServerStarted = Calendar.getInstance();
	
	public long getUsedMemoryMB()
	{
		return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576; // 1024 * 1024 = 1048576;
	}
	
	public SelectorThread<L2GameClient> getSelectorThread()
	{
		return _selectorThread;
	}
	
	public GameServer() throws Exception
	{
		gameServer = this;
		
		IdFactory.getInstance();
		ThreadPoolManager.getInstance();
		ThreadPool.init();
		
		new File("./data/crests").mkdirs();
		
		Util.printSection("World");
		GameTimeController.getInstance();
		L2World.getInstance();
		MapRegionTable.getInstance();
		AnnouncementTable.getInstance();
		BookmarkTable.getInstance();
        EventDroplist.getInstance();
        FaenorScriptEngine.getInstance();
        
		Util.printSection("Skills");
		SkillTable.getInstance();
		SkillTreeTable.getInstance();
		
		Util.printSection("Items");
		IconTable.getInstance();
		DressMeData.getInstance();
		ItemTable.getInstance();
		SummonItemsData.getInstance();
		BuyListTable.getInstance();
		MultisellData.getInstance();
		RecipeTable.getInstance();
		ArmorSetsTable.getInstance();
		FishTable.getInstance();
		SpellbookTable.getInstance();
		EnchantTable.getInstance();
		SoulCrystalData.getInstance();
		SkinTable.getInstance();
		DollsTable.getInstance();
	    RaidSpawnTable.getInstance();
		ItemTimeManager.getInstance();
        TimeInstanceManager.getInstance();
		
		Util.printSection("Augments");
		AugmentationData.getInstance();
		
		Util.printSection("Characters");
		AccessLevels.getInstance();
		AdminCommandAccessRights.getInstance();
		CharTemplateTable.getInstance();
		CharNameTable.getInstance();
		GmListTable.getInstance();
		RaidBossPointsManager.getInstance();
		
		Util.printSection("Community server");
		if (Config.ENABLE_COMMUNITY_BOARD) // Forums has to be loaded before clan data
			ForumsBBSManager.getInstance().initRoot();
		else
			_log.config("Community server is disabled.");
		
		Util.printSection("Cache");
		HtmCache.getInstance();
		CrestCache.load();
		ImagesCache.getInstance();
		TeleportLocationTable.getInstance();
		PartyMatchWaitingList.getInstance();
		PartyMatchRoomList.getInstance();
		PetitionManager.getInstance();
		HennaTable.getInstance();
		HelperBuffTable.getInstance();
		CursedWeaponsManager.getInstance();
		SkipTable.getInstance();
		
		Util.printSection("Clans");
		ClanTable.getInstance();
		AuctionManager.getInstance();
		ClanHallManager.getInstance();
		
		Util.printSection("Geodata & Pathfinding");
		GeoData.initialize();
		PathFinding.initialize();
		
		Util.printSection("World Bosses");
		GrandBossManager.getInstance();
		
		Util.printSection("Zones");
		ZoneManager.getInstance();
		GrandBossManager.getInstance().initZones();
		
		Util.printSection("Castles");
		CastleManager.getInstance().load();
		
		Util.printSection("Seven Signs");
		SevenSigns.getInstance().spawnSevenSignsNPC();
		SevenSignsFestival.getInstance();
		
		Util.printSection("Sieges");
		SiegeManager.getInstance();
		SiegeManager.getSieges();
		MercTicketManager.getInstance();
		
		Util.printSection("Manor Manager");
		CastleManorManager.getInstance();
		L2Manor.getInstance();
		
		Util.printSection("NPCs");
		BufferTable.getInstance();
		HerbDropTable.getInstance();
		PetDataTable.getInstance();
		NpcTable.getInstance();
		NpcWalkerRoutesTable.getInstance();
		DoorTable.getInstance();
		StaticObjects.load();
		SpawnTable.getInstance();
		RaidBossSpawnManager.getInstance();
		DayNightSpawnManager.getInstance().trim().notifyChangeMode();
		DimensionalRiftManager.getInstance();
		FakePcsTable.getInstance();
		RaidBossInfoManager.getInstance();
		
		Util.printSection("Olympiads & Heroes");
		OlympiadGameManager.getInstance();
		Olympiad.getInstance();
		Hero.getInstance();
		
		Util.printSection("Four Sepulchers");
		FourSepulchersManager.getInstance().init();
		
		Util.printSection("Quests & Scripts");
		QuestManager.getInstance();
		BoatManager.getInstance();
		
		if (!Config.ALT_DEV_NO_SCRIPTS)
		{
			try
			{
				File scripts = new File("./data/scripts.cfg");
				L2ScriptEngineManager.getInstance().executeScriptList(scripts);
			}
			catch (IOException ioe)
			{
				_log.severe("Failed loading scripts.cfg, no script going to be loaded");
			}
			QuestManager.getInstance().report();
		}
		else
			_log.config("QuestManager: Skipping scripts.");
		
		if (Config.SAVE_DROPPED_ITEM)
			ItemsOnGroundManager.getInstance();
		
		if (Config.ITEM_AUTO_DESTROY_TIME > 0 || Config.HERB_AUTO_DESTROY_TIME > 0)
			ItemsAutoDestroyTaskManager.getInstance();
		
		Util.printSection("Monster Derby Track");
		MonsterRace.getInstance();
		
		Util.printSection("Handlers");
		_log.config("AutoSpawnHandler: Loaded " + AutoSpawnManager.getInstance().size() + " handlers.");
		_log.config("AdminCommandHandler: Loaded " + AdminCommandHandler.getInstance().size() + " handlers.");
		_log.config("ChatHandler: Loaded " + ChatHandler.getInstance().size() + " handlers.");
		_log.config("ItemHandler: Loaded " + ItemHandler.getInstance().size() + " handlers.");
		_log.config("SkillHandler: Loaded " + SkillHandler.getInstance().size() + " handlers.");
        _log.config("TutorialHandler: Loaded " + TutorialHandler.getInstance().size() + " handlers.");
		_log.config("UserCommandHandler: Loaded " + UserCommandHandler.getInstance().size() + " handlers.");
        _log.config("VoicedCommandHandler: Loaded " + VoicedCommandHandler.getInstance().size() + " handlers.");
		
		if (Config.ALLOW_WEDDING)
			CoupleManager.getInstance();
		
		if (Config.ALT_FISH_CHAMPIONSHIP_ENABLED)
			FishingChampionshipManager.getInstance();

		Util.printSection("Daily Reward");
		DailyRewardCBManager.getInstance();
		
		if (Config.ENABLE_OFFLINE_FARM)
			OfflineFarmManager.getInstance();
		
		Util.printSection("Custom");
        IPManager.getInstance();
        HwidManager.getInstance();
        ZergManager.getInstance();
        PasswordChanger.getInstance();
        AntiBotTable.getInstance().loadImage();
        ColorSystem.getInstance();
        DailyRewardManager.getInstance();
        PartyZoneManager.getInstance();
        PartyZoneReward.getInstance();
        DemonZoneManager.getInstance();
        DemonZoneReward.getInstance();
        BonusZoneManager.getInstance();
        BonusZoneReward.getInstance();
        PvpProtection.getInstance();
        BalanceManager.getInstance();
        
		if (Config.RESET_MISSION_EVENT_ENABLED)
			MissionReset.getInstance().StartNextEventTime();
		
        if (Config.TOP_KILLER_PLAYER_ROUND)
        	TopKillerRoundSystem.getInstance();
        
        if (Config.ALLOW_HIDE_ITEM_EVENT)
        	Hide.getInstance();

 		if (Config.PVP_EVENT_ENABLED)
        	PvPEventManager.getInstance();

 		if (Config.ENABLE_CLASS_DAMAGES)
 			ClassDamageManager.loadConfig();
 		
 		if (Config.ENABLE_OLY_CLASS_DAMAGES)
 			OlyClassDamageManager.loadConfig();
 		
 	    if (Config.PCB_ENABLE)
 	        ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(PCBangPoint.getInstance(), Config.PCB_INTERVAL * 1000, Config.PCB_INTERVAL * 1000);

        if ((Config.OFFLINE_TRADE_ENABLE || Config.OFFLINE_CRAFT_ENABLE) && Config.RESTORE_OFFLINERS)
        	OfflineTradersTable.restoreOfflineTraders();

		if (Config.CKM_ENABLED)
			CharacterKillingManager.getInstance().init();

		Util.printSection("Tournament");
		
		ArenaConfig.init();
		if (ArenaConfig.TOURNAMENT_EVENT_TIME)
		{
			_log.info("Tournament Event is enabled.");
			ArenaEvent.getInstance().StartCalculationOfNextEventTime();
		}
		else if (ArenaConfig.TOURNAMENT_EVENT_START)
		{
			_log.info("Tournament Event is enabled.");
			ArenaTask.spawnNpc1();
			ArenaTask.spawnNpc2();
		}
		else
			_log.info("Tournament Event is disabled");
		
		Util.printSection("Events");

		CTFConfig.init();
		CTFManager.getInstance();
		
		DMConfig.init();
		DMManager.getInstance();

		LMConfig.init();
		LMManager.getInstance();
		
		TvTConfig.init();
		TvTManager.getInstance();
		
		MultiTvTConfig.init();
		MultiTvTManager.getInstance();
		
		KTBConfig.init();
		KTBManager.getInstance();
		
		FOSConfig.init();
		FOSManager.getInstance();
		
		ClanRankingConfig.load();
		ClanRankingManager.getInstance();

		if (Config.SOLO_BOSS_EVENT)
			SoloBossEvent.getInstance().StartCalculationOfNextEventTime();
		
		Util.printSection("Fake Players");
		FakePlayerConfig.init();
		FakePlayerManager.INSTANCE.initialise();

		Util.printSection("HwId");
		Hwid.Init();

		Util.printSection("System");
		TaskManager.getInstance();
		
		Runtime.getRuntime().addShutdownHook(Shutdown.getInstance());
		ForumsBBSManager.getInstance();
		_log.config("IdFactory: Free ObjectIDs remaining: " + IdFactory.getInstance().size());
		
		KnownListUpdateTaskManager.getInstance();
		MovieMakerManager.getInstance();
		
		if (Config.DEADLOCK_DETECTOR)
		{
			_log.info("Deadlock detector is enabled. Timer: " + Config.DEADLOCK_CHECK_INTERVAL + "s.");
			_deadDetectThread = new DeadLockDetector();
			_deadDetectThread.setDaemon(true);
			_deadDetectThread.start();
		}
		else
		{
			_log.info("Deadlock detector is disabled.");
			_deadDetectThread = null;
		}
		
		System.gc();
		
		long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576;
		long totalMem = Runtime.getRuntime().maxMemory() / 1048576;
		
		_log.info("Gameserver have started, used memory: " + usedMem + " / " + totalMem + " Mo.");
		_log.info("Maximum allowed players: " + Config.MAXIMUM_ONLINE_USERS);
		
		Util.printSection("Login");
		_loginThread = LoginServerThread.getInstance();
		_loginThread.start();

		final SelectorConfig sc = new SelectorConfig();
		sc.MAX_READ_PER_PASS = Config.MMO_MAX_READ_PER_PASS;
		sc.MAX_SEND_PER_PASS = Config.MMO_MAX_SEND_PER_PASS;
		sc.SLEEP_TIME = Config.MMO_SELECTOR_SLEEP_TIME;
		sc.HELPER_BUFFER_COUNT = Config.MMO_HELPER_BUFFER_COUNT;
		
		_gamePacketHandler = new L2GamePacketHandler();
		_selectorThread = new SelectorThread<>(sc, _gamePacketHandler, _gamePacketHandler, _gamePacketHandler, new IPv4Filter());
		
		InetAddress bindAddress = null;
		if (!Config.GAMESERVER_HOSTNAME.equals("*"))
		{
			try
			{
				bindAddress = InetAddress.getByName(Config.GAMESERVER_HOSTNAME);
			}
			catch (UnknownHostException e1)
			{
				_log.log(Level.SEVERE, "WARNING: The GameServer bind address is invalid, using all available IPs. Reason: " + e1.getMessage(), e1);
			}
		}
		
		try
		{
			_selectorThread.openServerSocket(bindAddress, Config.PORT_GAME);
		}
		catch (IOException e)
		{
			_log.log(Level.SEVERE, "FATAL: Failed to open server socket. Reason: " + e.getMessage(), e);
			System.exit(1);
		}
		_selectorThread.start();

	}
	
	public static void main(String[] args) throws Exception
	{
		Server.serverMode = Server.MODE_GAMESERVER;
		
		final String LOG_FOLDER = "./log"; // Name of folder for log file
		final String LOG_NAME = "config/log.cfg"; // Name of log file
		
		// Create log folder
		File logFolder = new File(LOG_FOLDER);
		logFolder.mkdir();
		
		// Create input stream for log file -- or store file data into memory
		InputStream is = new FileInputStream(new File(LOG_NAME));
		LogManager.getLogManager().readConfiguration(is);
		is.close();
		
		Util.printSection("Team");

		Team.info();
		
		Util.printSection("L2JMod aCis");
		
		// Initialize config
		Config.load();
		
		// Factories
		XMLDocumentFactory.getInstance();
		L2DatabaseFactory.getInstance();
		
		gameServer = new GameServer();
	}
	
	public static void SpawnFenceInstance()
	{
		//Kill The Boss
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fenceKtb = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, 100, 169592, -89512);
			fenceKtb.spawnMe(169592, -89512, -2912);
			FenceTable.addFence(fenceKtb);
		}
		//Capture The Flag
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fenceCtf = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 500, 13224, 119848);
			fenceCtf.spawnMe(13224, 119848, -12080);
			FenceTable.addFence(fenceCtf);
		}
		//Last Man Standing
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 500, 0, 57227, -29805);
			fence.spawnMe(57227, -29805, 574);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 0, 500, 57399, -29611);
			fence.spawnMe(57399, -29611, 574);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 400, 0, 58591, -29566);
			fence.spawnMe(58591, -29566, 574);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 0, 400, 58720, -29789);
			fence.spawnMe(58720, -29789, 574);
			FenceTable.addFence(fence);
		}
		DoorTable.getInstance().getDoor(21170006).openMe();
		DoorTable.getInstance().getDoor(21170005).openMe();
		DoorTable.getInstance().getDoor(21170004).openMe();
		DoorTable.getInstance().getDoor(21170003).openMe();
		/*
 		//Deathmach
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence1 = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 100, 100, 112639, -16893);
			fence1.spawnMe(112639, -16893, -818);
			FenceTable.addFence(fence1);
			
			L2FenceInstance fence2 = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 100, 100, 112065, -16890);
			fence2.spawnMe(112065, -16890, -818);
			FenceTable.addFence(fence2);
			
			L2FenceInstance fence3 = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 100, 100, 110648, -13642);
			fence3.spawnMe(110648, -13642, -818);
			FenceTable.addFence(fence3);
			
			L2FenceInstance fence4 = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 100, 100, 110048, -13636);
			fence4.spawnMe(110048, -13636, -818);
			FenceTable.addFence(fence4);
		}
		for (int i = 0;i < 2;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 200, -200, 81541, 147388);
			fence.setInstanceId(2);
			fence.spawnMe(81541, 147388, -3474);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 2;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -200, 200, 80563, 148618);
			fence.setInstanceId(2);
			fence.spawnMe(80563, 148618, -3491);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 2;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 200, -200, 81542, 149851);
			fence.setInstanceId(2);
			fence.spawnMe(81542, 149851, -3465);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -300, 200, 84520, 148621);
			fence.setInstanceId(2);
			fence.spawnMe(84520, 148621, -3405);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 2;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 200, -200, 83906, 147002);
			fence.setInstanceId(2);
			fence.spawnMe(83906, 147002, -3400);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 2;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -200, 200, 85882, 147361);
			fence.setInstanceId(2);
			fence.spawnMe(85882, 147361, -3402);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, -200, 85824, 150047);
			fence.setInstanceId(2);
			fence.spawnMe(85824, 150047, -3394);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 600, -100, 111392, 218651);
			fence.setInstanceId(2);
			fence.spawnMe(111392, 218651, -3468);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 600, -200, 111387, 223684);
			fence.setInstanceId(2);
			fence.spawnMe(111387, 223684, -3547);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 200, 113019, 221393);
			fence.setInstanceId(2);
			fence.spawnMe(113019, 221393, -3456);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 200, 109735, 221390);
			fence.setInstanceId(2);
			fence.spawnMe(109735, 221390, -3458);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 100, 100, 109548, 220456);
			fence.setInstanceId(2);
			fence.spawnMe(109548, 220456, -3616);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 400, -100, 110077, 218892);
			fence.setInstanceId(2);
			fence.spawnMe(110077, 218892, -3488);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 400, -100, 112702, 218898);
			fence.setInstanceId(2);
			fence.spawnMe(112702, 218898, -3536);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 400, -100, 147453, 25013);
			fence.setInstanceId(2);
			fence.spawnMe(147453, 25013, -1992);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 600, -100, 146354, 25332);
			fence.setInstanceId(2);
			fence.spawnMe(146354, 25332, -2013);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 600, -100, 148543, 25325);
			fence.setInstanceId(2);
			fence.spawnMe(148543, 25325, -2013);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 800, -100, 147456, 29306);
			fence.setInstanceId(2);
			fence.spawnMe(147456, 29306, -2264);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 400, 148818, 27625);
			fence.setInstanceId(2);
			fence.spawnMe(148818, 27625, -2205);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 400, 146085, 27625);
			fence.setInstanceId(2);
			fence.spawnMe(146085, 27625, -2200);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 300, 146245, 28242);
			fence.setInstanceId(2);
			fence.spawnMe(146245, 28242, -2255);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 300, 148665, 28244);
			fence.setInstanceId(2);
			fence.spawnMe(148665, 28244, -2255);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 300, 146239, 26967);
			fence.setInstanceId(2);
			fence.spawnMe(146239, 26967, -2191);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, -100, 300, 148672, 26967);
			fence.setInstanceId(2);
			fence.spawnMe(148672, 26967, -2184);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, -100, 146664, 28792);
			fence.setInstanceId(2);
			fence.spawnMe(146664, 28792, -2258);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, -100, 148248, 28794);
			fence.setInstanceId(2);
			fence.spawnMe(148248, 28794, -2255);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, -100, 146600, 26453);
			fence.setInstanceId(2);
			fence.spawnMe(146600, 26453, -2191);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, -100, 148313, 26447);
			fence.setInstanceId(2);
			fence.spawnMe(148313, 26447, -2194);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, 100, 117252, -78847);
			fence.setInstanceId(3);
			fence.spawnMe(117252, -78847, -48);
			FenceTable.addFence(fence);
		}
		for (int i = 0;i < 3;i++)
		{
			L2FenceInstance fence = new L2FenceInstance(IdFactory.getInstance().getNextId(), 2, 300, 100, 117245, -75120);
			fence.setInstanceId(3);
			fence.spawnMe(117245, -75120, -45);
			FenceTable.addFence(fence);
		}
		*/
	}
}