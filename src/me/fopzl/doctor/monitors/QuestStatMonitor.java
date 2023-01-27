package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.api.player.PlayerClass;
import com.sucy.skill.api.player.PlayerData;

import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class QuestStatMonitor extends Monitor {
	public QuestStatMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// String in key is class name
		Map<Pair<String, LevelGroup>, Integer> allClassCounts = new HashMap<Pair<String, LevelGroup>, Integer>();
		Map<Pair<String, LevelGroup>, Integer> activeClassCounts = new HashMap<Pair<String, LevelGroup>, Integer>();

		for (PlayerAccounts acc : SkillAPI.getPlayerAccountData().values()) {
			for (Entry<Integer, PlayerData> entry : acc.getAllData().entrySet()) {
				PlayerClass mainClass = entry.getValue().getMainClass();
				if (mainClass == null)
					continue;

				String className = mainClass.getData().getName();
				LevelGroup lg = LevelGroup.fromLevel(mainClass.getLevel());
				Pair<String, LevelGroup> pair = Pair.with(className, lg);

				allClassCounts.put(pair, allClassCounts.getOrDefault(pair, 0) + 1);
				if (acc.getActiveId() == entry.getKey()) {
					activeClassCounts.put(pair, activeClassCounts.getOrDefault(pair, 0) + 1);
				}
			}
		}
		// TODO: send counts to sql
	}
	
	@Override
	protected void saveData() {
	}

	@Override
	protected void loadData() {
	}

	private enum LevelGroup {
		T2LOW("1-9"), T2MID("10-19"), T2HIGH("20-29"), T3LOW("30-39"), T3MID("40-49"), T3HIGH("50-59"), MAX("60"); // will need to update when T4 comes out in 10 years

		private final String text;

		LevelGroup(final String text) {
			this.text = text;
		}

		public static LevelGroup fromLevel(int lvl) {
			if (lvl < 10)
				return T2LOW;
			if (lvl < 20)
				return T2MID;
			if (lvl < 30)
				return T2HIGH;
			if (lvl < 40)
				return T3LOW;
			if (lvl < 50)
				return T3MID;
			if (lvl < 60)
				return T3HIGH;
			return MAX;
		}

		@Override
		public String toString() {
			return text;
		}
	}
}
