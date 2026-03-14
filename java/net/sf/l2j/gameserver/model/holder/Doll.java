package net.sf.l2j.gameserver.model.holder;

public class Doll
{
	private final int _itemId;
	private final int _skillId;
	private final int _skillLvl;
	private final int _upgradeId;
	private final int _upgradePriceId;
	private final int _upgradePriceCount;
	private final String _name;
	private final int _level;
	private final int _upgradeChance;

	public Doll(int itemId, int skillId, int skillLvl, int upgradeId, int upgradePriceId, int upgradePriceCount, String name, int level, int upgradeChance)
	{
		_itemId = itemId;
		_skillId = skillId;
		_skillLvl = skillLvl;
		_upgradeId = upgradeId;
		_upgradePriceId = upgradePriceId;
		_upgradePriceCount = upgradePriceCount;
		_name = name;
		_level = level;
		_upgradeChance = upgradeChance;
	}

	public int getItemId()
	{
		return _itemId;
	}

	public int getSkillId()
	{
		return _skillId;
	}

	public int getSkillLvl()
	{
		return _skillLvl;
	}

	public int getUpgradeId()
	{
		return _upgradeId;
	}

	public int getUpgradePriceId()
	{
		return _upgradePriceId;
	}

	public int getUpgradePriceCount()
	{
		return _upgradePriceCount;
	}

	public String getName()
	{
		return _name;
	}

	public int getLevel()
	{
		return _level;
	}

	public int getUpgradeChance()
	{
		return _upgradeChance;
	}

	public boolean canUpgrade()
	{
		return _upgradeId > 0;
	}
}
