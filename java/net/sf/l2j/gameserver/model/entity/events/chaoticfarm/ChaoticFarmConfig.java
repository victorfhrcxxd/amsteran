package net.sf.l2j.gameserver.model.entity.events.chaoticfarm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.commons.config.ExProperties;

public class ChaoticFarmConfig
{
	protected static final Logger _log = Logger.getLogger(ChaoticFarmConfig.class.getName());

	private static final String CF_FILE = "./config/events/chaoticfarm.properties";

	public static boolean CF_ENABLED;
	public static int CF_ROOM_COUNT;
	public static int CF_MAX_FARM_SECONDS;
	public static int CF_DUEL_COUNTDOWN;
	public static int CF_ALLOWED_RADIUS;
	public static int CF_MOB_NPC_ID;
	public static int CF_MOB_RESPAWN_SECONDS;
	public static int CF_QUEUE_WAIT_SECONDS;
	public static int CF_FREEZE_SECONDS;
	public static int CF_SAFE_SECONDS;
	public static int[] CF_EXIT_LOC;
	public static int CF_FAKE_REQUEUE_DELAY_SECONDS;
	public static boolean CF_ANTI_DUALBOX_CHECK_IP;
	public static boolean CF_ANTI_DUALBOX_CHECK_HWID;
	public static List<RoomData> CF_ROOMS;
	public static List<ChaoticFarmDrop> CF_MOB_DROPS;

	public static class RoomData
	{
		public final int centerX, centerY, centerZ;
		public final int ownerX, ownerY, ownerZ;
		public final int challengerX, challengerY, challengerZ;
		public final int mobX, mobY, mobZ;

		public RoomData(int cx, int cy, int cz, int ox, int oy, int oz, int chx, int chy, int chz, int mx, int my, int mz)
		{
			centerX = cx;
			centerY = cy;
			centerZ = cz;
			ownerX = ox;
			ownerY = oy;
			ownerZ = oz;
			challengerX = chx;
			challengerY = chy;
			challengerZ = chz;
			mobX = mx;
			mobY = my;
			mobZ = mz;
		}
	}

	public static void init()
	{
		final ExProperties props = load(CF_FILE);

		CF_ENABLED = props.getProperty("EnableChaoticFarm", false);
		CF_ROOM_COUNT = props.getProperty("ChaoticFarmRoomCount", 5);
		CF_MAX_FARM_SECONDS = props.getProperty("ChaoticFarmMaxFarmSeconds", 300);
		CF_DUEL_COUNTDOWN = props.getProperty("ChaoticFarmDuelCountdown", 5);
		CF_ALLOWED_RADIUS = props.getProperty("ChaoticFarmAllowedRadius", 1200);
		CF_MOB_NPC_ID = props.getProperty("ChaoticFarmMobNpcId", 18001);
		CF_MOB_RESPAWN_SECONDS = props.getProperty("ChaoticFarmMobRespawnSeconds", 3);
		CF_QUEUE_WAIT_SECONDS = props.getProperty("ChaoticFarmQueueWaitSeconds", 30);
		CF_FREEZE_SECONDS = props.getProperty("ChaoticFarmFreezeSeconds", 5);
		CF_SAFE_SECONDS = props.getProperty("ChaoticFarmSafeSeconds", 10);

		final int[] exitLoc = parseCoords(props.getProperty("ChaoticFarmExitLoc", "83400,148600,-3400"), "ChaoticFarmExitLoc");
		CF_EXIT_LOC = (exitLoc != null) ? exitLoc : new int[] { 83400, 148600, -3400 };

		CF_FAKE_REQUEUE_DELAY_SECONDS = props.getProperty("ChaoticFarmFakeRequeueDelaySeconds", 5);
		CF_ANTI_DUALBOX_CHECK_IP = props.getProperty("ChaoticFarmAntiDualboxCheckIP", true);
		CF_ANTI_DUALBOX_CHECK_HWID = props.getProperty("ChaoticFarmAntiDualboxCheckHWID", true);

		CF_ROOMS = new ArrayList<>();
		for (int i = 1; i <= CF_ROOM_COUNT; i++)
		{
			final int[] center = parseCoords(props.getProperty("Room" + i + "Center", ""), "Room" + i + "Center");
			final int[] ownerSpawn = parseCoords(props.getProperty("Room" + i + "OwnerSpawn", ""), "Room" + i + "OwnerSpawn");
			final int[] challengerSpawn = parseCoords(props.getProperty("Room" + i + "ChallengerSpawn", ""), "Room" + i + "ChallengerSpawn");
			final int[] mobSpawn = parseCoords(props.getProperty("Room" + i + "MobSpawn", ""), "Room" + i + "MobSpawn");

			if (center == null || ownerSpawn == null || challengerSpawn == null || mobSpawn == null)
			{
				_log.warning("ChaoticFarm[Config]: Room" + i + " has missing or invalid position data. Skipping.");
				continue;
			}

			CF_ROOMS.add(new RoomData(center[0], center[1], center[2], ownerSpawn[0], ownerSpawn[1], ownerSpawn[2], challengerSpawn[0], challengerSpawn[1], challengerSpawn[2], mobSpawn[0], mobSpawn[1], mobSpawn[2]));
		}

		if (CF_ENABLED && CF_ROOMS.isEmpty())
		{
			_log.warning("ChaoticFarm[Config]: No valid rooms defined. System disabled.");
			CF_ENABLED = false;
		}

		CF_MOB_DROPS = new ArrayList<>();
		final String dropsRaw = props.getProperty("ChaoticFarmMobDrops", "").replaceAll("\\s+", "");
		if (!dropsRaw.isEmpty())
		{
			for (String entry : dropsRaw.split(";"))
			{
				final String trimmed = entry.trim();
				if (trimmed.isEmpty())
					continue;
				final String[] parts = trimmed.split(",");
				if (parts.length != 4)
				{
					_log.warning("ChaoticFarm[Config]: invalid drop entry '" + entry + "'. Expected itemId,min,max,chance");
					continue;
				}
				try
				{
					CF_MOB_DROPS.add(new ChaoticFarmDrop(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Double.parseDouble(parts[3])));
				}
				catch (NumberFormatException e)
				{
					_log.warning("ChaoticFarm[Config]: non-numeric drop entry '" + entry + "'");
				}
			}
		}
	}

	private static int[] parseCoords(String value, String key)
	{
		if (value == null || value.trim().isEmpty())
			return null;

		final String[] parts = value.trim().split(",");
		if (parts.length != 3)
		{
			_log.warning("ChaoticFarm[Config]: invalid coordinates for key '" + key + "': \"" + value + "\"");
			return null;
		}

		try
		{
			return new int[]
			{
				Integer.parseInt(parts[0].trim()),
				Integer.parseInt(parts[1].trim()),
				Integer.parseInt(parts[2].trim())
			};
		}
		catch (NumberFormatException e)
		{
			_log.warning("ChaoticFarm[Config]: non-numeric coordinates for key '" + key + "': \"" + value + "\"");
			return null;
		}
	}

	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}

	public static ExProperties load(File file)
	{
		final ExProperties result = new ExProperties();
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("ChaoticFarm[Config]: Error loading config: " + file.getName() + "!");
		}
		return result;
	}
}
