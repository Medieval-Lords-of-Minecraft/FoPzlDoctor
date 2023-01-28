package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class ChunkMonitor extends Monitor {
	// String key is world name, Pair in value is a chunk coordinate
	private static Map<String, Set<Pair<Integer, Integer>>> uniqueChunks = new HashMap<String, Set<Pair<Integer, Integer>>>();
	
	public ChunkMonitor(ScheduleInterval i) {
		super(i);
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		for (World w : Bukkit.getWorlds()) {
			String world = w.getName();
			int totalCount = w.getLoadedChunks().length;

			int uniqueCount;
			if (uniqueChunks.containsKey(world)) {
				uniqueCount = uniqueChunks.remove(world).size();
			} else {
				uniqueCount = 0;
			}
			
			sqls.add(
					"insert into fopzldoctor_chunkMonitor (server, world, uniqueCount, totalCount) values ('" + server + "', '" + world + "', " + uniqueCount
							+ ", " + totalCount + ");"
			);
		}
		permSaveData(sqls);
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
			uniqueChunks = (Map<String, Set<Pair<Integer, Integer>>>) (new ObjectInputStream(blobs.get("uniqueChunks").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	private static void reset() {
		uniqueChunks.clear();
	}
	
	public static void tryInc(String w, int chunkX, int chunkZ) {
		Pair<Integer, Integer> pair = Pair.with(chunkX, chunkZ);
		Set<Pair<Integer, Integer>> wChunks = uniqueChunks.getOrDefault(w, new HashSet<Pair<Integer, Integer>>());
		wChunks.add(pair);
		uniqueChunks.putIfAbsent(w, wChunks);
	}
}
