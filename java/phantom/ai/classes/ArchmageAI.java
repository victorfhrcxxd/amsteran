package phantom.ai.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import phantom.FakePlayer;
import phantom.FakePlayerConfig;
import phantom.ai.CombatAI;
import phantom.helpers.FakeHelpers;
import phantom.model.HealingSpell;
import phantom.model.OffensiveSpell;
import phantom.model.SpellUsageCondition;
import phantom.model.SupportSpell;
import phantom.task.ThreadPool;
import net.sf.l2j.gameserver.model.ShotType;
import net.sf.l2j.util.Rnd;

public class ArchmageAI extends CombatAI
{
	public ArchmageAI(FakePlayer character)
	{
		super(character);		
	}

	@Override
	public void thinkAndAct()
	{		
		super.thinkAndAct();
		setBusyThinking(true);		

		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				_fakePlayer.despawnPlayer();
			}

		}, Rnd.get(FakePlayerConfig.DESPAWN_PVP_RANDOM_TIME_1 * 60 * 1000, FakePlayerConfig.DESPAWN_PVP_RANDOM_TIME_2 * 60 * 1000));

		handleShots();	
		selfSupportBuffs();
		tryTargetRandomCreatureByTypeInRadius(FakeHelpers.getTestTargetClass(), FakeHelpers.getTestTargetRange());		
		tryAttackingUsingMageOffensiveSkill();
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
		List<OffensiveSpell> _offensiveSpells = new ArrayList<>();
		_offensiveSpells.add(new OffensiveSpell(1230, 1));
		_offensiveSpells.add(new OffensiveSpell(1339, 1));	
		return _offensiveSpells; 
	}

	@Override
	protected List<HealingSpell> getHealingSpells()
	{		
		return Collections.emptyList();
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