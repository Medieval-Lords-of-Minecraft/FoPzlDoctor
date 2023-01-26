package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageEvent;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class HurtMonitor extends Monitor {
	private static Map<Triplet<World, Rank, EntityDamageEvent.DamageCause>, Integer> deathCounts = new HashMap<Triplet<World, Rank, EntityDamageEvent.DamageCause>, Integer>();
	private static Map<Triplet<World, Rank, EntityDamageEvent.DamageCause>, Double> dmgSums = new HashMap<Triplet<World, Rank, EntityDamageEvent.DamageCause>, Double>();

	public HurtMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		deathCounts.clear();
		dmgSums.clear();
	}

	public static void incDeath(World world, Rank rank, EntityDamageEvent.DamageCause cause) {
		Triplet<World, Rank, EntityDamageEvent.DamageCause> triple = Triplet.with(world, rank, cause);
		deathCounts.put(triple, deathCounts.getOrDefault(triple, 0) + 1);
	}

	public static void addDamage(World world, Rank rank, EntityDamageEvent.DamageCause cause, double amt) {
		Triplet<World, Rank, EntityDamageEvent.DamageCause> triple = Triplet.with(world, rank, cause);
		dmgSums.put(triple, dmgSums.getOrDefault(triple, 0D) + amt);
	}
}
