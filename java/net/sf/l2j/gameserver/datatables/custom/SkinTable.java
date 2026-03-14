package net.sf.l2j.gameserver.datatables.custom;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.holder.DressMeHolder;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SkinTable 
{
	private static Logger _log = Logger.getLogger(SkinTable.class.getName());

	private static final Map<Integer, DressMeHolder> _map = new HashMap<>();

	public static SkinTable getInstance() 
	{
		return SingletonHolder._instance;
	}

	protected SkinTable() 
	{
		try 
		{
			File f = new File("./data/xml/skin.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
			{
				if ("list".equalsIgnoreCase(n.getNodeName()))
				{
					for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) 
					{
						if ("skin".equalsIgnoreCase(d.getNodeName())) 
						{
							/*
							NamedNodeMap attrs = d.getAttributes();
							int id = Integer.valueOf(attrs.getNamedItem("id").getNodeValue()).intValue();
							int hairId = Integer.valueOf(attrs.getNamedItem("hairId").getNodeValue()).intValue();
							int chestId = Integer.valueOf(attrs.getNamedItem("chestId").getNodeValue()).intValue();
							int legsId = Integer.valueOf(attrs.getNamedItem("legsId").getNodeValue()).intValue();
							int glovesId = Integer.valueOf(attrs.getNamedItem("glovesId").getNodeValue()).intValue();
							int feetId = Integer.valueOf(attrs.getNamedItem("feetId").getNodeValue()).intValue();
							_map.put(Integer.valueOf(id), new DressMeHolder(hairId, chestId, legsId, glovesId, feetId));
							*/
						} 
					}  
				}
			} 
			_log.info("SkinTable: Loaded " + _map.size() + " skins.");
		} 
		catch (Exception e) 
		{
			_log.warning("SkinTable: Error while loading skin table: " + e);
		} 
	}

	public DressMeHolder getSkin(int item) 
	{
		return _map.get(Integer.valueOf(item));
	}

	public boolean getSkinId(int skinId) 
	{
		if (!Config.ALLOW_DRESS_ME_SYSTEM)
			return false; 

		return _map.containsKey(Integer.valueOf(skinId));
	}

	private static class SingletonHolder 
	{
		protected static final SkinTable _instance = new SkinTable();
	}
}