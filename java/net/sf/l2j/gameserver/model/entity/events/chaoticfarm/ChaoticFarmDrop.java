package net.sf.l2j.gameserver.model.entity.events.chaoticfarm;

public class ChaoticFarmDrop
{
	public final int itemId;
	public final int min;
	public final int max;
	public final double chance;

	public ChaoticFarmDrop(int itemId, int min, int max, double chance)
	{
		this.itemId = itemId;
		this.min = min;
		this.max = max;
		this.chance = chance;
	}
}
