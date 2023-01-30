package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class EntityMonitor extends Monitor {
	// String in key is world name
	private static Map<Pair<String, EntityType>, Integer> newEntityCounts = new HashMap<Pair<String, EntityType>, Integer>();

	public EntityMonitor(ScheduleInterval i) {
		super(i);
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
	
	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(newEntityCounts);
			blobs.put("newEntityCounts", new SerialBlob(bytes.toByteArray()));
			
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
			if (blobs == null || blobs.isEmpty())
				return;

			newEntityCounts = (Map<Pair<String, EntityType>, Integer>) (new ObjectInputStream(blobs.get("newEntityCounts").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		newEntityCounts.clear();
	}

	public static void inc(String worldName, EntityType type) {
		Pair<String, EntityType> pair = Pair.with(worldName, type);
		newEntityCounts.put(pair, newEntityCounts.getOrDefault(pair, 0) + 1);
	}
}
