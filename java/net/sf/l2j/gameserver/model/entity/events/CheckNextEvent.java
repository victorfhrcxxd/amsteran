package net.sf.l2j.gameserver.model.entity.events;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Arrays;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.entity.events.capturetheflag.CTFConfig;
import net.sf.l2j.gameserver.model.entity.events.deathmatch.DMConfig;
import net.sf.l2j.gameserver.model.entity.events.fortress.FOSConfig;
import net.sf.l2j.gameserver.model.entity.events.killtheboss.KTBConfig;
import net.sf.l2j.gameserver.model.entity.events.lastman.LMConfig;
import net.sf.l2j.gameserver.model.entity.events.teamvsteam.TvTConfig;
import net.sf.l2j.gameserver.model.entity.events.tournaments.properties.ArenaConfig;

public class CheckNextEvent 
{
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	
	private String formatCountdown(Calendar nextEvent)
	{
		if (nextEvent == null || nextEvent.getTime() == null)
			return "Erro";
		
		long diff = nextEvent.getTimeInMillis() - System.currentTimeMillis();
		if (diff <= 0)
			return "Em breve";
		
		long hours = diff / (1000 * 60 * 60);
		long minutes = (diff / (1000 * 60)) % 60;
		
		if (hours > 0 && minutes > 0)
			return "em " + hours + "h " + minutes + "min";
		else if (hours > 0)
			return "em " + hours + "h";
		else if (minutes > 0)
			return "em " + minutes + "min";
		else
			return "Em breve";
	}
	
	public String getNextTvTTime()
	{
		return formatCountdown(getNextTvTEventTime());
	}
	
	public String getNextCTFTime()
	{
		return formatCountdown(getNextCTFEventTime());
	}
	
	public String getNextDMTime()
	{
		return formatCountdown(getNextDMEventTime());
	}
	
	public String getNextLMTime()
	{
		return formatCountdown(getNextLMEventTime());
	}
	
	public String getNextKTBTime()
	{
		return formatCountdown(getNextKTBEventTime());
	}
	
	public String getNextFOSTime()
	{
		return formatCountdown(getNextFOSEventTime());
	}
	
	public String getNextFarmTime()
	{
		return formatCountdown(getNextFarmEventTime());
	}
	
	public String getNextBonusTime()
	{
		return formatCountdown(getNextBonusEventTime());
	}
	
	public String getNextTournamentTime()
	{
		return formatCountdown(getNextTournamentEventTime());
	}
	
	public Calendar getNextTvTEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : TvTConfig.TVT_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextCTFEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : CTFConfig.CTF_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextDMEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : DMConfig.DM_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextLMEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : LMConfig.LM_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextKTBEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : KTBConfig.KTB_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextFOSEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : FOSConfig.FOS_EVENT_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextFarmEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : Config.PARTY_ZONE_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextBonusEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : Config.BONUS_ZONE_INTERVAL)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Calendar getNextTournamentEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar nextStartTime = null;
			Calendar testStartTime = null;
			for (String timeOfDay : ArenaConfig.TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY)
			{
				// Creating a Calendar object from the specified interval value
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				// If the date is in the past, make it the next day (Example: Checking for "1:00", when the time is 23:57.)
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				// Check for the test date to be the minimum (smallest in the specified list)
				if ((nextStartTime == null) || (testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis()))
				{
					nextStartTime = testStartTime;
				}
			}
			
			return nextStartTime;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public String getNextEventInfo()
	{
		final String[] keys  = { "TvT",         "CTF",            "DM",           "LM",           "KTB",          "FOS",             "Farm",           "Tournament" };
		final String[] names = { "Team vs Team", "CTF",            "Death Match",  "Last Man",     "KTB",          "Fortress",        "Party Farm",     "Tournament" };
		final Calendar[] times = {
			getNextTvTEventTime(),
			getNextCTFEventTime(),
			getNextDMEventTime(),
			getNextLMEventTime(),
			getNextKTBEventTime(),
			getNextFOSEventTime(),
			getNextFarmEventTime(),
			getNextTournamentEventTime()
		};

		final java.util.List<String> allowed = Arrays.asList(Config.MENU_NEXT_EVENT_LIST);
		String bestName = "N/A";
		Calendar bestTime = null;
		for (int i = 0; i < times.length; i++)
		{
			if (!allowed.contains(keys[i]))
				continue;
			if (times[i] == null)
				continue;
			if (bestTime == null || times[i].getTimeInMillis() < bestTime.getTimeInMillis())
			{
				bestTime = times[i];
				bestName = names[i];
			}
		}
		return bestName + ": " + formatCountdown(bestTime);
	}

	public static CheckNextEvent getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	private static class SingletonHolder
	{
		protected static final CheckNextEvent INSTANCE = new CheckNextEvent();
	}
}