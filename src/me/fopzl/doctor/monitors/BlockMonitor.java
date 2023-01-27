package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.inventory.CreativeCategory;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class BlockMonitor extends Monitor {
	// String in key is world name
	private static Map<Triplet<String, Rank, CreativeCategory>, Integer> breakCounts = new HashMap<Triplet<String, Doctor.Rank, CreativeCategory>, Integer>();
	private static Map<Triplet<String, Rank, CreativeCategory>, Integer> placeCounts = new HashMap<Triplet<String, Doctor.Rank, CreativeCategory>, Integer>();

	public BlockMonitor(ScheduleInterval i) {
		super(i);
	}

	@Override
	protected void update() {
		// TODO: send counts to sql (e.g. BlockIO) (round timestamp down to nearest 15m market)
		reset();
	}
	
	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(breakCounts);
			blobs.put("breakCounts", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(placeCounts);
			blobs.put("placeCounts", new SerialBlob(bytes.toByteArray()));

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
			breakCounts = (Map<Triplet<String, Rank, CreativeCategory>, Integer>) (new ObjectInputStream(blobs.get("breakCounts").getBinaryStream())
					.readObject());
			placeCounts = (Map<Triplet<String, Rank, CreativeCategory>, Integer>) (new ObjectInputStream(blobs.get("placeCounts").getBinaryStream())
					.readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		breakCounts.clear();
		placeCounts.clear();
	}

	public static void incBreak(String worldName, Rank rank, CreativeCategory cat) {
		Triplet<String, Rank, CreativeCategory> triplet = Triplet.with(worldName, rank, cat);
		breakCounts.put(triplet, breakCounts.getOrDefault(triplet, 0) + 1);
	}

	public static void incPlace(String worldName, Rank rank, CreativeCategory cat) {
		Triplet<String, Rank, CreativeCategory> triplet = Triplet.with(worldName, rank, cat);
		placeCounts.put(triplet, placeCounts.getOrDefault(triplet, 0) + 1);
	}
}
