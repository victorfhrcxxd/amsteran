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
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSEvent;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.model.itemcontainer.Inventory;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.skills.AbnormalEffect;

public class UserInfo extends L2GameServerPacket
{
	private final L2PcInstance _activeChar;
	private final int _runSpd, _walkSpd;
	private int _relation;
	
	private final int _fakeArmorObjectId;
	private final int _fakeArmorItemId;
	private final int _fakeWeaponObjectId;
	private final int _fakeWeaponItemId;
	
	private final float _moveMultiplier;
	
	public UserInfo(L2PcInstance character)
	{
		_activeChar = character;
		
		_moveMultiplier = _activeChar.getMovementSpeedMultiplier();
		_runSpd = (int) (_activeChar.getRunSpeed() / _moveMultiplier);
		_walkSpd = (int) (_activeChar.getWalkSpeed() / _moveMultiplier);
		_relation = _activeChar.isClanLeader() ? 0x40 : 0;
		
		if (_activeChar.getSiegeState() == 1)
			_relation |= 0x180;
		if (_activeChar.getSiegeState() == 2)
			_relation |= 0x80;
				
		_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR);
		_fakeArmorObjectId = _activeChar.getFakeArmorObjectId();
		_fakeArmorItemId = _activeChar.getFakeArmorItemId();
		_fakeWeaponObjectId = _activeChar.getFakeWeaponObjectId();
		_fakeWeaponItemId = _activeChar.getFakeWeaponItemId();
	}
	
	@Override
	protected final void writeImpl()
	{
		writeC(0x04);
		
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_activeChar.getHeading());
		writeD(_activeChar.getObjectId());
		
		String name = _activeChar.getName();
		if (_activeChar.getPoly().isMorphed())
		{
			NpcTemplate polyObj = NpcTable.getInstance().getTemplate(_activeChar.getPoly().getPolyId());
			if (polyObj != null)
				name = polyObj.getName();
		}
		writeS(name);
		
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex() ? 1 : 0);
		
		if (_activeChar.getClassIndex() == 0)
			writeD(_activeChar.getClassId().getId());
		else
			writeD(_activeChar.getBaseClass());
		
		writeD(_activeChar.getLevel());
		writeQ(_activeChar.getExp());
		writeD(_activeChar.getSTR());
		writeD(_activeChar.getDEX());
		writeD(_activeChar.getCON());
		writeD(_activeChar.getINT());
		writeD(_activeChar.getWIT());
		writeD(_activeChar.getMEN());
		writeD(_activeChar.getMaxHp());
		writeD((int) _activeChar.getCurrentHp());
		writeD(_activeChar.getMaxMp());
		writeD((int) _activeChar.getCurrentMp());
		writeD(_activeChar.getSp());
		writeD(_activeChar.getCurrentLoad());
		writeD(_activeChar.getMaxLoad());
		
		writeD(_activeChar.getActiveWeaponItem() != null ? 40 : 20); // 20 no weapon, 40 weapon equipped
		
		final boolean hasSkinOptions = _activeChar.getArmorSkinOption() > 0 || _activeChar.getWeaponSkinOption() > 0 || _activeChar.getHairSkinOption() > 0 || _activeChar.getShieldSkinOption() > 0;
		if ((!_activeChar.isDressMeEnabled() && !hasSkinOptions) || _activeChar.isDressMeDisabled())
		{
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
			writeD(_fakeArmorObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD) : 0);
			writeD(_fakeWeaponObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND) : _fakeWeaponObjectId);
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND));
			writeD(_fakeArmorObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES) : 0);
			writeD(_fakeArmorObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST) : _fakeArmorObjectId);
			writeD(_fakeArmorObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS) : 0);
			writeD(_fakeArmorObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET) : 0);
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_BACK));
			//writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
			writeD(_fakeWeaponObjectId == 0 ? _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND) : _fakeWeaponObjectId);
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FACE));

			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_REAR));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_NECK));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
			writeD(_fakeArmorItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HEAD) : 0);
			writeD(_fakeWeaponItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : _fakeWeaponItemId);
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(_fakeArmorItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) : 0);
			writeD(_fakeArmorItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST) : _fakeArmorItemId);
			writeD(_fakeArmorItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS) : 0);
			writeD(_fakeArmorItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET) : 0);
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			//writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(_fakeWeaponItemId == 0 ? _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND) : _fakeWeaponItemId);
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FACE));
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
			
			// Object IDs section - use skin itemId as fake objectId for skinned slots to force client refresh
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_REAR));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEAR));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_NECK));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RFINGER));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LFINGER));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HEAD));
			writeD(skinWeapon > 0 ? skinWeapon : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
			writeD(skinShield > 0 ? skinShield : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND));
			writeD(skinGloves > 0 ? skinGloves : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_GLOVES));
			writeD(skinChest > 0 ? skinChest : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_CHEST));
			writeD(skinLegs > 0 ? skinLegs : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LEGS));
			writeD(skinFeet > 0 ? skinFeet : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FEET));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_BACK));
			writeD(skinWeapon > 0 ? skinWeapon : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_RHAND));
			writeD(skinHair > 0 ? skinHair : _activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_HAIR));
			writeD(_activeChar.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_FACE));
			
			// Item IDs section (visual appearance)
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_REAR));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEAR));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_NECK));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RFINGER));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LFINGER));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
			writeD(skinWeapon > 0 ? skinWeapon : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			writeD(skinShield > 0 ? skinShield : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND));
			writeD(skinGloves > 0 ? skinGloves : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getGlovesId() > 0 ? _activeChar.getDressMeData().getGlovesId() : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_GLOVES)));
			writeD(skinChest > 0 ? skinChest : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getChestId() > 0 ? _activeChar.getDressMeData().getChestId() : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_CHEST)));
			writeD(skinLegs > 0 ? skinLegs : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getLegsId() > 0 ? _activeChar.getDressMeData().getLegsId() : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LEGS)));
			writeD(skinFeet > 0 ? skinFeet : (_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getBootsId() > 0 ? _activeChar.getDressMeData().getBootsId() : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FEET)));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_BACK));
			writeD(skinWeapon > 0 ? skinWeapon : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
			if (skinHair > 0)
				writeD(skinHair);
			else if (_activeChar.isDressMeHelmEnabled())
				writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			else
				writeD(_activeChar.getDressMeData() != null && _activeChar.getDressMeData().getHairId() > 0 ? _activeChar.getDressMeData().getHairId() : _activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_HAIR));
			writeD(_activeChar.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		}
		
		// c6 new h's
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
		writeH(0x00);
		writeH(0x00);
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
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
		writeD(_activeChar.getInventory().getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		// end of c6 new h's
		writeD(_activeChar.getPAtk(null));
		writeD(_activeChar.getPAtkSpd());
		writeD(_activeChar.getPDef(null));
		writeD(_activeChar.getEvasionRate(null));
		writeD(_activeChar.getAccuracy());
		writeD(_activeChar.getCriticalHit(null, null));
		writeD(_activeChar.getMAtk(null, null));
		
		writeD(_activeChar.getMAtkSpd());
		writeD(_activeChar.getPAtkSpd());
		
		writeD(_activeChar.getMDef(null, null));
		
		if (Config.ENABLE_CHAOTIC_COLOR_NAME && _activeChar.isInsideZone(ZoneId.FLAG_AREA_SELF))
			writeD(0);
		else
			writeD(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
		
		writeD(_activeChar.getKarma());
		
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_runSpd); // swim run speed
		writeD(_walkSpd); // swim walk speed
		writeD(0);
		writeD(0);
		writeD(_activeChar.isFlying() ? _runSpd : 0); // fly speed
		writeD(_activeChar.isFlying() ? _walkSpd : 0); // fly speed
		writeF(_moveMultiplier);
		writeF(_activeChar.getAttackSpeedMultiplier());
		
		L2Summon pet = _activeChar.getPet();
		if (_activeChar.getMountType() != 0 && pet != null)
		{
			writeF(pet.getTemplate().getCollisionRadius());
			writeF(pet.getTemplate().getCollisionHeight());
		}
		else
		{
			writeF(_activeChar.getBaseTemplate().getCollisionRadius());
			writeF(_activeChar.getBaseTemplate().getCollisionHeight());
		}
		
		writeD(_activeChar.getAppearance().getHairStyle());
		writeD(_activeChar.getAppearance().getHairColor());
		writeD(_activeChar.getAppearance().getFace());
		writeD(_activeChar.isGM() ? 1 : 0); // builder level
		
		if (_activeChar.isInTVTEvent())
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
			writeS(_activeChar.getPoly().isMorphed() ? "Morphed" : _activeChar.getTitle());
		
		writeD(_activeChar.getClanId());
		writeD(_activeChar.getClanCrestId());
		writeD(_activeChar.getAllyId());
		writeD(_activeChar.getAllyCrestId()); // ally crest id
		// 0x40 leader rights
		// siege flags: attacker - 0x180 sword over name, defender - 0x80 shield, 0xC0 crown (|leader), 0x1C0 flag (|leader)
		writeD(_relation);
		writeC(_activeChar.getMountType()); // mount type
		writeC(_activeChar.getPrivateStoreType());
		writeC(_activeChar.hasDwarvenCraft() ? 1 : 0);
		writeD(_activeChar.getPkKills());
		writeD(_activeChar.getPvpKills());
		
		writeH(_activeChar.getCubics().size());
		for (int id : _activeChar.getCubics().keySet())
			writeH(id);
		
		writeC(_activeChar.isInPartyMatchRoom() ? 1 : 0);
		
		if (_activeChar.getAppearance().getInvisible() && _activeChar.isGM())
			writeD(_activeChar.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask());
		else
			writeD(_activeChar.getAbnormalEffect());
		writeC(0x00);
		
		writeD(_activeChar.getClanPrivileges());
		
		writeH(_activeChar.getRecomLeft()); // c2 recommendations remaining
		writeH(_activeChar.getRecomHave()); // c2 recommendations received
		writeD(_activeChar.getMountNpcId() > 0 ? _activeChar.getMountNpcId() + 1000000 : 0);
		writeH(_activeChar.getInventoryLimit());
		
		writeD(_activeChar.getClassId().getId());
		writeD(0x00); // special effects? circles around player...
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		
	    if (_activeChar.isDisableGlowWeapon()) 
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
		writeC(_activeChar.isNoble() ? 1 : 0); // 0x01: symbol on char menu ctrl+I
		
	    if (_activeChar.isDisableHeroAura())
	        writeC(0); 
	    else 
	        writeC((_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA) || _activeChar.getIsPVPHero()) ? 1 : 0); // Hero Aura
		
		writeC(_activeChar.isFishing() ? 1 : 0); // Fishing Mode
		
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

		// new c5
		writeC(_activeChar.isRunning() ? 0x01 : 0x00); // changes the Speed display on Status Window
		
		writeD(_activeChar.getPledgeClass()); // changes the text above CP on Status Window
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