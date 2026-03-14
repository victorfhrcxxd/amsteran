package net.sf.l2j.gameserver.model.entity.events.partyzone;

public class PartyZone 
{
	private PartyZoneEngineState _state = PartyZoneEngineState.INACTIVE;

	public boolean startPartyEvent() 
	{
		setState(PartyZoneEngineState.ACTIVE);
		return true;
	}

	public boolean endPartyEvent() 
	{
		setState(PartyZoneEngineState.INACTIVE);
		return true;
	}

	private void setState(PartyZoneEngineState state) 
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
			return _state == PartyZoneEngineState.ACTIVE;
		} 
	}

	public boolean isInactive() 
	{
		synchronized (_state) 
		{
			return _state == PartyZoneEngineState.INACTIVE;
		} 
	}

	public static PartyZone getInstance()
	{
		return SingletonHolder._instance;
	}

	private static class SingletonHolder 
	{
		protected static final PartyZone _instance = new PartyZone();
	}
}
