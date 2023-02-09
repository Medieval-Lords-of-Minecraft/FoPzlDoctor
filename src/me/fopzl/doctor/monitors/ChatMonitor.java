package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class ChatMonitor extends Monitor {
	// String in key is chat channel
	public static Map<Pair<Rank, String>, Integer> chatCounts = new HashMap<Pair<Rank, String>, Integer>();

	public ChatMonitor(ScheduleInterval i) {
		super(i);

		dataFields.add("chatCounts");
	}

	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();

		String server = NeoCore.getInstanceKey();
		for (Entry<Pair<Rank, String>, Integer> entry : chatCounts.entrySet()) {
			String channel = entry.getKey().getValue1();
			String rank = entry.getKey().getValue0().toString();
			String count = entry.getValue().toString();

			sqls.add(
					"insert into fopzldoctor_chatMonitor (server, channel, rank, count) values ('" + server + "', '" + channel + "', '" + rank + "', " + count
							+ ");"
			);
		}

		permSaveData(sqls);
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
