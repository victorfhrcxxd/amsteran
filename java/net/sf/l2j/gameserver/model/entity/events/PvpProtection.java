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
package net.sf.l2j.gameserver.model.entity.events;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 

import phantom.FakePlayer;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
 
/**
 * @author Anarchy
 */
public class PvpProtection
{
	private Map<KillStats, Long> killStats = null;
	private Map<Integer, List<Integer>> protections = null;
 
	protected PvpProtection()
	{
		killStats = new HashMap<>();
		protections = new HashMap<>();
	}
 
	public void checkKill(L2PcInstance killer, L2PcInstance victim)
	{
		if (killer.isGM() || killer instanceof FakePlayer)
			return;
 
		KillStats ks = null;
		for (KillStats k : killStats.keySet())
		{
			if (k.getKiller() == killer.getObjectId() && k.getVictim() == victim.getObjectId())
			{
				ks = k;
				break;
			}
		}
 
		if (ks != null)
		{
			if (System.currentTimeMillis() - killStats.get(ks) < 120*1000)
			{
				ks.addKill();
				killStats.put(ks, System.currentTimeMillis());
			}
			else
			{
				ks.removeKills();
				ks.addKill();
			}
 
			if (ks.getKills() >= 4)
				addNewProtection(killer, victim, ks);
		}
		else
		{
			final KillStats nfks = new KillStats(killer.getObjectId(), victim.getObjectId(), 1);
			killStats.put(nfks, System.currentTimeMillis());
		}
	}
 
	public void addNewProtection(L2PcInstance killer, L2PcInstance victim, KillStats ks)
	{
		killStats.remove(ks);
 
		if (protections.containsKey(victim))
			protections.get(victim).add(killer.getObjectId());
		else
		{
			List<Integer> temp = new ArrayList<>();
			temp.add(killer.getObjectId());
			protections.put(victim.getObjectId(), temp);
		}
 
		killer.sendCustomMessage("Ranked protection is enabled!");
		killer.sendCustomMessage("You may not get elo for killing " + victim + " for 30 minutes.");
		ThreadPoolManager.getInstance().scheduleGeneral(() -> protections.get(victim.getObjectId()).remove(killer.getObjectId()), 1000*60*30);
	}
 
	public boolean protectionExists(L2PcInstance killer, L2PcInstance victim)
	{
		if (killer instanceof FakePlayer)
			return false;
		
		if (victim instanceof FakePlayer)
			return false;
		
		if (!killer.isGM() && ((killer.getClan() != null && killer.getClan() == victim.getClan()) || (killer.getClan() != null && victim.getClan() != null && killer.getClan().getAllyName() != "" && killer.getClan().getAllyName() != null && killer.getClan().getAllyName().equals(victim.getClan().getAllyName())) || (killer.getClient().getConnection().getInetAddress().getHostAddress().equals(victim.getClient().getConnection().getInetAddress().getHostAddress())) || killer.getHWid().equals(victim.getHWid())))
			return true;
 
		if (protections.containsKey(victim.getObjectId()))
			if (protections.get(victim.getObjectId()).contains(killer.getObjectId()))
				return true;
 
		return false;
	}
 
	public static PvpProtection getInstance()
	{
		return SingletonHolder.instance;
	}
 
	private static class SingletonHolder
	{
		protected static final PvpProtection instance = new PvpProtection();
	}
 
	private class KillStats
	{
		private int killer;
		private int victim;
		private int kills;
 
		public KillStats(int killer, int victim, int kills)
		{
			this.killer = killer;
			this.victim = victim;
			this.kills = kills;
		}
 
		public void removeKills()
		{
			kills = 0;
		}
 
		public void addKill()
		{
			kills++;
		}
 
		public int getKills()
		{
			return kills;
		}
 
		public int getKiller()
		{
			return killer;
		}
 
		public int getVictim()
		{
			return victim;
		}
	}
}