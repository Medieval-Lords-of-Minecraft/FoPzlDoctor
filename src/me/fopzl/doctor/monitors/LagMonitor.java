package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.List;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow.CpuUsage;
import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class LagMonitor extends Monitor {
	private static Spark spark;

	public LagMonitor(ScheduleInterval i) {
		super(i);
		assert i == ScheduleInterval.FIFTEEN_MINUTES; // thanks spark
		spark = SparkProvider.get();
	}

	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		double cpu = spark.cpuProcess().poll(CpuUsage.MINUTES_15); // because spark
		double tps = spark.tps().poll(TicksPerSecond.MINUTES_15); // same

		sqls.add("insert into fopzldoctor_lagMonitor (server, avgCpu, avgTps) values ('" + server + "', " + cpu + ", " + tps + ");");

		permSaveData(sqls);
	}
	
	@Override
	protected void saveData() {
	}

	@Override
	protected void loadData() {
	}
}
