package net.sf.l2j.gameserver.instancemanager.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.MapRegionTable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.zone.ZoneId;

public class LimitHwidFarm 
{
	protected static final Logger _log = Logger.getLogger(Config.class.getName());

	public static final LimitHwidFarm getInstance() 
	{
		return SingletonHolder._instance;
	}

	private Map<String, L2PcInstance> _checkHwidFarm = new HashMap<>();

	public boolean checkFarmPlayer(L2PcInstance player) 
	{
		if (_checkHwidFarm != null && _checkHwidFarm.containsKey(player.getHWid())) 
		{
			L2PcInstance playerValue = _checkHwidFarm.get(player.getHWid());
			if (Config.FARM_PROTECT_RADIUS)
			{
				for (L2PcInstance knownChar : player.getKnownList().getKnownTypeInRadius(L2PcInstance.class, 1500)) 
				{
					if (knownChar == null)
						continue; 
					
					if (knownChar.getHWid() != null && player.getHWid() != null)
					{
						if (player.getHWid().equals(knownChar.getHWid())) 
						{
							knownChar.sendMessage("You cannot use multibox to farm.");
							ThreadPoolManager.getInstance().scheduleGeneral(() -> knownChar.teleToLocation(MapRegionTable.TeleportWhereType.Town), 1000);
						}  
					}
				}  
			}
			
			if (!playerValue.isOnline() || playerValue.isInsideZone(ZoneId.TOWN)) 
			{
				_checkHwidFarm.put(player.getHWid(), player);
				return true;
			} 
			
			if (playerValue == player)
				return true; 
		}
		else 
		{
			_checkHwidFarm.put(player.getHWid(), player);
			return true;
		} 
		return false;
	}

	public static class SingletonHolder 
	{
		protected static final LimitHwidFarm _instance = new LimitHwidFarm();
	}
}