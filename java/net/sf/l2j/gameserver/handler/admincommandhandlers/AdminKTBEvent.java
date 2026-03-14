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
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBConfig;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEventTeleporter;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBManager;

public class AdminKTBEvent implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS =
    {
        "admin_ktb_add",
        "admin_ktb_remove",
        "admin_ktb_advance"
    };
   
    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (command.startsWith("admin_ktb_add"))
        {
            L2Object target = activeChar.getTarget();
           
            if (!(target instanceof L2PcInstance))
            {
                activeChar.sendMessage("You should select a player!");
                return true;
            }
           
            add(activeChar, (L2PcInstance) target);
        }
        else if (command.startsWith("admin_ktb_remove"))
        {
            L2Object target = activeChar.getTarget();
           
            if (!(target instanceof L2PcInstance))
            {
                activeChar.sendMessage("You should select a player!");
                return true;
            }
           
            remove(activeChar, (L2PcInstance) target);
        }
        else if (command.startsWith( "admin_ktb_advance" ))
        {
            KTBManager.getInstance().skipDelay();
        }
       
        return true;
    }
   
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
   
    private void add(L2PcInstance activeChar, L2PcInstance playerInstance)
    {
        if (KTBEvent.isPlayerParticipant(playerInstance.getObjectId()))
        {
            activeChar.sendMessage("Player already participated in the event!");
            return;
        }
       
        if (!KTBEvent.addParticipant(playerInstance))
        {
            activeChar.sendMessage("Player instance could not be added, it seems to be null!");
            return;
        }
       
        if (KTBEvent.isStarted())
        {
        	new KTBEventTeleporter(playerInstance, true, false);
        }
    }
   
    private void remove(L2PcInstance activeChar, L2PcInstance playerInstance)
    {
        if (!KTBEvent.removeParticipant(playerInstance))
        {
            activeChar.sendMessage("Player is not part of the event!");
            return;
        }
       
        new KTBEventTeleporter(playerInstance, KTBConfig.KTB_EVENT_PARTICIPATION_NPC_COORDINATES, true, true);
    }
}