package me.fopzl.doctor.monitors;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class EntityMonitor extends Monitor {
	private static  Set<Pair<World, EntityType>> newEntities = new HashSet<Pair<World, EntityType>>();

	public EntityMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		for(World w : Bukkit.getWorlds()) {
			int numEntities = w.getEntities().size();
		}
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		newEntities.clear();
	}

	public static void inc(World world, EntityType type) {
		Pair<World, EntityType> pair = Pair.with(world, type);
		newEntities.add(pair);
	}
}
