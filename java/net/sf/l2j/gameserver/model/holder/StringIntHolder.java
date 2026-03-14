package net.sf.l2j.gameserver.model.holder;

public class StringIntHolder
{
	private String _name;
	
	private int _value;
	
	public StringIntHolder(String id, int value)
	{
		_name = id;
		_value = value;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getValue()
	{
		return _value;
	}
	
	public void setId(String id)
	{
		_name = id;
	}
	
	public void setValue(int value)
	{
		_value = value;
	}
}