package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class ChatMonitor extends Monitor {
	// String in key is chat channel
	private static Map<Pair<Rank, String>, Integer> chatCounts = new HashMap<Pair<Rank, String>, Integer>();

	public ChatMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		chatCounts.clear();
	}

	public static void inc(Rank rank, String channel) {
		Pair<Rank, String> pair = Pair.with(rank, channel);
		chatCounts.put(pair, chatCounts.getOrDefault(pair, 0) + 1);
	}
}
