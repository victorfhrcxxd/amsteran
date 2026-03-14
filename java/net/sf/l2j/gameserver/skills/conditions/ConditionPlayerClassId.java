package net.sf.l2j.gameserver.skills.conditions;

import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.skills.Env;

public class ConditionPlayerClassId extends Condition
{
    private final int[] _classIds;

    public ConditionPlayerClassId(String[] ids)
    {
        _classIds = new int[ids.length];
        for (int i = 0; i < ids.length; i++)
            _classIds[i] = Integer.parseInt(ids[i]);
    }

    @Override
    public boolean testImpl(Env env)
    {
    	final L2PcInstance player = env.getPlayer();
    	
		if (env.getPlayer() == null)
			return false;
		
        int playerClassId = ((L2PcInstance) player).getClassId().getId();
        for (int id : _classIds)
            if (playerClassId == id)
                return true;

        return false;
    }
}