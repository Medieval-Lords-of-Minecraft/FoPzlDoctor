package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.fopzl.doctor.IOManager;
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
		for (World w : Bukkit.getWorlds()) {
			int numChunks = w.getLoadedChunks().length;
		}
		// TODO: send counts to sql
		reset();
	}
	
	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(uniqueChunks);
			blobs.put("uniqueChunks", new SerialBlob(bytes.toByteArray()));

			bytes.close();

			IOManager.saveBlobs(getClass().getName(), blobs);
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception saving BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void loadData() {
		try {
			Map<String, Blob> blobs = IOManager.loadBlobs(getClass().getName());
			uniqueChunks = (Set<Pair<Integer, Integer>>) (new ObjectInputStream(blobs.get("uniqueChunks").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		uniqueChunks.clear();
	}

	public static void tryInc(World w, int chunkX, int chunkZ) {
		Pair<Integer, Integer> pair = Pair.with(chunkX, chunkZ);
		uniqueChunks.add(pair);
	}
}
