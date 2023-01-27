package me.fopzl.doctor.monitors;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;

abstract class Monitor {
	public Monitor(ScheduleInterval i) {
		loadData();

		SchedulerAPI.scheduleRepeating(getClass().getName() + "-" + NeoCore.getInstanceKey() + "-" + i.toString(), i, new Runnable() {
			@Override
			public void run() {
				update();
			}
		});
		
		if (i == ScheduleInterval.DAILY) {
			SchedulerAPI
					.scheduleRepeating(getClass().getName() + "-" + NeoCore.getInstanceKey() + "-Autosave", ScheduleInterval.FIFTEEN_MINUTES, new Runnable() {
						@Override
						public void run() {
							saveData();
						}
					});
		}
	}
	
	abstract protected void update();
	
	abstract protected void saveData();
	
	abstract protected void loadData();
}
