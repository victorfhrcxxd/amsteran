package net.sf.l2j.gameserver.model.entity.events.demonzone;

public class DemonZone 
{
	private DemonZoneEngineState _state = DemonZoneEngineState.INACTIVE;

	public boolean startDemonEvent() 
	{
		setState(DemonZoneEngineState.ACTIVE);
		return true;
	}

	public boolean endDemonEvent() 
	{
		setState(DemonZoneEngineState.INACTIVE);
		return true;
	}

	private void setState(DemonZoneEngineState state) 
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
			return _state == DemonZoneEngineState.ACTIVE;
		} 
	}

	public boolean isInactive() 
	{
		synchronized (_state) 
		{
			return _state == DemonZoneEngineState.INACTIVE;
		} 
	}

	public static DemonZone getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder 
	{
		protected static final DemonZone _instance = new DemonZone();
	}
}