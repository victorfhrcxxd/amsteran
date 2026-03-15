package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.List;

import phantom.ai.event.ChaoticFarmController;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.dimension.InstanceManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmManager;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmRoom;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminChaoticFarm implements IAdminCommandHandler
{
	private static final int ROOMS_PER_PAGE = 10;

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_cf_spawn",
		"admin_cf_despawn",
		"admin_cf_stats",
		"admin_cf_panel",
		"admin_cf_room_tp",
		"admin_cf_room_reset",
		"admin_cf_room_kick_owner",
		"admin_cf_room_kick_challenger",
		"admin_cf_room_kick_all"
	};

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (activeChar.getAccessLevel().getLevel() < 1)
			return false;

		if (command.startsWith("admin_cf_panel"))
		{
			final String[] parts = command.split(" ");
			int page = 1;
			if (parts.length >= 2)
			{
				try { page = Integer.parseInt(parts[1]); }
				catch (NumberFormatException e) { page = 1; }
			}
			showRoomPanel(activeChar, page);
		}
		else if (command.startsWith("admin_cf_room_tp"))
		{
			final int roomId = parseRoomId(command, activeChar);
			if (roomId < 0)
				return false;

			final ChaoticFarmRoom room = ChaoticFarmManager.getInstance().getRoom(roomId);
			if (room == null)
			{
				activeChar.sendMessage("Room " + roomId + " not found.");
				return false;
			}

			activeChar.setNewInstance(InstanceManager.getInstance().getInstance(room.getInstanceId()), true);
			activeChar.teleToLocation(room.getCenterX(), room.getCenterY(), room.getCenterZ(), 0);
			activeChar.sendMessage("Teleported to room " + roomId + " (instanceId=" + room.getInstanceId() + ").");
		}
		else if (command.startsWith("admin_cf_room_reset"))
		{
			final int roomId = parseRoomId(command, activeChar);
			if (roomId < 0)
				return false;

			final ChaoticFarmRoom room = ChaoticFarmManager.getInstance().getRoom(roomId);
			if (room == null)
			{
				activeChar.sendMessage("Room " + roomId + " not found.");
				return false;
			}

			room.resetRoom();
			ChaoticFarmManager.getInstance().processQueue();
			activeChar.sendMessage("Room " + roomId + " reset.");
			showRoomPanel(activeChar, parsePage(command, 1));
		}
		else if (command.startsWith("admin_cf_room_kick_owner"))
		{
			final int roomId = parseRoomId(command, activeChar);
			if (roomId < 0)
				return false;

			final ChaoticFarmRoom room = ChaoticFarmManager.getInstance().getRoom(roomId);
			if (room == null)
			{
				activeChar.sendMessage("Room " + roomId + " not found.");
				return false;
			}

			final L2PcInstance owner = room.getOwner();
			if (owner == null)
			{
				activeChar.sendMessage("Room " + roomId + " has no owner.");
				showRoomPanel(activeChar, parsePage(command, 1));
				return true;
			}

			room.handleDisconnect(owner);
			activeChar.sendMessage("Owner " + owner.getName() + " kicked from room " + roomId + ".");
			showRoomPanel(activeChar, parsePage(command, 1));
		}
		else if (command.startsWith("admin_cf_room_kick_challenger"))
		{
			final int roomId = parseRoomId(command, activeChar);
			if (roomId < 0)
				return false;

			final ChaoticFarmRoom room = ChaoticFarmManager.getInstance().getRoom(roomId);
			if (room == null)
			{
				activeChar.sendMessage("Room " + roomId + " not found.");
				return false;
			}

			final L2PcInstance challenger = room.getChallenger();
			if (challenger == null)
			{
				activeChar.sendMessage("Room " + roomId + " has no challenger.");
				showRoomPanel(activeChar, parsePage(command, 1));
				return true;
			}

			room.handleDisconnect(challenger);
			activeChar.sendMessage("Challenger " + challenger.getName() + " kicked from room " + roomId + ".");
			showRoomPanel(activeChar, parsePage(command, 1));
		}
		else if (command.startsWith("admin_cf_room_kick_all"))
		{
			final int roomId = parseRoomId(command, activeChar);
			if (roomId < 0)
				return false;

			final ChaoticFarmRoom room = ChaoticFarmManager.getInstance().getRoom(roomId);
			if (room == null)
			{
				activeChar.sendMessage("Room " + roomId + " not found.");
				return false;
			}

			room.resetRoom();
			ChaoticFarmManager.getInstance().processQueue();
			activeChar.sendMessage("All players kicked from room " + roomId + ".");
			showRoomPanel(activeChar, parsePage(command, 1));
		}
		else if (command.startsWith("admin_cf_spawn"))
		{
			final String[] parts = command.split(" ");
			if (parts.length < 2)
			{
				activeChar.sendMessage("Usage: //cf_spawn <count>");
				return false;
			}

			int count;
			try
			{
				count = Integer.parseInt(parts[1]);
			}
			catch (NumberFormatException e)
			{
				activeChar.sendMessage("Invalid count. Usage: //cf_spawn <count>");
				return false;
			}

			if (count <= 0)
			{
				activeChar.sendMessage("Count must be greater than 0.");
				return false;
			}

			ChaoticFarmController.spawnAndEnqueue(count);
			activeChar.sendMessage("Spawned " + count + " Chaotic Farm fake players.");
		}
		else if (command.startsWith("admin_cf_despawn"))
		{
			ChaoticFarmController.despawnAll();
			activeChar.sendMessage("All Chaotic Farm fake players removed.");
		}
		else if (command.startsWith("admin_cf_stats"))
		{
			final ChaoticFarmManager mgr = ChaoticFarmManager.getInstance();
			activeChar.sendMessage("=== Chaotic Farm Fake Player Stats ===");
			activeChar.sendMessage("Fake players active: " + ChaoticFarmController.getCount());
			activeChar.sendMessage("Rooms farming: " + mgr.getFarmingRoomCount());
			activeChar.sendMessage("Rooms in duel: " + mgr.getDuelRoomCount());
			activeChar.sendMessage("Queue size: " + mgr.getQueueSize());
		}

		return true;
	}

	private void showRoomPanel(L2PcInstance activeChar, int page)
	{
		final ChaoticFarmManager mgr = ChaoticFarmManager.getInstance();
		final List<ChaoticFarmRoom> rooms = mgr.getRooms();
		final int totalRooms = rooms.size();
		final int totalPages = Math.max(1, (totalRooms + ROOMS_PER_PAGE - 1) / ROOMS_PER_PAGE);
		page = Math.max(1, Math.min(page, totalPages));

		final int start = (page - 1) * ROOMS_PER_PAGE;
		final int end = Math.min(start + ROOMS_PER_PAGE, totalRooms);

		final NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/admin/chaoticfarm_panel.htm");

		final StringBuilder rows = new StringBuilder();
		for (int i = start; i < end; i++)
		{
			final ChaoticFarmRoom room = rooms.get(i);
			final String owner = room.getOwner() != null ? room.getOwner().getName() : "-";
			final String challenger = room.getChallenger() != null ? room.getChallenger().getName() : "-";
			final String safe = room.isSafeActive() ? "YES" : "NO";
			final String mob = room.getCurrentMob() != null ? "ALIVE" : "NONE";

			rows.append("<tr>");
			rows.append("<td align=center>").append(room.getRoomId()).append("</td>");
			rows.append("<td align=center>").append(room.getState()).append("</td>");
			rows.append("<td align=center>").append(owner).append("</td>");
			rows.append("<td align=center>").append(challenger).append("</td>");
			rows.append("<td align=center>").append(safe).append("</td>");
			rows.append("<td align=center>").append(mob).append("</td>");
			rows.append("<td align=center>").append(room.getInstanceId()).append("</td>");
			rows.append("<td align=center>");
			rows.append("<button value=\"TP\" action=\"bypass -h admin_cf_room_tp ").append(room.getRoomId()).append(" ").append(page).append("\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			rows.append("<button value=\"RST\" action=\"bypass -h admin_cf_room_reset ").append(room.getRoomId()).append(" ").append(page).append("\" width=40 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			rows.append("<button value=\"KICK\" action=\"bypass -h admin_cf_room_kick_all ").append(room.getRoomId()).append(" ").append(page).append("\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
			rows.append("</td>");
			rows.append("</tr>");
		}

		final StringBuilder nav = new StringBuilder();
		if (page > 1)
			nav.append("<button value=\"Prev\" action=\"bypass -h admin_cf_panel ").append(page - 1).append("\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
		if (page < totalPages)
			nav.append("<button value=\"Next\" action=\"bypass -h admin_cf_panel ").append(page + 1).append("\" width=60 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");

		html.replace("%cf_fake_count%", String.valueOf(ChaoticFarmController.getCount()));
		html.replace("%cf_queue_count%", String.valueOf(mgr.getQueueSize()));
		html.replace("%cf_farming_count%", String.valueOf(mgr.getFarmingRoomCount()));
		html.replace("%cf_duel_count%", String.valueOf(mgr.getDuelRoomCount()));
		html.replace("%cf_page%", String.valueOf(page));
		html.replace("%cf_total_pages%", String.valueOf(totalPages));
		html.replace("%cf_nav%", nav.toString());
		html.replace("%cf_room_rows%", rows.toString());

		activeChar.sendPacket(html);
	}

	private int parseRoomId(String command, L2PcInstance activeChar)
	{
		final String[] parts = command.split(" ");
		if (parts.length < 2)
		{
			activeChar.sendMessage("Usage: " + parts[0].replace("admin_", "//") + " <roomId>");
			return -1;
		}

		try
		{
			return Integer.parseInt(parts[1]);
		}
		catch (NumberFormatException e)
		{
			activeChar.sendMessage("Invalid room ID.");
			return -1;
		}
	}

	private int parsePage(String command, int defaultPage)
	{
		final String[] parts = command.split(" ");
		if (parts.length >= 3)
		{
			try { return Integer.parseInt(parts[2]); }
			catch (NumberFormatException e) { /* fall through */ }
		}
		return defaultPage;
	}
}
