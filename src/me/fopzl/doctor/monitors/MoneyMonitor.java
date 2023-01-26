package me.fopzl.doctor.monitors;

import java.util.HashMap;
import java.util.Map;

import me.fopzl.doctor.Doctor.Rank;
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
