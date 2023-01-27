package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.entity.EntityDamageEvent;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
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
	
	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(deathCounts);
			blobs.put("deathCounts", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(dmgSums);
			blobs.put("dmgSums", new SerialBlob(bytes.toByteArray()));

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
			deathCounts = (Map<Triplet<World, Rank, EntityDamageEvent.DamageCause>, Integer>) (new ObjectInputStream(blobs.get("deathCounts").getBinaryStream())
					.readObject());
			dmgSums = (Map<Triplet<World, Rank, EntityDamageEvent.DamageCause>, Double>) (new ObjectInputStream(blobs.get("dmgSums").getBinaryStream())
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

	public static void incDeath(World world, Rank rank, EntityDamageEvent.DamageCause cause) {
		Triplet<World, Rank, EntityDamageEvent.DamageCause> triple = Triplet.with(world, rank, cause);
		deathCounts.put(triple, deathCounts.getOrDefault(triple, 0) + 1);
	}

	public static void addDamage(World world, Rank rank, EntityDamageEvent.DamageCause cause, double amt) {
		Triplet<World, Rank, EntityDamageEvent.DamageCause> triple = Triplet.with(world, rank, cause);
		dmgSums.put(triple, dmgSums.getOrDefault(triple, 0D) + amt);
	}
}
