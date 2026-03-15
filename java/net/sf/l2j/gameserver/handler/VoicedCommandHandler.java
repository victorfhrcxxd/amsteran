/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedAutoFarm;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBanking;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedBossSpawn;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedCastles;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedColor;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedDonate;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedDonateColor;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedEvent;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedMenu;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedMission;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedNewColor;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedRanking;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedRepair;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedSoloBoss;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedStatus;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedStreamer;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedVipManager;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedDressMe;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedJoinPvpEvent;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedOfflineFarm;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedVisualRefresh;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoicedZerg;

public class VoicedCommandHandler
{
    private final Map<Integer, IVoicedCommandHandler> _datatable = new HashMap<>();
    
    public static VoicedCommandHandler getInstance()
    {
        return SingletonHolder._instance;
    }
    
    protected VoicedCommandHandler()
    {
		if (Config.ALLOW_AUTOFARM_COMMANDS)
		    registerHandler(new VoicedAutoFarm());
		
		if (Config.ALLOW_MOD_MENU)
		{
    		registerHandler(new VoicedBanking());
    		registerHandler(new VoicedBossSpawn());
    		registerHandler(new VoicedCastles());
    		registerHandler(new VoicedColor());
    		registerHandler(new VoicedMenu());
    		registerHandler(new VoicedRanking());
    		registerHandler(new VoicedRepair());
    		registerHandler(new VoicedStreamer());
    		//registerHandler(new VoicedSecurity());
		}
		
		if (Config.ALLOW_EVENT_COMMANDS)
		    registerHandler(new VoicedEvent());
		
		if (Config.ALLOW_STATUS_COMMANDS)
		    registerHandler(new VoicedStatus());

		if (Config.ALLOW_NEW_COLOR_MANAGER)
			registerHandler(new VoicedNewColor());

		if (Config.ALLOW_MISSION_COMMANDS)
			registerHandler(new VoicedMission());

		if (Config.ENABLE_VIP_SYSTEM)
			registerHandler(new VoicedVipManager());
			
		if (Config.ALLOW_DONATE_COMMANDS)
		{
			registerHandler(new VoicedDonate());
			registerHandler(new VoicedDonateColor());
		}
		
		 registerHandler(new VoicedZerg());
		 registerHandler(new VoicedVisualRefresh());
		 
		if (Config.ALLOW_DRESS_ME_SYSTEM)
			registerHandler(new VoicedDressMe());
		
		if (Config.PVP_EVENT_ENABLED)
			registerHandler(new VoicedJoinPvpEvent());
		
		if (Config.SOLO_BOSS_EVENT)
			registerHandler(new VoicedSoloBoss());
		
		if (Config.ENABLE_OFFLINE_FARM)
			registerHandler(new VoicedOfflineFarm());
    }
    
    public void registerHandler(IVoicedCommandHandler handler)
    {
        String[] ids = handler.getVoicedCommandList();
        
        for (int i = 0; i < ids.length; i++)        
            _datatable.put(ids[i].hashCode(), handler);
    }
        
    public IVoicedCommandHandler getHandler(String voicedCommand)
    {
        String command = voicedCommand;
        
        if (voicedCommand.indexOf(" ") != -1)        
            command = voicedCommand.substring(0, voicedCommand.indexOf(" "));        

        return _datatable.get(command.hashCode());        
    }
    
    public int size()
    {
        return _datatable.size();
    }
    
    private static class SingletonHolder
    {
        protected static final VoicedCommandHandler _instance = new VoicedCommandHandler();
    }
}