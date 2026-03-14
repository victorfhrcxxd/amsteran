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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2RaidBossInstance;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

public class SoloBossEvent
{
	public enum EventState
	{
		INACTIVE,
		REGISTRATION,
		RUNNING
	}
	
	private static SoloBossEvent _instance = null;
	protected static final Logger _log = Logger.getLogger(SoloBossEvent.class.getName());
	private Calendar NextEvent;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	
	private EventState _state = EventState.INACTIVE;
	private final List<L2PcInstance> _participants = new CopyOnWriteArrayList<>();
	private final List<int[]> _originalLocations = new ArrayList<>();
	private int _currentBossIndex = 0;
	
	public static SoloBossEvent getInstance()
	{
		if (_instance == null)
			_instance = new SoloBossEvent();
		return _instance;
	}
	
	private SoloBossEvent()
	{
	}
	
	public String getNextTime()
	{
		if (NextEvent != null && NextEvent.getTime() != null)
			return format.format(NextEvent.getTime());
		return "Erro";
	}
	
	public EventState getState()
	{
		return _state;
	}
	
	public boolean isInactive()
	{
		return _state == EventState.INACTIVE;
	}
	
	public boolean isRegistering()
	{
		return _state == EventState.REGISTRATION;
	}
	
	public boolean isRunning()
	{
		return _state == EventState.RUNNING;
	}
	
	public int getParticipantCount()
	{
		return _participants.size();
	}
	
	// =========================================================
	// Scheduling
	// =========================================================
	
	public void StartCalculationOfNextEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0, timeL = 0;
			int count = 0;
			
			for (String timeOfDay : Config.SOLO_BOSS_EVENT_INTERVAL_BY_TIME_OF_DAY)
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
			_log.info("Solo Boss: Next Event " + NextEvent.getTime().toString());
			ThreadPoolManager.getInstance().scheduleGeneral(new StartRegistrationTask(), flush2);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("[Solo Boss]: " + e);
		}
	}
	
	// =========================================================
	// Registration
	// =========================================================
	
	public void startRegistration()
	{
		if (_state != EventState.INACTIVE)
			return;
		
		_state = EventState.REGISTRATION;
		_participants.clear();
		_originalLocations.clear();
		_currentBossIndex = 0;
		
		Broadcast.gameAnnounceToOnlinePlayers("Solo Boss: Registro aberto! Digite .soloboss para se registrar!");
		Broadcast.gameAnnounceToOnlinePlayers("Solo Boss: O registro fecha em " + Config.SOLO_BOSS_REGISTRATION_TIME + " minuto(s).");
		
		ThreadPoolManager.getInstance().scheduleGeneral(new StartEventTask(), Config.SOLO_BOSS_REGISTRATION_TIME * 60 * 1000);
		
		_log.info("Solo Boss: Registration started.");
	}
	
	public synchronized boolean registerPlayer(L2PcInstance player)
	{
		if (_state != EventState.REGISTRATION)
		{
			player.sendMessage("Solo Boss: O registro nao esta aberto.");
			return false;
		}
		
		if (_participants.contains(player))
		{
			player.sendMessage("Solo Boss: Voce ja esta registrado.");
			return false;
		}
		
		if (player.getLevel() < Config.SOLO_BOSS_MIN_LEVEL || player.getLevel() > Config.SOLO_BOSS_MAX_LEVEL)
		{
			player.sendMessage("Solo Boss: Seu nivel nao atende os requisitos (" + Config.SOLO_BOSS_MIN_LEVEL + "-" + Config.SOLO_BOSS_MAX_LEVEL + ").");
			return false;
		}
		
		if (player.isInOlympiadMode())
		{
			player.sendMessage("Solo Boss: Voce nao pode se registrar durante a Olympiad.");
			return false;
		}
		
		if (player.isDead())
		{
			player.sendMessage("Solo Boss: Voce nao pode se registrar estando morto.");
			return false;
		}
		
		_participants.add(player);
		player.sendMessage("Solo Boss: Voce foi registrado! (" + _participants.size() + " participantes)");
		return true;
	}
	
	public synchronized boolean unregisterPlayer(L2PcInstance player)
	{
		if (_state != EventState.REGISTRATION)
		{
			player.sendMessage("Solo Boss: O registro nao esta aberto.");
			return false;
		}
		
		if (!_participants.contains(player))
		{
			player.sendMessage("Solo Boss: Voce nao esta registrado.");
			return false;
		}
		
		_participants.remove(player);
		player.sendMessage("Solo Boss: Voce foi removido do evento.");
		return true;
	}
	
	// =========================================================
	// Event Lifecycle
	// =========================================================
	
	public void startEvent()
	{
		if (_state != EventState.REGISTRATION)
			return;
		
		if (_participants.size() < Config.SOLO_BOSS_MIN_PLAYERS)
		{
			Broadcast.gameAnnounceToOnlinePlayers("Solo Boss: Evento cancelado por falta de participantes (" + _participants.size() + "/" + Config.SOLO_BOSS_MIN_PLAYERS + ").");
			_state = EventState.INACTIVE;
			_participants.clear();
			_originalLocations.clear();
			StartCalculationOfNextEventTime();
			return;
		}
		
		_state = EventState.RUNNING;
		_currentBossIndex = 0;
		
		Broadcast.gameAnnounceToOnlinePlayers("Solo Boss: Evento iniciado com " + _participants.size() + " participantes!");
		
		saveOriginalLocations();
		teleportParticipantsToBoss(_currentBossIndex);
		
		ThreadPoolManager.getInstance().scheduleGeneral(() -> spawnCurrentBoss(), 3000);
		
		_log.info("Solo Boss: Event started with " + _participants.size() + " participants.");
	}
	
	public void onBossDeath(int npcId)
	{
		if (_state != EventState.RUNNING)
			return;
		
		if (!isSoloBossNpc(npcId))
			return;
		
		_currentBossIndex++;
		
		if (_currentBossIndex >= Config.SOLO_BOSS_TOTAL_BOSSES)
		{
			announceToParticipants("Solo Boss: Todos os bosses foram derrotados! Evento encerrado!");
			Broadcast.gameAnnounceToOnlinePlayers("Solo Boss: Evento finalizado!");
			endEvent();
			return;
		}
		
		announceToParticipants("Solo Boss: Boss derrotado! Teleportando para o proximo boss em " + Config.SOLO_BOSS_TELEPORT_DELAY + " segundos...");
		
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			teleportParticipantsToBoss(_currentBossIndex);
			ThreadPoolManager.getInstance().scheduleGeneral(() -> spawnCurrentBoss(), 3000);
		}, Config.SOLO_BOSS_TELEPORT_DELAY * 1000);
	}
	
	private void endEvent()
	{
		_state = EventState.INACTIVE;
		
		ThreadPoolManager.getInstance().scheduleGeneral(() -> teleportParticipantsBack(), 5000);
		
		ThreadPoolManager.getInstance().scheduleGeneral(() ->
		{
			_participants.clear();
			_originalLocations.clear();
			_currentBossIndex = 0;
			StartCalculationOfNextEventTime();
		}, 10000);
	}
	
	// =========================================================
	// Teleportation
	// =========================================================
	
	private void saveOriginalLocations()
	{
		_originalLocations.clear();
		for (L2PcInstance player : _participants)
		{
			if (player != null && player.isOnline())
				_originalLocations.add(new int[] { player.getObjectId(), player.getX(), player.getY(), player.getZ() });
		}
	}
	
	private void teleportParticipantsToBoss(int bossIndex)
	{
		int[] coords = getBossCoords(bossIndex);
		if (coords == null)
			return;
		
		for (L2PcInstance player : _participants)
		{
			if (player != null && player.isOnline())
			{
				if (player.isDead())
					player.doRevive();
				
				player.teleToLocation(coords[0] + Rnd.get(-300, 300), coords[1] + Rnd.get(-300, 300), coords[2], 0);
			}
		}
	}
	
	private void teleportParticipantsBack()
	{
		for (L2PcInstance player : _participants)
		{
			if (player == null || !player.isOnline())
				continue;
			
			for (int[] loc : _originalLocations)
			{
				if (loc[0] == player.getObjectId())
				{
					player.teleToLocation(loc[1], loc[2], loc[3], 0);
					break;
				}
			}
		}
	}
	
	// =========================================================
	// Boss Management
	// =========================================================
	
	private void spawnCurrentBoss()
	{
		int bossId = getBossId(_currentBossIndex);
		int[] coords = getBossCoords(_currentBossIndex);
		if (bossId > 0 && coords != null)
		{
			L2RaidBossInstance.spawnPolyBoss(bossId, coords[0], coords[1], coords[2]);
			announceToParticipants("Solo Boss: " + getBossName(_currentBossIndex) + " apareceu! Boa sorte!");
		}
	}
	
	public boolean isSoloBossNpc(int npcId)
	{
		return npcId == Config.SOLO_BOSS_ID_ONE || npcId == Config.SOLO_BOSS_ID_TWO
			|| npcId == Config.SOLO_BOSS_ID_TREE || npcId == Config.SOLO_BOSS_ID_FOUR
			|| npcId == Config.SOLO_BOSS_ID_FIVE || npcId == Config.SOLO_BOSS_ID_SIX
			|| npcId == Config.SOLO_BOSS_ID_SEVEN || npcId == Config.SOLO_BOSS_ID_EIGHT;
	}
	
	public int getBossId(int index)
	{
		switch (index)
		{
			case 0: return Config.SOLO_BOSS_ID_ONE;
			case 1: return Config.SOLO_BOSS_ID_TWO;
			case 2: return Config.SOLO_BOSS_ID_TREE;
			case 3: return Config.SOLO_BOSS_ID_FOUR;
			case 4: return Config.SOLO_BOSS_ID_FIVE;
			case 5: return Config.SOLO_BOSS_ID_SIX;
			case 6: return Config.SOLO_BOSS_ID_SEVEN;
			case 7: return Config.SOLO_BOSS_ID_EIGHT;
			default: return -1;
		}
	}
	
	public int[] getBossCoords(int index)
	{
		switch (index)
		{
			case 0: return new int[] { Config.BOSS_ID_ONE_X, Config.BOSS_ID_ONE_Y, Config.BOSS_ID_ONE_Z };
			case 1: return new int[] { Config.BOSS_ID_TWO_X, Config.BOSS_ID_TWO_Y, Config.BOSS_ID_TWO_Z };
			case 2: return new int[] { Config.BOSS_ID_TREE_X, Config.BOSS_ID_TREE_Y, Config.BOSS_ID_TREE_Z };
			case 3: return new int[] { Config.BOSS_ID_FOUR_X, Config.BOSS_ID_FOUR_Y, Config.BOSS_ID_FOUR_Z };
			case 4: return new int[] { Config.BOSS_ID_FIVE_X, Config.BOSS_ID_FIVE_Y, Config.BOSS_ID_FIVE_Z };
			case 5: return new int[] { Config.BOSS_ID_SIX_X, Config.BOSS_ID_SIX_Y, Config.BOSS_ID_SIX_Z };
			case 6: return new int[] { Config.BOSS_ID_SEVEN_X, Config.BOSS_ID_SEVEN_Y, Config.BOSS_ID_SEVEN_Z };
			case 7: return new int[] { Config.BOSS_ID_EIGHT_X, Config.BOSS_ID_EIGHT_Y, Config.BOSS_ID_EIGHT_Z };
			default: return null;
		}
	}
	
	private String getBossName(int index)
	{
		return "Boss " + (index + 1);
	}
	
	// =========================================================
	// Participant Utilities
	// =========================================================
	
	public boolean isParticipant(L2PcInstance player)
	{
		return _participants.contains(player);
	}
	
	public boolean isParticipant(int objectId)
	{
		for (L2PcInstance player : _participants)
		{
			if (player != null && player.getObjectId() == objectId)
				return true;
		}
		return false;
	}
	
	public void announceToParticipants(String message)
	{
		CreatureSay cs = new CreatureSay(0, Say2.PARTY, "Solo Boss", message);
		for (L2PcInstance player : _participants)
		{
			if (player != null && player.isOnline())
				player.sendPacket(cs);
		}
	}
	
	public void removeParticipant(L2PcInstance player)
	{
		_participants.remove(player);
	}
	
	// =========================================================
	// Tasks
	// =========================================================
	
	class StartRegistrationTask implements Runnable
	{
		@Override
		public void run()
		{
			_log.info("Solo Boss: Starting registration phase.");
			startRegistration();
		}
	}
	
	class StartEventTask implements Runnable
	{
		@Override
		public void run()
		{
			_log.info("Solo Boss: Registration closed. Starting event.");
			startEvent();
		}
	}
}