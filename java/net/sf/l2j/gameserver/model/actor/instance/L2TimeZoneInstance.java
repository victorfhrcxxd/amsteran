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
package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.instancemanager.custom.TimeInstanceManager;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.taskmanager.TimeInstanceRemainTaskManager;

public class L2TimeZoneInstance extends L2NpcInstance
{	
	public L2TimeZoneInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		boolean pass = false;
		
		if (command.startsWith("timepass"))
		{
			if (Config.TIME_INSTANCE_BLOCK_CLASS_LIST.contains(player.getClassId().getId()))
			{
				player.sendMessage("Your class is not allowed in Time Instance Zone.");
				return;
			}
			
			if (player.TimeInstanceAvaiable())
				pass = true;
			else
			{
				if (player.getInventory().getInventoryItemCount(Config.TIME_INSTANCE_ITEM_ID_TO_ACESS, 0) >= 1)
				{
					pass = true;
					
					player.getInventory().destroyItemByItemId("TI", Config.TIME_INSTANCE_ITEM_ID_TO_ACESS, 1, player, null);
					
					TimeInstanceManager.updatePlayerTime(player);
					TimeInstanceManager.broadcastTimer(player);
				}
				else
					player.sendMessage("You don't have the necessary item to go to the Time Instance Zone.");
			}
			
			if (pass)
			{
				int spot = Integer.parseInt(command.substring(8).trim());
				
				new TimeInstanceRemainTaskManager(player);
				
				if (spot == 1)
					player.teleToLocation(Config.TIME_INSTANCE_AREA_LOC_1, 0);
				else if (spot == 2)
					player.teleToLocation(Config.TIME_INSTANCE_AREA_LOC_2, 0);
				else if (spot == 3)
					player.teleToLocation(Config.TIME_INSTANCE_AREA_LOC_3, 0);
				else if (spot == 4)
					player.teleToLocation(Config.TIME_INSTANCE_AREA_LOC_4, 0);
				else
					player.teleToLocation(Config.TIME_INSTANCE_AREA_LOC_1, 0);
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/mods/timezone/Main.htm");
		html.replace("%objectId%", String.valueOf(player.getTargetId()));
		player.sendPacket(html);
	}
}