/*
 * Copyright (C) 2004-2013 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.model.entity.events.multiteamvsteam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.commons.config.ExProperties;
import net.sf.l2j.gameserver.model.holder.RewardHolder;
import net.sf.l2j.util.StringUtil;

public class MultiTvTConfig
{
	protected static final Logger _log = Logger.getLogger(MultiTvTConfig.class.getName());
	
	private static final String TVT_FILE = "./config/events/multiteamvsteam.properties";
	
	public static boolean MULTI_TVT_EVENT_ENABLED;
	public static String[] MULTI_TVT_EVENT_INTERVAL;
	public static int MULTI_TVT_EVENT_PARTICIPATION_TIME;
	public static int MULTI_TVT_EVENT_RUNNING_TIME;
	public static String MULTI_TVT_NPC_LOC_NAME;
	public static int MULTI_TVT_EVENT_PARTICIPATION_NPC_ID;
	public static int[] MULTI_TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
	public static int[] MULTI_TVT_EVENT_PARTICIPATION_FEE = new int[2];
	public static int MULTI_TVT_EVENT_MIN_PLAYERS_IN_TEAMS;
	public static int MULTI_TVT_EVENT_MAX_PLAYERS_IN_TEAMS;
	public static int MULTI_TVT_EVENT_RESPAWN_TELEPORT_DELAY;
	public static int MULTI_TVT_EVENT_START_LEAVE_TELEPORT_DELAY;
	
	public static String MULTI_TVT_EVENT_TEAM_1_NAME;
	public static int[] MULTI_TVT_EVENT_TEAM_1_COORDINATES = new int[3];
	public static String MULTI_TVT_EVENT_TEAM_2_NAME;
	public static int[] MULTI_TVT_EVENT_TEAM_2_COORDINATES = new int[3];
	public static String MULTI_TVT_EVENT_TEAM_3_NAME;
	public static int[] MULTI_TVT_EVENT_TEAM_3_COORDINATES = new int[3];
	public static String MULTI_TVT_EVENT_TEAM_4_NAME;
	public static int[] MULTI_TVT_EVENT_TEAM_4_COORDINATES = new int[3];
	
	public static List<RewardHolder> MULTI_TVT_EVENT_REWARDS_WIN = new ArrayList<>();
	public static boolean MULTI_TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED;
	public static boolean MULTI_TVT_EVENT_SCROLL_ALLOWED;
	public static boolean MULTI_TVT_EVENT_POTIONS_ALLOWED;
	public static boolean MULTI_TVT_EVENT_SUMMON_BY_ITEM_ALLOWED;
	public static List<Integer> MULTI_TVT_DOORS_IDS_TO_OPEN;
	public static List<Integer> MULTI_TVT_DOORS_IDS_TO_CLOSE;
	public static boolean MULTI_TVT_REWARD_TEAM_TIE;
	public static byte MULTI_TVT_EVENT_MIN_LVL;
	public static byte MULTI_TVT_EVENT_MAX_LVL;
	public static int MULTI_TVT_EVENT_EFFECTS_REMOVAL;
	public static Map<Integer, Integer> MULTI_TVT_EVENT_FIGHTER_BUFFS;
	public static Map<Integer, Integer> MULTI_TVT_EVENT_MAGE_BUFFS;
	public static boolean MULTI_TVT_EVENT_MULTIBOX_PROTECTION_ENABLE;
	public static int MULTI_TVT_EVENT_NUMBER_BOX_REGISTER;
	public static boolean MULTI_TVT_REWARD_PLAYER;
	public static boolean MULTI_TVT_REWARD_NO_CARRIER_PLAYER;
	public static String MULTI_TVT_EVENT_ON_KILL;
	public static String DISABLE_ID_CLASSES_STRING;
	public static List<Integer> DISABLE_ID_CLASSES;
	public static boolean MULTI_ALLOW_TVT_DLG;
	
	public static boolean MULTI_TVT_PLAYER_CAN_BE_KILLED_IN_PZ;
	public static boolean ENABLE_MULTI_TVT_INSTANCE;
	public static int MULTI_TVT_INSTANCE_ID;
	
	public static void init()
	{
		ExProperties events = load(TVT_FILE);
		
		MULTI_TVT_EVENT_ENABLED = events.getProperty("MultiTvTEventEnabled", false);
		MULTI_TVT_EVENT_INTERVAL = events.getProperty("MultiTvTEventInterval", "20:00").split(",");
		MULTI_TVT_EVENT_PARTICIPATION_TIME = events.getProperty("MultiTvTEventParticipationTime", 3600);
		MULTI_TVT_EVENT_RUNNING_TIME = events.getProperty("MultiTvTEventRunningTime", 1800);
		MULTI_TVT_NPC_LOC_NAME = events.getProperty("MultiTvTNpcLocName", "Giran Town");
		MULTI_TVT_EVENT_PARTICIPATION_NPC_ID = events.getProperty("MultiTvTEventParticipationNpcId", 0);
		
		if (MULTI_TVT_EVENT_PARTICIPATION_NPC_ID == 0)
		{
			MULTI_TVT_EVENT_ENABLED = false;
			_log.warning("TvTEventEngine: invalid config property -> MultiTvTEventParticipationNpcId");
		}
		else
		{
			String[] propertySplit = events.getProperty("MultiTvTEventParticipationNpcCoordinates", "0,0,0").split(",");
			if (propertySplit.length < 3)
			{
				MULTI_TVT_EVENT_ENABLED = false;
				_log.warning("MultiTvTEventEngine: invalid config property -> MultiTvTEventParticipationNpcCoordinates");
			}
			else
			{
				//TVT_EVENT_REWARDS = new ArrayList<>();
				MULTI_TVT_DOORS_IDS_TO_OPEN = new ArrayList<>();
				MULTI_TVT_DOORS_IDS_TO_CLOSE = new ArrayList<>();
				MULTI_TVT_EVENT_PARTICIPATION_NPC_COORDINATES = new int[4];
				MULTI_TVT_EVENT_TEAM_1_COORDINATES = new int[3];
				MULTI_TVT_EVENT_TEAM_2_COORDINATES = new int[3];
				MULTI_TVT_EVENT_PARTICIPATION_NPC_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
				MULTI_TVT_EVENT_PARTICIPATION_NPC_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
				MULTI_TVT_EVENT_PARTICIPATION_NPC_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
				if (propertySplit.length == 4)
				{
					MULTI_TVT_EVENT_PARTICIPATION_NPC_COORDINATES[3] = Integer.parseInt(propertySplit[3]);
				}
				MULTI_TVT_EVENT_MIN_PLAYERS_IN_TEAMS = events.getProperty("MultiTvTEventMinPlayersInTeams", 1);
				MULTI_TVT_EVENT_MAX_PLAYERS_IN_TEAMS = events.getProperty("MultiTvTEventMaxPlayersInTeams", 20);
				MULTI_TVT_EVENT_MIN_LVL = Byte.parseByte(events.getProperty("MultiTvTEventMinPlayerLevel", "1"));
				MULTI_TVT_EVENT_MAX_LVL = Byte.parseByte(events.getProperty("MultiTvTEventMaxPlayerLevel", "80"));
				MULTI_TVT_EVENT_RESPAWN_TELEPORT_DELAY = events.getProperty("MultiTvTEventRespawnTeleportDelay", 20);
				MULTI_TVT_EVENT_START_LEAVE_TELEPORT_DELAY = events.getProperty("MultiTvTEventStartLeaveTeleportDelay", 20);
				MULTI_TVT_EVENT_EFFECTS_REMOVAL = events.getProperty("MultiTvTEventEffectsRemoval", 0);
				
				MULTI_TVT_EVENT_TEAM_1_NAME = events.getProperty("MultiTvTEventTeam1Name", "Team1");
				propertySplit = events.getProperty("MultiTvTEventTeam1Coordinates", "0,0,0").split(",");
				if (propertySplit.length < 3)
				{
					MULTI_TVT_EVENT_ENABLED = false;
					_log.warning("TvTEventEngine: invalid config property -> MultiTvTEventTeam1Coordinates");
				}
				else
				{
					MULTI_TVT_EVENT_TEAM_1_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
					MULTI_TVT_EVENT_TEAM_1_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
					MULTI_TVT_EVENT_TEAM_1_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
					
					MULTI_TVT_EVENT_TEAM_2_NAME = events.getProperty("MultiTvTEventTeam2Name", "Team2");
					propertySplit = events.getProperty("MultiTvTEventTeam2Coordinates", "0,0,0").split(",");
					if (propertySplit.length < 3)
					{
						MULTI_TVT_EVENT_ENABLED = false;
						_log.warning("TvTEventEngine: invalid config property -> MultiTvTEventTeam2Coordinates");
					}
					else
					{
						MULTI_TVT_EVENT_TEAM_2_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
						MULTI_TVT_EVENT_TEAM_2_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
						MULTI_TVT_EVENT_TEAM_2_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
						
						MULTI_TVT_EVENT_TEAM_3_NAME = events.getProperty("MultiTvTEventTeam3Name", "Team3");
						propertySplit = events.getProperty("MultiTvTEventTeam3Coordinates", "0,0,0").split(",");
						MULTI_TVT_EVENT_TEAM_3_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
						MULTI_TVT_EVENT_TEAM_3_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
						MULTI_TVT_EVENT_TEAM_3_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
						
						MULTI_TVT_EVENT_TEAM_4_NAME = events.getProperty("MultiTvTEventTeam4Name", "Team4");
						propertySplit = events.getProperty("MultiTvTEventTeam4Coordinates", "0,0,0").split(",");
						MULTI_TVT_EVENT_TEAM_4_COORDINATES[0] = Integer.parseInt(propertySplit[0]);
						MULTI_TVT_EVENT_TEAM_4_COORDINATES[1] = Integer.parseInt(propertySplit[1]);
						MULTI_TVT_EVENT_TEAM_4_COORDINATES[2] = Integer.parseInt(propertySplit[2]);
						
						propertySplit = events.getProperty("MultiTvTEventParticipationFee", "0,0").split(",");
						try
						{
							MULTI_TVT_EVENT_PARTICIPATION_FEE[0] = Integer.parseInt(propertySplit[0]);
							MULTI_TVT_EVENT_PARTICIPATION_FEE[1] = Integer.parseInt(propertySplit[1]);
						}
						catch (NumberFormatException nfe)
						{
							if (propertySplit.length > 0)
							{
								_log.warning("TvTEventEngine: invalid config property -> TvTEventParticipationFee");
							}
						}

						MULTI_TVT_EVENT_REWARDS_WIN = Config.parseReward(events, "MultiTvTEventRewardWinners");

						MULTI_TVT_EVENT_TARGET_TEAM_MEMBERS_ALLOWED = events.getProperty("MultiTvTEventTargetTeamMembersAllowed", true);
						MULTI_TVT_EVENT_SCROLL_ALLOWED = events.getProperty("MultiTvTEventScrollsAllowed", false);
						MULTI_TVT_EVENT_POTIONS_ALLOWED = events.getProperty("MultiTvTEventPotionsAllowed", false);
						MULTI_TVT_EVENT_SUMMON_BY_ITEM_ALLOWED = events.getProperty("MultiTvTEventSummonByItemAllowed", false);
						MULTI_TVT_REWARD_TEAM_TIE = events.getProperty("MultiTvTRewardTeamTie", false);
						propertySplit = events.getProperty("MultiTvTDoorsToOpen", "").split(";");
						for (String door : propertySplit)
						{
							try
							{
								MULTI_TVT_DOORS_IDS_TO_OPEN.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
								{
									_log.warning(StringUtil.concat("TvTEventEngine: invalid config property -> MultiTvTDoorsToOpen \"", door, "\""));
								}
							}
						}
						
						propertySplit = events.getProperty("MultiTvTDoorsToClose", "").split(";");
						for (String door : propertySplit)
						{
							try
							{
								MULTI_TVT_DOORS_IDS_TO_CLOSE.add(Integer.parseInt(door));
							}
							catch (NumberFormatException nfe)
							{
								if (!door.isEmpty())
								{
									_log.warning(StringUtil.concat("TvTEventEngine: invalid config property -> MultiTvTDoorsToClose \"", door, "\""));
								}
							}
						}
						
						propertySplit = events.getProperty("MultiTvTEventFighterBuffs", "").split(";");
						if (!propertySplit[0].isEmpty())
						{
							MULTI_TVT_EVENT_FIGHTER_BUFFS = new HashMap<>(propertySplit.length);
							for (String skill : propertySplit)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									_log.warning(StringUtil.concat("TvTEventEngine: invalid config property -> MultiTvTEventFighterBuffs \"", skill, "\""));
								}
								else
								{
									try
									{
										MULTI_TVT_EVENT_FIGHTER_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											_log.warning(StringUtil.concat("TvTEventEngine: invalid config property -> MultiTvTEventFighterBuffs \"", skill, "\""));
										}
									}
								}
							}
						}
						
						propertySplit = events.getProperty("MultiTvTEventMageBuffs", "").split(";");
						if (!propertySplit[0].isEmpty())
						{
							MULTI_TVT_EVENT_MAGE_BUFFS = new HashMap<>(propertySplit.length);
							for (String skill : propertySplit)
							{
								String[] skillSplit = skill.split(",");
								if (skillSplit.length != 2)
								{
									_log.warning(StringUtil.concat("TvTEventEngine: invalid config property -> MultiTvTEventMageBuffs \"", skill, "\""));
								}
								else
								{
									try
									{
										MULTI_TVT_EVENT_MAGE_BUFFS.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
									}
									catch (NumberFormatException nfe)
									{
										if (!skill.isEmpty())
										{
											_log.warning(StringUtil.concat("TvTEventEngine: invalid config property -> MultiTvTEventMageBuffs \"", skill, "\""));
										}
									}
								}
							}
						}
						
						MULTI_TVT_EVENT_MULTIBOX_PROTECTION_ENABLE = events.getProperty("MultiTvTEventMultiBoxEnable", false);
						MULTI_TVT_EVENT_NUMBER_BOX_REGISTER = events.getProperty("MultiTvTEventNumberBoxRegister", 1);
                        
						MULTI_TVT_REWARD_PLAYER = events.getProperty("MultiTvTRewardOnlyKillers", false);
						MULTI_TVT_REWARD_NO_CARRIER_PLAYER = events.getProperty("MultiTvTRewardNoCarrierPlayers", false);
                        
						MULTI_TVT_EVENT_ON_KILL = events.getProperty("MultiTvTEventOnKill", "pmteam");
            			DISABLE_ID_CLASSES_STRING = events.getProperty("MultiTvTDisabledForClasses");
            			DISABLE_ID_CLASSES = new ArrayList<>();
            			for(String class_id : DISABLE_ID_CLASSES_STRING.split(","))
            				DISABLE_ID_CLASSES.add(Integer.parseInt(class_id));
            			
            			MULTI_ALLOW_TVT_DLG = events.getProperty("AllowDlgMultiTvTInvite", false);
            			
            			MULTI_TVT_PLAYER_CAN_BE_KILLED_IN_PZ = events.getProperty("EnableMultiTvTPeaceZoneAttack", false);
            			ENABLE_MULTI_TVT_INSTANCE = events.getProperty("EnableMultiTvTInstance", false);
            			MULTI_TVT_INSTANCE_ID = events.getProperty("MultiTvTInstanceId", 1);
					}
				}
			}
		}
	}
	
	public static ExProperties load(String filename)
	{
		return load(new File(filename));
	}
	
	public static ExProperties load(File file)
	{
		ExProperties result = new ExProperties();
		
		try
		{
			result.load(file);
		}
		catch (IOException e)
		{
			_log.warning("Error loading config : " + file.getName() + "!");
		}
		
		return result;
	}
}