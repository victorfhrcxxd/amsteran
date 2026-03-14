package net.sf.l2j.gameserver.handler.community.dailyreward.data;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.handler.community.dailyreward.DailyRewardCB;
import net.sf.l2j.gameserver.xmlfactory.XMLDocument;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DailyRewardCBData extends XMLDocument
{
	private Map<Integer, DailyRewardCB> _dailyRewards = new LinkedHashMap<>();
	private Map<DailyRewardCB, Set<Integer>> _playersReceivedObjId = new LinkedHashMap<>();
	private Map<DailyRewardCB, Set<String>> _playersReceivedHIWD = new LinkedHashMap<>();
	
	public Map<Integer, DailyRewardCB> getDailyRewards()
	{
		return _dailyRewards;
	}

	public void setDailyRewards(Map<Integer, DailyRewardCB> dailyRewards)
	{
		_dailyRewards = dailyRewards;
	}

	public DailyRewardCBData()
	{
		load();
	}
	
	public void  getRewardedPlayersObjectIds()
	{
		for (DailyRewardCB dr : getAllDailyRewads())
		{
			_playersReceivedObjId.put(dr, dr.getPlayersReceivdList());
		}
	}
	
	public void getRewardedPlayersHWIDs()
	{
		for (DailyRewardCB dr : getAllDailyRewads())
		{
			_playersReceivedHIWD.put(dr, dr.getHwidReceivedList());
		}
	}
	
	public void addReceivedPlayersToReward(DailyRewardCB reward, Set<Integer> listObjId, Set<String> listHwids)
	{
		reward.setPlayersReceivdList(listObjId);
		reward.setHwidReceivedList(listHwids);
	}

	public void reload()
	{
		// armazena a lista de players
		_playersReceivedObjId.clear();
		getRewardedPlayersObjectIds();
		_playersReceivedHIWD.clear();
		getRewardedPlayersHWIDs();
		// destroi a lista de reward
		_dailyRewards.clear();
		// cria nova lista de reward
		load();
		// adiciona nova lista de player a cada reward
		if (!_playersReceivedObjId.isEmpty())
		{
			for (Map.Entry<DailyRewardCB, Set<Integer>> entry : _playersReceivedObjId.entrySet())
			{
				for (DailyRewardCB dr : getAllDailyRewads())
				{
					if (dr.getDay() == entry.getKey().getDay())
					{
						dr.setPlayersReceivdList(entry.getValue());
					}
				}	
			}
		}
		if(!_playersReceivedObjId.isEmpty())
		{
			for(Map.Entry<DailyRewardCB, Set<String>> entry : _playersReceivedHIWD.entrySet())
			{
				for(DailyRewardCB dr : getAllDailyRewads())
				{
					if(dr.getDay() == entry.getKey().getDay())
					{
						dr.setHwidReceivedList(entry.getValue());
					}
				}	
			}
		}
	}

	private static class SingleTonHolder
	{
		protected static final DailyRewardCBData _instance = new DailyRewardCBData();
	}

	public static DailyRewardCBData getInstance()
	{
		return SingleTonHolder._instance;
	}

	@Override
	protected void load()
	{
		loadDocument("./data/xml/dailyreward/dailyrewards.xml");
		LOG.info("Loaded " + _dailyRewards.size() + " Daily Rewards data.");
	}

	@Override
	protected void parseDocument(Document doc, File f)
	{
		try
		{
			// First element is never read.
			final Node n = doc.getFirstChild();

			for (Node o = n.getFirstChild(); o != null; o = o.getNextSibling())
			{
				if (!"reward".equalsIgnoreCase(o.getNodeName()))
					continue;

				NamedNodeMap attrs = o.getAttributes();
				DailyRewardCB reward = null;
				final int day = Integer.parseInt(attrs.getNamedItem("Day").getNodeValue());

				for (Node d = o.getFirstChild(); d != null; d = d.getNextSibling())
				{
					if (!"item".equalsIgnoreCase(d.getNodeName()))
						continue;

					attrs = d.getAttributes();

					final int itemId = Integer.parseInt(attrs.getNamedItem("itemId").getNodeValue());
					final int amount = Integer.parseInt(attrs.getNamedItem("amount").getNodeValue());
					final int enchantLevel = Integer.parseInt(attrs.getNamedItem("enchantLevel").getNodeValue());
					reward = new DailyRewardCB(day, itemId);
					reward.setAmount(amount);
					reward.setEnchantLevel(enchantLevel);
					
					if (ItemTable.getInstance().getTemplate(itemId) != null)
						_dailyRewards.put(day, reward);
					else
						LOG.warning("Daily Reward Data: Item ID: " + itemId + " doesn't exists in game.");
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("Daily Reward Data: Error while creating table: " + e);
		}
	}

	public DailyRewardCB getDailyRewardByDay(int day)
	{
		return _dailyRewards.get(day);
	}

	public List<DailyRewardCB> getAllDailyRewads()
	{
		return new ArrayList<DailyRewardCB>(_dailyRewards.values());
	}
}