package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.fopzl.doctor.Doctor.Rank;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class MoneyMonitor extends Monitor {
	public static Map<Rank, Double> senderSums = new HashMap<Rank, Double>();
	public static Map<Rank, Double> receiverSums = new HashMap<Rank, Double>();

	public MoneyMonitor(ScheduleInterval i) {
		super(i);

		dataFields.add("senderSums");
		dataFields.add("receiverSums");
	}

	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();

		String server = NeoCore.getInstanceKey();
		for (Entry<Rank, Double> entry : senderSums.entrySet()) {
			String rank = entry.getKey().toString();
			double senderSum = entry.getValue();
			
			double receiverSum;
			if (receiverSums.containsKey(entry.getKey())) {
				receiverSum = receiverSums.remove(entry.getKey());
			} else {
				receiverSum = 0;
			}
			
			sqls.add(
					"insert into fopzldoctor_moneyMonitor (server, rank, senderSum, receiverSum) values ('" + server + "', '" + rank + "', " + senderSum + ", "
							+ receiverSum + ");"
			);
		}
		for (Entry<Rank, Double> entry : receiverSums.entrySet()) {
			String rank = entry.getKey().toString();
			double receiverSum = entry.getValue();
			
			sqls.add(
					"insert into fopzldoctor_moneyMonitor (server, rank, senderSum, receiverSum) values ('" + server + "', '" + rank + "', 0, " + receiverSum
							+ ");"
			);
		}

		permSaveData(sqls);
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
