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

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.bonuzone.BonusZoneManager;
import net.sf.l2j.gameserver.model.entity.events.demonzone.DemonZoneManager;
import net.sf.l2j.gameserver.model.entity.events.partyzone.PartyZoneManager;

public class AdminStartEvents implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS =
    {
        "admin_start_farm",
        "admin_start_demon",
        "admin_start_bonus"
    };
   
    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (command.startsWith("admin_start_farm"))
        {
        	PartyZoneManager.getInstance().startEvent();
        }
        
        if (command.startsWith("admin_start_demon"))
        {
        	DemonZoneManager.getInstance().startEvent();
        }
        
        if (command.startsWith( "admin_start_bonus" ))
        {
            BonusZoneManager.getInstance().startEvent();
        }
       
        return true;
    }
   
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}