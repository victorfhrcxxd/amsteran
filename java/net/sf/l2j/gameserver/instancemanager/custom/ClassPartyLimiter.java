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
package net.sf.l2j.gameserver.instancemanager.custom;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2Party.MessageType;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.StringIntHolder;

public class ClassPartyLimiter
{
	public static final ClassPartyLimiter getInstance()
	{
		return SingletonHolder._instance;
	}
	
	public boolean checkPlayerClassLimit(L2PcInstance player)
	{
		if (Config.ANTZERG_CLASS_LIMIT == null)
			return false;
		
		if (player.getParty() == null)
			return false;
		
		L2Party party = player.getParty();
		
		for (StringIntHolder info : Config.ANTZERG_CLASS_LIMIT)
		{
			int classCount = (int) party.getPartyMembers().stream().filter(x -> checkClassCount(info.getName(), x.getClassId().getId())).count();
			if (classCount > info.getValue())
			{
				player.sendMessage("Your team have extend limit of " + info.getValue() + " " + info.getName() + ".");
				player.getParty().removePartyMember(player, MessageType.Left);
				return true;
			}
		}
		return false;
	}
	
	public boolean checkRequestorClassLimit(L2PcInstance requestor, L2PcInstance target)
	{
		if (Config.ANTZERG_CLASS_LIMIT == null)
			return false;
		
		if (requestor.getParty() == null)
			return false;
		
		L2Party party = requestor.getParty();
		
		for (StringIntHolder info : Config.ANTZERG_CLASS_LIMIT)
		{
			int classCount = (int) party.getPartyMembers().stream().filter(x -> checkClassCount(info.getName(), x.getClassId().getId())).count();
			if (checkClassCount(info.getName(), target.getClassId().getId()))
				classCount++;
			
			if (classCount > info.getValue())
			{
				requestor.sendMessage("Your team have extend limit of " + info.getValue() + " " + info.getName() + ".");
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkClassCount(String className, int id)
	{
		switch (className)
		{
			case "healer":
				switch (id)
				{
					// 2rd job
					case 16:
					case 30:
					case 42:
					case 43:
						// 3rd job
					case 97:
					case 105:
					case 112:
						return true;
				}
				break;
			case "archer":
				switch (id)
				{
					// 2rd job
					case 37:
					case 24:
					case 9:
						// 3rd job
					case 92:
					case 102:
					case 109:
						return true;
				}
				break;
			case "dominator":
				switch (id)
				{
					// 2rd job
					case 115:
						// 3rd job
					case 51:
						return true;
				}
				break;
			case "duelist":
				switch (id)
				{
					// 2rd job
					case 2:
						// 3rd job
					case 88:
						return true;
				}
				break;
			case "dreadnought":
				switch (id)
				{
					// 2rd job
					case 3:
						// 3rd job
					case 89:
						return true;
				}
				break;
			case "tanker":
				switch (id)
				{
					// 2rd job
					case 19:
					case 20:
					case 21:
					case 32:
					case 33:
					case 34:
						// 3rd job
					case 90:
					case 91:
					case 99:
					case 100:
					case 106:
						return true;
				}
				break;
			case "mage":
				switch (id)
				{
					// 2rd job
					case 12:
					case 13:
					case 27:
					case 40:
						// 3rd job
					case 94:
					case 95:
					case 103:
					case 110:
						return true;
				}
			case "titan":
				switch (id)
				{
					// 2rd job
					case 45:
						// 3rd job
					case 113:
						return true;
				}
				break;
			case "gk":
				switch (id)
				{
					// 2rd job
					case 47:
						// 3rd job
					case 114:
						return true;
				}
				break;
			case "doomcryer":
				switch (id)
				{
					// 2rd job
					case 52:
						// 3rd job
					case 116:
						return true;
				}
				break;
		}
		return false;
	}
	
	private static class SingletonHolder
	{
		protected static final ClassPartyLimiter _instance = new ClassPartyLimiter();
	}
}