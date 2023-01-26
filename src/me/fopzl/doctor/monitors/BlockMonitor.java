package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.inventory.CreativeCategory;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class BlockMonitor extends Monitor {
	private static Map<Triplet<World, Rank, CreativeCategory>, Integer> breakCounts = new HashMap<Triplet<World, Doctor.Rank, CreativeCategory>, Integer>();
	private static Map<Triplet<World, Rank, CreativeCategory>, Integer> placeCounts = new HashMap<Triplet<World, Doctor.Rank, CreativeCategory>, Integer>();

	public BlockMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// TODO: send counts to sql (e.g. BlockIO) (round timestamp down to nearest 15m market)
		reset();
	}

	private static void reset() {
		breakCounts.clear();
		placeCounts.clear();
	}

	public static void incBreak(World world, Rank rank, CreativeCategory cat) {
		Triplet<World, Rank, CreativeCategory> triplet = Triplet.with(world, rank, cat);
		breakCounts.put(triplet, breakCounts.getOrDefault(triplet, 0) + 1);
	}

	public static void incPlace(World world, Rank rank, CreativeCategory cat) {
		Triplet<World, Rank, CreativeCategory> triplet = Triplet.with(world, rank, cat);
		placeCounts.put(triplet, placeCounts.getOrDefault(triplet, 0) + 1);
	}
}
