package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.instancemanager.autofarm.AutofarmManager;
import net.sf.l2j.gameserver.model.L2Object;
import net.sf.l2j.gameserver.model.entity.events.chaoticfarm.ChaoticFarmManager;
import net.sf.l2j.gameserver.model.actor.instance.L2MonsterInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.util.StringUtil;

public class VoicedAutoFarm implements IVoicedCommandHandler 
{
    private final String[] VOICED_COMMANDS = 
    {
    	"autofarm",
    	"farm_on",
    	"radiusAutoFarm",
    	"pageAutoFarm",
    	"enableBuffProtect",
    	"healAutoFarm",
    	"hpAutoFarm",
    	"mpAutoFarm",
    	"enableAntiKs",
    	"enableSummonAttack",
    	"summonSkillAutoFarm",
    	"ignoreMonster",
    	"activeMonster"
    };

    @Override
    public boolean useVoicedCommand(final String command, final L2PcInstance activeChar, final String args)
    {
    	if (command.startsWith("autofarm"))
    		showAutoFarm(activeChar);
    	
		if (command.startsWith("radiusAutoFarm"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				String param = st.nextToken();

				if (param.startsWith("inc_radius"))
				{
					activeChar.setRadius(activeChar.getRadius() + 200);
					showAutoFarm(activeChar);
				}
				else if (param.startsWith("dec_radius"))
				{
					activeChar.setRadius(activeChar.getRadius() - 200);
					showAutoFarm(activeChar);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (command.startsWith("pageAutoFarm"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				String param = st.nextToken();

				if (param.startsWith("inc_page"))
				{
					activeChar.setPage(activeChar.getPage() + 1);
					showAutoFarm(activeChar);
				}
				else if (param.startsWith("dec_page"))
				{
					activeChar.setPage(activeChar.getPage() - 1);
					showAutoFarm(activeChar);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (command.startsWith("healAutoFarm"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				String param = st.nextToken();

				if (param.startsWith("inc_heal"))
				{
					activeChar.setHealPercent(activeChar.getHealPercent() + 10);
					showAutoFarm(activeChar);
				}
				else if (param.startsWith("dec_heal"))
				{
					activeChar.setHealPercent(activeChar.getHealPercent() - 10);
					showAutoFarm(activeChar);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (command.startsWith("hpAutoFarm"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				String param = st.nextToken();

				if (param.contains("inc_hp_pot"))
				{
					activeChar.setHpPotionPercentage(activeChar.getHpPotionPercentage() + 5);
					showAutoFarm(activeChar);
				}
				else if (param.contains("dec_hp_pot"))
				{
					activeChar.setHpPotionPercentage(activeChar.getHpPotionPercentage() - 5);
					showAutoFarm(activeChar);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (command.startsWith("mpAutoFarm"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				String param = st.nextToken();

				if (param.contains("inc_mp_pot"))
				{
					activeChar.setMpPotionPercentage(activeChar.getMpPotionPercentage() + 5);
					showAutoFarm(activeChar);
				}
				else if (param.contains("dec_mp_pot"))
				{
					activeChar.setMpPotionPercentage(activeChar.getMpPotionPercentage() - 5);
					showAutoFarm(activeChar);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (command.startsWith("farm_on"))
		{
	    	if (activeChar.isAutoFarm())
	    	{
	    		AutofarmManager.INSTANCE.stopFarm(activeChar);
				activeChar.setAutoFarm(false);
	    	}
	    	else
	    	{
	    		if (ChaoticFarmManager.getInstance().isInRoom(activeChar))
	    		{
	    			activeChar.sendMessage("AutoFarm nao pode ser ativado dentro de uma sala do Chaotic Farm.");
	    			showAutoFarm(activeChar);
	    			return false;
	    		}
	    		AutofarmManager.INSTANCE.startFarm(activeChar);
				activeChar.setAutoFarm(true);
	    	}

	    	showAutoFarm(activeChar);
		}
		
		if (command.startsWith("enableBuffProtect"))
		{
			activeChar.setNoBuffProtection(!activeChar.isNoBuffProtected());
			showAutoFarm(activeChar);
		}
		
		if (command.startsWith("enableAntiKs"))
		{
			activeChar.setAntiKsProtection(!activeChar.isAntiKsProtected());
			showAutoFarm(activeChar);
		}
		
		if (command.startsWith("enableSummonAttack"))
		{
			activeChar.setSummonAttack(!activeChar.isSummonAttack());
			showAutoFarm(activeChar);
		}
		
		if (command.startsWith("summonSkillAutoFarm"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken();
			try
			{
				String param = st.nextToken();

				if (param.startsWith("inc_summonSkill"))
				{
					activeChar.setSummonSkillPercent(activeChar.getSummonSkillPercent() + 10);
					showAutoFarm(activeChar);
				}
				else if (param.startsWith("dec_summonSkill"))
				{
					activeChar.setSummonSkillPercent(activeChar.getSummonSkillPercent() - 10);
					showAutoFarm(activeChar);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if (command.startsWith("ignoreMonster"))
		{
			int monsterId = 0;
			L2Object target = activeChar.getTarget();
			if (target instanceof L2MonsterInstance)
				monsterId = ((L2MonsterInstance) target).getNpcId();
			
			if (target == null)
			{
				activeChar.sendMessage("You dont have a target");
				return false;
			}
			
			activeChar.sendMessage(target.getName() + " has been added to the ignore list.");
			activeChar.ignoredMonster(monsterId);
		}
		
		if (command.startsWith("activeMonster"))
		{
			int monsterId = 0;
			L2Object target = activeChar.getTarget();
			if (target instanceof L2MonsterInstance)
				monsterId = ((L2MonsterInstance) target).getNpcId();
			
			if (target == null)
			{
				activeChar.sendMessage("You dont have a target");
				return false;
			}
			
			activeChar.sendMessage(target.getName() + " has been removed from the ignore list.");
			activeChar.activeMonster(monsterId);
		}
		
        return false;
    }
    
	private static final String ACTIVED = "<font color=00FF00>STARTED</font>";
	private static final String DESATIVED = "<font color=FF0000>STOPPED</font>";
	private static final String STOP = "STOP";
	private static final String START = "START";
	
	public static void showAutoFarm(L2PcInstance activeChar)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		html.setFile("data/html/mods/menu/AutoFarm.htm"); 
		html.replace("%player%", activeChar.getName());
		html.replace("%page%", StringUtil.formatNumber(activeChar.getPage() + 1));
		html.replace("%heal%", StringUtil.formatNumber(activeChar.getHealPercent()));
		html.replace("%radius%", StringUtil.formatNumber(activeChar.getRadius()));
		html.replace("%summonSkill%", StringUtil.formatNumber(activeChar.getSummonSkillPercent()));
		html.replace("%hpPotion%", StringUtil.formatNumber(activeChar.getHpPotionPercentage()));
		html.replace("%mpPotion%", StringUtil.formatNumber(activeChar.getMpPotionPercentage()));
		html.replace("%noBuff%", activeChar.isNoBuffProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		html.replace("%summonAtk%", activeChar.isSummonAttack() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		html.replace("%antiKs%", activeChar.isAntiKsProtected() ? "back=L2UI.CheckBox_checked fore=L2UI.CheckBox_checked" : "back=L2UI.CheckBox fore=L2UI.CheckBox");
		html.replace("%autofarm%", activeChar.isAutoFarm() ? ACTIVED : DESATIVED);
		html.replace("%button%", activeChar.isAutoFarm() ? STOP : START);
		activeChar.sendPacket(html);
	}

    @Override
    public String[] getVoicedCommandList() 
    {
        return VOICED_COMMANDS;
    }
}