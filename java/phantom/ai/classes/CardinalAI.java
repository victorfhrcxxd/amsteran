package phantom.ai.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.ai.CombatAI;
import phantom.ai.addon.IHealer;
import phantom.helpers.FakeHelpers;
import phantom.model.HealingSpell;
import phantom.model.OffensiveSpell;
import phantom.model.SpellUsageCondition;
import phantom.model.SupportSpell;
import phantom.task.ThreadPool;
import net.sf.l2j.gameserver.model.L2Skill.SkillTargetType;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.util.Rnd;

public class CardinalAI extends CombatAI implements IHealer
{
	public CardinalAI(FakePlayer character)
	{
		super(character);
	}

	@Override
	public void thinkAndAct()
	{
		super.thinkAndAct();
		setBusyThinking(true);

		ThreadPool.schedule(() -> _fakePlayer.despawnPlayer(), Rnd.get(FakePlayerConfig.DESPAWN_PVP_RANDOM_TIME_1 * 60 * 1000, FakePlayerConfig.DESPAWN_PVP_RANDOM_TIME_2 * 60 * 1000));

		handleShots();		
		selfSupportBuffs();
		tryTargetingLowestHpTargetInRadius(_fakePlayer, FakePlayer.class, FakeHelpers.getTestTargetRange());
		tryHealingTarget(_fakePlayer);
		setBusyThinking(false);
	}

	@Override
	protected ShotType getShotType()
	{
		return ShotType.BLESSED_SPIRITSHOT;
	}

	@Override
	protected List<OffensiveSpell> getOffensiveSpells()
	{		
		return Collections.emptyList();
	}

	@Override
	protected List<HealingSpell> getHealingSpells()
	{		
		List<HealingSpell> _healingSpells = new ArrayList<>();
		_healingSpells.add(new HealingSpell(1218, SkillTargetType.TARGET_ONE, 70, 1));		
		_healingSpells.add(new HealingSpell(1217, SkillTargetType.TARGET_ONE, 80, 3));
		return _healingSpells; 
	}

	@Override
	protected List<SupportSpell> getSelfSupportSpells()
	{
		List<SupportSpell> _selfSupportSpells = new ArrayList<>();
		_selfSupportSpells.add(new SupportSpell(2037, SpellUsageCondition.LESSHPPERCENT, 85, 1));
		_selfSupportSpells.add(new SupportSpell(2166, SpellUsageCondition.MISSINGCP, 500, 1));
		_selfSupportSpells.add(new SupportSpell(2005, SpellUsageCondition.MISSINGMP, 2000, 1));
		return _selfSupportSpells;
	}
}