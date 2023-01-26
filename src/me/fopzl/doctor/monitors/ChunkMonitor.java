package me.fopzl.doctor.monitors;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class ChunkMonitor extends Monitor {
	// Key is a chunk coordinate
	private static Set<Pair<Integer, Integer>> uniqueChunks = new HashSet<Pair<Integer, Integer>>();

	public ChunkMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		for(World w : Bukkit.getWorlds()) {
			int numChunks = w.getLoadedChunks().length;
		}
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		uniqueChunks.clear();
	}

	public static void tryInc(World w, int chunkX, int chunkZ) {
		Pair<Integer, Integer> pair = Pair.with(chunkX, chunkZ);
		uniqueChunks.add(pair);
	}
}
