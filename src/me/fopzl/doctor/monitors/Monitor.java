package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.IOManager;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

abstract class Monitor {
	// extending classes should put names of the data fields to save and load in here
	protected Set<String> dataFields = new HashSet<String>();

	public Monitor(ScheduleInterval i) {
		loadData();
		
		Doctor.addToScheduler(i, new Runnable() {
			@Override
			public void run() {
				update();
			}
		});

		Doctor.addToCleanup(new Runnable() {
			@Override
			public void run() {
				saveData(false);
			}
		});
		
		if (i == ScheduleInterval.DAILY) {
			Doctor.addToScheduler(ScheduleInterval.FIFTEEN_MINUTES, new Runnable() {
				@Override
				public void run() {
					saveData(true);
				}
			});
		}
	}
	
	// for polling new data
	abstract protected void update();
	
	// for saving in case of server restart
	protected void saveData(boolean async) {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();
			
			ByteArrayOutputStream bytes;
			ObjectOutputStream os;
			
			for (var field : getClass().getDeclaredFields()) {
				if (!dataFields.contains(field.getName()))
					continue;
				bytes = new ByteArrayOutputStream();
				os = new ObjectOutputStream(bytes);
				os.writeObject(field.get(this));
				blobs.put(field.getName(), new SerialBlob(bytes.toByteArray()));
				os.close();
				bytes.close();
			}
			
			IOManager.saveBlobs(async, getClass().getName(), blobs);
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception saving BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
	
	// for loading saved data
	protected void loadData() {
		try {
			Map<String, Blob> blobs = IOManager.loadBlobs(getClass().getName());
			
			ObjectInputStream is;
			for (Entry<String, Blob> entry : blobs.entrySet()) {
				is = new ObjectInputStream(entry.getValue().getBinaryStream());
				Object obj = is.readObject();
				is.close();
				
				getClass().getDeclaredField(entry.getKey()).set(this, obj);
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	// for permanently saving data in a useful format
	protected void permSaveData(List<String> sqlStatements) {
		new BukkitRunnable() {
			@Override
			public void run() {
				IOManager.writeToSQL(sqlStatements);
			}
		}.runTaskAsynchronously(Doctor.getInstance());
	}
}
