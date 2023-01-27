package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;

import me.fopzl.doctor.IOManager;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class VoteMonitor extends Monitor {
	// Key is votesite
	private static Map<String, Integer> votesiteCounts = new HashMap<String, Integer>();
	
	public VoteMonitor(ScheduleInterval i) {
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
			new ObjectOutputStream(bytes).writeObject(votesiteCounts);
			blobs.put("votesiteCounts", new SerialBlob(bytes.toByteArray()));
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
			votesiteCounts = (Map<String, Integer>) (new ObjectInputStream(blobs.get("votesiteCounts").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	private static void reset() {
		votesiteCounts.clear();
	}
	
	public static void inc(String votesite) {
		votesiteCounts.put(votesite, votesiteCounts.getOrDefault(votesite, 0) + 1);
	}
}
