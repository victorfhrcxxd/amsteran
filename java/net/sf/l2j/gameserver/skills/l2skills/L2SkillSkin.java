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
package net.sf.l2j.gameserver.skills.l2skills;

import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;

public class L2SkillSkin extends L2Skill
{
	public L2SkillSkin(StatsSet set)
	{
		super(set);
	}

	@Override
	public void useSkill(L2Character activeChar, L2Object[] targets)
	{
		if (activeChar.isAlikeDead())
			return;

		//final L2PcInstance player = activeChar.getActingPlayer();

		if (getSkillType() == L2SkillType.RANK_DIAMOND) //Diamond Suit
		{
			/*
			if (player.getDressMeData() == null)
			{
				DressMeData dmd = new DressMeData();
				player.setDressMeData(dmd);
			}

			player.setDressMeEnabled(true);
			player.setDressMeHelmEnabled(false);
			
			if (player.getClassId() == ClassId.maestro || player.getClassId() == ClassId.fortuneSeeker || player.getClassId() == ClassId.titan || player.getClassId() == ClassId.dreadnought || player.getClassId() == ClassId.duelist || player.getClassId() == ClassId.phoenixKnight || player.getClassId() == ClassId.hellKnight || player.getClassId() == ClassId.evaTemplar || player.getClassId() == ClassId.swordMuse || player.getClassId() == ClassId.shillienTemplar || player.getClassId() == ClassId.spectralDancer)
			{
				player.getDressMeData().setChestId(40000);
				player.getDressMeData().setLegsId(40001);
				player.getDressMeData().setGlovesId(40002);
				player.getDressMeData().setBootsId(40003);
			}
			
			if (player.getClassId() == ClassId.grandKhauatari || player.getClassId() == ClassId.sagittarius || player.getClassId() == ClassId.adventurer || player.getClassId() == ClassId.windRider || player.getClassId() == ClassId.moonlightSentinel || player.getClassId() == ClassId.ghostHunter || player.getClassId() == ClassId.ghostSentinel)
			{
				player.getDressMeData().setChestId(40004);
				player.getDressMeData().setLegsId(40005);
				player.getDressMeData().setGlovesId(40006);
				player.getDressMeData().setBootsId(40007);
			}
			
			if (player.isMageClass() || player.getClassId() == ClassId.dominator)
			{
				player.getDressMeData().setChestId(40008);
				player.getDressMeData().setLegsId(40009);
				player.getDressMeData().setGlovesId(40010);
				player.getDressMeData().setBootsId(40011);
			}
			player.broadcastUserInfo();
			*/
		}
	}
}