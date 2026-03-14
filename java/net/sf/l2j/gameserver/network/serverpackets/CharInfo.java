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
package net.sf.l2j.gameserver.network.serverpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.custom.DressMeData;
import net.sf.l2j.gameserver.model.holder.SkinPackage;
import net.sf.l2j.gameserver.instancemanager.CursedWeaponsManager;
import net.sf.l2j.gameserver.model.Location;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFConfig;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSConfig;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSEvent;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTConfig;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTConfig;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.skills.AbnormalEffect;

public class CharInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final Inventory _inv;
	
	private final int _fakeArmorItemId;
	private final int _fakeWeaponItemId;
	
	private final int _x, _y, _z;
	private final int _mAtkSpd, _pAtkSpd;
	private final int _runSpd, _walkSpd;
	private final float _moveMultiplier;
	
	public CharInfo(L2PcInstance cha)
	{
		_activeChar = cha;
		_inv = _activeChar.getInventory();
		
		_fakeArmorItemId = _activeChar.getFakeArmorItemId();
		_fakeWeaponItemId = _activeChar.getFakeWeaponItemId();
		
		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		
		_mAtkSpd = _activeChar.getMAtkSpd();
		_pAtkSpd = _activeChar.getPAtkSpd();
		
		_moveMultiplier = _activeChar.getMovementSpeedMultiplier();
		_runSpd = (int) (_activeChar.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) (_activeChar.getWalkSpeed() / _moveMultiplier);
	}
	
	@Override
	protected final void writeImpl()
	{
		boolean gmSeeInvis = false;
		L2PcInstance player = getClient().getActiveChar();
		
		if (_activeChar.getAppearance().getInvisible())
		{
			L2PcInstance tmp = getClient().getActiveChar();
			if (tmp != null && tmp.isGM())
				gmSeeInvis = true;
		}
		
		writeC(0x03);
		writeD(_x);
		writeD(_y);
		writeD(_z);
		writeD(0);
		writeD(_activeChar.getObjectId());
		
		if (_activeChar.isInTVTEvent())
		{
			byte playerTeamId = TvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + TvTConfig.TVT_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + TvTConfig.TVT_EVENT_TEAM_2_NAME);
		}
		else if (_activeChar.isInMultiTVTEvent())
		{
			byte playerTeamId = MultiTvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + MultiTvTConfig.MULTI_TVT_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + MultiTvTConfig.MULTI_TVT_EVENT_TEAM_2_NAME);
			
			if (playerTeamId == 2)
				writeS("Team " + MultiTvTConfig.MULTI_TVT_EVENT_TEAM_3_NAME);
			
			if (playerTeamId == 3)
				writeS("Team " + MultiTvTConfig.MULTI_TVT_EVENT_TEAM_4_NAME);
		}
		else if (_activeChar.isInCTFEvent())
		{
			byte playerTeamId = CTFEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + CTFConfig.CTF_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + CTFConfig.CTF_EVENT_TEAM_2_NAME);
		}
		else if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Team " + FOSConfig.FOS_EVENT_TEAM_1_NAME);
			
			if (playerTeamId == 1)
				writeS("Team " + FOSConfig.FOS_EVENT_TEAM_2_NAME);
		}
		else if (_activeChar.isInDMEvent() || _activeChar.isInLMEvent())
			writeS("Enemy");
		else if (_activeChar.isInsideZone(ZoneId.HIDE))
			writeS("Player");
		else
			writeS(_activeChar.getName());
		
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex() ? 1 : 0);
		
		if (_activeChar.getClassIndex() == 0)
			writeD(_activeChar.getClassId().getId());
		else
			writeD(_activeChar.getBaseClass());
		
		final boolean hasSkinOptions = _activeChar.getArmorSkinOption() > 0 || _activeChar.getWeaponSkinOption() > 0 || _activeChar.getHairSkinOption() > 0 || _activeChar.getShieldSkinOption() > 0;
		if ((!_activeChar.isDressMeEnabled() && !hasSkinOptions) || player.isDressMeDisabled())
		{
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_fakeArmorItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD) : 0);
			writeD(_fakeWeaponItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : _fakeWeaponItemId);
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(_fakeArmorItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) : 0);
			writeD(_fakeArmorItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST) : _fakeArmorItemId);
			writeD(_fakeArmorItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS) : 0);
			writeD(_fakeArmorItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET) : 0);
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			writeD(_fakeWeaponItemId == 0 ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : _fakeWeaponItemId);
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		}
		else
		{
			// Resolve skin option IDs (new system takes priority over DressMeHolder)
			int skinChest = 0, skinLegs = 0, skinGloves = 0, skinFeet = 0, skinWeapon = 0, skinHair = 0, skinShield = 0;
			if (_activeChar.getArmorSkinOption() > 0)
			{
				SkinPackage asp = DressMeData.getInstance().getArmorSkinsPackage(_activeChar.getArmorSkinOption());
				if (asp != null) { skinChest = asp.getChestId(); skinLegs = asp.getLegsId(); skinGloves = asp.getGlovesId(); skinFeet = asp.getFeetId(); }
			}
			if (_activeChar.getWeaponSkinOption() > 0)
			{
				SkinPackage wsp = DressMeData.getInstance().getWeaponSkinsPackage(_activeChar.getWeaponSkinOption());
				if (wsp != null) skinWeapon = wsp.getWeaponId();
			}
			if (_activeChar.getHairSkinOption() > 0)
			{
				SkinPackage hsp = DressMeData.getInstance().getHairSkinsPackage(_activeChar.getHairSkinOption());
				if (hsp != null) skinHair = hsp.getHairId();
			}
			if (_activeChar.getShieldSkinOption() > 0)
			{
				SkinPackage ssp = DressMeData.getInstance().getShieldSkinsPackage(_activeChar.getShieldSkinOption());
				if (ssp != null) skinShield = ssp.getShieldId();
			}
			
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
			writeD(skinWeapon > 0 ? skinWeapon : _inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(skinShield > 0 ? skinShield : _inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(skinGloves > 0 ? skinGloves : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getGlovesId() > 0 ? _activeChar.getDressMeData().getGlovesId() : _inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES)));
			writeD(skinChest > 0 ? skinChest : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getChestId() > 0 ? _activeChar.getDressMeData().getChestId() : _inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST)));
			writeD(skinLegs > 0 ? skinLegs : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getLegsId() > 0 ? _activeChar.getDressMeData().getLegsId() : _inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS)));
			writeD(skinFeet > 0 ? skinFeet : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getBootsId() > 0 ? _activeChar.getDressMeData().getBootsId() : _inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET)));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			writeD(skinWeapon > 0 ? skinWeapon : _inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			if (skinHair > 0)
				writeD(skinHair);
			else if (player.isDressMeHelmEnabled())
				writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			else
				writeD(_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getHairId() > 0 ? _activeChar.getDressMeData().getHairId() : _inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		}
		
		// c6 new h's
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		if (Config.ENABLE_CHAOTIC_COLOR_NAME && _activeChar.isInsideZone(ZoneId.FLAG_AREA_SELF))
			writeD(0);
		else
			writeD(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		
		writeD(_activeChar.getKarma());
		
		writeD(_mAtkSpd);
		writeD(_pAtkSpd);
		
		if (Config.ENABLE_CHAOTIC_COLOR_NAME && _activeChar.isInsideZone(ZoneId.FLAG_AREA_SELF))
			writeD(0);
		else
			writeD(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		
		writeD(_activeChar.getKarma());
		
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_runSpd); // swim run speed
		writeD(_walkSpd); // swim walk speed
		writeD(_runSpd); // fl run speed
		writeD(_walkSpd); // fl walk speed
		writeD(_runSpd); // fly run speed
		writeD(_walkSpd); // fly walk speed
		writeF(_activeChar.getMovementSpeedMultiplier());
		writeF(_activeChar.getAttackSpeedMultiplier());
		
		if (_activeChar.getMountType() != 0)
		{
			writeF(NpcTable.getInstance().getTemplate(_activeChar.getMountNpcId()).getCollisionRadius());
			writeF(NpcTable.getInstance().getTemplate(_activeChar.getMountNpcId()).getCollisionHeight());
		}
		else
		{
			writeF(_activeChar.getBaseTemplate().getCollisionRadius());
			writeF(_activeChar.getBaseTemplate().getCollisionHeight());
		}
		
		writeD(_activeChar.getAppearance().getHairStyle());
		writeD(_activeChar.getAppearance().getHairColor());
		writeD(_activeChar.getAppearance().getFace());
		
		if (gmSeeInvis && !_activeChar.isNoCarrier())
			writeS("Invisible");
		else if (_activeChar.isNoCarrier() && !_activeChar.isFakeOffline())
			writeS(Config.NO_CARRIER_TITLE);
		else if (_activeChar.isInTVTEvent())
		{
			byte playerTeamId = TvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Kills: " + _activeChar.getPointScore());
			
			if (playerTeamId == 1)
				writeS("Kills: " + _activeChar.getPointScore());
		}
		else if (_activeChar.isInMultiTVTEvent())
		{
			byte playerTeamId = MultiTvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Kills: " + _activeChar.getPointScore());
			
			if (playerTeamId == 1)
				writeS("Kills: " + _activeChar.getPointScore());
			
			if (playerTeamId == 2)
				writeS("Kills: " + _activeChar.getPointScore());
			
			if (playerTeamId == 3)
				writeS("Kills: " + _activeChar.getPointScore());
		}
		else if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeS("Score: " + _activeChar.getFOSPointScore());
			
			if (playerTeamId == 1)
				writeS("Score: " + _activeChar.getFOSPointScore());
		}
		else if (_activeChar.isInDMEvent())
			writeS("Kills: " + _activeChar.getDMPointScore());
		else
			writeS(_activeChar.getTitle());
		
		if (_activeChar.isInMultiTVTEvent() || _activeChar.isInTVTEvent() || _activeChar.isInCTFEvent() || _activeChar.isInFOSEvent() || _activeChar.isInDMEvent() || _activeChar.isInLMEvent() || _activeChar.isInsideZone(ZoneId.HIDE))
		{
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
			writeD(0x00);
		}
		else
		{
			writeD(_activeChar.getClanId());
			writeD(_activeChar.getClanCrestId());
			writeD(_activeChar.getAllyId());
			writeD(_activeChar.getAllyCrestId());
		}
		
		writeD(0);
		
		writeC(_activeChar.isSitting() ? 0 : 1); // standing = 1 sitting = 0
		writeC(_activeChar.isRunning() ? 1 : 0); // running = 1 walking = 0
		writeC(_activeChar.isInCombat() ? 1 : 0);
		writeC(_activeChar.isAlikeDead() ? 1 : 0);
		
		if (gmSeeInvis)
			writeC(0);
		else
			writeC(_activeChar.getAppearance().getInvisible() ? 1 : 0); // invisible = 1 visible =0
			
		writeC(_activeChar.getMountType()); // 1 on strider 2 on wyvern 0 no mount
		writeC(_activeChar.getPrivateStoreType()); // 1 - sellshop
		
		writeH(_activeChar.getCubics().size());
		for (int id : _activeChar.getCubics().keySet())
			writeH(id);
		
		writeC(_activeChar.isInPartyMatchRoom() ? 1 : 0);
		
		if (gmSeeInvis)
			writeD((_activeChar.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask()));
		else
			writeD(_activeChar.getAbnormalEffect());
		
		writeC(_activeChar.getRecomLeft());
		writeH(_activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeD(_activeChar.getClassId().getId());
		
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		
	    if (player.isDisableGlowWeapon())
	        writeC(0); 
	    else 
	    	writeC(_activeChar.isMounted() ? 0 : _activeChar.getEnchantEffect());
	    
		if (_activeChar.getTeam() == 1)
			writeC(0x01); // team circle around feet 1= Blue, 2 = red
		else if (_activeChar.getTeam() == 2)
			writeC(0x02); // team circle around feet 1= Blue, 2 = red
		else
			writeC(0x00); // team circle around feet 1= Blue, 2 = red
			
		writeD(_activeChar.getClanCrestLargeId());
		writeC(_activeChar.isNoble() ? 1 : 0); // Symbol on char menu ctrl+I
		
		if (player.isDisableHeroAura())
			writeC(0);
		else 
			writeC((_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) || _activeChar.getIsPVPHero()) ? 1 : 0); // Hero Aura

		writeC(_activeChar.isFishing() ? 1 : 0); // 0x01: Fishing Mode (Cant be undone by setting back to 0)
		
		Location loc = _activeChar.getFishingLoc();
		if (loc != null)
		{
			writeD(loc.getX());
			writeD(loc.getY());
			writeD(loc.getZ());
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
		}
		
		if (_activeChar.isInTVTEvent())
		{
			byte playerTeamId = TvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
			
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
		}
		else if (_activeChar.isInMultiTVTEvent())
		{
			byte playerTeamId = MultiTvTEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
			
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
			
			if (playerTeamId == 2)
				writeD(0x66D9FF); // Yellow
			
			if (playerTeamId == 3)
				writeD(0x00FF00); // Green
		}
		else if (_activeChar.isInCTFEvent())
		{
			byte playerTeamId = CTFEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
			
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
		}
		else if (_activeChar.isInFOSEvent())
		{
			byte playerTeamId = FOSEvent.getParticipantTeamId(_activeChar.getObjectId());
			
			if (playerTeamId == 0)
				writeD(0xFF3500); // Blue
			
			if (playerTeamId == 1)
				writeD(0x0000F8); // Red
		}
		else if (_activeChar.isInDMEvent() || _activeChar.isInLMEvent())
			writeD(0x0000F8); // Red
		else if (_activeChar.isInKTBEvent())
			writeD(0x00F821); // Green
		else if (Config.ENABLE_CHAOTIC_COLOR_NAME && _activeChar.isInsideZone(ZoneId.FLAG_AREA_SELF))
			writeD(_activeChar.getVisibleNameColor());
		else
			writeD(_activeChar.getAppearance().getNameColor());
		
		writeD(0x00);
		
		writeD(_activeChar.getPledgeClass());
		writeD(_activeChar.getPledgeType());
		
		if (_activeChar.isInFunEvent())
			writeD(0xfdf3f1); // Green
		else if (_activeChar.isInKTBEvent())
			writeD(0xfdf3f1); // Green
		else
			writeD(_activeChar.getAppearance().getTitleColor());
		
		if (_activeChar.isCursedWeaponEquipped())
			writeD(CursedWeaponsManager.getInstance().getCurrentStage(_activeChar.getCursedWeaponEquippedId()) - 1);
		else
			writeD(0x00);
	}
}