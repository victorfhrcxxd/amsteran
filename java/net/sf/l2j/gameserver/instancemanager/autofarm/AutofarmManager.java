package net.sf.l2j.gameserver.instancemanager.autofarm;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;

public enum AutofarmManager 
{
    INSTANCE;
    
    private final long iterationSpeedMs = 450L;
    
    private final ConcurrentHashMap<Integer, AutofarmPlayerRoutine> activeFarmers = new ConcurrentHashMap<>();
    private ScheduledFuture<?> onUpdateTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(onUpdate(), 1000, iterationSpeedMs);
    
    private Runnable onUpdate() 
    {
        return () -> activeFarmers.forEach((integer, autofarmPlayerRoutine) -> autofarmPlayerRoutine.executeRoutine());
    }

    public void startFarm(L2PcInstance player)
    {
        if (isAutofarming(player)) 
        {
            player.sendMessage("You are already autofarming.");
            return;
        }
        
        if (player.isOlympiadProtection())
        {
            player.sendMessage("You cannot use autofarm in the olympiad.");
            return;
        }
        
        activeFarmers.put(player.getObjectId(), new AutofarmPlayerRoutine(player));
        player.sendMessage("Autofarming Activated.");
		player.sendPacket(new ExShowScreenMessage("Auto Farming Actived...", 5*1000, SMPOS.BOTTOM_RIGHT, false));
		player.doCast(SkillTable.getInstance().getInfo(9501, 1));
    }
    
    public void stopFarm(L2PcInstance player)
    {
        if (!isAutofarming(player)) 
        {
            player.sendMessage("You are not autofarming.");
            return;
        }

        activeFarmers.remove(player.getObjectId());
        player.sendMessage("Autofarming Deactivated.");
        player.sendPacket(new ExShowScreenMessage("Auto Farming Deactivated...", 5*1000, SMPOS.BOTTOM_RIGHT, false));
        player.stopSkillEffects(9501);
    }

	public synchronized void stopFarmTask()
	{
		if (onUpdateTask != null)
		{
			onUpdateTask.cancel(false);
			onUpdateTask = null;
		}
	}
	
    public void toggleFarm(L2PcInstance player)
    {
        if (isAutofarming(player))
        {
            stopFarm(player);
            return;
        }
        
        startFarm(player);
    }
    
    public boolean isAutofarming(L2PcInstance player)
    {
        return activeFarmers.containsKey(player.getObjectId());
    }
    
    public void onPlayerLogout(L2PcInstance player)
    {
        stopFarm(player);
    }

    public void onDeath(L2PcInstance player) 
    {
        if (isAutofarming(player))
        {
            activeFarmers.remove(player.getObjectId());
            player.stopSkillEffects(9501);
        }
    }
}