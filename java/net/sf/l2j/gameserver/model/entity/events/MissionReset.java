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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.util.Broadcast;

public class MissionReset
{
	private static MissionReset _instance = null;
	protected static final Logger _log = Logger.getLogger(MissionReset.class.getName());
	public Calendar NextEvent;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");

	public static MissionReset getInstance()
	{
		if (_instance == null)
			_instance = new MissionReset();
		return _instance;
	}

	public String getNextTime()
	{
		if (NextEvent.getTime() != null)
			return format.format(NextEvent.getTime());
		return "Erro";
	}

	private MissionReset()
	{
	}

	public void StartNextEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0, timeL = 0;
			int count = 0;

			for (String timeOfDay : Config.RESET_MISSION_INTERVAL_BY_TIME_OF_DAY)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(Calendar.SECOND, 00);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}

				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();

				if (count == 0)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}

				if (timeL < flush2)
				{
					flush2 = timeL;
					NextEvent = testStartTime;
				}

				count++;
			}
			_log.info("Mission Reset: Next Reset: " + NextEvent.getTime().toString());
			ThreadPoolManager.getInstance().scheduleGeneral(new StartEventTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("Mission Reset: "+e);
		}
	}

	class StartEventTask implements Runnable
	{
		@Override
		public void run()
		{
			Clear();	
		}
	}

	static void Clear()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()            
		{               
			@Override                    
			public void run()                    
			{ 
				//TVT
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tvt_event=?,tvt_completed=?,tvt_hwid=? WHERE tvt_completed like '1'");
					stmt.setInt(1, 0);       			
					stmt.setInt(2, 0);
					stmt.setString(3, " ");      			
					stmt.execute();
					stmt.close();
					stmt = null;       			
				}
				catch(Exception e)
				{
					e.printStackTrace();      			
					_log.log(Level.SEVERE, "[characters_mission: - tvt_completed]:  ", e);
				}

				//DM
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET dm_event=?,dm_completed=?,dm_hwid=? WHERE dm_completed like '1'");
					stmt.setInt(1, 0);       			
					stmt.setInt(2, 0);
					stmt.setString(3, " ");      			
					stmt.execute();
					stmt.close();
					stmt = null;       			
				}
				catch(Exception e)
				{
					e.printStackTrace();      			
					_log.log(Level.SEVERE, "[characters_mission: - dm_completed]:  ", e);
				}
				
				//KTB
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET ktb_event=?,ktb_completed=?,ktb_hwid=? WHERE ktb_completed like '1'");
					stmt.setInt(1, 0);       			
					stmt.setInt(2, 0);
					stmt.setString(3, " ");      			
					stmt.execute();
					stmt.close();
					stmt = null;       			
				}
				catch(Exception e)
				{
					e.printStackTrace();      			
					_log.log(Level.SEVERE, "[characters_mission: - ktb_completed]:  ", e);
				}
				
				//TOURNAMENT
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET tournament_event=?,tournament_completed=?,tournament_hwid=? WHERE tournament_completed like '1'");
					stmt.setInt(1, 0);       			
					stmt.setInt(2, 0);
					stmt.setString(3, " ");      			
					stmt.execute();
					stmt.close();
					stmt = null;       			
				}
				catch(Exception e)
				{
					e.printStackTrace();       			
					_log.log(Level.SEVERE, "[characters_mission: - tournament_completed]:  ", e);
				}
				
				//PVP
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET pvp_event=?,pvp_completed=?,pvp_hwid=? WHERE pvp_completed like '1'");
					stmt.setInt(1, 0);       			
					stmt.setInt(2, 0);
					stmt.setString(3, " ");      			
					stmt.execute();
					stmt.close();
					stmt = null;       			
				}
				catch(Exception e)
				{
					e.printStackTrace();       			
					_log.log(Level.SEVERE, "[characters_mission: - tournament_2x2_completed]:  ", e);
				}
				
				//Raid
				try (Connection con = L2DatabaseFactory.getInstance().getConnection())
				{
					PreparedStatement stmt = con.prepareStatement("UPDATE characters_mission SET raid_1=?,raid_2=?,raid_3=?,raid_4=?,raid_5=?,raid_6=?,raid_kill_completed=?,raid_kill_hwid=? WHERE raid_kill_completed like '1'");
					stmt.setInt(1, 0);
					stmt.setInt(2, 0);
					stmt.setInt(3, 0);
					stmt.setInt(4, 0);
					stmt.setInt(5, 0);
					stmt.setInt(6, 0);
					stmt.setInt(7, 0);
					stmt.setString(8, " ");      			
					stmt.execute();
					stmt.close();
					stmt = null;       			
				}
				catch(Exception e)
				{
					e.printStackTrace();       			
					_log.log(Level.SEVERE, "[characters_mission: - raid_kill_completed]:  ", e);
				}

				for (L2PcInstance player : L2World.getInstance().getL2Players())
				{
					if (player != null && player.isOnline())
					{          				
						if (player.isTvTCompleted())
						{      					
							player._tvtCount = 0;			
							player.setTvTCompleted(false);
						}
						
						if (player.isDMCompleted())
						{      					       				
							player._dmCount = 0;
							player.setDMCompleted(false);
						}

						if (player.isKTBCompleted())
						{      					       				
							player._ktbCount = 0;
							player.setKTBCompleted(false);
						}

						if (player.isTournamentCompleted())
						{      					       				
							player._tournamentCount = 0;
							player.setTournamentCompleted(false);
						}

						if (player.isPVPCompleted())
						{      					       				
							player._pvpCount = 0;
							player.setPVPCompleted(false);
						}
						
						if (player.isRaidKillCompleted())
						{      					       				          				          				
							player._raidkill_1 = 0;
							player._raidkill_2 = 0;
							player._raidkill_3 = 0;
							player._raidkill_4 = 0;
							player._raidkill_5 = 0;
							player._raidkill_6 = 0;
							player.setRaidKillCompleted(false);           				
						}
						
						player.ReloadMission();

						Broadcast.ServerAnnounce("Daily missions have been reseted ::");
					}
				}
				_log.info("Mission Reset: Tasks have been reset.");		
			}
		}, 1);

		NextEvent();
	}

	public static void NextEvent()
	{
		ThreadPoolManager.getInstance().scheduleGeneral(new Runnable()            
		{               
			@Override                    
			public void run()                    
			{                        
				MissionReset.getInstance().StartNextEventTime();	
			}

		}, 1* 1000);
	}	
}