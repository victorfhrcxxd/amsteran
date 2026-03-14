package phantom;

import java.util.List;
import java.util.stream.Collectors;

import phantom.ai.FakePlayerUtilsAI;
import phantom.ai.autospawn.AutoSpawnAI;
import phantom.ai.check.CheckFakeManager;
import phantom.helpers.FakeHelpers;
import net.sf.l2j.gameserver.datatables.MapRegionTable.TeleportWhereType;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.L2GameClient;
import net.sf.l2j.gameserver.network.L2GameClient.GameClientState;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public enum FakePlayerManager
{
	INSTANCE;
	
	private FakePlayerManager()
	{
		
	}
	
	public void initialise()
	{
		FakePlayerNameManager.INSTANCE.initialise();
		FakePlayerTaskManager.INSTANCE.initialise();
		CheckFakeManager.getInstance();
		AutoSpawnAI.getInstance();
	}
	
	//TvT Fake Player
	public static FakePlayer spawnEventPlayer(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createRandomTvTFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//Without Clan
	public static FakePlayer spawnPlayer(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createRandomFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	// With Clan
	public static FakePlayer spawnClanPlayer(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createRandomClanFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);
		
		handlePlayerClanOnSpawn(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	// Without Clan
	public static FakePlayer spawnArcher(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createArcherFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	// With Clan
	public static FakePlayer spawnClanArcher(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createArcherClanFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);
		
		handlePlayerClanOnSpawn(activeChar);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	// Without Clan
	public static FakePlayer spawnNuker(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createNukerFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	// With Clan
	public static FakePlayer spawnClanNuker(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createNukerClanFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		handlePlayerClanOnSpawn(activeChar);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//Without Clan
	public static FakePlayer spawnWarrior(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createWarriorFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//With Clan
	public static FakePlayer spawnClanWarrior(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createWarriorClanFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		handlePlayerClanOnSpawn(activeChar);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//Without Clan
	public static FakePlayer spawnDagger(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createDaggerFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//With Clan
	public static FakePlayer spawnClanDagger(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createDaggerClanFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		handlePlayerClanOnSpawn(activeChar);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//Without Clan
	public static FakePlayer spawnTanker(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createTankerFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	//With Clan
	public static FakePlayer spawnClanTanker(int x, int y, int z)
	{
		L2GameClient client = new L2GameClient(null);
		client.setDetached(true);
		
		FakePlayer activeChar = FakeHelpers.createTankerClanFakePlayer();
		activeChar.setClient(client);
		client.setActiveChar(activeChar);
		activeChar.setOnlineStatus(true, false);
		client.setState(GameClientState.IN_GAME);
		client.setAccountName(activeChar.getAccountName());
		L2World.getInstance().addPlayer(activeChar);

		handlePlayerClanOnSpawn(activeChar);
		
		activeChar.spawnMe(x, y, z);
		activeChar.onPlayerEnter();
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_NAME.isEmpty())
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_NAME));
		else
			activeChar.getAppearance().setNameColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorNameFromWordlist()));
		
		if (!FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE.isEmpty())
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerConfig.FAKE_PLAYER_COLOR_TITLE));
		else
			activeChar.getAppearance().setTitleColor(Integer.decode("0x" + FakePlayerUtilsAI.getRandomColorTitleFromWordlist()));
		
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(TeleportWhereType.Town);
		
		activeChar.heal();
		return activeChar;
	}
	
	public static void despawnFakePlayer(int objectId)
	{
		L2PcInstance player = L2World.getInstance().getPlayer(objectId);
		if (player instanceof FakePlayer)
		{
			FakePlayer fakePlayer = (FakePlayer) player;
			fakePlayer.despawnPlayer();
		}
	}
	
	private static void handlePlayerClanOnSpawn(FakePlayer activeChar)
	{
		final L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
			
			final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addCharName(activeChar);
			final PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(activeChar);
			
			// Send packets to others members.
			for (L2PcInstance member : clan.getOnlineMembers())
			{
				if (member == activeChar)
					continue;
				
				member.sendPacket(msg);
				member.sendPacket(update);
			}
		}
	}
	
	public static int getFakePlayersCount()
	{
		return getFakePlayers().size();
	}
	
	public static List<FakePlayer> getFakePlayers()
	{
		return L2World.getInstance().getL2Players().stream().filter(x -> x instanceof FakePlayer).map(x -> (FakePlayer) x).collect(Collectors.toList());
	}
}