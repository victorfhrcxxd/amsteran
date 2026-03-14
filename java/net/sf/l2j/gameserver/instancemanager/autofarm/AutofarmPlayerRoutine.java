package net.sf.l2j.gameserver.instancemanager.autofarm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import java.util.stream.Collectors;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ai.CtrlEvent;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.ai.NextAction;
import net.sf.l2j.gameserver.geoengine.PathFinding;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.handler.ItemHandler;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedAutoFarm;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.L2ShortCut;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.L2WorldRegion;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.L2Summon;
import net.sf.l2j.gameserver.model.actor.instance.L2ChestInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PetInstance;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.templates.skills.L2SkillType;
import net.sf.l2j.gameserver.util.Util;
import net.sf.l2j.util.Rnd;

import net.sf.l2j.gameserver.instancemanager.OfflineFarmManager;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFEvent;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMEvent;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBEvent;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMEvent;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTEvent;

public class AutofarmPlayerRoutine
{
	private static final Logger _log = Logger.getLogger(AutofarmPlayerRoutine.class.getName());
	private final L2PcInstance player;
	private L2Character committedTarget = null;

	public AutofarmPlayerRoutine(L2PcInstance player)
	{
		this.player = player;
	}
	
	public void executeRoutine()
	{
		// Don't do anything while dead (event will revive, don't deactivate autofarm)
		if (player.isDead())
			return;

		if (player.isNoBuffProtected() && player.getAllEffects().length <= 8)
		{
			// Skip this check for offline farm players in events (buffs may be stripped on respawn)
			if (!(player.isOfflineFarm() && OfflineFarmManager.isInActiveEvent(player)))
			{
				player.sendMessage("You don't have buffs to use autofarm.");
				player.broadcastUserInfo();
				player.setAutoFarm(false);
				VoicedAutoFarm.showAutoFarm(player);
				return;
			}
		}
		
		boolean inEvent = OfflineFarmManager.isInActiveEvent(player);
		
		calculatePotions();
		if (!inEvent)
			checkSpoil();
		targetEligibleCreature();
		attack();
		if (!inEvent)
			checkSpoil();
		useAppropriateSpell();
	}

	private void attack() 
	{
		Boolean shortcutsContainAttack = shotcutsContainAttack(2);
		
		if (shortcutsContainAttack) 
			physicalAttack();
	}

	private void useAppropriateSpell() 
	{
		L2Skill chanceSkill = nextAvailableSkill(getChanceSpells(), AutofarmSpellType.Chance);

		if (chanceSkill != null)
		{
			useMagicSkill(chanceSkill, false);
			return;
		}

		L2Skill lowLifeSkill = nextAvailableSkill(getLowLifeSpells(), AutofarmSpellType.LowLife);

		if (lowLifeSkill != null) 
		{
			useMagicSkill(lowLifeSkill, true);
			return;
		}

		L2Skill attackSkill = nextAvailableSkill(getAttackSpells(), AutofarmSpellType.Attack);

		if (attackSkill != null) 
		{
			useMagicSkill(attackSkill, false);
			return;
		}
	}

	public L2Skill nextAvailableSkill(List<Integer> skillIds, AutofarmSpellType spellType) 
	{
		for (Integer skillId : skillIds) 
		{
			L2Skill skill = player.getKnownSkill(skillId);

			if (skill == null) 
				continue;
			
			if (skill.getSkillType() == L2SkillType.SIGNET || skill.getSkillType() == L2SkillType.SIGNET_CASTTIME)
				continue;

			if (!player.checkDoCastConditions(skill)) 
				continue;

			if (isSpoil(skillId))
			{
				if (monsterIsAlreadySpoiled())
				{
					continue;
				}
				return skill;
			}
			
			if (spellType == AutofarmSpellType.Chance)
			{
				if ((KTBEvent.isStarted() || KTBEvent.isStarting()) && KTBEvent.isPlayerParticipant(player.getObjectId()))
				{
					// KTB: target is the boss NPC
					if (player.getTarget() instanceof L2Npc)
						return skill;
				}
				else if (OfflineFarmManager.isInActiveEvent(player))
				{
					// Other events: target is a player
					if (player.getTarget() instanceof L2PcInstance)
						return skill;
				}
				else if (getMonsterTarget() != null)
				{
					if (getMonsterTarget().getFirstEffect(skillId) == null) 
						return skill;
					else 
						continue;
				}
				continue;
			}

			if (spellType == AutofarmSpellType.LowLife && getHpPercentage() > player.getHealPercent()) 
				break;

			return skill;
		}

		return null;
	}
	
	private void checkSpoil() 
	{
		if (canBeSweepedByMe() && getMonsterTarget().isDead()) 
		{
			L2Skill sweeper = player.getKnownSkill(42);
			if (sweeper == null) 
				return;
			
			useMagicSkill(sweeper, false);
		}
	}

	private Double getHpPercentage() 
	{
		return player.getCurrentHp() * 100.0f / player.getMaxHp();
	}
		
	private Double percentageMpIsLessThan() 
	{
		return player.getCurrentMp() * 100.0f / player.getMaxMp();
	}
	
	private Double percentageHpIsLessThan()
	{
		return player.getCurrentHp() * 100.0f / player.getMaxHp();
	}
	
	private List<Integer> getAttackSpells()
	{
		return getSpellsInSlots(AutofarmConstants.attackSlots);
	}

	private List<Integer> getSpellsInSlots(List<Integer> attackSlots) 
	{
		return Arrays.stream(player.getAllShortCuts()).filter(shortcut -> shortcut.getPage() == player.getPage() && shortcut.getType() == L2ShortCut.TYPE_SKILL && attackSlots.contains(shortcut.getSlot())).map(L2ShortCut::getId).collect(Collectors.toList());
	}

	private List<Integer> getChanceSpells()
	{
		return getSpellsInSlots(AutofarmConstants.chanceSlots);
	}

	private List<Integer> getLowLifeSpells()
	{
		return getSpellsInSlots(AutofarmConstants.lowLifeSlots);
	}

	private boolean shotcutsContainAttack(int id) 
	{
		return Arrays.stream(player.getAllShortCuts()).anyMatch(shortcut -> shortcut.getPage() == player.getPage() && shortcut.getType() == L2ShortCut.TYPE_ACTION && (shortcut.getId() == 2 || player.isSummonAttack() && shortcut.getId() == 22));
	}
	
	private boolean monsterIsAlreadySpoiled()
	{
		return getMonsterTarget() != null && getMonsterTarget().getIsSpoiledBy() != 0;
	}
	
	private static boolean isSpoil(Integer skillId)
	{
		return skillId == 254 || skillId == 302;
	}
	
	private boolean canBeSweepedByMe() 
	{
	    return getMonsterTarget() != null && getMonsterTarget().isDead() && getMonsterTarget().getIsSpoiledBy() == player.getObjectId();
	}
	
	private void castSpellWithAppropriateTarget(L2Skill skill, Boolean forceOnSelf)
	{
		boolean isKtbBoss = player.getTarget() instanceof L2Character && ((L2Character) player.getTarget())._isKTBEvent;

		if (forceOnSelf) 
		{
			L2Object oldTarget = player.getTarget();
			player.setTarget(player);
			player.useMagic(skill, false, false);
			player.setTarget(oldTarget);
			return;
		}

		player.useMagic(skill, isKtbBoss, false);
	}

	private void physicalAttack()
	{
		L2Character target = null;

		if (player.getTarget() instanceof L2MonsterInstance)
			target = (L2MonsterInstance) player.getTarget();
		else if (player.getTarget() instanceof L2Npc && (KTBEvent.isStarted() || KTBEvent.isStarting()) && KTBEvent.isPlayerParticipant(player.getObjectId()))
			target = (L2Npc) player.getTarget();
		else if (player.getTarget() instanceof L2PcInstance && OfflineFarmManager.isInActiveEvent(player))
			target = (L2PcInstance) player.getTarget();

		if (target == null)
			return;

		boolean isKtbBoss = target._isKTBEvent;
		boolean inEvent = OfflineFarmManager.isInActiveEvent(player);
		boolean canAttack = isKtbBoss || target.isAutoAttackable(player);
		// In events, skip geodata check (event zones may lack proper geodata)
		boolean canSee = inEvent || PathFinding.getInstance().canSeeTarget(player, target);

		if (canAttack && canSee)
		{
			if (!player.isMageClass() || inEvent)
			{
				player.getAI().setIntention(CtrlIntention.ATTACK, target);
				player.onActionRequest();
			}

			if (player.isSummonAttack() && player.getPet() != null)
			{
				// Siege Golem's
				if (player.getPet().getNpcId() >= 14702 && player.getPet().getNpcId() <= 14798 || player.getPet().getNpcId() >= 14839 && player.getPet().getNpcId() <= 14869)
					return;
				
				L2Summon activeSummon = player.getPet();
				activeSummon.setTarget(target);
				activeSummon.getAI().setIntention(CtrlIntention.ATTACK, target);

				int[] summonAttackSkills = {4261, 4068, 4137, 4260, 4708, 4709, 4710, 4712, 5135, 5138, 5141, 5442, 5444, 6095, 6096, 6041, 6044};
				if (Rnd.get(100) < player.getSummonSkillPercent())
				{
					for (int skillId : summonAttackSkills)
					{
						useMagicSkillBySummon(skillId, target);
					}
				}
			}
		}
		else if (!canAttack && canSee)
		{
			player.getAI().setIntention(CtrlIntention.FOLLOW, target);
		}
	}

	public void targetEligibleCreature() 
	{ 
		if (committedTarget != null) 
		{            
			if (committedTarget.isDead())
			{
				committedTarget = null;
				player.setTarget(null);
			}
			else if (committedTarget._isKTBEvent)
			{
				// KTB boss: always attack directly (bypass geodata, event zone may lack it)
				player.setTarget(committedTarget);
				attack();
				return;
			}
			else if (OfflineFarmManager.isInActiveEvent(player))
			{
				// In event: if target is a stale monster from farm, clear it
				if (!(committedTarget instanceof L2PcInstance))
				{
					committedTarget = null;
					player.setTarget(null);
				}
				else if (Util.checkIfInRange(3000, player, committedTarget, true))
				{
					// Event player target is in range, re-set target (may have been cleared by teleport) and attack
					player.setTarget(committedTarget);
					attack();
					return;
				}
				else
				{
					// Event player target is out of range (respawned in different area), clear and find new
					committedTarget = null;
					player.setTarget(null);
				}
			}
			else if (PathFinding.getInstance().canSeeTarget(player, committedTarget))
			{
				attack();
				return;
			}
			else
			{
				player.getAI().setIntention(CtrlIntention.FOLLOW, committedTarget);
				committedTarget = null;
				player.setTarget(null);
			}
		}
		
		if (committedTarget instanceof L2Summon) 
			return;

		// During events, choose target based on event type
		if (OfflineFarmManager.isInActiveEvent(player))
		{
			// KTB: target the boss NPC
			if ((KTBEvent.isStarted() || KTBEvent.isStarting()) && KTBEvent.isPlayerParticipant(player.getObjectId()))
			{
				L2Npc boss = KTBEvent.getEventBoss();
				if (boss != null && !boss.isDead())
				{
					committedTarget = boss;
					player.setTarget(boss);
					attack();
				}
				return;
			}
			// Other events (TvT, CTF, DM, LM): target enemy players
			List<L2PcInstance> enemies = getEnemyPlayersInRadius(3000);
			_log.info("AutofarmEvent: " + player.getName() + " in event, enemies found=" + enemies.size() + " region=" + (player.getWorldRegion() != null ? player.getWorldRegion().getName() : "null"));
			if (!enemies.isEmpty())
			{
				L2PcInstance closestEnemy = enemies.stream().min((o1, o2) -> Integer.compare((int) Math.sqrt(player.getDistanceSq(o1)), (int) Math.sqrt(player.getDistanceSq(o2)))).get();
				committedTarget = closestEnemy;
				player.setTarget(closestEnemy);
				attack();
			}
			return;
		}
			
		List<L2MonsterInstance> targets = getKnownMonstersInRadius(player, player.getRadius(), creature -> PathFinding.getInstance().canMoveToTarget(player.getX(), player.getY(), player.getZ(), creature.getX(), creature.getY(), creature.getZ()) && !player.ignoredMonsterContain(creature.getNpcId()) && !creature.isAgathion() && !creature.isRaidMinion() && !creature.isRaid() && !creature.isDead() && !(creature instanceof L2ChestInstance) && !(player.isAntiKsProtected() && creature.getTarget() != null && creature.getTarget() != player && creature.getTarget() != player.getPet()));

		if (targets.isEmpty())
			return;

		L2MonsterInstance closestTarget = targets.stream().min((o1, o2) -> Integer.compare((int) Math.sqrt(player.getDistanceSq(o1)), (int) Math.sqrt(player.getDistanceSq(o2)))).get();

		committedTarget = closestTarget;
		player.setTarget(closestTarget);
	}

	public final List<L2MonsterInstance> getKnownMonstersInRadius(L2PcInstance player, int radius, Function<L2MonsterInstance, Boolean> condition)
	{
		final L2WorldRegion region = player.getWorldRegion();
		if (region == null)
			return Collections.emptyList();

		final List<L2MonsterInstance> result = new ArrayList<>();

		for (L2WorldRegion reg : region.getSurroundingRegions())
		{
			for (L2Object obj : reg.getVisibleObjects().values())
			{
				if (!(obj instanceof L2MonsterInstance) || !Util.checkIfInRange(radius, player, obj, true) || !condition.apply((L2MonsterInstance) obj))
					continue;

				result.add((L2MonsterInstance) obj);
			}
		}

		return result;
	}

	public L2MonsterInstance getMonsterTarget()
	{
		if(!(player.getTarget() instanceof L2MonsterInstance)) 
		{
			return null;
		}

		return (L2MonsterInstance)player.getTarget();
	}

	private void useMagicSkill(L2Skill skill, Boolean forceOnSelf)
	{
		if (skill.getSkillType() == L2SkillType.SIGNET)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (skill.getSkillType() == L2SkillType.RECALL && !Config.KARMA_PLAYER_CAN_TELEPORT && player.getKarma() > 0)
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (skill.isToggle() && player.isMounted())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.isOutOfControl())
		{
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (player.isAttackingNow())
			player.getAI().setNextAction(new NextAction(CtrlEvent.EVT_READY_TO_ACT, CtrlIntention.CAST, () -> castSpellWithAppropriateTarget(skill, forceOnSelf)));
		else 
			castSpellWithAppropriateTarget(skill, forceOnSelf);
	}
	
	private boolean useMagicSkillBySummon(int skillId, L2Object target)
	{
		// No owner, or owner in shop mode.
		if (player == null || player.isInStoreMode())
			return false;
		
		final L2Summon activeSummon = player.getPet();
		if (activeSummon == null)
			return false;
		
		// Pet which is 20 levels higher than owner.
		if (activeSummon instanceof L2PetInstance && activeSummon.getLevel() - player.getLevel() > 20)
		{
			player.sendPacket(SystemMessageId.PET_TOO_HIGH_TO_CONTROL);
			return false;
		}
		
		// Out of control pet.
		if (activeSummon.isOutOfControl())
		{
			player.sendPacket(SystemMessageId.PET_REFUSING_ORDER);
			return false;
		}
		
		// Verify if the launched skill is mastered by the summon.
		final L2Skill skill = activeSummon.getKnownSkill(skillId);
		if (skill == null)
			return false;
		
		// Can't launch offensive skills on owner.
		if (skill.isOffensive() && player == target)
			return false;
		
		activeSummon.setTarget(target);
		return activeSummon.useMagic(skill, false, false);
	}
		
	private void calculatePotions()
	{
		if (percentageHpIsLessThan() < player.getHpPotionPercentage())
			forceUseItem(1539); 
		
		if (percentageMpIsLessThan() < player.getMpPotionPercentage())
			forceUseItem(728); 
	}	

	private void forceUseItem(int itemId)
	{
		final ItemInstance potion = player.getInventory().getItemByItemId(itemId);
		if (potion == null)
			return; 

		final IItemHandler handler = ItemHandler.getInstance().getItemHandler(potion.getEtcItem());
		if (handler != null)
			handler.useItem(player, potion, false); 
	}

	private List<L2PcInstance> getEnemyPlayersInRadius(int radius)
	{
		final List<L2PcInstance> enemies = new ArrayList<>();
		final L2WorldRegion region = player.getWorldRegion();
		if (region == null)
			return enemies;

		for (L2WorldRegion reg : region.getSurroundingRegions())
		{
			for (L2Object obj : reg.getVisibleObjects().values())
			{
				if (!(obj instanceof L2PcInstance) || obj == player)
					continue;

				L2PcInstance target = (L2PcInstance) obj;
				if (target.isDead() || !Util.checkIfInRange(radius, player, target, true))
					continue;

				// Skip geodata check for event zones (may lack proper geodata)
				if (isEnemyInEvent(target))
					enemies.add(target);
			}
		}

		return enemies;
	}

	private boolean isEnemyInEvent(L2PcInstance target)
	{
		int myObjId = player.getObjectId();
		int targetObjId = target.getObjectId();

		// TvT - different teams are enemies
		if ((TvTEvent.isStarted() || TvTEvent.isStarting()) && TvTEvent.isPlayerParticipant(myObjId) && TvTEvent.isPlayerParticipant(targetObjId))
		{
			byte myTeam = TvTEvent.getParticipantTeamId(myObjId);
			byte targetTeam = TvTEvent.getParticipantTeamId(targetObjId);
			return myTeam != -1 && targetTeam != -1 && myTeam != targetTeam;
		}

		// CTF - different teams are enemies
		if ((CTFEvent.isStarted() || CTFEvent.isStarting()) && CTFEvent.isPlayerParticipant(myObjId) && CTFEvent.isPlayerParticipant(targetObjId))
		{
			byte myTeam = CTFEvent.getParticipantTeamId(myObjId);
			byte targetTeam = CTFEvent.getParticipantTeamId(targetObjId);
			return myTeam != -1 && targetTeam != -1 && myTeam != targetTeam;
		}

		// DM - everyone else is an enemy
		if ((DMEvent.isStarted() || DMEvent.isStarting()) && DMEvent.isPlayerParticipant(myObjId) && DMEvent.isPlayerParticipant(targetObjId))
			return true;

		// LM - everyone else is an enemy
		if ((LMEvent.isStarted() || LMEvent.isStarting()) && LMEvent.isPlayerParticipant(myObjId) && LMEvent.isPlayerParticipant(targetObjId))
			return true;

		return false;
	}
}