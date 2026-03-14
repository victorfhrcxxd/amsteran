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
package net.sf.l2j.gameserver.handler.itemhandlers.custom.special;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import phantom.task.ThreadPool;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.templates.StatsSet;

public class ClassItem implements IItemHandler
{
	public static final Logger _log = Logger.getLogger(ClassItem.class.getName());
	
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;
		
		final L2PcInstance activeChar = (L2PcInstance) playable;

		int itemId = item.getItemId();
		
		switch (itemId)
		{
			case 18100:
				setClass(activeChar, item, 88);
				break;
				
			case 18101:
				setClass(activeChar, item, 89);
				break;
				
			case 18102:
				setClass(activeChar, item, 90);
				break;
				
			case 18103:
				setClass(activeChar, item, 91);
				break;
				
			case 18104:
				setClass(activeChar, item, 92);
				break;
				
			case 18105:
				setClass(activeChar, item, 93);
				break;
				
			case 18106:
				setClass(activeChar, item, 94);
				break;
				
			case 18107:
				setClass(activeChar, item, 95);
				break;
				
			case 18108:
				setClass(activeChar, item, 96);
				break;
				
			case 18109:
				setClass(activeChar, item, 97);
				break;
				
			case 18110:
				setClass(activeChar, item, 98);
				break;
				
			case 18111:
				setClass(activeChar, item, 99);
				break;
				
			case 18112:
				setClass(activeChar, item, 100);
				break;
				
			case 18113:
				setClass(activeChar, item, 101);
				break;
				
			case 18114:
				setClass(activeChar, item, 102);
				break;
				
			case 18115:
				setClass(activeChar, item, 103);
				break;
				
			case 18116:
				setClass(activeChar, item, 104);
				break;
				
			case 18117:
				setClass(activeChar, item, 105);
				break;
				
			case 18118:
				setClass(activeChar, item, 106);
				break;
				
			case 18119:
				setClass(activeChar, item, 107);
				break;
				
			case 18120:
				setClass(activeChar, item, 108);
				break;
				
			case 18121:
				setClass(activeChar, item, 109);
				break;
				
			case 18122:
				setClass(activeChar, item, 110);
				break;
				
			case 18123:
				setClass(activeChar, item, 111);
				break;
				
			case 18124:
				setClass(activeChar, item, 112);
				break;
				
			case 18125:
				setClass(activeChar, item, 113);
				break;
				
			case 18126:
				setClass(activeChar, item, 114);
				break;
				
			case 18127:
				setClass(activeChar, item, 115);
				break;
				
			case 18128:
				setClass(activeChar, item, 116);
				break;
				
			case 18129:
				setClass(activeChar, item, 117);
				break;
				
			case 18130:
				setClass(activeChar, item, 118);
				break;
		}
	}
	
	private void setClass(L2PcInstance player, ItemInstance item, int classId)
	{
		String nameclasse = player.getTemplate().getClassName();
		
		if (player.getBaseClass() != player.getClassId().getId())
		{
			player.sendMessage("You need to be in your base class to be able to use this item.");
			return;
		}

		if (player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_SELL || player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_BUY || player.getPrivateStoreType() == L2PcInstance.STORE_PRIVATE_PACKAGE_SELL)
		{
			player.sendMessage("Cannot use class changer while in store mode.");
			return;
		}

		if (player.getActiveTradeList() != null)
		{
			player.sendMessage("Cannot use class changer while in trade mode.");
			return;
		}
		
		if (player.getClassId().getId() == classId)
		{
			player.sendMessage("Your class is already " + nameclasse + ".");				
			return;
		}
		
		if (player.isInOlympiadMode() || OlympiadManager.getInstance().isRegisteredInComp(player))
		{
			player.sendPacket(SystemMessageId.THIS_ITEM_IS_NOT_AVAILABLE_FOR_THE_OLYMPIAD_EVENT);
			return;
		}
		
		if (!player.destroyItem("Consume", item, 1, null, false))
			return;
		
		RemoveSkills(player);
		
		player.setClassId(classId);
		
		if (!player.isSubClassActive())
			player.setBaseClass(classId);

		Finish(player);
	}

	private static void Finish(L2PcInstance player)
	{
		//msg
		String newclass = player.getTemplate().getClassName();
		player.sendMessage(player.getName() + " is now a " + newclass + ".");
		player.sendMessage("You will be disconnected for security reasons.");
		
		if (player.isNoble())
		{
			StatsSet playerStat = Olympiad.getNobleStats(player.getObjectId());
			if (!(playerStat == null))
			{
				updatePoints(player);
				DeleteHero(player);
				player.sendMessage("You now have " + Olympiad.getInstance().getNoblePoints(player.getObjectId()) + " olympiad points.");
			}
		}
		
		DeleteHero(player);
		DeleteHenna(player, 0);
		player.clearHennas();
		player.refreshOverloaded();
		player.store();
		player.rewardSkills();
		player.sendSkillList();
		player.broadcastUserInfo();
		ThreadPool.schedule(() -> player.getClient().closeNow(), 5000);
	}
	
	private static void RemoveSkills(L2PcInstance activeChar)
	{
		L2Skill[] skills = activeChar.getAllSkills();

		for (L2Skill skill : skills)
			activeChar.removeSkill(skill);
	}
	
	public static void updatePoints(L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("REPLACE INTO olympiad_nobles (char_id, class_id, olympiad_points) VALUES (?,?,?)"))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, player.getClassId().getId());
			statement.setInt(3, 18);
			statement.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.warning("could not clear char Olympiad Points: " + e);
		}
	}
	
	public static void DeleteHero(L2PcInstance player)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM heroes WHERE char_id=?"))
		{
			statement.setInt(1, player.getObjectId());
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.warning("could not clear char Hero: " + e);
		}
	}
	
	public static void DeleteHenna(L2PcInstance player, int classIndex)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?"))
		{
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, classIndex);
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			_log.warning("could not clear char henna dyes: " + e);
		}
	}
}