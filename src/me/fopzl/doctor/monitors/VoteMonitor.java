package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;

import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class VoteMonitor extends Monitor {
	// Key is votesite
	private static Map<String, Integer> votesiteCounts = new HashMap<String, Integer>();

	public VoteMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		votesiteCounts.clear();
	}

	public static void inc(String votesite) {
		votesiteCounts.put(votesite, votesiteCounts.getOrDefault(votesite, 0) + 1);
	}
}
