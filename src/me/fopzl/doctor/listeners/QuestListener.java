package me.fopzl.doctor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.event.PlayerLevelUpEvent;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.monitors.QuestMonitor;
import me.neoblade298.neobossinstances.BossStartEvent;
import me.neoblade298.neoquests.events.QuestCompleteEvent;

public class QuestListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLevelUp(PlayerLevelUpEvent e) {
		Player p = e.getPlayerData().getPlayer();

		Rank rank = Doctor.getPlayerRank(p);

		QuestMonitor.incLevelup(rank);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onQuestComplete(QuestCompleteEvent e) {
		Player p = e.getPlayer();

		Rank rank = Doctor.getPlayerRank(p);

		QuestMonitor.incQuestComplete(rank);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBossStart(BossStartEvent e) {
		int numFighters = e.getFighters().size();
		int maxFighterLvl = 0;
		for(Player p : e.getFighters()) {
			int lvl = SkillAPI.getPlayerData(p).getMainClass().getLevel();
			if(lvl > maxFighterLvl) maxFighterLvl = lvl;
		}
		String bossName = e.getBossName();

		QuestMonitor.incBossStart(bossName, numFighters, maxFighterLvl);
	}
}
