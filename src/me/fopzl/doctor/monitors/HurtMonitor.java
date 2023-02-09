package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.event.entity.EntityDamageEvent;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class HurtMonitor extends Monitor {
	// String in key is world name
	public static Map<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Integer> deathCounts = new HashMap<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Integer>();
	public static Map<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Double> dmgSums = new HashMap<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Double>();

	public HurtMonitor(ScheduleInterval i) {
		super(i);

		dataFields.add("deathCounts");
		dataFields.add("dmgSums");
	}

	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		for (Entry<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Integer> entry : deathCounts.entrySet()) {
			String world = entry.getKey().getValue0();
			String rank = entry.getKey().getValue1().toString();
			String dmgCause = entry.getKey().getValue2().toString();
			int deathCount = entry.getValue();
			double dmgSum = dmgSums.get(entry.getKey());

			sqls.add(
					"insert into fopzldoctor_hurtMonitor (server, world, rank, dmgCause, deathCount, dmgSum) values ('" + server + "', '" + world + "', '"
							+ rank + "', '" + dmgCause + "', " + deathCount + ", " + dmgSum + ");"
			);
		}
		permSaveData(sqls);
		reset();
	}

	private static void reset() {
		deathCounts.clear();
		dmgSums.clear();
	}

	public static void incDeath(String worldName, Rank rank, EntityDamageEvent.DamageCause cause) {
		Triplet<String, Rank, EntityDamageEvent.DamageCause> triple = Triplet.with(worldName, rank, cause);
		deathCounts.put(triple, deathCounts.getOrDefault(triple, 0) + 1);
	}

	public static void addDamage(String worldName, Rank rank, EntityDamageEvent.DamageCause cause, double amt) {
		Triplet<String, Rank, EntityDamageEvent.DamageCause> triple = Triplet.with(worldName, rank, cause);
		dmgSums.put(triple, dmgSums.getOrDefault(triple, 0D) + amt);
	}
}
