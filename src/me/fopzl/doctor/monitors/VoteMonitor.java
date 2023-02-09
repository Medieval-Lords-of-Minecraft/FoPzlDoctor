package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class VoteMonitor extends Monitor {
	// Key is votesite
	public static Map<String, Integer> votesiteCounts = new HashMap<String, Integer>();

	public VoteMonitor(ScheduleInterval i) {
		super(i);

		dataFields.add("votesiteCounts");
	}

	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();

		String server = NeoCore.getInstanceKey();
		for (Entry<String, Integer> entry : votesiteCounts.entrySet()) {
			String votesite = entry.getKey();
			int count = entry.getValue();
			sqls.add("insert into fopzldoctor_voteMonitor (server, votesite, count) values ('" + server + "', '" + votesite + "', " + count + ");");
		}
		
		permSaveData(sqls);
		reset();
	}

	private static void reset() {
		votesiteCounts.clear();
	}

	public static void inc(String votesite) {
		votesiteCounts.put(votesite, votesiteCounts.getOrDefault(votesite, 0) + 1);
	}
}
