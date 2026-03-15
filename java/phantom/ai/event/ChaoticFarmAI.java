package phantom.ai.event;

import phantom.FakePlayer;
import phantom.ai.FakePlayerAI;
import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmManager;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmRoom;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmState;

public class ChaoticFarmAI extends FakePlayerAI
{
	private enum CfState
	{
		QUEUEING, FROZEN, FARMING, IN_DUEL, LEAVING
	}

	private boolean _wasInRoom = false;

	public ChaoticFarmAI(FakePlayer fp)
	{
		super(fp);
	}

	@Override
	public void thinkAndAct()
	{
		if (_fakePlayer.isDead())
		{
			_fakePlayer.doRevive();
			return;
		}

		final ChaoticFarmRoom room = ChaoticFarmManager.getInstance().findRoomForPlayer(_fakePlayer);
		final CfState state = resolveState(room);

		if (state == CfState.QUEUEING && _wasInRoom)
		{
			_wasInRoom = false;
			ChaoticFarmController.scheduleRequeue(_fakePlayer);
		}
		else if (state != CfState.QUEUEING)
		{
			_wasInRoom = true;
		}

		switch (state)
		{
			case QUEUEING:
			case FROZEN:
			case LEAVING:
				stopCombat();
				break;
			case FARMING:
				doFarm(room);
				break;
			case IN_DUEL:
				doDuel(room);
				break;
		}
	}

	private CfState resolveState(ChaoticFarmRoom room)
	{
		if (room == null)
			return CfState.QUEUEING;

		final ChaoticFarmState roomState = room.getState();
		if (roomState == ChaoticFarmState.FARMING)
			return CfState.FARMING;
		if (roomState == ChaoticFarmState.DUEL_COUNTDOWN)
			return CfState.FROZEN;
		if (roomState == ChaoticFarmState.IN_DUEL)
			return CfState.IN_DUEL;
		if (roomState == ChaoticFarmState.RESETTING)
			return CfState.LEAVING;
		return CfState.QUEUEING;
	}

	private void stopCombat()
	{
		_fakePlayer.abortAttack();
		_fakePlayer.abortCast();
		if (_fakePlayer.getTarget() != null)
		{
			_fakePlayer.setTarget(null);
			_fakePlayer.getAI().setIntention(CtrlIntention.ACTIVE);
		}
	}

	private void doFarm(ChaoticFarmRoom room)
	{
		if (room == null)
			return;

		final L2MonsterInstance mob = room.getCurrentMob();
		if (mob == null || mob.isDead())
		{
			_fakePlayer.setTarget(null);
			return;
		}

		if (mob.getInstanceId() != _fakePlayer.getInstanceId())
			return;

		if (_fakePlayer.getTarget() != mob)
			_fakePlayer.setTarget(mob);

		_fakePlayer.forceAutoAttack(mob);
	}

	private void doDuel(ChaoticFarmRoom room)
	{
		if (room == null)
			return;

		final L2PcInstance opponent = room.getOpponent(_fakePlayer);
		if (opponent == null || opponent.isDead())
		{
			_fakePlayer.setTarget(null);
			return;
		}

		if (opponent.getInstanceId() != _fakePlayer.getInstanceId())
			return;

		if (_fakePlayer.getTarget() != opponent)
			_fakePlayer.setTarget(opponent);

		_fakePlayer.forceAutoAttack(opponent);
	}
}
