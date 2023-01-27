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
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class OnlineMonitor extends Monitor {
	private static Essentials ess;
	
	private static int newPlayers = 0;
	
	public OnlineMonitor(ScheduleInterval i) {
		super(i);
		ess = Bukkit.getServicesManager().getRegistration(Essentials.class).getProvider();
	}
	
	@Override
	protected void update() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			World w = p.getWorld();
			Rank r = Doctor.getPlayerRank(p);
			boolean afk = ess.getUser(p).isAfk();
		}
		// TODO: send counts to sql
		reset();
	}

	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();
			
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(newPlayers);
			blobs.put("newPlayers", new SerialBlob(bytes.toByteArray()));
			
			bytes.close();
			
			IOManager.saveBlobs(getClass().getName(), blobs);
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception saving BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	@Override
	protected void loadData() {
		try {
			Map<String, Blob> blobs = IOManager.loadBlobs(getClass().getName());
			newPlayers = (Integer) (new ObjectInputStream(blobs.get("newPlayers").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	private static void reset() {
		newPlayers = 0;
	}
	
	public static void incNewbie() {
		newPlayers++;
	}
}
