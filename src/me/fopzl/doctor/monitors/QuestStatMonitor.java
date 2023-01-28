package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.QuestLevelGroup;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class QuestStatMonitor extends Monitor {
	public QuestStatMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		
		// String in key is class name
		Map<Pair<String, QuestLevelGroup>, Integer> allClassCounts = new HashMap<Pair<String, QuestLevelGroup>, Integer>();
		Map<Pair<String, QuestLevelGroup>, Integer> activeClassCounts = new HashMap<Pair<String, QuestLevelGroup>, Integer>();

		for (PlayerAccounts acc : SkillAPI.getPlayerAccountData().values()) {
			for (Entry<Integer, PlayerData> entry : acc.getAllData().entrySet()) {
				PlayerClass mainClass = entry.getValue().getMainClass();
				if (mainClass == null)
					continue;

				String className = mainClass.getData().getName();
				QuestLevelGroup lg = QuestLevelGroup.fromLevel(mainClass.getLevel());
				Pair<String, QuestLevelGroup> pair = Pair.with(className, lg);

				allClassCounts.put(pair, allClassCounts.getOrDefault(pair, 0) + 1);
				if (acc.getActiveId() == entry.getKey()) {
					activeClassCounts.put(pair, activeClassCounts.getOrDefault(pair, 0) + 1);
				}
			}
		}
		
		String server = NeoCore.getInstanceKey();
		for (Entry<Pair<String, QuestLevelGroup>, Integer> entry : allClassCounts.entrySet()) {
			String class_ = entry.getKey().getValue0();
			String levelGroup = entry.getKey().getValue1().toString();
			int totalCount = entry.getValue();
			int activeCount = activeClassCounts.get(entry.getKey());
			
			sqls.add(
					"insert into fopzldoctor_questStatMonitor (server, class, levelGroup, activeCount, totalCount) values ('" + server + "', '" + class_
							+ "', '" + levelGroup + "', " + activeCount + ", " + totalCount + ");"
			);
		}
		
		permSaveData(sqls);
	}
	
	@Override
	protected void saveData() {
	}

	@Override
	protected void loadData() {
	}
}
