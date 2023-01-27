package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class LoginMonitor extends Monitor {
	private static Set<UUID> uniquePlayersDay = new HashSet<UUID>();
	private static Set<UUID> uniquePlayersWeek = new HashSet<UUID>();
	private static Set<UUID> uniquePlayersMonth = new HashSet<UUID>();
	
	// Boolean in key indicates if player is new this month (to this server)
	private static Map<Pair<Boolean, Rank>, Integer> uniqueLoginCountsDay = new HashMap<Pair<Boolean, Rank>, Integer>();
	private static Map<Pair<Boolean, Rank>, Integer> uniqueLoginCountsWeek = new HashMap<Pair<Boolean, Rank>, Integer>();
	private static Map<Pair<Boolean, Rank>, Integer> uniqueLoginCountsMonth = new HashMap<Pair<Boolean, Rank>, Integer>();
	
	public LoginMonitor(ScheduleInterval i) {
		super(i);
		assert i == ScheduleInterval.DAILY; // :)
	}
	
	@Override
	protected void update() {
		LocalDateTime now = LocalDateTime.now();
		
		updateDay();
		if (now.getDayOfWeek() == DayOfWeek.SUNDAY)
			updateWeek();
		if (now.getDayOfMonth() == 1)
			updateMonth();
	}

	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(uniquePlayersDay);
			blobs.put("uniquePlayersDay", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(uniquePlayersWeek);
			blobs.put("uniquePlayersWeek", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(uniquePlayersMonth);
			blobs.put("uniquePlayersMonth", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(uniqueLoginCountsDay);
			blobs.put("uniqueLoginCountsDay", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(uniqueLoginCountsWeek);
			blobs.put("uniqueLoginCountsWeek", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(uniqueLoginCountsMonth);
			blobs.put("uniqueLoginCountsMonth", new SerialBlob(bytes.toByteArray()));

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
			uniquePlayersDay = (Set<UUID>) (new ObjectInputStream(blobs.get("uniquePlayersDay").getBinaryStream()).readObject());
			uniquePlayersWeek = (Set<UUID>) (new ObjectInputStream(blobs.get("uniquePlayersWeek").getBinaryStream()).readObject());
			uniquePlayersMonth = (Set<UUID>) (new ObjectInputStream(blobs.get("uniquePlayersMonth").getBinaryStream()).readObject());
			uniqueLoginCountsDay = (Map<Pair<Boolean, Rank>, Integer>) (new ObjectInputStream(blobs.get("uniqueLoginCountsDay").getBinaryStream())
					.readObject());
			uniqueLoginCountsWeek = (Map<Pair<Boolean, Rank>, Integer>) (new ObjectInputStream(blobs.get("uniqueLoginCountsWeek").getBinaryStream())
					.readObject());
			uniqueLoginCountsMonth = (Map<Pair<Boolean, Rank>, Integer>) (new ObjectInputStream(blobs.get("uniqueLoginCountsMonth").getBinaryStream())
					.readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	private void updateDay() {
		
		// TODO: send counts to sql
		resetDay();
	}
	
	private void updateWeek() {
		
		// TODO: send counts to sql
		resetWeek();
	}
	
	private void updateMonth() {
		
		// TODO: send counts to sql
		resetMonth();
	}
	
	private static void resetDay() {
		uniquePlayersDay.clear();
		uniqueLoginCountsDay.clear();
	}
	
	private static void resetWeek() {
		uniquePlayersDay.clear();
		uniqueLoginCountsDay.clear();
	}
	
	private static void resetMonth() {
		uniquePlayersDay.clear();
		uniqueLoginCountsDay.clear();
	}
	
	public static void tryIncPlayer(UUID uuid) {
		tryIncPlayer(uuid, uniquePlayersDay, uniqueLoginCountsDay);
		tryIncPlayer(uuid, uniquePlayersWeek, uniqueLoginCountsWeek);
		tryIncPlayer(uuid, uniquePlayersMonth, uniqueLoginCountsMonth);
	}
	
	private static void tryIncPlayer(UUID uuid, Set<UUID> uniquePlayers, Map<Pair<Boolean, Rank>, Integer> uniqueLoginCounts) {
		if (uniquePlayers.contains(uuid))
			return;
		
		Player p = Bukkit.getPlayer(uuid);
		Rank rank = Doctor.getPlayerRank(p);
		
		LocalDateTime firstPlayed = LocalDateTime.ofEpochSecond(p.getFirstPlayed(), 0, ZoneOffset.UTC);
		boolean noob = firstPlayed.isAfter(LocalDateTime.now().minusMonths(1));
		
		Pair<Boolean, Rank> pair = Pair.with(noob, rank);
		uniqueLoginCounts.put(pair, uniqueLoginCounts.getOrDefault(pair, 0) + 1);
	}
}
