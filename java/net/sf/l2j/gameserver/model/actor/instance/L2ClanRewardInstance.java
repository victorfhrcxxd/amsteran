package net.sf.l2j.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.Config;
import net.sf.l2j.L2DatabaseFactory;
import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class L2ClanRewardInstance extends L2NpcInstance
{
	public L2ClanRewardInstance(int objectID, NpcTemplate template)
	{
		super(objectID, template);
	}

    private static final int reputation = 30000000;
    private static final byte level = 8;
    
    //id skills
    private static final int[] clanSkills =
    {
        370,
        371,
        372,
        373,
        374,
        375,
        376,
        377,
        378,
        379,
        380,
        381,
        382,
        383,
        384,
        385,
        386,
        387,
        388,
        389,
        390,
        391
    };
    
	@Override
	public void onBypassFeedback(L2PcInstance player, String command)
	{
		if (_ClanFullData.isEmpty())
			loadClanRewardData();
			
		if (command.startsWith("clan_reward_full"))
		{
			if (!player.isClanLeader())
			{
				player.sendMessage("You are not a clan leader!");
				return;
			}

			if (player.isClanLeader() && player.getClan().getOnlineMembersCount() < Config.MIN_PLAYERS_CLANFULL_REWARD)
			{
				player.sendMessage("You must have at least " + Config.MIN_PLAYERS_CLANFULL_REWARD + " members online.");
				return;
			}

			if (_ClanFullData.containsKey(player.getClan().getClanId()))
			{
				player.sendMessage("Your clan has already been rewarded!");
				return;
			}
			else
			{
				addClanFullData(player.getClanId(), 1);
				player.getClan().changeLevel(level);    
				player.getClan().addReputationScore(reputation);

				for (int s : clanSkills)
				{
					L2Skill clanSkill = SkillTable.getInstance().getInfo(s, SkillTable.getInstance().getMaxLevel(s));
					player.getClan().addNewSkill(clanSkill);
				}

				player.getClan().updateClanInDB();
				player.sendSkillList();
				player.sendMessage("Your clan status was been updated!");   
			}
		}
		
		if (_ClanItemsData.isEmpty())
			loadClanRewardData();
			
		if (command.startsWith("clan_reward_items"))
		{
			if (!player.isClanLeader())
			{
				player.sendMessage("You are not a clan leader!");
				return;
			}

			if (player.isClanLeader() && player.getClan().getOnlineMembersCount() < Config.MIN_PLAYERS_CLANITEMS_REWARD)
			{
				player.sendMessage("You must have at least " + Config.MIN_PLAYERS_CLANITEMS_REWARD + " members online.");
				return;
			}

			if (_ClanItemsData.containsKey(player.getClan().getClanId()))
			{
				player.sendMessage("Your clan has already been rewarded!");
				return;
			}
			else
			{
				addClanItemsData(player.getClanId(), 1);
				
				for (int[] reward : Config.CLAN_ITEMS_REWARD)
				{
					if (player != null && player.isOnline())
					{
						InventoryUpdate iu = new InventoryUpdate();
						player.addItem("Top Reward", reward[0], reward[1], player, true);
						player.getInventory().updateDatabase();
						player.sendPacket(iu);
					}
				}
				player.sendMessage("You have been rewarded by the clan manager!");   
			}
		}
		else
			super.onBypassFeedback(player, command);
	}

	@Override
	public void showChatWindow(L2PcInstance player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile("data/html/mods/clanreward/Main.htm");
		html.replace("%objectId%", getObjectId());
		html.replace("%min_clanFull%", String.valueOf(Config.MIN_PLAYERS_CLANFULL_REWARD));
		html.replace("%min_clanItems%", String.valueOf(Config.MIN_PLAYERS_CLANITEMS_REWARD));
		player.sendPacket(html);
	}

	public static Map<Integer, Integer> _ClanFullData = new ConcurrentHashMap<>();
	public static Map<Integer, Integer> _ClanItemsData = new ConcurrentHashMap<>();
	
	public void loadClanRewardData()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT clanId, clanFull FROM clan_reward");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				_ClanFullData.put(rset.getInt("clanId"), rset.getInt("clanFull"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void loadClanRewardItemsData()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement = con.prepareStatement("SELECT clanId, clanItems FROM clan_items_reward");
			ResultSet rset = statement.executeQuery();

			while (rset.next())
			{
				_ClanItemsData.put(rset.getInt("clanId"), rset.getInt("clanItems"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addClanFullData(int clan, int val)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;
			if (!_ClanFullData.containsKey(clan))
				statement = con.prepareStatement("INSERT INTO clan_reward VALUES(?,?)");
			else
			{
				statement = con.prepareStatement("UPDATE clan_reward SET clanId=?, clanFull=?");
				_ClanFullData.remove(clan);
			}

			statement.setInt(1, clan);
			statement.setInt(2, val);
			statement.execute();
			_ClanFullData.put(clan, val);
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void addClanItemsData(int clan, int val)
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection())
		{
			PreparedStatement statement;
			if (!_ClanItemsData.containsKey(clan))
				statement = con.prepareStatement("INSERT INTO clan_items_reward VALUES(?,?)");
			else
			{
				statement = con.prepareStatement("UPDATE clan_reward SET clanId=?, clanItems=?");
				_ClanItemsData.remove(clan);
			}

			statement.setInt(1, clan);
			statement.setInt(2, val);
			statement.execute();
			_ClanItemsData.put(clan, val);
			statement.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}