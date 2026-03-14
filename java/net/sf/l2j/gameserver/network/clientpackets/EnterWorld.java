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
package net.sf.l2j.gameserver.network.clientpackets;

import hwid.Hwid;
import hwid.HwidConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameTimeController;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.communitybbs.Manager.MailBBSManager;
import net.sf.l2j.gameserver.datatables.AdminCommandAccessRights;
import net.sf.l2j.gameserver.datatables.AnnouncementTable;
import net.sf.l2j.gameserver.datatables.custom.DollsTable;
import net.sf.l2j.gameserver.datatables.GmListTable;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.datatables.SkillTable.FrequentSkill;
import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.instancemanager.OfflineFarmManager;
import net.sf.l2j.gameserver.instancemanager.ClanHallManager;
import net.sf.l2j.gameserver.instancemanager.CoupleManager;
import net.sf.l2j.gameserver.instancemanager.DimensionalRiftManager;
import net.sf.l2j.gameserver.instancemanager.PetitionManager;
import net.sf.l2j.gameserver.instancemanager.QuestManager;
import net.sf.l2j.gameserver.instancemanager.SevenSigns;
import net.sf.l2j.gameserver.instancemanager.SiegeManager;
import net.sf.l2j.gameserver.instancemanager.custom.DailyRewardManager;
import net.sf.l2j.gameserver.instancemanager.custom.GloryRewardManager;
import net.sf.l2j.gameserver.instancemanager.custom.HwidManager;
import net.sf.l2j.gameserver.instancemanager.custom.IPManager;
import net.sf.l2j.gameserver.instancemanager.custom.TimeInstanceManager;
import net.sf.l2j.gameserver.model.BlockList;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2Clan.SubPledge;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2ClassMasterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance.PunishLevel;
import net.sf.l2j.gameserver.model.base.Race;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.model.entity.ClanHall;
import net.sf.l2j.gameserver.model.entity.Couple;
import net.sf.l2j.gameserver.model.entity.Siege;
import net.sf.l2j.gameserver.model.entity.events.StartupSystem;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.multiteamvsteam.MultiTvTEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
import net.sf.l2j.gameserver.model.zone.ZoneId;
import net.sf.l2j.gameserver.network.L2GameClient.GameClientState;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.Die;
import net.sf.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ExMailArrived;
import net.sf.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import net.sf.l2j.gameserver.network.serverpackets.FriendList;
import net.sf.l2j.gameserver.network.serverpackets.HennaInfo;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListAll;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import net.sf.l2j.gameserver.network.serverpackets.PledgeSkillList;
import net.sf.l2j.gameserver.network.serverpackets.PledgeStatusChanged;
import net.sf.l2j.gameserver.network.serverpackets.QuestList;
import net.sf.l2j.gameserver.network.serverpackets.ShortCutInit;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.network.serverpackets.SpecialCamera;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskHeroEnd;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskItemDestroy;
import net.sf.l2j.gameserver.taskmanager.tasks.TaskVipEnd;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.gameserver.util.HwidLog;
import net.sf.l2j.gameserver.util.Util;

public class EnterWorld extends L2GameClientPacket
{
	long _daysleft, _minleft;
	SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void readImpl()
	{
		// this is just a trigger packet. it has no content
	}
	
	@Override
	protected void runImpl()
	{
		final L2PcInstance activeChar = getClient().getActiveChar();
		if (activeChar == null)
		{
			_log.warning("EnterWorld failed! activeChar is null...");
			getClient().closeNow();
			return;
		}
		
		getClient().setState(GameClientState.IN_GAME);
		
        // Multibox Protection by IP
        if (Config.MULTIBOX_PROTECTION_ENABLED)
        	IPManager.getInstance().validBox(activeChar, Config.MULTIBOX_PROTECTION_CLIENTS_PER_PC, L2World.getInstance().getAllPlayers().values(), true);
		
        // Multibox Protection by Hwid
        if (Config.HWID_MULTIBOX_PROTECTION_ENABLED)
            HwidManager.getInstance().validBox(activeChar, Config.HWID_MULTIBOX_PROTECTION_CLIENTS_PER_PC, L2World.getInstance().getAllPlayers().values(), true);
		
        if (L2World.getInstance().findObject(activeChar.getObjectId()) != null)
        {
        	_log.warning("User already exist in OID map! User " + activeChar.getName() + " is character clone.");
        }

		if (activeChar.isGM())
		{
		    activeChar.getAppearance().setNameColor(Config.MASTERACCESS_NAME_COLOR);
		    activeChar.getAppearance().setTitleColor(Config.MASTERACCESS_TITLE_COLOR);
		    
			if (Config.GM_STARTUP_INVULNERABLE && AdminCommandAccessRights.getInstance().hasAccess("admin_invul", activeChar.getAccessLevel()))
				activeChar.setIsInvul(true);
			
			if (!Util.contains(Config.GM_NAMES, activeChar.getName()))
				activeChar.setPunishLevel(PunishLevel.ACC, 0);

			if (Config.GM_STARTUP_INVISIBLE && AdminCommandAccessRights.getInstance().hasAccess("admin_hide", activeChar.getAccessLevel()))
				activeChar.getAppearance().setInvisible();
			
			if (Config.GM_STARTUP_SILENCE && AdminCommandAccessRights.getInstance().hasAccess("admin_silence", activeChar.getAccessLevel()))
				activeChar.setInRefusalMode(true);
			
			if (Config.GM_STARTUP_AUTO_LIST && AdminCommandAccessRights.getInstance().hasAccess("admin_gmliston", activeChar.getAccessLevel()))
				GmListTable.getInstance().addGm(activeChar, false);
			else
				GmListTable.getInstance().addGm(activeChar, true);
		}
		
		// Means that it's not ok multiBox situation, so logout
		if (!activeChar.checkMultiBox())
		{
			System.out.println("DUAL BOX: " + activeChar.getName() + " Disconnected..");
			activeChar.sendMessage("I'm sorry, but multibox is not allowed here.");
			activeChar.logout();
		}
		
		// Set dead status if applies
		if (activeChar.getCurrentHp() < 0.5)
			activeChar.setIsDead(true);
		
		final L2Clan clan = activeChar.getClan();
		if (clan != null)
		{
			activeChar.sendPacket(new PledgeSkillList(clan));
			notifyClanMembers(activeChar);
			notifySponsorOrApprentice(activeChar);
			
			// Add message at connexion if clanHall not paid.
			final ClanHall clanHall = ClanHallManager.getInstance().getClanHallByOwner(clan);
			if (clanHall != null)
			{
				if (!clanHall.getPaid())
					activeChar.sendPacket(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW);
			}
			
			for (Siege siege : SiegeManager.getSieges())
			{
				if (!siege.isInProgress())
					continue;
				
				for (Castle castle : CastleManager.getInstance().getCastles())
				{
					if (siege.checkIsAttacker(clan))
					{
						activeChar.setSiegeState((byte) 1);
						activeChar.setSiegeSide(castle.getCastleId());
					}
					else if (siege.checkIsDefender(clan))
					{
						activeChar.setSiegeState((byte) 2);
						activeChar.setSiegeSide(castle.getCastleId());
					}
				}
			}
			
			activeChar.sendPacket(new PledgeShowMemberListAll(clan, 0));
			
			for (SubPledge sp : clan.getAllSubPledges())
				activeChar.sendPacket(new PledgeShowMemberListAll(clan, sp.getId()));
			
			activeChar.sendPacket(new UserInfo(activeChar));
			activeChar.sendPacket(new PledgeStatusChanged(clan));
			clan.broadcastClanStatus();
		}
		
		// Updating Seal of Strife Buff/Debuff
		if (SevenSigns.getInstance().isSealValidationPeriod() && SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE) != SevenSigns.CABAL_NULL)
		{
			int cabal = SevenSigns.getInstance().getPlayerCabal(activeChar.getObjectId());
			if (cabal != SevenSigns.CABAL_NULL)
			{
				if (cabal == SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE))
					activeChar.addSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
				else
					activeChar.addSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
			}
		}
		else
		{
			activeChar.removeSkill(FrequentSkill.THE_VICTOR_OF_WAR.getSkill());
			activeChar.removeSkill(FrequentSkill.THE_VANQUISHED_OF_WAR.getSkill());
		}

		activeChar.spawnMe(activeChar.getX(), activeChar.getY(), activeChar.getZ());
		
		// engage and notify Partner
		if (Config.ALLOW_WEDDING)
			engage(activeChar);
		
		// Welcome message
		if (Config.ALLOW_WELCOME_TO_LINEAGE)
			activeChar.sendPacket(SystemMessageId.WELCOME_TO_LINEAGE);

		// Check player skills
		if (Config.CHECK_SKILLS_ON_ENTER)
		    if (!activeChar.isAio())
		    		activeChar.checkAllowedSkills();

		// Seven signs period messages
		SevenSigns.getInstance().sendCurrentPeriodMsg(activeChar);
		AnnouncementTable.getInstance().showAnnouncements(activeChar, false);
		
		TvTEvent.onLogin(activeChar);
		DMEvent.onLogin(activeChar);
		FOSEvent.onLogin(activeChar);
		KTBEvent.onLogin(activeChar);
		MultiTvTEvent.onLogin(activeChar);
		TimeInstanceManager.onLogin(activeChar);
		OfflineFarmManager.getInstance().onPlayerEnterWorld(activeChar);
		
		//Daily Mission
		activeChar.ReloadMission();
		
		if (Config.ALT_OLY_END_ANNOUNCE)
			Olympiad.getInstance().olympiadEnd(activeChar);

		if (Config.ALLOW_VIP_NCOLOR && activeChar.isVip())
			activeChar.getAppearance().setNameColor(Config.VIP_NCOLOR);

		if (Config.ALLOW_VIP_TCOLOR && activeChar.isVip())
			activeChar.getAppearance().setTitleColor(Config.VIP_TCOLOR);

		if (activeChar.isAio())
			onEnterAio(activeChar);
		
		if (activeChar.isAio())
		{
			activeChar.removeSkills();
			activeChar.rewardAioSkills();
		}
		
		if (Config.ALLOW_AIO_NCOLOR && activeChar.isAio())
			activeChar.getAppearance().setNameColor(Config.AIO_NCOLOR);
		
		if (Config.ALLOW_AIO_TCOLOR && activeChar.isAio())
			activeChar.getAppearance().setTitleColor(Config.AIO_TCOLOR);
			
		// if player is DE, check for shadow sense skill at night
		if (activeChar.getRace() == Race.DarkElf && activeChar.getSkillLevel(294) == 1)
			activeChar.sendPacket(SystemMessage.getSystemMessage((GameTimeController.getInstance().isNight()) ? SystemMessageId.NIGHT_S1_EFFECT_APPLIES : SystemMessageId.DAY_S1_EFFECT_DISAPPEARS).addSkillName(294));

		activeChar.getMacroses().sendUpdate();
		activeChar.sendPacket(new UserInfo(activeChar));
		activeChar.sendPacket(new HennaInfo(activeChar));
		activeChar.sendPacket(new FriendList(activeChar));
		// activeChar.queryGameGuard();
		activeChar.sendPacket(new ItemList(activeChar, false));
		activeChar.sendPacket(new ShortCutInit(activeChar));
		activeChar.sendPacket(new ExStorageMaxCount(activeChar));
		activeChar.updateEffectIcons();
		activeChar.broadcastUserInfo();
		// Only Test
		// activeChar.broadcastTitleInfo();
		activeChar.sendPacket(new EtcStatusUpdate(activeChar));
		activeChar.sendSkillList();
		
		// Pvp/pk Color
		//ColorSystem pvpcolor = new ColorSystem();
		//pvpcolor.updateNameColor(activeChar);
		//pvpcolor.updateTitleColor(activeChar);
		
		// Reload inventory to give SA skill
		activeChar.getInventory().reloadEquippedItems();

		// Dolls system - apply doll skills on login
		DollsTable.refreshAllDollSkills(activeChar);

		//Hwid Check
		Hwid.enterlog(activeChar, getClient());

		if (HwidConfig.ALLOW_GUARD_SYSTEM)
			HwidLog.auditGMAction(activeChar.getHWid(), activeChar.getName());

		Quest.playerEnter(activeChar);
		if (!Config.DISABLE_TUTORIAL)
			loadTutorial(activeChar);

		for (Quest quest : QuestManager.getInstance().getAllManagedScripts())
		{
			if (quest != null && quest.getOnEnterWorld())
				quest.notifyEnterWorld(activeChar);
		}
		activeChar.sendPacket(new QuestList(activeChar));
		
		// Unread mails make a popup appears.
		if (Config.ENABLE_COMMUNITY_BOARD && MailBBSManager.getInstance().checkUnreadMail(activeChar) > 0)
		{
			activeChar.sendPacket(SystemMessageId.NEW_MAIL);
			activeChar.sendPacket(new PlaySound("systemmsg_e.1233"));
			activeChar.sendPacket(ExMailArrived.STATIC_PACKET);
		}
		
		// Clan notice, if active.
		if (Config.ALLOW_MOD_MENU && clan != null && clan.isNoticeEnabled())
		{
			NpcHtmlMessage notice = new NpcHtmlMessage(0);
			notice.setFile("data/html/clan_notice.htm");
			notice.replace("%clan_name%", clan.getName());
			notice.replace("%notice_text%", clan.getNotice().replaceAll("\r\n", "<br>").replaceAll("action", "").replaceAll("bypass", ""));
			sendPacket(notice);
		}
		else if (Config.SERVER_NEWS)
		{
			NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/servnews.htm");
			sendPacket(html);
		}
		
		if (Config.ANNOUNCE_CASTLE_LORDS)
			notifyCastleOwner(activeChar);
		
		if (Config.ANNOUNCE_AIO_LOGIN)
			notifyAioEnter(activeChar);
		
		if (Config.ANNOUNCE_HERO_LOGIN)
			notifyHeroEnter(activeChar);
		
		if (Config.ANNOUNCE_STREAMER_LOGIN)
			notifyStreamerEnter(activeChar);
		
		PetitionManager.getInstance().checkPetitionMessages(activeChar);
		
		// no broadcast needed since the player will already spawn dead to others
		if (activeChar.isAlikeDead())
			sendPacket(new Die(activeChar));
		
		activeChar.onPlayerEnter();

		sendPacket(new SkillCoolTime(activeChar));
		
		// If player logs back in a stadium, port him in nearest town.
		if (Olympiad.getInstance().playerInStadia(activeChar))
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);

		if (DimensionalRiftManager.getInstance().checkIfInRiftZone(activeChar.getX(), activeChar.getY(), activeChar.getZ(), false))
			DimensionalRiftManager.getInstance().teleportToWaitingRoom(activeChar);
		
		if (activeChar.getClanJoinExpiryTime() > System.currentTimeMillis())
			activeChar.sendPacket(SystemMessageId.CLAN_MEMBERSHIP_TERMINATED);
		
		// Attacker or spectator logging into a siege zone will be ported at town.
		if (!activeChar.isGM() && (!activeChar.isInSiege() || activeChar.getSiegeState() < 2) && activeChar.isInsideZone(ZoneId.SIEGE))
			activeChar.teleToLocation(MapRegionTable.TeleportWhereType.Town);

		if (Config.PLAYER_SPAWN_PROTECTION > 0)
			activeChar.setProtection(true);
		
		L2ClassMasterInstance.showQuestionMark(activeChar);

		// VIP Delete
		if (activeChar.isVip())
		{
			onEnterVip(activeChar);
			if (activeChar.getVipEndTime() > System.currentTimeMillis())
				ThreadPoolManager.getInstance().scheduleGeneral(new TaskVipEnd(activeChar), activeChar.getVipEndTime() - System.currentTimeMillis());
		}

		// Hero Timed Delete
		if (activeChar.isTimedHero())
		{
			activeChar.setHero(true);
			onEnterTimedHero(activeChar);
			if (activeChar.getTimedHeroEndTime() > System.currentTimeMillis())
				ThreadPoolManager.getInstance().scheduleGeneral(new TaskHeroEnd(activeChar), activeChar.getTimedHeroEndTime() - System.currentTimeMillis());
		}
		
		// Daily Reward Delete
		if (activeChar.getTimedItens() != 0)
		{
			if (System.currentTimeMillis() - activeChar.getTimedItens() >= Config.DAILY_REWARDS_DELETE_TIME * 60 * 1000)
				activeChar.removeTimedItens();
			else
				ThreadPoolManager.getInstance().scheduleGeneral(new TaskItemDestroy(activeChar), (activeChar.getTimedItens() + Config.DAILY_REWARDS_DELETE_TIME * 1000 * 60) - System.currentTimeMillis());
		}
		
		// Daily Reward Give
		if (!activeChar.getFirstLog() || !activeChar.getSelectArmor() || !activeChar.getSelectWeapon() || !activeChar.getSelectClasse())
			DailyRewardManager.claimDailyReward(activeChar);

		//if (Config.ALLOW_VIP_REWARD)
		//	VipRewardManager.claimVipReward(activeChar);
		
		// Ranked And PCBang
		if (Config.PCB_ENABLE)
		{
			activeChar.showPcBangWindow();
		}
		else if (Config.ALLOW_RANKED_SYSTEM)
		{
			activeChar.showPcBangWindow();
			GloryRewardManager.claimDailyReward(activeChar);
		}
		
		onEnterRunaReload(activeChar);
		
		// Startup
		if (activeChar.getFirstLog() || activeChar.getSelectArmor() || activeChar.getSelectWeapon() || activeChar.getSelectClasse())
			onEnterNewbie(activeChar);

		activeChar.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	private void onEnterVip(L2PcInstance activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getVipEndTime();
		if(now > endDay)
		{
			activeChar.setVip(false);
			activeChar.setVipEndTime(0);
			if(Config.ALLOW_VIP_ITEM)
			{
				activeChar.getInventory().destroyItemByItemId("", Config.VIP_ITEMID, 1, activeChar, null);
				activeChar.getWarehouse().destroyItemByItemId("", Config.VIP_ITEMID, 1, activeChar, null);
			}
			activeChar.sendMessage("Your VIP status period is over!");
		}
		else
		{
			Date dt = new Date(endDay);
			_minleft = (endDay - now)/60000;
			_daysleft = (endDay - now)/86400000;
			
			if (_daysleft > 30)
				activeChar.sendMessage("Your VIP status period ends in " + df.format(dt) + "!");
			else if (_daysleft > 0)
				activeChar.sendMessage("Left " + (int)_daysleft + " days for your's VIP status period ends!");
			else if (_daysleft < 1 && _minleft > 121)
			{
				long hour = (endDay - now)/3600000;
			    activeChar.sendMessage("Left " + (int)hour + " hours to your's VIP status period ends!");
			}
			else if (_minleft < 120)
			{
				long minutes = (endDay - now)/60000;
				activeChar.sendMessage("Left " + (int)minutes + " minutes to your's VIP status period ends!");
			}
		}
	}
	
	private void onEnterTimedHero(L2PcInstance activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getTimedHeroEndTime();
		if(now > endDay)
		{
			activeChar.setHero(false);
			activeChar.setTimedHero(false);
			activeChar.setTimedHeroEndTime(0);
			activeChar.sendMessage("Your heroic status period is over!");
			activeChar.getInventory().destroyItemByItemId("Wing", 6842, 1, activeChar, null);
		}
		else
		{
			Date dt = new Date(endDay);
			_minleft = (endDay - now)/60000;
			_daysleft = (endDay - now)/86400000;
			
			if (_daysleft > 30)
				activeChar.sendMessage("Your heroic status period ends in " + df.format(dt) + "!");
			else if (_daysleft > 0)
				activeChar.sendMessage("Left " + (int)_daysleft + " days for your's heroic status period ends!");
			else if (_daysleft < 1 && _minleft > 121)
			{
				long hour = (endDay - now)/3600000;
			    activeChar.sendMessage("Left " + (int)hour + " hours to your's heroic status period ends!");
			}
			else if (_minleft < 120)
			{
				long minutes = (endDay - now)/60000;
				activeChar.sendMessage("Left " + (int)minutes + " minutes to your's heroic status period ends!");
			}
		}
	}

	private void onEnterAio(L2PcInstance activeChar)
	{
		long now = Calendar.getInstance().getTimeInMillis();
		long endDay = activeChar.getAioEndTime();
		
		if(now > endDay)
		{
			activeChar.setAio(false);
			activeChar.setAioEndTime(0);
			activeChar.removeSkills();
			activeChar.removeExpAndSp(6299994999L, 366666666);
			if(Config.ALLOW_AIO_ITEM)
			{
				activeChar.getInventory().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
				activeChar.getWarehouse().destroyItemByItemId("", Config.AIO_ITEMID, 1, activeChar, null);
			}
			activeChar.sendPacket(new CreatureSay(0,Say2.HERO_VOICE, "System", "Your AIO period is up."));
		}
		else
		{
			Date dt = new Date(endDay);
			_daysleft = (endDay - now)/86400000;
			
			if(_daysleft > 30)
				activeChar.sendMessage("AIO period ends in " + df.format(dt) + ".");
			else if(_daysleft > 0)
				activeChar.sendMessage("Left " + (int)_daysleft + " days for AIO period ends");
			else if(_daysleft < 1)
			{
				long hour = (endDay - now)/3600000;
			    activeChar.sendMessage("Left " + (int)hour + " hours to AIO period ends");
			}
		}
	}
	
	private static void engage(L2PcInstance cha)
	{
		int _chaid = cha.getObjectId();
		
		for (Couple cl : CoupleManager.getInstance().getCouples())
		{
			if (cl.getPlayer1Id() == _chaid || cl.getPlayer2Id() == _chaid)
			{
				if (cl.getMaried())
					cha.setMarried(true);
				
				cha.setCoupleId(cl.getId());
			}
		}
	}
	
	private static void notifyClanMembers(L2PcInstance activeChar)
	{
		final L2Clan clan = activeChar.getClan();
		
		// Refresh player instance.
		clan.getClanMember(activeChar.getObjectId()).setPlayerInstance(activeChar);
		
		final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_LOGGED_IN).addPcName(activeChar);
		final PledgeShowMemberListUpdate update = new PledgeShowMemberListUpdate(activeChar);
		
		// Send packet to others members.
		for (L2PcInstance member : clan.getOnlineMembers())
		{
			if (member == activeChar)
				continue;
			
			member.sendPacket(msg);
			member.sendPacket(update);
		}
	}
	
	private static void notifySponsorOrApprentice(L2PcInstance activeChar)
	{
		if (activeChar.getSponsor() != 0)
		{
			L2PcInstance sponsor = L2World.getInstance().getPlayer(activeChar.getSponsor());
			if (sponsor != null)
				sponsor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_APPRENTICE_S1_HAS_LOGGED_IN).addPcName(activeChar));
		}
		else if (activeChar.getApprentice() != 0)
		{
			L2PcInstance apprentice = L2World.getInstance().getPlayer(activeChar.getApprentice());
			if (apprentice != null)
				apprentice.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOUR_SPONSOR_S1_HAS_LOGGED_IN).addPcName(activeChar));
		}
	}
	
	private static void loadTutorial(L2PcInstance player)
	{
		QuestState qs = player.getQuestState("Tutorial");
		if (qs != null)
			qs.getQuest().notifyEvent("UC", null, player);
	}

	private void notifyCastleOwner(L2PcInstance activeChar)
	{
	    if (activeChar.isCastleLord(1) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Gludio Castle is Now Online!");

	    else if (activeChar.isCastleLord(2) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Dion Castle is Now Online!");

	    else if (activeChar.isCastleLord(3) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Giran Castle is Now Online!");

	    else if (activeChar.isCastleLord(4) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Oren Castle is Now Online!");
	    
	    else if (activeChar.isCastleLord(5) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Aden Castle is Now Online!");

	    else if (activeChar.isCastleLord(6) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Innadril Castle is Now Online!");

	    else if (activeChar.isCastleLord(7) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Goddard Castle is Now Online!");

	    else if (activeChar.isCastleLord(8) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Rune Castle is Now Online!");

	    else if (activeChar.isCastleLord(9) && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Lord " + activeChar.getName() + " Ruler Of Schuttgart Castle is Now Online!");
	}
	
	private void notifyAioEnter(L2PcInstance activeChar)
	{
	    if (activeChar.isAio() && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Aio " + activeChar.getName() + " is Now Online!");
	}
	
	private void notifyHeroEnter(L2PcInstance activeChar)
	{
	    if (activeChar.isHero() && (!activeChar.isGM()))
	        Broadcast.gameAnnounceToOnlinePlayers("Hero " + activeChar.getName() + " is Now Online!");
	}
	
	private void notifyStreamerEnter(L2PcInstance activeChar)
	{
		if (activeChar.isStreamer())
		{
			if (!activeChar.getTwitchLink().isEmpty() || activeChar.getTwitchLink() == null)
			{
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), Say2.TRADE, "[Streamer] " + activeChar.getName(), "" + activeChar.getTwitchLink());

				Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();

				for (L2PcInstance player : pls)
				{
					if (!BlockList.isBlocked(player, activeChar))
						player.sendPacket(cs);
				}
			}
			
			if (!activeChar.getFacebookLink().isEmpty() || activeChar.getFacebookLink() == null)
			{
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), Say2.TRADE, "[Streamer] " + activeChar.getName(), "" + activeChar.getFacebookLink());

				Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();

				for (L2PcInstance player : pls)
				{
					if (!BlockList.isBlocked(player, activeChar))
						player.sendPacket(cs);
				}
			}
			
			if (!activeChar.getYoutubeLink().isEmpty() || activeChar.getYoutubeLink() == null)
			{
				CreatureSay cs = new CreatureSay(activeChar.getObjectId(), Say2.TRADE, "[Streamer] " + activeChar.getName(), "" + activeChar.getYoutubeLink());

				Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();

				for (L2PcInstance player : pls)
				{
					if (!BlockList.isBlocked(player, activeChar))
						player.sendPacket(cs);
				}
			}
		}
	}

	private void onEnterNewbie(L2PcInstance activeChar)
	{
		if (Config.STARTUP_SYSTEM_ENABLED)
		{
			//make char disappears
			activeChar.getAppearance().setInvisible();
			activeChar.broadcastUserInfo();
			activeChar.decayMe();
			activeChar.spawnMe();
			//active start system
			sendPacket(new SpecialCamera(activeChar.getObjectId(), 30, 200, 20, 999999999, 999999999, 0, 0, 1, 0));
			StartupSystem.startSetup(activeChar);
		}
	}
	
	private void onEnterRunaReload(L2PcInstance activeChar)
	{
		for (ItemInstance item : activeChar.getInventory().getItems())
		{
			if (item == null)
				continue;
			
			for (Entry<Integer, Integer> itemSkill : Config.LIST_RUNE_ITEMS.entrySet())
			{
				if (item.getItemId() == itemSkill.getKey())
				{
					L2Skill skill = SkillTable.getInstance().getInfo(itemSkill.getValue(), 1);
					if (skill != null)
					{
						activeChar.addSkill(skill, false);
						activeChar.sendSkillList();
					}
				}
			}
		}
	}

	@Override
	protected boolean triggersOnActionRequest()
	{
		return false;
	}
}