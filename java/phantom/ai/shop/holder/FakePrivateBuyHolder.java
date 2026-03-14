package phantom.ai.shop.holder;

/**
 * @author DooFy
 */
public class FakePrivateBuyHolder
{
	private int _id;
	private int _minCount;
	private int _maxCount;
	private int _minPrice;
	private int _maxPrice;
	private int _listChance;
	
	/**
	 * @param rewardId
	 * @param rewardMin
	 * @param rewardMax
	 */
	public FakePrivateBuyHolder(int buyId, int countMin, int countMax, int priceMin, int priceMax)
	{
		_id = buyId;
		_minCount = countMin;
		_maxCount = countMax;
		_minPrice = priceMin;
		_maxPrice = priceMax;
		_listChance = 100;
	}
	
	/**
	 * @param rewardId
	 * @param rewardMin
	 * @param rewardMax
	 */
	public FakePrivateBuyHolder(int buyId, int countMin, int countMax, int priceMin, int priceMax, int rewardChance)
	{
		_id = buyId;
		_minCount = countMin;
		_maxCount = countMax;
		_minPrice = priceMin;
		_maxPrice = priceMax;
		_listChance = rewardChance;
	}
	
	public int getBuyId()
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

	public int getPriceMin()
	{
		return _minPrice;
	}
	
	public int getPriceMax()
	{
		return _maxPrice;
	}
	
	public int getListChance()
	{
		return _listChance;
	}
	
	public void setBuyId(int id)
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

	public void setPriceMin(int min)
	{
		_minPrice = min;
	}
	
	public void setPriceMax(int max)
	{
		_maxPrice = max;
	}
	
	public void setListChance(int chance)
	{
		_listChance = chance;
	}
}