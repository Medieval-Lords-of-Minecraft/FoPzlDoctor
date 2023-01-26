package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class QuestMonitor extends Monitor {
	private static Map<Rank, Integer> levelupCounts = new HashMap<Rank, Integer>();
	private static Map<Rank, Integer> questCompleteCounts = new HashMap<Rank, Integer>();
	// Key is <Boss name, Group size, Max lvl of group>
	private static Map<Triplet<String, Integer, Integer>, Integer> bossStartCounts = new HashMap<Triplet<String, Integer, Integer>, Integer>();

	public QuestMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		levelupCounts.clear();
		questCompleteCounts.clear();
		bossStartCounts.clear();
	}

	public static void incLevelup(Rank rank) {
		levelupCounts.put(rank, levelupCounts.getOrDefault(rank, 0) + 1);
	}

	public static void incQuestComplete(Rank rank) {
		questCompleteCounts.put(rank, questCompleteCounts.getOrDefault(rank, 0) + 1);
	}

	public static void incBossStart(String bossName, int groupSize, int maxLvlInGroup) {
		Triplet<String, Integer, Integer> triple = Triplet.with(bossName, groupSize, maxLvlInGroup);
		bossStartCounts.put(triple, bossStartCounts.getOrDefault(triple, 0) + 1);
	}
}
