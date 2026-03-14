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

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class AdminPCBPoint implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_addpcpoint",
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(command);
		command = st.nextToken();
		
		L2PcInstance target = activeChar;
		if (activeChar.getTarget() != null && activeChar.getTarget() instanceof L2PcInstance)
			target = (L2PcInstance) activeChar.getTarget();
		
		if (command.startsWith("admin_addpcpoint"))
		{
			try
			{
				int count = 1;
				
				if (st.hasMoreTokens())
					count = Integer.parseInt(st.nextToken());
				
				createPcBang(activeChar, target, count);
			}
			catch (Exception e)
			{
				activeChar.sendMessage("Usage: //addpcpoint count");
			}
		}
		
		return true;
	}

	private static void createPcBang(L2PcInstance activeChar, L2PcInstance player, int score)
	{
		player.addPcBangScore(score);
		player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ACQUIRED_S1_PCPOINT).addNumber(score));
		player.updatePcBangWnd(score, true, false);
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}