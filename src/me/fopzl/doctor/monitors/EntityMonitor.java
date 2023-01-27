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
import org.bukkit.entity.EntityType;

import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class EntityMonitor extends Monitor {
	private static Set<Pair<World, EntityType>> newEntities = new HashSet<Pair<World, EntityType>>();

	public EntityMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		for (World w : Bukkit.getWorlds()) {
			int numEntities = w.getEntities().size();
		}
		// TODO: send counts to sql
		reset();
	}
	
	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(newEntities);
			blobs.put("newEntities", new SerialBlob(bytes.toByteArray()));
			
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
			newEntities = (Set<Pair<World, EntityType>>) (new ObjectInputStream(blobs.get("newEntities").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		newEntities.clear();
	}

	public static void inc(World world, EntityType type) {
		Pair<World, EntityType> pair = Pair.with(world, type);
		newEntities.add(pair);
	}
}
