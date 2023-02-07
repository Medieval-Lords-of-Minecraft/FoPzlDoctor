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

import me.fopzl.doctor.IOManager;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class VoteMonitor extends Monitor {
	// Key is votesite
	private static Map<String, Integer> votesiteCounts = new HashMap<String, Integer>();
	
	public VoteMonitor(ScheduleInterval i) {
		super(i);
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		
		String server = NeoCore.getInstanceKey();
		for (Entry<String, Integer> entry : votesiteCounts.entrySet()) {
			String votesite = entry.getKey();
			int count = entry.getValue();
			sqls.add("insert into fopzldoctor_voteMonitor (server, votesite, count) values ('" + server + "', '" + votesite + "', " + count + ");");
		}

		permSaveData(sqls);
		reset();
	}

	@Override
	protected void saveData(boolean async) {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(votesiteCounts);
			blobs.put("votesiteCounts", new SerialBlob(bytes.toByteArray()));
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
