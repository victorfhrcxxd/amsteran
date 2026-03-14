package net.sf.l2j.gameserver.model.entity.events.bonuzone;

public class BonusZone 
{
	private BonusZoneEngineState _state = BonusZoneEngineState.INACTIVE;

	public boolean startBonusEvent() 
	{
		setState(BonusZoneEngineState.ACTIVE);
		return true;
	}

	public boolean endBonusEvent() 
	{
		setState(BonusZoneEngineState.INACTIVE);
		return true;
	}

	private void setState(BonusZoneEngineState state) 
	{
		synchronized (state)
		{
			_state = state;
		} 
	}

	public boolean isActive()
	{
		synchronized (_state) 
		{
			return _state == BonusZoneEngineState.ACTIVE;
		} 
	}

	public boolean isInactive() 
	{
		synchronized (_state) 
		{
			return _state == BonusZoneEngineState.INACTIVE;
		} 
	}

	public static BonusZone getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder 
	{
		protected static final BonusZone _instance = new BonusZone();
	}
}