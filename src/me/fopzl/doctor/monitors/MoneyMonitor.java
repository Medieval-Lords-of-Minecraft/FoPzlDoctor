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
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class MoneyMonitor extends Monitor {
	private static Map<Rank, Double> senderSums = new HashMap<Rank, Double>();
	private static Map<Rank, Double> receiverSums = new HashMap<Rank, Double>();

	public MoneyMonitor(ScheduleInterval i) {
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
			new ObjectOutputStream(bytes).writeObject(senderSums);
			blobs.put("senderSums", new SerialBlob(bytes.toByteArray()));

			bytes.flush();
			new ObjectOutputStream(bytes).writeObject(receiverSums);
			blobs.put("receiverSums", new SerialBlob(bytes.toByteArray()));

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
			senderSums = (Map<Rank, Double>) (new ObjectInputStream(blobs.get("senderSums").getBinaryStream()).readObject());
			receiverSums = (Map<Rank, Double>) (new ObjectInputStream(blobs.get("receiverSums").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		senderSums.clear();
		receiverSums.clear();
	}

	public static void addSender(Rank rank, double amt) {
		senderSums.put(rank, senderSums.getOrDefault(rank, 0D) + amt);
	}

	public static void addReceiver(Rank rank, double amt) {
		receiverSums.put(rank, receiverSums.getOrDefault(rank, 0D) + amt);
	}
}
