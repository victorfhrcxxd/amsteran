package phantom.ai.shop.holder;

/**
 * @author DooFy
 */
public class FakePrivateSellHolder
{
	private int _id;
	private int _minCount;
	private int _maxCount;
	private int _listChance;
	
	/**
	 * @param rewardId
	 * @param rewardMin
	 * @param rewardMax
	 */
	public FakePrivateSellHolder(int buyId, int countMin, int countMax)
	{
		_id = buyId;
		_minCount = countMin;
		_maxCount = countMax;
		_listChance = 100;
	}
	
	/**
	 * @param rewardId
	 * @param rewardMin
	 * @param rewardMax
	 */
	public FakePrivateSellHolder(int buyId, int countMin, int countMax, int rewardChance)
	{
		_id = buyId;
		_minCount = countMin;
		_maxCount = countMax;
		_listChance = rewardChance;
	}
	
	public int getSellId()
	{
		return _id;
	}
	
	public int getCountMin()
	{
		return _minCount;
	}
	
	public int getCountMax()
	{
		return _maxCount;
	}

	public int getListChance()
	{
		return _listChance;
	}
	
	public void setSellId(int id)
	{
		_id = id;
	}
	
	public void setCountMin(int min)
	{
		_minCount = min;
	}
	
	public void setCountMax(int max)
	{
		_maxCount = max;
	}

	public void setListChance(int chance)
	{
		_listChance = chance;
	}
}