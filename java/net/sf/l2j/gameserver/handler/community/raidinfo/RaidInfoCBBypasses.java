package net.sf.l2j.gameserver.handler.community.raidinfo;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.cache.HtmCache;
import net.sf.l2j.gameserver.communitybbs.Manager.BaseBBSManager;
import net.sf.l2j.gameserver.datatables.ItemTable;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.datatables.custom.IconTable;
import net.sf.l2j.gameserver.handler.ICBBypassHandler;
import net.sf.l2j.gameserver.instancemanager.RaidBossInfoManager;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.DropCategory;
import net.sf.l2j.gameserver.model.item.DropData;
import net.sf.l2j.gameserver.model.item.kind.Item;

public class RaidInfoCBBypasses implements ICBBypassHandler
{
	private final Map<Integer, Integer> _lastPageRaid = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> _lastPageGrand = new ConcurrentHashMap<>();
	
	@Override
	public boolean handleBypass(String bypass, L2PcInstance activeChar)
	{
		StringTokenizer st = new StringTokenizer(bypass, " ");
		st.nextToken();
		
		if (bypass.startsWith("bp_RaidBossInfo"))
		{
			int pageId = Integer.parseInt(st.nextToken());
			_lastPageRaid.put(activeChar.getObjectId(), pageId);
			showRaidBossBoard(activeChar, "raid", pageId);
		}
		
		if (bypass.startsWith("bp_EpicRaidBossInfo"))
		{
			if (!Config.RETAIL_EVENTS_STARTED)
			{
				activeChar.sendMessage("Event currently unavailable, please wait!");
				return false;
			}
			
			int pageId = Integer.parseInt(st.nextToken());
			_lastPageGrand.put(activeChar.getObjectId(), pageId);
			showEpicRaidBossBoard(activeChar, "epic", pageId);
		}
		
		if (bypass.startsWith("bp_RaidBossDrop"))
		{
			int bossId = Integer.parseInt(st.nextToken());
			int pageId = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			showRaidBossDropBoard(activeChar, "drop", bossId, pageId);
		}

		return false;
	}

	public void showRaidBossBoard(L2PcInstance player, String file, int pageId)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/raidinfo/" + file + ".htm");
		content = content.replace("%raidList%", generateRaidInfoHtml(player, pageId));

		BaseBBSManager.separateAndSend(content, player);
	}

	public void showEpicRaidBossBoard(L2PcInstance player, String file, int pageId)
	{
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/raidinfo/" + file + ".htm");
		content = content.replace("%epicList%", generateEpicRaidInfoHtml(player, pageId));

		BaseBBSManager.separateAndSend(content, player);
	}

	public void showRaidBossDropBoard(L2PcInstance player, String file, int bossId, int pageId)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
		
		String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/raidinfo/" + file + ".htm");
		content = content.replace("%dropList%", generateRaidDropInfoHtml(player, bossId, pageId));
		content = content.replace("%level%", Integer.toString(template.getLevel()));
		content = content.replace("%name%", template.getName());

		BaseBBSManager.separateAndSend(content, player);
	}
	
	public String generateRaidInfoHtml(L2PcInstance player, int pageId)
	{
		List<Integer> infos = new ArrayList<>();
		infos.addAll(Config.LIST_RAID_BOSS_IDS);

		final int limit = Config.RAID_BOSS_INFO_PAGE_LIMIT;
		final int max = infos.size() / limit + (infos.size() % limit == 0 ? 0 : 1);
		infos = infos.subList((pageId - 1) * limit, Math.min(pageId * limit, infos.size()));

		final StringBuilder sb = new StringBuilder();
		
		for (int bossId : infos)
		{
			final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
			if (template == null)
				continue;

			String bossName = template.getName();
			final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
			if (respawnTime <= System.currentTimeMillis())
			{
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\"><font color=\"0FE4EE\"><a action=\"bypass bp_RaidBossDrop " + bossId + "\">" + bossName + "</a></font></td>");
				sb.append("<td width=\"110\" align=\"right\"><font color=\"00FF00\">Alive</font></td>");
				sb.append("<td width=\"5\" align=\"right\"><img src=\"panel.online\" width=\"16\" height=\"16\"></td>");
				sb.append("</tr>");
			}
			else
			{
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\"><font color=\"84857E\"><a action=\"bypass bp_RaidBossDrop " + bossId + "\">" + bossName + "</a></font></td>");
				sb.append("<td width=\"110\" align=\"right\"><font color=\"FF0000\">Dead</font> " + new SimpleDateFormat(Config.RAID_BOSS_DATE_FORMAT).format(new Date(respawnTime)) + "</td>");
				sb.append("<td width=\"5\" align=\"right\"><img src=\"panel.offline\" width=\"16\" height=\"16\"></td>");
				sb.append("</tr>");
			}
		}

		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareWhite\" width=\"350\" height=\"1\">");
		sb.append("<table width=\"224\" cellspacing=\"2\">");
		sb.append("<tr>");

		for (int x = 0; x < max; x++)
		{
			final int pageNr = x + 1;
			if (pageId == pageNr)
				sb.append("");
			else
				sb.append("<td align=\"center\"><a action=\"bypass bp_RaidBossInfo " + pageNr + "\">" + pageNr + "</a></td>");
		}

		return sb.toString();
	}
	
	public String generateEpicRaidInfoHtml(L2PcInstance player, int pageId)
	{
		List<Integer> infos = new ArrayList<>();
		infos.addAll(Config.LIST_GRAND_BOSS_IDS);

		final int limit = Config.RAID_BOSS_INFO_PAGE_LIMIT;
		final int max = infos.size() / limit + (infos.size() % limit == 0 ? 0 : 1);
		infos = infos.subList((pageId - 1) * limit, Math.min(pageId * limit, infos.size()));

		final StringBuilder sb = new StringBuilder();
		for (int bossId : infos)
		{
			final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
			if (template == null)
				continue;

			String bossName = template.getName();
			if (bossName.length() > 23)
				bossName = bossName.substring(0, 23) + "...";

			final long respawnTime = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(bossId);
			if (respawnTime <= System.currentTimeMillis())
			{
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\"><font color=\"0FE4EE\"><a action=\"bypass bp_RaidBossDrop " + bossId + "\">" + bossName + "</a></font></td>");
				sb.append("<td width=\"110\" align=\"right\"><font color=\"9CC300\">Alive</font></td>");
				sb.append("<td width=\"5\" align=\"right\"><img src=\"panel.online\" width=\"16\" height=\"16\"></td>");
				sb.append("</tr>");
			}
			else
			{
				sb.append("<tr>");
				sb.append("<td width=\"150\" align=\"left\"><font color=\"84857E\"><a action=\"bypass bp_RaidBossDrop " + bossId + "\">" + bossName + "</a></font></td>");
				sb.append("<td width=\"110\" align=\"right\"><font color=\"FB5858\">Dead</font> " + new SimpleDateFormat(Config.RAID_BOSS_DATE_FORMAT).format(new Date(respawnTime)) + "</td>");
				sb.append("<td width=\"5\" align=\"right\"><img src=\"panel.offline\" width=\"16\" height=\"16\"></td>");
				sb.append("</tr>");
			}
		}

		sb.append("</table>");
		sb.append("<img src=\"L2UI.SquareWhite\" width=\"350\" height=\"1\">");
		sb.append("<table width=\"224\" cellspacing=\"2\">");
		sb.append("<tr>");

		for (int x = 0; x < max; x++)
		{
			final int pageNr = x + 1;
			if (pageId == pageNr)
				sb.append("");
			else
				sb.append("<td align=\"center\"><a action=\"bypass bp_EpicRaidBossInfo " + pageNr + "\">" + pageNr + "</a></td>");
		}

		return sb.toString();
	}
	
	public String generateRaidDropInfoHtml(L2PcInstance player, int bossId, int pageId)
	{
		final NpcTemplate template = NpcTable.getInstance().getTemplate(bossId);
		if (template == null)
			return null;

		final StringBuilder sb = new StringBuilder(2000);
		if (!template.getDropData().isEmpty())
		{
			int myPage = 1;
			int i = 0;
			int shown = 0;
			boolean hasMore = false;

			for (DropCategory cat : template.getDropData())
			{
				if (shown == 6)
				{
					hasMore = true;
					break;
				}

				for (DropData drop : cat.getAllDrops())
				{
					int mind = 0, maxd = 0, enchmax = 0, enchmin = 0, enchsucess = 0;
					double chance = (double)drop.getChance() / 10000;

					mind = (int) (Config.RATE_DROP_ITEMS * drop.getMinDrop());
					maxd = (int) (Config.RATE_DROP_ITEMS * drop.getMaxDrop());
					
					enchmin = (int) drop.getMinEnchant();
					enchmax = (int) drop.getMaxEnchant();
					enchsucess = (int) drop.getEnchantSucess();

					String smind = null, smaxd = null, drops = null;
					if (mind > 999999)
					{
						DecimalFormat df = new DecimalFormat("###.#");
						smind = df.format(((double)(mind))/1000000) + "KK";
						smaxd = df.format(((double)(maxd))/1000000) + "KK";
					}
					else if (mind > 999)
					{
						smind = (mind/1000) + "K";
						smaxd = (maxd/1000) + "K";
					}                                              
					else
					{
						smind = Integer.toString(mind);
						smaxd = Integer.toString(maxd);
					}

					if (chance <= 0.001)
					{
						DecimalFormat df = new DecimalFormat("#.####");
						drops = df.format(chance);
					}
					else if (chance <= 0.01)
					{
						DecimalFormat df = new DecimalFormat("#.###");
						drops = df.format(chance);
					}
					else
					{
						DecimalFormat df = new DecimalFormat("##.##");
						drops = df.format(chance);
					}

					Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
					String name = item.getName();

					if (name.length() >= 53)
						name = name.substring(0, 50) + "...";

					if (myPage != pageId)
					{
						i++;
						if (i == 6)
						{
							myPage++;
							i = 0;
						}
						continue;
					}

					if (shown == 6)
					{
						hasMore = true;
						break;
					}

					sb.append("<table width=350 bgcolor=000000>");
					sb.append("<tr>");
					sb.append("<td align=left width=32><button width=32 height=32 back=" + IconTable.getIcon(item.getItemId()) + " fore=" + IconTable.getIcon(item.getItemId()) + "></td>");
					sb.append("<td align=left width=263>");
					sb.append("<table>");
					
					if (enchsucess == 0)
						sb.append("<tr><td align=left width=263>" + (cat.isSweep() ? "<font color=FF0099>Sweep Chance</font>" : "<font color=00FF00>Drop Chance</font>") + " : (" + drops + "%)</td></tr>");
					else
						sb.append("<tr><td align=left width=263>" + (cat.isSweep() ? "<font color=FF0099>Sweep Chance</font>" : "<font color=00FF00>Drop Chance</font>") + " : (" + drops + "%)<font color=FF0099> Enchant Chance</font> : (" + enchsucess + "%)</td></tr>");

					if (Config.NOT_SHOW_DROP_INFO.contains(Integer.valueOf(item.getItemId())))
					{
						if (enchmax == 0)
							sb.append("<tr><td align=left width=263><font color=F01E23>" + name + "</font></td></tr>");
						else
							sb.append("<tr><td align=left width=263><font color=F01E23>" + name + "</font> - Enchant Min: <font color=0CFFF9>+" + enchmin + "</font> Max: <font color=FF0C0C>+" + enchmax + "</font></td></tr>");
					}
					else
						sb.append("<tr><td align=left width=263><font color=F9FF00>" + name + "</font> - Min Drop: <font color=00ECFF>" + smind + "</font> Max Drop: <font color=FF0C0C>" + smaxd + "</font></td></tr>");

					sb.append("</table>");
					sb.append("</td>");
					sb.append("</tr>");
					sb.append("</table>");
					sb.append("<img src=l2ui.squaregray width=294 height=1>");
					shown++;
				}
			}

			if (shown == 1)
				sb.append("<img height=200>");

			if (shown == 2)
				sb.append("<img height=160>");

			if (shown == 3)
				sb.append("<img height=120>");

			if (shown == 4)
				sb.append("<img height=80>");

			if (shown == 5)
				sb.append("<img height=40>");

			sb.append("<img src=L2UI.SquareWhite width=375 height=1>");
			
			sb.append("<table><tr>");

			if (pageId > 1)
			{
				sb.append("<td width=130><button value=\"<Prev\" action=\"bypass bp_RaidBossDrop ");
				sb.append(bossId);
				sb.append(" ");
				sb.append(pageId - 1);
				sb.append("\" width=75 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
				if (!hasMore)
					sb.append("<td width=178>Page " +pageId+ "</td>");
			}

			if (pageId == 1 && !hasMore)
				sb.append("<td>Page " + pageId + "</td>");

			if (hasMore)
			{
				if (pageId <= 1)
					sb.append("<td width=130></td>");

				sb.append("<td width=100>Page " +pageId+ "</td>");
				sb.append("<td><button value=\"Next>\" action=\"bypass bp_RaidBossDrop ");
				sb.append(bossId);
				sb.append(" ");
				sb.append(pageId + 1);
				sb.append("\" width=75 height=22 back=L2UI_CH3.Btn1_normalOn fore=L2UI_CH3.Btn1_normal></td>");
			}
			sb.append("</tr></table>");
		}
		else
		{
			sb.append("<br>");
			sb.append("This NPC has no drops.");
			sb.append("<br>");
			sb.append("<img src=L2UI.SquareWhite width=375 height=1>");
		}

		return sb.toString();
	}

	@Override
	public String[] getBypassHandlersList()
	{
		return new String[] { "bp_RaidBossInfo", "bp_EpicRaidBossInfo", "bp_RaidBossDrop" };
	}
}