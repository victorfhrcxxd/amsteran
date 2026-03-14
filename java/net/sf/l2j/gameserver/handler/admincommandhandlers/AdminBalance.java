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

import net.sf.l2j.gameserver.datatables.CharTemplateTable;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.instancemanager.custom.BalanceManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.base.ClassId;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class AdminBalance implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS =
    {
        "admin_balance",
        "admin_listBalance",
        "admin_makeBalance",
        "admin_modDamage"
    };
   
    public boolean useAdminCommand(String command, L2PcInstance activeChar)
    {
        if (command.startsWith("admin_balance"))
        {
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/admin/balance/main.htm");
			activeChar.sendPacket(htm);
        }
        else if (command.startsWith("admin_listBalance"))
        {
			int classId = Integer.parseInt(command.substring(18));
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/admin/balance/show_list.htm");
			htm.replace("%classid%", classId);
			htm.replace("%classname%", CharTemplateTable.getInstance().getClassNameById(classId));
			activeChar.sendPacket(htm);
        }
        else if (command.startsWith("admin_makeBalance"))
        {
			String[] args = command.substring(18).split(" ");
			int targetClassId = args[0].equals("ALL") ? -1 : Integer.parseInt(args[0]);
			int classId = Integer.parseInt(args[1]);
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/admin/balance/show_change.htm");
			htm.replace("%classid%", classId);
			htm.replace("%targetclassid%", targetClassId);
			htm.replace("%dmgmod%", BalanceManager.getInstance().getMod(classId, targetClassId));
			for (ClassId ci : ClassId.values())
			{
				if (ci.getId() == classId)
				{
					htm.replace("%classname%", ci.name());
					break;
				}
			}
			if (targetClassId == -1)
				htm.replace("%targetclassname%", "ALL CLASSES");
			else
			{
				for (ClassId ci : ClassId.values())
				{
					if (ci.getId() == targetClassId)
					{
						htm.replace("%targetclassname%", ci.name());
						break;
					}
				}
			}
			activeChar.sendPacket(htm);
        }
        else if (command.startsWith("admin_modDamage"))
        {
			String[] args = command.substring(16).split(" ");
			if (args.length < 3)
				return false;
			
			int targetClassId = args[0].equals("ALL") ? -1 : Integer.parseInt(args[0]);
			int classId = Integer.parseInt(args[1]);
			int mod = Integer.parseInt(args[2]);
			
			BalanceManager.getInstance().addMod(classId, targetClassId, mod);
			
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			htm.setFile("data/html/admin/balance/show_change.htm");
			htm.replace("%classid%", classId);
			htm.replace("%targetclassid%", targetClassId);
			htm.replace("%dmgmod%", BalanceManager.getInstance().getMod(classId, targetClassId));
			for (ClassId ci : ClassId.values())
			{
				if (ci.getId() == classId)
				{
					htm.replace("%classname%", ci.name());
					break;
				}
			}
			if (targetClassId == -1)
				htm.replace("%targetclassname%", "ALL CLASSES");
			else
			{
				for (ClassId ci : ClassId.values())
				{
					if (ci.getId() == targetClassId)
					{
						htm.replace("%targetclassname%", ci.name());
						break;
					}
				}
			}
			activeChar.sendPacket(htm);
        }
       
        return true;
    }
   
    public String[] getAdminCommandList()
    {
        return ADMIN_COMMANDS;
    }
}