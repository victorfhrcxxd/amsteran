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

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.L2Playable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

public class ItemSkins2 implements IItemHandler
{
	@Override
	public void useItem(L2Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof L2PcInstance))
			return;

		L2PcInstance activeChar = (L2PcInstance) playable;

		int itemId = item.getItemId();

		if (itemId == 30600)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55000);
			activeChar.getDressMeData().setChestId(55001);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30601)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55002);
			activeChar.getDressMeData().setChestId(55003);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30602)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55004);
			activeChar.getDressMeData().setChestId(55005);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30603)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55006);
			activeChar.getDressMeData().setChestId(55007);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30604)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55008);
			activeChar.getDressMeData().setChestId(55009);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30605)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55010);
			activeChar.getDressMeData().setChestId(55011);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30606)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55012);
			activeChar.getDressMeData().setChestId(55013);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30607)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55014);
			activeChar.getDressMeData().setChestId(55015);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30608)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55016);
			activeChar.getDressMeData().setChestId(55017);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30609)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55018);
			activeChar.getDressMeData().setChestId(55019);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30610)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55020);
			activeChar.getDressMeData().setChestId(55021);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30611)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55022);
			activeChar.getDressMeData().setChestId(55023);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30612)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55024);
			activeChar.getDressMeData().setChestId(55025);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30613)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55026);
			activeChar.getDressMeData().setChestId(55027);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30614)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55028);
			activeChar.getDressMeData().setChestId(55029);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30615)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55030);
			activeChar.getDressMeData().setChestId(55031);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30616)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55032);
			activeChar.getDressMeData().setChestId(55033);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30617)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55034);
			activeChar.getDressMeData().setChestId(55035);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30618)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55036);
			activeChar.getDressMeData().setChestId(55037);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30619)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55038);
			activeChar.getDressMeData().setChestId(55039);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30620)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55040);
			activeChar.getDressMeData().setChestId(55041);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30621)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55042);
			activeChar.getDressMeData().setChestId(55043);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30622)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55044);
			activeChar.getDressMeData().setChestId(55045);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30623)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55046);
			activeChar.getDressMeData().setChestId(55047);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30624)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55048);
			activeChar.getDressMeData().setChestId(55049);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30625)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55050);
			activeChar.getDressMeData().setChestId(55051);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30626)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55052);
			activeChar.getDressMeData().setChestId(55053);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30627)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55054);
			activeChar.getDressMeData().setChestId(55055);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30628)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55056);
			activeChar.getDressMeData().setChestId(55057);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30629)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55058);
			activeChar.getDressMeData().setChestId(55059);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30630)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55060);
			activeChar.getDressMeData().setChestId(55061);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30631)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55062);
			activeChar.getDressMeData().setChestId(55063);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30632)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55064);
			activeChar.getDressMeData().setChestId(55065);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30633)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55066);
			activeChar.getDressMeData().setChestId(55067);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30634)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55068);
			activeChar.getDressMeData().setChestId(55069);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30635)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55070);
			activeChar.getDressMeData().setChestId(55071);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30636)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55072);
			activeChar.getDressMeData().setChestId(55073);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30637)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55074);
			activeChar.getDressMeData().setChestId(55075);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30638)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55076);
			activeChar.getDressMeData().setChestId(55077);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30639)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55078);
			activeChar.getDressMeData().setChestId(55079);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30640)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55080);
			activeChar.getDressMeData().setChestId(55081);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30641)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55082);
			activeChar.getDressMeData().setChestId(55083);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30642)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55084);
			activeChar.getDressMeData().setChestId(55085);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30643)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55086);
			activeChar.getDressMeData().setChestId(55087);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30644)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55088);
			activeChar.getDressMeData().setChestId(55089);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30645)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55090);
			activeChar.getDressMeData().setChestId(55091);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30646)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55092);
			activeChar.getDressMeData().setChestId(55093);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30647)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55094);
			activeChar.getDressMeData().setChestId(55095);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30648)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55096);
			activeChar.getDressMeData().setChestId(55097);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30649)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55098);
			activeChar.getDressMeData().setChestId(55099);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30650)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55100);
			activeChar.getDressMeData().setChestId(55101);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30651)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55102);
			activeChar.getDressMeData().setChestId(55103);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30652)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55104);
			activeChar.getDressMeData().setChestId(55105);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30653)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55106);
			activeChar.getDressMeData().setChestId(55107);

			activeChar.broadcastUserInfo();
		}
		if (itemId == 30654)
		{
			if (activeChar.getDressMeData() == null)
			{
				DressMeHolder dmd = new DressMeHolder();
				activeChar.setDressMeData(dmd);
			}
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, activeChar, 1036, 1, 4000, 0));

			activeChar.setDressMeHelmEnabled(false);
			activeChar.setDressMeEnabled(true);

			activeChar.getDressMeData().setHairId(55108);
			activeChar.getDressMeData().setChestId(55109);

			activeChar.broadcastUserInfo();
		}
	}
}