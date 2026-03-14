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
package net.sf.l2j.gameserver.model.actor.instance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.SpawnTable;
import net.sf.l2j.gameserver.instancemanager.RaidBossPointsManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager;
import net.sf.l2j.gameserver.instancemanager.RaidBossSpawnManager.StatusEnum;
import net.sf.l2j.gameserver.model.L2Clan;
import net.sf.l2j.gameserver.model.L2Party;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.L2World;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Hero;
import net.sf.l2j.gameserver.model.entity.events.SoloBossEvent;
import net.sf.l2j.gameserver.model.entity.events.clanranking.ClanRankingConfig;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.clientpackets.Say2;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.SocialAction;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.util.Broadcast;
import net.sf.l2j.util.Rnd;

/**
 * This class manages all RaidBoss. In a group mob, there are one master called RaidBoss and several slaves called Minions.
 */
public class L2RaidBossInstance extends L2MonsterInstance
{
	private StatusEnum _raidStatus;
	protected ScheduledFuture<?> _maintenanceTask;
	
	/**
	 * Constructor of L2RaidBossInstance (use L2Character and L2NpcInstance constructor).
	 * <ul>
	 * <li>Call the L2Character constructor to set the _template of the L2RaidBossInstance (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
	 * <li>Set the name of the L2RaidBossInstance</li>
	 * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li>
	 * </ul>
	 * @param objectId Identifier of the object to initialized
	 * @param template L2NpcTemplate to apply to the NPC
	 */
	public L2RaidBossInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		setIsRaid(true);
	}
	
	@Override
	public void onSpawn()
	{
		setIsNoRndWalk(true);
		super.onSpawn();
	}
	
	@Override
	public boolean doDie(L2Character killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		RaidBossSpawnManager.getInstance().updateStatusDeath(this);
		
		if (killer != null)
		{
			final L2PcInstance player = killer.getActingPlayer();
			
			if (player != null)
			{
				if (Config.SOLO_BOSS_EVENT && SoloBossEvent.getInstance().isSoloBossNpc(getNpcId()))
				{
					SoloBossEvent.getInstance().onBossDeath(getNpcId());
				}
					
				if (Config.NOBLESS_FROM_BOSS)
				{
					if (getNpcId() == Config.BOSS_ID)
					{
						if (player.getParty() != null)
						{
							for (L2PcInstance member : player.getParty().getPartyMembers())
							{
								if (member.isNoble() == true)
								{
									member.sendMessage("Your party gained nobless status for defeating " + getName() + "!");
								}
								else if (member.isInsideRadius(getX(), getY(), getZ(), Config.RADIUS_TO_RAID, false, false))
								{
									member.setNoble(true, true);
									member.addItem("Quest", 7694, 1, member, true);
									member.broadcastPacket(new SocialAction(player, 16));
			                        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			                        html.setHtml("<html><body>Congratulations, you're now a noble!<br1>Open the Skills & Magic (ALT+K) to see your acquired abilities.</body></html>");
			                        member.sendPacket(html);
								}
								else
								{
									member.sendMessage("Your party killed " + getName() + "! But you were to far...");
								}
							}
						}
						else if (player.getParty() == null && !player.isNoble())
						{
							player.setNoble(true, true);
							player.addItem("Quest", 7694, 1, player, true);
							player.broadcastPacket(new SocialAction(player, 16));
	                        NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
	                        html.setHtml("<html><body>Congratulations, you're now a noble!<br1>Open the Skills & Magic (ALT+K) to see your acquired abilities.</body></html>");
	                        player.sendPacket(html);
						}
					}
				}
				if (ClanRankingConfig.ENABLE_CLAN_RANKING)
				{
					if (player != null && ClanRankingConfig.CLAN_RANKING_BOSS_POINTS_KILLER.containsKey(Integer.valueOf(getNpcId()))) 
					{
						L2Clan clan = player.getClan();
						if (clan != null) 
						{
							int points = ClanRankingConfig.CLAN_RANKING_BOSS_POINTS_KILLER.get(Integer.valueOf(getNpcId())).intValue();
							clan.addRankingBossPoints(points);
						} 
					} 
				}
				if (Config.ANNOUNCE_RAID_BOSS_DEATH)
				{
					if (player.getClan() != null)
						Broadcast.gameAnnounceToOnlinePlayers("Raid Boss: " + getName() + " was killed by " + player.getName()+ " of the clan: " + player.getClan().getName());
					else
						Broadcast.gameAnnounceToOnlinePlayers("Raid Boss: " + getName() + " was killed by " + player.getName());
				}

				broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.RAID_WAS_SUCCESSFUL));
				broadcastPacket(new PlaySound("systemmsg_e.1209"));

				if (player.isInParty())
				{
					for (L2PcInstance member : player.getParty().getPartyMembers())
					{
						RaidBossPointsManager.addPoints(member, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
						if (member.isNoble())
							Hero.getInstance().setRBkilled(member.getObjectId(), getNpcId());
					}
				}
				else
				{
					RaidBossPointsManager.addPoints(player, getNpcId(), (getLevel() / 2) + Rnd.get(-5, 5));
					if (player.isNoble())
						Hero.getInstance().setRBkilled(player.getObjectId(), getNpcId());
				}
				
				if (getNpcId() == Config.RAIDKILL_ID_1 && Config.ACTIVE_MISSION_RAIDKILL)
				{				
					if (player.getParty() != null)
					{
						for (L2PcInstance member : player.getParty().getPartyMembers())
						{
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false))
							{
								if(!member.checkMissions(member.getObjectId()))
									member.updateMissions();
				               
								if(!(member.isRaidKillCompleted() || member.getRaidKill_1() >= 1))
									member.setRaidKill_1(member.getRaidKill_1() + 1);									
							}
							else
								member.sendMessage("Raid Missions: you are too far from the boss...");
						}
					}
					else if (player.getParty() == null)
					{
						if(!player.checkMissions(player.getObjectId()))
							player.updateMissions();
		               
						if(!(player.isRaidKillCompleted() || player.getRaidKill_1() >= 1))
							player.setRaidKill_1(player.getRaidKill_1() + 1);			               
					}
				}

				if  (getNpcId() == Config.RAIDKILL_ID_2 && Config.ACTIVE_MISSION_RAIDKILL)
				{				
					if (player.getParty() != null)
					{
						for (L2PcInstance member : player.getParty().getPartyMembers())
						{
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false))
							{
								if(!member.checkMissions(member.getObjectId()))
									member.updateMissions();
				               
								if(!(member.isRaidKillCompleted() || member.getRaidKill_2() >= 1))
									member.setRaidKill_2(member.getRaidKill_2() + 1);									
							}
							else
								member.sendMessage("Raid Missions: you are too far from the boss...");
						}
					}
					else if (player.getParty() == null)
					{
						if(!player.checkMissions(player.getObjectId()))
							player.updateMissions();
		               
						if(!(player.isRaidKillCompleted() || player.getRaidKill_2() >= 1))
							player.setRaidKill_2(player.getRaidKill_2() + 1);			               
					}
				}	
				
				if  (getNpcId() == Config.RAIDKILL_ID_3 && Config.ACTIVE_MISSION_RAIDKILL)
				{				
					if (player.getParty() != null)
					{
						for (L2PcInstance member : player.getParty().getPartyMembers())
						{
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false))
							{
								if(!member.checkMissions(member.getObjectId()))
									member.updateMissions();
				               
								if(!(member.isRaidKillCompleted() || member.getRaidKill_3() >= 1))
									member.setRaidKill_3(member.getRaidKill_3() + 1);									
							}
							else
								member.sendMessage("Raid Missions: you are too far from the boss...");
						}
					}
					else if (player.getParty() == null)
					{
						if(!player.checkMissions(player.getObjectId()))
							player.updateMissions();
		               
						if(!(player.isRaidKillCompleted() || player.getRaidKill_3() >= 1))
							player.setRaidKill_3(player.getRaidKill_3() + 1);			               
					}
				}				

				if  (getNpcId() == Config.RAIDKILL_ID_4 && Config.ACTIVE_MISSION_RAIDKILL)
				{				
					if (player.getParty() != null)
					{
						for (L2PcInstance member : player.getParty().getPartyMembers())
						{
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false))
							{
								if(!member.checkMissions(member.getObjectId()))
									member.updateMissions();
				               
								if(!(member.isRaidKillCompleted() || member.getRaidKill_4() >= 1))
									member.setRaidKill_4(member.getRaidKill_4() + 1);									
							}
							else
								member.sendMessage("Raid Missions: you are too far from the boss...");
						}
					}
					else if (player.getParty() == null)
					{
						if(!player.checkMissions(player.getObjectId()))
							player.updateMissions();
		               
						if(!(player.isRaidKillCompleted() || player.getRaidKill_4() >= 1))
							player.setRaidKill_4(player.getRaidKill_4() + 1);			               
					}
				}				

				if  (getNpcId() == Config.RAIDKILL_ID_5 && Config.ACTIVE_MISSION_RAIDKILL)
				{				
					if (player.getParty() != null)
					{
						for (L2PcInstance member : player.getParty().getPartyMembers())
						{
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false))
							{
								if(!member.checkMissions(member.getObjectId()))
									member.updateMissions();
				               
								if(!(member.isRaidKillCompleted() || member.getRaidKill_5() >= 1))
									member.setRaidKill_5(member.getRaidKill_5() + 1);									
							}
							else
								member.sendMessage("Raid Missions: you are too far from the boss...");
						}
					}
					else if (player.getParty() == null)
					{
						if (!player.checkMissions(player.getObjectId()))
							player.updateMissions();
		               
						if (!(player.isRaidKillCompleted() || player.getRaidKill_5() >= 1))
							player.setRaidKill_5(player.getRaidKill_5() + 1);			               
					}
				}				

				if  (getNpcId() == Config.RAIDKILL_ID_6 && Config.ACTIVE_MISSION_RAIDKILL)
				{				
					if (player.getParty() != null)
					{
						for (L2PcInstance member : player.getParty().getPartyMembers())
						{
							if (member.isInsideRadius(getX(), getY(), getZ(), 2000, false, false))
							{
								if(!member.checkMissions(member.getObjectId()))
									member.updateMissions();
				               
								if(!(member.isRaidKillCompleted() || member.getRaidKill_6() >= 1) || member.getHWid().equals(player.getHWid()))
									member.setRaidKill_6(member.getRaidKill_6() + 1);									
							}
							else
								member.sendMessage("Raid Missions: you are too far from the boss...");
						}
					}
					else if (player.getParty() == null)
					{
						if (!player.checkMissions(player.getObjectId()))
							player.updateMissions();
		               
						if (!(player.isRaidKillCompleted() || player.getRaidKill_6() >= 1))
							player.setRaidKill_6(player.getRaidKill_6() + 1);			               
					}
				}				
			}
		}

		if (Config.SOLO_BOSS_EVENT)
			rewardSoloEventPlayer();

		rewardNearPlayer();
		updatePvpFlagById();
		return true;
	}
	
	@Override
	public void deleteMe()
	{
		if (_maintenanceTask != null)
		{
			_maintenanceTask.cancel(false);
			_maintenanceTask = null;
		}
		
		super.deleteMe();
	}
	
	/**
	 * Spawn minions.<br>
	 * Also if boss is too far from home location at the time of this check, teleport it to home.
	 */
	@Override
	protected void startMaintenanceTask()
	{
		super.startMaintenanceTask();
		
		_maintenanceTask = ThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				checkAndReturnToSpawn();
			}
		}, 60000, 30000);
	}
	
	protected void checkAndReturnToSpawn()
	{
		if (isDead() || isMovementDisabled() || _isKTBEvent)
			return;
		
		// Gordon does not have permanent spawn
		if (getNpcId() == 29095)
			return;
		
		final L2Spawn spawn = getSpawn();
		if (spawn == null)
			return;
		
		final int spawnX = spawn.getLocx();
		final int spawnY = spawn.getLocy();
		final int spawnZ = spawn.getLocz();
		
		if (!isMovementDisabled())
		{
			if (!isInsideRadius(spawnX, spawnY, spawnZ, Math.max(Config.MAX_DRIFT_RANGE, 1000), true, false))
				teleToLocation(spawnX, spawnY, spawnZ, 0);
		}
	}
	
	public void setRaidStatus(StatusEnum status)
	{
		_raidStatus = status;
	}
	
	public StatusEnum getRaidStatus()
	{
		return _raidStatus;
	}
	
	public static void spawnPolyBoss(int mobId, int xPos, int yPos, int zPos)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(mobId);
		
		try
		{
			final L2Spawn spawn = new L2Spawn(template);
			spawn.setLocx(xPos);
			spawn.setLocy(yPos);
			spawn.setLocz(zPos);
			spawn.setHeading(0);
			spawn.setRespawnDelay(10);
			SpawnTable.getInstance().addNewSpawn(spawn, false);
			spawn.init();
		
			spawn.getLastSpawn().isAggressive();
			spawn.getLastSpawn().decayMe();
			spawn.getLastSpawn().spawnMe(spawn.getLastSpawn().getX(), spawn.getLastSpawn().getY(), spawn.getLastSpawn().getZ());
			spawn.getLastSpawn().broadcastPacket(new MagicSkillUse(spawn.getLastSpawn(), spawn.getLastSpawn(), 1034, 1, 1, 1));
			
			Broadcast.gameAnnounceToOnlinePlayers("Solo Boss: " + NpcTable.getInstance().getTemplate(mobId).getName() + " was spawned in the world!");
			
			spawn.stopRespawn();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void rewardNearPlayer()
	{
		if (Config.RAID_REWARD_LIST.contains(getNpcId()) && Config.ALLOW_RAID_REWARD_RANGE)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.RAID_REWARDS_LIST.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead())
		    		player.addItem("Reward", ids.get(rnd), Config.RAID_REWARDS_LIST.get(ids.get(rnd)), null, true);
			}
		}
	}
	
	private void rewardSoloEventPlayer()
	{
		if (getNpcId() == Config.SOLO_BOSS_ID_ONE)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_ONE.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_ONE.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_TWO)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_TWO.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_TWO.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_TREE)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_TREE.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_TREE.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_FOUR)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_FOUR.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_FOUR.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_FIVE)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_FIVE.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_FIVE.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_SIX)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_SIX.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_SIX.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_SEVEN)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_SEVEN.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_SEVEN.get(ids.get(rnd)), null, true);
			}
		}
		else if (getNpcId() == Config.SOLO_BOSS_ID_EIGHT)
		{
			List<String> _rewarded_hwid = new ArrayList<>();
			for (L2PcInstance player : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.SOLO_RAID_REWARDS_RANGE)) 
			{
				String playerIp = player.getHWid();
				if (_rewarded_hwid.contains(playerIp)) 
					continue;
				
				_rewarded_hwid.add(player.getHWid());
				
		    	List<Integer> ids = new ArrayList<>();
		    	ids.addAll(Config.SOLO_RAID_REWARDS_LIST_EIGHT.keySet());
		    	int rnd = Rnd.get(ids.size());
		    	if (!player.isDead() || !player.isSitting())
		    		player.addItem("Reward", ids.get(rnd), Config.SOLO_RAID_REWARDS_LIST_EIGHT.get(ids.get(rnd)), null, true);
			}
		}
	}
	
	private void updatePvpFlagById()
	{
		if (Config.NPCS_FLAG_LIST.contains(getNpcId()) && Config.ALLOW_FLAG_ONKILL_BY_ID)
		{
			for (L2PcInstance playerInRadius : getKnownList().getKnownTypeInRadius(L2PcInstance.class, Config.NPCS_FLAG_RANGE)) 
			{
				final L2Party party = playerInRadius.getParty();
				if (party != null)
				{
					for (L2PcInstance member : party.getPartyMembers())
					{
						member.updatePvPStatus();
					}
				}
				else
					playerInRadius.updatePvPStatus();
			}
		}		
	}
}