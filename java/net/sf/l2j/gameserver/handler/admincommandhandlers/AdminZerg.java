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

import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaRanking;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.util.Broadcast;

/**
 * This class handles teleport admin commands
 */
public class AdminZerg implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_zerg",
		"admin_resettour"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		if (command.startsWith("admin_resettour"))
			ArenaRanking.rankingRewardPlayer();

		if (command.startsWith("admin_zerg"))
		{
			try
			{
				String targetName = command.substring(11);
				L2PcInstance player = L2World.getInstance().getPlayer(targetName);
				if (player == null)
				{
					activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
					return false;
				}
				
				if (player.isInParty())
				{
					for (L2PcInstance member : player.getParty().getPartyMembers())
					{
						kill(activeChar, member);
						sendHome(member);
					}
					Broadcast.gameAnnounceToOnlinePlayers("Zerg Punisher: Party members of " + player.getName() + " were punished.");
					activeChar.sendMessage("You killed " + player.getName() + "'s party.");
				}
				else
				{
					activeChar.sendMessage("You killed " + player.getName() + ", but he isn't in a party.");
					sendHome(player);
				}
			}
			catch (StringIndexOutOfBoundsException e)
			{
			}
		}
		return true;
	}
	
	private static void kill(L2PcInstance activeChar, L2Character target)
	{
		if (target instanceof L2PcInstance)
		{
			if (!((L2PcInstance) target).isGM())
				target.stopAllEffects(); // e.g. invincibility effect
			target.reduceCurrentHp(target.getMaxHp() + target.getMaxCp() + 1, activeChar, null);
		}
	}
	
	private static void sendHome(L2PcInstance player)
	{
		player.teleToLocation(MapRegionTable.TeleportWhereType.Town);
		player.setIsIn7sDungeon(false);
		player.sendMessage("A GM sent you at nearest town.");
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}