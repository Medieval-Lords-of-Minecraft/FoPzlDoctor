package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class EntityMonitor extends Monitor {
	// String in key is world name
	public static Map<Pair<String, EntityType>, Integer> newEntityCounts = new HashMap<Pair<String, EntityType>, Integer>();
	
	public EntityMonitor(ScheduleInterval i) {
		super(i);

		dataFields.add("newEntityCounts");
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		for (World w : Bukkit.getWorlds()) {
			String world = w.getName();
			int totalCount = w.getEntities().size();

			sqls.add("insert into fopzldoctor_entityMonitor_total (server, world, totalCount) values ('" + server + "', '" + world + "', " + totalCount + ");");
		}

		for (Entry<Pair<String, EntityType>, Integer> entry : newEntityCounts.entrySet()) {
			String world = entry.getKey().getValue0();
			String entityType = entry.getKey().getValue1().toString();
			int newCount = entry.getValue();

			sqls.add(
					"insert into fopzldoctor_entityMonitor_new (server, world, entityType, newCount) values ('" + server + "', '" + world + "', '" + entityType
							+ "', " + newCount + ");"
			);
		}

		permSaveData(sqls);
		reset();
	}
	
	private static void reset() {
		newEntityCounts.clear();
	}
	
	public static void inc(String worldName, EntityType type) {
		Pair<String, EntityType> pair = Pair.with(worldName, type);
		newEntityCounts.put(pair, newEntityCounts.getOrDefault(pair, 0) + 1);
	}
}
