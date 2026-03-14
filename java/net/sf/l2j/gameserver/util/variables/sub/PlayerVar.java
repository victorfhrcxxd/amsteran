package net.sf.l2j.gameserver.util.variables.sub;

import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import phantom.task.ThreadPool;

public class PlayerVar
{
	private L2PcInstance _owner;
	private String _name;
	private String _value;
	private long _expire_time;
	
	@SuppressWarnings("rawtypes")
	private ScheduledFuture _task;
	
	public PlayerVar(L2PcInstance owner, String name, String value, long expire_time)
	{
		_owner = owner;
		_name = name;
		_value = value;
		_expire_time = expire_time;
		
		if (expire_time > 0) // if expires schedule expiration
			_task = ThreadPool.schedule(new PlayerVarExpireTask(this), expire_time - System.currentTimeMillis());
	}
	
	public String getName()
	{
		return _name;
	}
	
	public L2PcInstance getOwner()
	{
		return _owner;
	}
	
	public boolean hasExpired()
	{
		return _task == null || _task.isDone();
	}
	
	public long getTimeToExpire()
	{
		return _expire_time - System.currentTimeMillis();
	}
	
	public String getValue()
	{
		return _value;
	}
	
	public boolean getValueBoolean()
	{
		if (isNumeric(_value))
			return Integer.parseInt(_value) > 0;
		
		return _value.equalsIgnoreCase("true");
	}
	
	public void setValue(String val)
	{
		_value = val;
	}
	
	public void stopExpireTask()
	{
		if (_task != null && !_task.isDone())
			_task.cancel(true);
	}
	
	private static class PlayerVarExpireTask implements Runnable
	{
		private PlayerVar _pv;
		
		public PlayerVarExpireTask(PlayerVar pv)
		{
			_pv = pv;
		}
		
		@Override
		public void run()
		{
			L2PcInstance pc = _pv.getOwner();
			if (pc == null)
			{
				return;
			}
			
			PlayerVariables.unsetVar(pc, _pv.getName());
		}
	}
	
	public boolean isNumeric(String str)
	{
		try
		{
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}