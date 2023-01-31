package me.fopzl.doctor.monitors;

import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.IOManager;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

abstract class Monitor {
	public Monitor(ScheduleInterval i) {
		loadData();
		
		Doctor.addToScheduler(i, new BukkitRunnable() {
			@Override
			public void run() {
				update();
			}
		});
		
		BukkitRunnable save = new BukkitRunnable() {
			@Override
			public void run() {
				saveData();
			}
		};

		Doctor.addToCleanup(save);
		if (i == ScheduleInterval.DAILY) {
			Doctor.addToScheduler(ScheduleInterval.FIFTEEN_MINUTES, save);
		}
	}
	
	// for polling new data
	abstract protected void update();
	
	// for saving in case of server restart
	abstract protected void saveData();
	
	// for loading saved data
	abstract protected void loadData();

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
