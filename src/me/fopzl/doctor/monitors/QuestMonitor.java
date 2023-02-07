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

import me.fopzl.doctor.Doctor.QuestLevelGroup;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Triplet;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class QuestMonitor extends Monitor {
	private static Map<Rank, Integer> levelupCounts = new HashMap<Rank, Integer>();
	private static Map<Rank, Integer> questCompleteCounts = new HashMap<Rank, Integer>();
	// Key is <Boss name, Group size, Max questLevelGroup of group>
	private static Map<Triplet<String, Integer, QuestLevelGroup>, Integer> bossStartCounts = new HashMap<Triplet<String, Integer, QuestLevelGroup>, Integer>();
	
	public QuestMonitor(ScheduleInterval i) {
		super(i);
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();

		String server = NeoCore.getInstanceKey();
		for (Entry<Rank, Integer> entry : levelupCounts.entrySet()) {
			String rank = entry.getKey().toString();
			int levelUpCount = entry.getValue();
			
			int questCompleteCount;
			if (questCompleteCounts.containsKey(entry.getKey())) {
				questCompleteCount = questCompleteCounts.remove(entry.getKey());
			} else {
				questCompleteCount = 0;
			}
			
			sqls.add(
					"insert into fopzldoctor_questMonitor_general (server, rank, levelUpCount, questCompleteCount) values ('" + server + "', '" + rank + "', "
							+ levelUpCount + ", " + questCompleteCount + ");"
			);
		}
		for (Entry<Rank, Integer> entry : questCompleteCounts.entrySet()) {
			String rank = entry.getKey().toString();
			int questCompleteCount = entry.getValue();
			
			sqls.add(
					"insert into fopzldoctor_questMonitor_general (server, rank, levelUpCount, questCompleteCount) values ('" + server + "', '" + rank
							+ "', 0, " + questCompleteCount + ");"
			);
		}

		for (Entry<Triplet<String, Integer, QuestLevelGroup>, Integer> entry : bossStartCounts.entrySet()) {
			String bossName = entry.getKey().getValue0();
			int groupSize = entry.getKey().getValue1();
			String maxLvlGroup = entry.getKey().getValue2().toString();
			int count = entry.getValue();

			sqls.add(
					"insert into fopzldoctor_questMonitor_boss (server, bossName, groupSize, maxLvlGroup, count) values ('" + server + "', '" + bossName + "', "
							+ groupSize + ", '" + maxLvlGroup + "', " + count + ");"
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
			new ObjectOutputStream(bytes).writeObject(levelupCounts);
			blobs.put("levelupCounts", new SerialBlob(bytes.toByteArray()));
			
			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(questCompleteCounts);
			blobs.put("questCompleteCounts", new SerialBlob(bytes.toByteArray()));
			
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
	
	public static void incBossStart(String bossName, int groupSize, QuestLevelGroup maxLvlGroup) {
		Triplet<String, Integer, QuestLevelGroup> triple = Triplet.with(bossName, groupSize, maxLvlGroup);
		bossStartCounts.put(triple, bossStartCounts.getOrDefault(triple, 0) + 1);
	}
}
