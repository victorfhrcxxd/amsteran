package net.sf.l2j.gameserver.handler.admincommandhandlers;

import phantom.FakePlayer;
import phantom.FakePlayerManager;
import phantom.FakePlayerTaskManager;
import phantom.ai.shop.PrivateStoreBuyAI;
import phantom.ai.shop.PrivateStoreSellAI;
import phantom.ai.walker.CitizenAI;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminFakePlayers implements IAdminCommandHandler
{
	private final String fakesFolder = "data/html/admin/";
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_robot",
		"admin_deleterobot",
		"admin_deleteAllrobots",
		"admin_spawncitizen",
		"admin_spawnbuyer",
		"admin_spawnseller",
		"admin_spawnclass",
		"admin_takecontrol",
		"admin_releasecontrol"
	};
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
	
	private void showFakeDashboard(L2PcInstance activeChar)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile(fakesFolder + "fakeplayer.htm");
		html.replace("%fakecount%", FakePlayerManager.getFakePlayersCount());
		html.replace("%taskcount%", FakePlayerTaskManager.INSTANCE.getTaskCount());
		activeChar.sendPacket(html);
	}
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 1)
			return false;

		if (command.startsWith("admin_robot"))
			showFakeDashboard(activeChar);
		
		if (command.startsWith("admin_deleterobot"))
		{
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer)
			{
				FakePlayer fakePlayer = (FakePlayer) activeChar.getTarget();
				fakePlayer.despawnPlayer();
				showFakeDashboard(activeChar);
			}
			showFakeDashboard(activeChar);
		}
		
		if (command.startsWith("admin_deleteAllrobots"))
		{
			int counter = 0;
			for (FakePlayer fakePlayer : FakePlayerManager.getFakePlayers())
			{
				counter++;
				fakePlayer.despawnPlayer();
			}
			activeChar.sendMessage("A total of " + counter + " fake players have been kicked.");
			showFakeDashboard(activeChar);
		}
		
		if (command.startsWith("admin_spawnbuyer"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
				    case "solo":
					    FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
					    fakeSoloPlayer.setFakeAi(new PrivateStoreBuyAI(fakeSoloPlayer));
					    break;

				    case "clan":
					    FakePlayer fakeClanPlayer = FakePlayerManager.spawnClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
					    fakeClanPlayer.setFakeAi(new PrivateStoreBuyAI(fakeClanPlayer));
					    break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		
		if (command.startsWith("admin_spawnseller"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
				    case "solo":
					    FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
					    fakeSoloPlayer.setFakeAi(new PrivateStoreSellAI(fakeSoloPlayer));
					    break;

				    case "clan":
					    FakePlayer fakeClanPlayer = FakePlayerManager.spawnClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
					    fakeClanPlayer.setFakeAi(new PrivateStoreSellAI(fakeClanPlayer));
					    break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		if (command.startsWith("admin_spawncitizen"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
				    case "solo":
					    FakePlayer fakeSoloPlayer = FakePlayerManager.spawnPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
					    fakeSoloPlayer.setFakeAi(new CitizenAI(fakeSoloPlayer));
					    showFakeDashboard(activeChar);
					    break;

				    case "clan":
					    FakePlayer fakeClanPlayer = FakePlayerManager.spawnClanPlayer(activeChar.getX(), activeChar.getY(), activeChar.getZ());
					    fakeClanPlayer.setFakeAi(new CitizenAI(fakeClanPlayer));
					    showFakeDashboard(activeChar);
					    break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}

		if (command.startsWith("admin_spawnclass"))
		{
			if (command.contains(" "))
			{
				String locationName = command.split(" ")[1];
				switch (locationName)
				{
				    //PvP Without Clan
					case "archer":
						FakePlayer archerFake = FakePlayerManager.spawnArcher(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						archerFake.setInstanceId(activeChar.getInstanceId(), true);
						archerFake.setFakePvp(true);
						archerFake.setLastCords(archerFake.getX(), archerFake.getY(), archerFake.getZ());
						archerFake.assignDefaultAI();
						break;
					case "nuker":
						FakePlayer nukerFake = FakePlayerManager.spawnNuker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						nukerFake.setInstanceId(activeChar.getInstanceId(), true);
						nukerFake.setFakePvp(true);
						nukerFake.setLastCords(nukerFake.getX(), nukerFake.getY(), nukerFake.getZ());
						nukerFake.assignDefaultAI();
						break;
					case "warrior":
						FakePlayer warriorFake = FakePlayerManager.spawnWarrior(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						warriorFake.setInstanceId(activeChar.getInstanceId(), true);
						warriorFake.setFakePvp(true);
						warriorFake.setLastCords(warriorFake.getX(), warriorFake.getY(), warriorFake.getZ());
						warriorFake.assignDefaultAI();
						break;
					case "dagger":
						FakePlayer daggerFake = FakePlayerManager.spawnDagger(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						daggerFake.setInstanceId(activeChar.getInstanceId(), true);
						daggerFake.setFakePvp(true);
						daggerFake.setLastCords(daggerFake.getX(), daggerFake.getY(), daggerFake.getZ());
						daggerFake.assignDefaultAI();
						break;
					case "tanker":
						FakePlayer tankerFake = FakePlayerManager.spawnTanker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						tankerFake.setInstanceId(activeChar.getInstanceId(), true);
						tankerFake.setFakePvp(true);
						tankerFake.setLastCords(tankerFake.getX(), tankerFake.getY(), tankerFake.getZ());
						tankerFake.assignDefaultAI();
						break;
					//With Clan	
					case "archer_clan":
						FakePlayer archerClanFake = FakePlayerManager.spawnClanArcher(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						archerClanFake.setFakePvp(true);
						archerClanFake.setLastCords(archerClanFake.getX(), archerClanFake.getY(), archerClanFake.getZ());
						archerClanFake.assignDefaultAI();
						break;
					case "nuker_clan":
						FakePlayer nukerClanFake = FakePlayerManager.spawnClanNuker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						nukerClanFake.setFakePvp(true);
						nukerClanFake.setLastCords(nukerClanFake.getX(), nukerClanFake.getY(), nukerClanFake.getZ());
						nukerClanFake.assignDefaultAI();
						break;
					case "warrior_clan":
						FakePlayer warriorClanFake = FakePlayerManager.spawnClanWarrior(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						warriorClanFake.setFakePvp(true);
						warriorClanFake.setLastCords(warriorClanFake.getX(), warriorClanFake.getY(), warriorClanFake.getZ());
						warriorClanFake.assignDefaultAI();
						break;
					case "dagger_clan":
						FakePlayer daggerClanFake = FakePlayerManager.spawnClanDagger(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						daggerClanFake.setFakePvp(true);
						daggerClanFake.setLastCords(daggerClanFake.getX(), daggerClanFake.getY(), daggerClanFake.getZ());
						daggerClanFake.assignDefaultAI();
						break;
					case "tanker_clan":
						FakePlayer tankerClanFake = FakePlayerManager.spawnClanTanker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						tankerClanFake.setFakePvp(true);
						tankerClanFake.setLastCords(tankerClanFake.getX(), tankerClanFake.getY(), tankerClanFake.getZ());
						tankerClanFake.assignDefaultAI();
						break;
					//Farm Without Clan
					case "archer_farm":
						FakePlayer archerFarmFake = FakePlayerManager.spawnArcher(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						archerFarmFake.setInstanceId(activeChar.getInstanceId(), true);
						archerFarmFake.setFakeFarm(true);
						archerFarmFake.setLastCords(archerFarmFake.getX(), archerFarmFake.getY(), archerFarmFake.getZ());
						archerFarmFake.assignDefaultAI();
						break;
					case "nuker_farm":
						FakePlayer nukerFarmFake = FakePlayerManager.spawnNuker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						nukerFarmFake.setInstanceId(activeChar.getInstanceId(), true);
						nukerFarmFake.setFakeFarm(true);
						nukerFarmFake.setLastCords(nukerFarmFake.getX(), nukerFarmFake.getY(), nukerFarmFake.getZ());
						nukerFarmFake.assignDefaultAI();
						break;
					case "warrior_farm":
						FakePlayer warriorFarmFake = FakePlayerManager.spawnWarrior(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						warriorFarmFake.setInstanceId(activeChar.getInstanceId(), true);
						warriorFarmFake.setFakeFarm(true);
						warriorFarmFake.setLastCords(warriorFarmFake.getX(), warriorFarmFake.getY(), warriorFarmFake.getZ());
						warriorFarmFake.assignDefaultAI();
						break;
					case "dagger_farm":
						FakePlayer daggerFarmFake = FakePlayerManager.spawnDagger(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						daggerFarmFake.setInstanceId(activeChar.getInstanceId(), true);
						daggerFarmFake.setFakeFarm(true);
						daggerFarmFake.setLastCords(daggerFarmFake.getX(), daggerFarmFake.getY(), daggerFarmFake.getZ());
						daggerFarmFake.assignDefaultAI();
						break;
					case "tanker_farm":
						FakePlayer tankerFarmFake = FakePlayerManager.spawnTanker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						tankerFarmFake.setInstanceId(activeChar.getInstanceId(), true);
						tankerFarmFake.setFakeFarm(true);
						tankerFarmFake.setLastCords(tankerFarmFake.getX(), tankerFarmFake.getY(), tankerFarmFake.getZ());
						tankerFarmFake.assignDefaultAI();
						break;
					//Farm With Clan	
					case "archer_farm_clan":
						FakePlayer archerClanFarmFake = FakePlayerManager.spawnClanArcher(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						archerClanFarmFake.setFakeFarm(true);
						archerClanFarmFake.setLastCords(archerClanFarmFake.getX(), archerClanFarmFake.getY(), archerClanFarmFake.getZ());
						archerClanFarmFake.assignDefaultAI();
						break;
					case "nuker_farm_clan":
						FakePlayer nukerClanFarmFake = FakePlayerManager.spawnClanNuker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						nukerClanFarmFake.setFakeFarm(true);
						nukerClanFarmFake.setLastCords(nukerClanFarmFake.getX(), nukerClanFarmFake.getY(), nukerClanFarmFake.getZ());
						nukerClanFarmFake.assignDefaultAI();
						break;
					case "warrior_farm_clan":
						FakePlayer warriorClanFarmFake = FakePlayerManager.spawnClanWarrior(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						warriorClanFarmFake.setFakeFarm(true);
						warriorClanFarmFake.setLastCords(warriorClanFarmFake.getX(), warriorClanFarmFake.getY(), warriorClanFarmFake.getZ());
						warriorClanFarmFake.assignDefaultAI();
						break;
					case "dagger_farm_clan":
						FakePlayer daggerClanFarmFake = FakePlayerManager.spawnClanDagger(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						daggerClanFarmFake.setFakeFarm(true);
						daggerClanFarmFake.setLastCords(daggerClanFarmFake.getX(), daggerClanFarmFake.getY(), daggerClanFarmFake.getZ());
						daggerClanFarmFake.assignDefaultAI();
						break;
					case "tanker_farm_clan":
						FakePlayer tankerClanFarmFake = FakePlayerManager.spawnClanTanker(activeChar.getX(), activeChar.getY(), activeChar.getZ());
						tankerClanFarmFake.setFakeFarm(true);
						tankerClanFarmFake.setLastCords(tankerClanFarmFake.getX(), tankerClanFarmFake.getY(), tankerClanFarmFake.getZ());
						tankerClanFarmFake.assignDefaultAI();
						break;
				}
				showFakeDashboard(activeChar);
				return true;
			}
		}
		
		if (command.startsWith("admin_takecontrol"))
		{
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof FakePlayer)
			{
				FakePlayer fakePlayer = (FakePlayer) activeChar.getTarget();
				fakePlayer.setUnderControl(true);
				activeChar.setPlayerUnderControl(fakePlayer);
				activeChar.sendMessage("You are now controlling: " + fakePlayer.getName());
			}
			else
				activeChar.sendMessage("You can only take control of a Fake Player");
		}
		
		if (command.startsWith("admin_releasecontrol"))
		{
			if (activeChar.isControllingFakePlayer())
			{
				FakePlayer fakePlayer = activeChar.getPlayerUnderControl();
				activeChar.sendMessage("You are no longer controlling: " + fakePlayer.getName());
				fakePlayer.setUnderControl(false);
				activeChar.setPlayerUnderControl(null);
				
			}
			else
				activeChar.sendMessage("You are not controlling a Fake Player");
		}
		
		return true;
	}
}