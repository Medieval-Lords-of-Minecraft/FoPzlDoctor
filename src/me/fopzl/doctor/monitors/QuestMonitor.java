package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class QuestMonitor extends Monitor {
	private static Map<Rank, Integer> levelupCounts = new HashMap<Rank, Integer>();
	private static Map<Rank, Integer> questCompleteCounts = new HashMap<Rank, Integer>();
	// Key is <Boss name, Group size, Max lvl of group>
	private static Map<Triplet<String, Integer, Integer>, Integer> bossStartCounts = new HashMap<Triplet<String, Integer, Integer>, Integer>();

	public QuestMonitor(ScheduleInterval i) {
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
			new ObjectOutputStream(bytes).writeObject(levelupCounts);
			blobs.put("levelupCounts", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(questCompleteCounts);
			blobs.put("questCompleteCounts", new SerialBlob(bytes.toByteArray()));

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
			levelupCounts = (Map<Rank, Integer>) (new ObjectInputStream(blobs.get("levelupCounts").getBinaryStream()).readObject());
			questCompleteCounts = (Map<Rank, Integer>) (new ObjectInputStream(blobs.get("questCompleteCounts").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		levelupCounts.clear();
		questCompleteCounts.clear();
		bossStartCounts.clear();
	}

	public static void incLevelup(Rank rank) {
		levelupCounts.put(rank, levelupCounts.getOrDefault(rank, 0) + 1);
	}

	public static void incQuestComplete(Rank rank) {
		questCompleteCounts.put(rank, questCompleteCounts.getOrDefault(rank, 0) + 1);
	}

	public static void incBossStart(String bossName, int groupSize, int maxLvlInGroup) {
		Triplet<String, Integer, Integer> triple = Triplet.with(bossName, groupSize, maxLvlInGroup);
		bossStartCounts.put(triple, bossStartCounts.getOrDefault(triple, 0) + 1);
	}
}
