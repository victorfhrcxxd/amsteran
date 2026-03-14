package net.sf.l2j.gameserver.datatables.custom;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.l2j.gameserver.datatables.SkillTable;
import net.sf.l2j.gameserver.model.L2Skill;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.holder.Doll;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.xmlfactory.XMLDocument;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DollsTable extends XMLDocument
{
	private Map<Integer, Doll> dolls = new HashMap<>();

	public DollsTable()
	{
		load();
	}

	public void reload()
	{
		dolls.clear();
		load();
	}

	public static DollsTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}

	@Override
	protected void load()
	{
		loadDocument("./data/xml/Dolls.xml");
		LOG.info("DollsTable: Loaded " + dolls.size() + " dolls.");
	}

	@Override
	protected void parseDocument(Document doc, File file)
	{
		try
		{
			Node root = doc.getFirstChild();

			for (Node node = root.getFirstChild(); node != null; node = node.getNextSibling())
			{
				if ("Dolls".equalsIgnoreCase(node.getNodeName()))
				{
					NamedNodeMap attrs = node.getAttributes();
					int id = Integer.parseInt(attrs.getNamedItem("Id").getNodeValue());
					int skillId = Integer.parseInt(attrs.getNamedItem("SkillId").getNodeValue());
					int skillLvl = Integer.parseInt(attrs.getNamedItem("SkillLvl").getNodeValue());
					
					int upgradeId = 0;
					int upgradePriceId = 0;
					int upgradePriceCount = 0;
					String name = "Doll";
					int level = 1;
					
					if (attrs.getNamedItem("UpgradeId") != null)
						upgradeId = Integer.parseInt(attrs.getNamedItem("UpgradeId").getNodeValue());
					if (attrs.getNamedItem("UpgradePriceId") != null)
						upgradePriceId = Integer.parseInt(attrs.getNamedItem("UpgradePriceId").getNodeValue());
					if (attrs.getNamedItem("UpgradePriceCount") != null)
						upgradePriceCount = Integer.parseInt(attrs.getNamedItem("UpgradePriceCount").getNodeValue());
					if (attrs.getNamedItem("Name") != null)
						name = attrs.getNamedItem("Name").getNodeValue();
					if (attrs.getNamedItem("Level") != null)
						level = Integer.parseInt(attrs.getNamedItem("Level").getNodeValue());
					
					int upgradeChance = 0;
					if (attrs.getNamedItem("UpgradeChance") != null)
						upgradeChance = Integer.parseInt(attrs.getNamedItem("UpgradeChance").getNodeValue());
					
					Doll doll = new Doll(id, skillId, skillLvl, upgradeId, upgradePriceId, upgradePriceCount, name, level, upgradeChance);
					dolls.put(id, doll);
				}
			}
		}
		catch (Exception e)
		{
			LOG.warning("DollsTable: Error while loading dolls: " + e.getMessage());
		}
	}

	public Map<Integer, Doll> getDolls()
	{
		return dolls;
	}

	public Doll getDollById(int id)
	{
		return dolls.get(id);
	}

	public boolean isDollById(int id)
	{
		return dolls.containsKey(id);
	}

	public static Doll getDoll(L2PcInstance player)
	{
		int skillLv = 0;
		int itemId = 0;

		for (ItemInstance dollItem : player.getInventory().getItems())
		{
			if (getInstance().isDollById(dollItem.getItemId()))
			{
				int skillLvl = getInstance().getDollById(dollItem.getItemId()).getSkillLvl();
				if (skillLvl > skillLv)
				{
					skillLv = skillLvl;
					itemId = dollItem.getItemId();
				}
			}
		}

		if (itemId == 0)
			return null;

		return getInstance().getDollById(itemId);
	}

	public static void setSkillForDoll(L2PcInstance player, int dollItemId)
	{
		Doll doll = getInstance().getDollById(dollItemId);
		if (doll == null)
			return;

		int skillId = doll.getSkillId();
		int skillLvl = doll.getSkillLvl();
		L2Skill skill = SkillTable.getInstance().getInfo(skillId, skillLvl);
		if (skill == null)
			return;

		int currentSkillLvl = player.getSkillLevel(skillId);
		if (currentSkillLvl > 0)
			player.removeSkill(skillId);

		if (player.getInventory().getItemByItemId(dollItemId) == null)
			refreshAllDollSkills(player);
		else
			player.addSkill(skill);

		player.sendSkillList();
	}

	public static void refreshAllDollSkills(L2PcInstance player)
	{
		Map<Integer, Integer> highestSkillLevels = new HashMap<>();

		for (ItemInstance dollItem : player.getInventory().getItems())
		{
			if (getInstance().isDollById(dollItem.getItemId()))
			{
				int skillId = getInstance().getDollById(dollItem.getItemId()).getSkillId();
				int skillLvl = getInstance().getDollById(dollItem.getItemId()).getSkillLvl();
				if (!highestSkillLevels.containsKey(skillId) || skillLvl > highestSkillLevels.get(skillId))
					highestSkillLevels.put(skillId, skillLvl);
			}
		}

		Iterator<Entry<Integer, Integer>> iter = highestSkillLevels.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<Integer, Integer> entry = iter.next();
			L2Skill skill = SkillTable.getInstance().getInfo(entry.getKey(), entry.getValue());
			if (skill != null)
				player.addSkill(skill);
		}

		player.sendSkillList();
	}

	public static void getSkillDoll(L2PcInstance player, ItemInstance item)
	{
		if (item != null && getInstance().isDollById(item.getItemId()))
		{
			setSkillForDoll(player, item.getItemId());
			refreshAllDollSkills(player);
		}
	}

	private static class SingletonHolder
	{
		protected static final DollsTable INSTANCE = new DollsTable();
	}
}
