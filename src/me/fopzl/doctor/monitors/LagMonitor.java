package me.fopzl.doctor.monitors;

import org.bukkit.Bukkit;
import org.bukkit.World;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow.CpuUsage;
import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond;
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
		for(World w : Bukkit.getWorlds()) {
			double cpu = spark.cpuProcess().poll(CpuUsage.MINUTES_15); // because spark
			double tps = spark.tps().poll(TicksPerSecond.MINUTES_15); // same
		}
		// TODO: send counts to sql
	}
}
