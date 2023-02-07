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
import org.bukkit.event.entity.EntityDamageEvent;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class HurtMonitor extends Monitor {
	// String in key is world name
	private static Map<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Integer> deathCounts = new HashMap<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Integer>();
	private static Map<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Double> dmgSums = new HashMap<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Double>();

	public HurtMonitor(ScheduleInterval i) {
		super(i);
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
	
	@Override
	protected void saveData(boolean async) {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(deathCounts);
			blobs.put("deathCounts", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(dmgSums);
			blobs.put("dmgSums", new SerialBlob(bytes.toByteArray()));

			bytes.close();

			IOManager.saveBlobs(async, getClass().getName(), blobs);
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
			
			deathCounts = (Map<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Integer>) (new ObjectInputStream(
					blobs.get("deathCounts").getBinaryStream()
			).readObject());
			dmgSums = (Map<Triplet<String, Rank, EntityDamageEvent.DamageCause>, Double>) (new ObjectInputStream(blobs.get("dmgSums").getBinaryStream())
					.readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
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
