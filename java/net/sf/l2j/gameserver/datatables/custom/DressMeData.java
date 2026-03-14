package net.sf.l2j.gameserver.datatables.custom;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.gameserver.model.holder.SkinPackage;
import net.sf.l2j.gameserver.templates.StatsSet;
import net.sf.l2j.gameserver.xmlfactory.XMLDocumentFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DressMeData
{
	private static final Logger _log = Logger.getLogger(DressMeData.class.getName());
	
	private final static Map<Integer, SkinPackage> _armorSkins = new HashMap<>();
	private final static Map<Integer, SkinPackage> _weaponSkins = new HashMap<>();
	private final static Map<Integer, SkinPackage> _hairSkins = new HashMap<>();
	private final static Map<Integer, SkinPackage> _faceSkins = new HashMap<>();
	private final static Map<Integer, SkinPackage> _shieldSkins = new HashMap<>();
	
	protected DressMeData()
	{
		load();
	}
	
	public void load()
	{
		_armorSkins.clear();
		_weaponSkins.clear();
		_hairSkins.clear();
		_faceSkins.clear();
		_shieldSkins.clear();
		
		try
		{
			File f = new File("./data/xml/dressme.xml");
			if (!f.exists())
			{
				_log.warning("DressMeData: dressme.xml not found.");
				return;
			}
			
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			for (Node list = doc.getFirstChild(); list != null; list = list.getNextSibling())
			{
				if ("list".equalsIgnoreCase(list.getNodeName()))
				{
					for (Node skin = list.getFirstChild(); skin != null; skin = skin.getNextSibling())
					{
						if ("skin".equalsIgnoreCase(skin.getNodeName()))
						{
							final NamedNodeMap attrs = skin.getAttributes();
							String type = attrs.getNamedItem("type").getNodeValue();
							
							for (Node typeN = skin.getFirstChild(); typeN != null; typeN = typeN.getNextSibling())
							{
								if ("type".equalsIgnoreCase(typeN.getNodeName()))
								{
									final NamedNodeMap attrs2 = typeN.getAttributes();
									
									final StatsSet set = new StatsSet();
									
									int id = Integer.parseInt(attrs2.getNamedItem("id").getNodeValue());
									String name = attrs2.getNamedItem("name").getNodeValue();
									
									set.set("type", type);
									set.set("id", id);
									set.set("name", name);
									set.set("weaponId", getAttrInt(attrs2, "weaponId", 0));
									set.set("shieldId", getAttrInt(attrs2, "shieldId", 0));
									set.set("chestId", getAttrInt(attrs2, "chestId", 0));
									set.set("hairId", getAttrInt(attrs2, "hairId", 0));
									set.set("faceId", getAttrInt(attrs2, "faceId", 0));
									set.set("legsId", getAttrInt(attrs2, "legsId", 0));
									set.set("glovesId", getAttrInt(attrs2, "glovesId", 0));
									set.set("feetId", getAttrInt(attrs2, "feetId", 0));
									set.set("priceId", getAttrInt(attrs2, "priceId", 0));
									set.set("priceCount", getAttrInt(attrs2, "priceCount", 0));
									
									switch (type.toLowerCase())
									{
										case "armor":
											_armorSkins.put(id, new SkinPackage(set));
											break;
										case "weapon":
											_weaponSkins.put(id, new SkinPackage(set));
											break;
										case "hair":
											_hairSkins.put(id, new SkinPackage(set));
											break;
										case "face":
											_faceSkins.put(id, new SkinPackage(set));
											break;
										case "shield":
											_shieldSkins.put(id, new SkinPackage(set));
											break;
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "DressMeData: Error loading dressme.xml: " + e.getMessage(), e);
		}
		
		_log.info("DressMeData: Loaded " + _armorSkins.size() + " armor skins.");
		_log.info("DressMeData: Loaded " + _weaponSkins.size() + " weapon skins.");
		_log.info("DressMeData: Loaded " + _hairSkins.size() + " hair skins.");
		_log.info("DressMeData: Loaded " + _faceSkins.size() + " face skins.");
		_log.info("DressMeData: Loaded " + _shieldSkins.size() + " shield skins.");
	}
	
	private static int getAttrInt(NamedNodeMap attrs, String name, int defaultVal)
	{
		Node node = attrs.getNamedItem(name);
		if (node == null)
			return defaultVal;
		try
		{
			return Integer.parseInt(node.getNodeValue());
		}
		catch (NumberFormatException e)
		{
			return defaultVal;
		}
	}
	
	public void reload()
	{
		load();
	}
	
	public SkinPackage getArmorSkinsPackage(int id)
	{
		return _armorSkins.get(id);
	}
	
	public Map<Integer, SkinPackage> getArmorSkinOptions()
	{
		return _armorSkins;
	}
	
	public SkinPackage getWeaponSkinsPackage(int id)
	{
		return _weaponSkins.get(id);
	}
	
	public Map<Integer, SkinPackage> getWeaponSkinOptions()
	{
		return _weaponSkins;
	}
	
	public SkinPackage getHairSkinsPackage(int id)
	{
		return _hairSkins.get(id);
	}
	
	public Map<Integer, SkinPackage> getHairSkinOptions()
	{
		return _hairSkins;
	}
	
	public SkinPackage getFaceSkinsPackage(int id)
	{
		return _faceSkins.get(id);
	}
	
	public Map<Integer, SkinPackage> getFaceSkinOptions()
	{
		return _faceSkins;
	}
	
	public SkinPackage getShieldSkinsPackage(int id)
	{
		return _shieldSkins.get(id);
	}
	
	public Map<Integer, SkinPackage> getShieldSkinOptions()
	{
		return _shieldSkins;
	}
	
	public int getCorrespondingHairSkinId(int armorSkinId)
	{
		final SkinPackage armorSkin = _armorSkins.get(armorSkinId);
		if (armorSkin == null)
			return 0;
		
		final int chestId = armorSkin.getChestId();
		if (chestId == 0)
			return 0;
		
		final int hairItemId = chestId + 1;
		
		for (Map.Entry<Integer, SkinPackage> entry : _hairSkins.entrySet())
		{
			if (entry.getValue().getHairId() == hairItemId)
				return entry.getKey();
		}
		
		return 0;
	}
	
	public static DressMeData getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DressMeData _instance = new DressMeData();
	}
}
