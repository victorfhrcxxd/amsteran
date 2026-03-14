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
package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminStreamer implements IAdminCommandHandler
{
	protected static final Logger _log = Logger.getLogger(AdminStreamer.class.getName());

	private static final String[] ADMIN_COMMANDS =
	{
		"admin_streamer",
		"admin_setstreamer",
		"admin_settwitch", 
		"admin_give_twitch_cap",
		"admin_setfacebook", 
		"admin_give_facebook_cap",
		"admin_setyoutube", 
		"admin_give_youtube_cap"
	};

	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("admin_streamer"))
		{
			if (activeChar.getTarget() == null)
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			if (!(activeChar.getTarget() instanceof L2PcInstance))
			{
				activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
				return false;
			}
			showStreamerPage(activeChar);
		}
		if (command.startsWith("admin_setstreamer"))
		{
			L2PcInstance target = null;
			if (activeChar.getTarget() instanceof L2PcInstance)
				target = (L2PcInstance) activeChar.getTarget();
			else
				target = activeChar;

			target.setStreamer(!target.isStreamer(), true);
			activeChar.sendMessage("You have modified " + target.getName() + "'s streamer status.");
			showStreamerPage(activeChar);
		}

		if (command.startsWith("admin_settwitch"))
		{
			if (activeChar.getAccessLevel().getLevel() < 1)
				return false;

			try
			{
				String val = "";
				StringTokenizer s = new StringTokenizer(command);
				s.nextToken();

				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;

				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;

				try
				{
					while(s.hasMoreTokens())
						val = val + s.nextToken() + " ";
					player.setTwitchLink(val);
					showStreamerPage(activeChar);

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character title
				activeChar.sendMessage("Usage: //settwitch Link");
			}
		}	
		
		if (command.startsWith("admin_give_twitch_cap"))
		{
			L2PcInstance target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
				target = (L2PcInstance) activeChar.getTarget();

			target.getInventory().addItem("Twitch Cap", 19074, 1, target, activeChar);

			activeChar.sendMessage("You have spawned " + ItemTable.getInstance().getTemplate(19074) + " set in " + target.getName() + "'s inventory.");

			// Send the whole item list and open inventory window.
			target.sendPacket(new ItemList(target, true));
			showStreamerPage(activeChar);
		}

		if (command.startsWith("admin_setfacebook"))
		{
			if (activeChar.getAccessLevel().getLevel() < 1)
				return false;

			try
			{
				String val = "";
				StringTokenizer s = new StringTokenizer(command);
				s.nextToken();

				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;

				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;

				try
				{
					while(s.hasMoreTokens())
						val = val + s.nextToken() + " ";
					player.setFacebookLink(val);
					showStreamerPage(activeChar);

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character title
				activeChar.sendMessage("Usage: //setfacebook Link");
			}
		}
		
		if (command.startsWith("admin_give_facebook_cap"))
		{
			L2PcInstance target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
				target = (L2PcInstance) activeChar.getTarget();

			target.getInventory().addItem("Facebook Cap", 19076, 1, target, activeChar);

			activeChar.sendMessage("You have spawned " + ItemTable.getInstance().getTemplate(19076) + " set in " + target.getName() + "'s inventory.");

			// Send the whole item list and open inventory window.
			target.sendPacket(new ItemList(target, true));
			showStreamerPage(activeChar);
		}
		
		if (command.startsWith("admin_setyoutube"))
		{
			if (activeChar.getAccessLevel().getLevel() < 1)
				return false;

			try
			{
				String val = "";
				StringTokenizer s = new StringTokenizer(command);
				s.nextToken();

				L2Object target = activeChar.getTarget();
				L2PcInstance player = null;

				if (target instanceof L2PcInstance)
					player = (L2PcInstance) target;
				else
					return false;

				try
				{
					while(s.hasMoreTokens())
						val = val + s.nextToken() + " ";
					player.setYoutubeLink(val);
					showStreamerPage(activeChar);

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
				// Case of empty character title
				activeChar.sendMessage("Usage: //setyoutube Link");
			}
		}
		
		if (command.startsWith("admin_give_youtube_cap"))
		{
			L2PcInstance target = activeChar;
			if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
				target = (L2PcInstance) activeChar.getTarget();

			target.getInventory().addItem("Youtube Cap", 19075, 1, target, activeChar);

			activeChar.sendMessage("You have spawned " + ItemTable.getInstance().getTemplate(19075) + " set in " + target.getName() + "'s inventory.");

			// Send the whole item list and open inventory window.
			target.sendPacket(new ItemList(target, true));
			showStreamerPage(activeChar);
		}
		
		return true;
	}

	private static void showStreamerPage(L2PcInstance activeChar)
	{
		NpcHtmlMessage adminReply = new NpcHtmlMessage(0);
		L2PcInstance target = (L2PcInstance)activeChar.getTarget();

		adminReply.setFile("data/html/admin/stream/stream.htm");

		String twitch = target.getTwitchLink();
		String facebook = target.getFacebookLink();
		String youtube = target.getYoutubeLink();

		adminReply.replace("%twitch%", twitch == null ? "N/A" : twitch);
		adminReply.replace("%facebook%", facebook == null ? "N/A" : facebook);
		adminReply.replace("%youtube%", youtube == null ? "N/A" : youtube);

		activeChar.sendPacket(adminReply);
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}