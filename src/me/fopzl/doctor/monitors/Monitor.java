package me.fopzl.doctor.monitors;

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;

import me.fopzl.doctor.IOManager;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;

abstract class Monitor {
	public Monitor(ScheduleInterval i) {
		loadData();

		SchedulerAPI.scheduleRepeating("FoPzlDoctor-" + getClass().getName() + "-" + NeoCore.getInstanceKey() + "-" + i.toString(), i, new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
		
		if (i == ScheduleInterval.DAILY) {
			SchedulerAPI.scheduleRepeating(
					"FoPzlDoctor-" + getClass().getName() + "-" + NeoCore.getInstanceKey() + "-Autosave", ScheduleInterval.FIFTEEN_MINUTES, new Runnable() {
						@Override
						public void run() {
							saveData();
						}
					}
			);
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
		try {
			IOManager.writeToSQL(sqlStatements);
		} catch (SQLException e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception writing SQL for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}
}
