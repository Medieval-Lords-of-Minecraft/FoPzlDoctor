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

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class ChatMonitor extends Monitor {
	// String in key is chat channel
	private static Map<Pair<Rank, String>, Integer> chatCounts = new HashMap<Pair<Rank, String>, Integer>();
	
	public ChatMonitor(ScheduleInterval i) {
		super(i);
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		
		String server = NeoCore.getInstanceKey();
		for (Entry<Pair<Rank, String>, Integer> entry : chatCounts.entrySet()) {
			String channel = entry.getKey().getValue1();
			String rank = entry.getKey().getValue0().toString();
			String count = entry.getValue().toString();
			
			sqls.add(
					"insert into fopzldoctor_chatMonitor (server, channel, rank, count) values ('" + server + "', '" + channel + "', '" + rank + "', " + count
							+ ");"
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
			new ObjectOutputStream(bytes).writeObject(chatCounts);
			blobs.put("chatCounts", new SerialBlob(bytes.toByteArray()));
			
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
			chatCounts = (Map<Pair<Rank, String>, Integer>) (new ObjectInputStream(blobs.get("chatCounts").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	private static void reset() {
		chatCounts.clear();
	}
	
	public static void inc(Rank rank, String channel) {
		Pair<Rank, String> pair = Pair.with(rank, channel);
		chatCounts.put(pair, chatCounts.getOrDefault(pair, 0) + 1);
	}
}
