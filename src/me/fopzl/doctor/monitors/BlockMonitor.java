package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.CreativeCategory;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class BlockMonitor extends Monitor {
	// String in key is world name
	public static Map<Triplet<String, Rank, CreativeCategory>, Integer> breakCounts = new HashMap<Triplet<String, Doctor.Rank, CreativeCategory>, Integer>();
	public static Map<Triplet<String, Rank, CreativeCategory>, Integer> placeCounts = new HashMap<Triplet<String, Doctor.Rank, CreativeCategory>, Integer>();
	
	public BlockMonitor(ScheduleInterval i) {
		super(i);

		dataFields.add("breakCounts");
		dataFields.add("placeCounts");
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		
		String server = NeoCore.getInstanceKey();
		for (Entry<Triplet<String, Rank, CreativeCategory>, Integer> entry : breakCounts.entrySet()) {
			String world = entry.getKey().getValue0();
			String rank = entry.getKey().getValue1().toString();
			String itemCategory = entry.getKey().getValue2().toString();
			int breakCount = entry.getValue();
			
			int placeCount;
			if (placeCounts.containsKey(entry.getKey())) {
				placeCount = placeCounts.remove(entry.getKey());
			} else {
				placeCount = 0;
			}
			
			sqls.add(
					"insert into fopzldoctor_blockMonitor (server, world, rank, itemCategory, breakCount, placeCount) values ('" + server + "', '" + world
							+ "', '" + rank + "', '" + itemCategory + "', " + breakCount + ", " + placeCount + ");"
			);
		}
		for (Entry<Triplet<String, Rank, CreativeCategory>, Integer> entry : placeCounts.entrySet()) {
			String world = entry.getKey().getValue0();
			String rank = entry.getKey().getValue1().toString();
			String itemCategory = entry.getKey().getValue2().toString();
			int placeCount = entry.getValue();
			
			sqls.add(
					"insert into fopzldoctor_blockMonitor (server, world, rank, itemCategory, breakCount, placeCount) values ('" + server + "', '" + world
							+ "', '" + rank + "', '" + itemCategory + "', 0, " + placeCount + ");"
			);
		}

		permSaveData(sqls);
		reset();
	}
	
	private static void reset() {
		breakCounts.clear();
		placeCounts.clear();
	}
	
	public static void incBreak(String worldName, Rank rank, CreativeCategory cat) {
		Triplet<String, Rank, CreativeCategory> triplet = Triplet.with(worldName, rank, cat);
		breakCounts.put(triplet, breakCounts.getOrDefault(triplet, 0) + 1);
	}
	
	public static void incPlace(String worldName, Rank rank, CreativeCategory cat) {
		Triplet<String, Rank, CreativeCategory> triplet = Triplet.with(worldName, rank, cat);
		placeCounts.put(triplet, placeCounts.getOrDefault(triplet, 0) + 1);
	}
}
