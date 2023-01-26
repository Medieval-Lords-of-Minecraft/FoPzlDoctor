package me.fopzl.doctor.monitors;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import me.neoblade298.neocore.bukkit.scheduler.SchedulerAPI;

abstract class Monitor {
	public Monitor(ScheduleInterval i) {
		SchedulerAPI.scheduleRepeating(getClass().getName() + "-" + NeoCore.getInstanceKey() + "-" + i.toString(), i, new Runnable() {
			@Override
			public void run() { update(); }}
				);
		// TODO: if i == daily, also schedule a separate 15m autosave to sql
	}

	abstract protected void update();
}
