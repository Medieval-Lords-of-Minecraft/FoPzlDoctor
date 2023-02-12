package me.fopzl.doctor.monitors;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class LoginMonitor extends Monitor {
	public static Set<UUID> uniquePlayersDay = new HashSet<UUID>();
	public static Set<UUID> uniquePlayersWeek = new HashSet<UUID>();
	public static Set<UUID> uniquePlayersMonth = new HashSet<UUID>();

	// value item0 is newbie count, item1 is regular player count
	// (newbies are identified by having joined within the past month)
	public static Map<Rank, Pair<Integer, Integer>> uniqueLoginCountsDay = new HashMap<Rank, Pair<Integer, Integer>>();
	public static Map<Rank, Pair<Integer, Integer>> uniqueLoginCountsWeek = new HashMap<Rank, Pair<Integer, Integer>>();
	public static Map<Rank, Pair<Integer, Integer>> uniqueLoginCountsMonth = new HashMap<Rank, Pair<Integer, Integer>>();

	public LoginMonitor(ScheduleInterval i) {
		super(i);
		assert i == ScheduleInterval.DAILY; // :)
		
		dataFields.add("uniquePlayersDay");
		dataFields.add("uniquePlayersWeek");
		dataFields.add("uniquePlayersMonth");
		dataFields.add("uniqueLoginCountsDay");
		dataFields.add("uniqueLoginCountsWeek");
		dataFields.add("uniqueLoginCountsMonth");
	}

	// temp debug
	@Override
	protected void saveData(boolean async) {
		Bukkit.getLogger().info("[FoPzlDoctor] Saving loginMonitor data (uniqueDay size=" + uniquePlayersDay.size() + ")");

		super.saveData(async);
	}

	// temp debug
	@Override
	protected void loadData() {
		super.loadData();
		
		Bukkit.getLogger().info("[FoPzlDoctor] Loaded loginMonitor data (uniqueDay size=" + uniquePlayersDay.size() + ")");
	}

	@Override
	protected void update() {
		LocalDateTime now = LocalDateTime.now();

		updateDay();
		if (now.getDayOfWeek() == DayOfWeek.SUNDAY)
			updateWeek();
		if (now.getDayOfMonth() == 1)
			updateMonth();
	}

	private void updateDay() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		String period = "daily";
		for (Entry<Rank, Pair<Integer, Integer>> entry : uniqueLoginCountsDay.entrySet()) {
			String rank = entry.getKey().toString();
			int noobCount = entry.getValue().getValue0();
			int regularCount = entry.getValue().getValue1();

			sqls.add(
					"insert into fopzldoctor_loginMonitor (server, period, rank, noobCount, regularCount) values ('" + server + "', '" + period + "', '" + rank
							+ "', " + noobCount + ", " + regularCount + ");"
			);
		}
		permSaveData(sqls);
		resetDay();
	}

	private void updateWeek() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		String period = "weekly";
		for (Entry<Rank, Pair<Integer, Integer>> entry : uniqueLoginCountsWeek.entrySet()) {
			String rank = entry.getKey().toString();
			int noobCount = entry.getValue().getValue0();
			int regularCount = entry.getValue().getValue1();

			sqls.add(
					"insert into fopzldoctor_loginMonitor (server, period, rank, noobCount, regularCount) values ('" + server + "', '" + period + "', '" + rank
							+ "', " + noobCount + ", " + regularCount + ");"
			);
		}
		permSaveData(sqls);
		resetWeek();
	}

	private void updateMonth() {
		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		String period = "monthly";
		for (Entry<Rank, Pair<Integer, Integer>> entry : uniqueLoginCountsMonth.entrySet()) {
			String rank = entry.getKey().toString();
			int noobCount = entry.getValue().getValue0();
			int regularCount = entry.getValue().getValue1();

			sqls.add(
					"insert into fopzldoctor_loginMonitor (server, period, rank, noobCount, regularCount) values ('" + server + "', '" + period + "', '" + rank
							+ "', " + noobCount + ", " + regularCount + ");"
			);
		}
		permSaveData(sqls);
		resetMonth();
	}

	private static void resetDay() {
		uniquePlayersDay.clear();
		uniqueLoginCountsDay.clear();
	}

	private static void resetWeek() {
		uniquePlayersDay.clear();
		uniqueLoginCountsDay.clear();
	}

	private static void resetMonth() {
		uniquePlayersDay.clear();
		uniqueLoginCountsDay.clear();
	}

	public static void tryIncPlayer(UUID uuid) {
		tryIncPlayer(uuid, uniquePlayersDay, uniqueLoginCountsDay);
		tryIncPlayer(uuid, uniquePlayersWeek, uniqueLoginCountsWeek);
		tryIncPlayer(uuid, uniquePlayersMonth, uniqueLoginCountsMonth);
	}

	private static void tryIncPlayer(UUID uuid, Set<UUID> uniquePlayers, Map<Rank, Pair<Integer, Integer>> uniqueLoginCounts) {
		if (uniquePlayers.contains(uuid))
			return;

		Player p = Bukkit.getPlayer(uuid);
		Rank rank = Rank.getPlayerRank(p);

		LocalDateTime firstPlayed = LocalDateTime.ofEpochSecond(p.getFirstPlayed(), 0, ZoneOffset.UTC);
		boolean noob = firstPlayed.isAfter(LocalDateTime.now().minusMonths(1));

		Pair<Integer, Integer> pair = uniqueLoginCounts.getOrDefault(rank, Pair.with(0, 0));
		if (noob) {
			pair.setAt0(pair.getValue0() + 1);
		} else {
			pair.setAt1(pair.getValue1() + 1);
		}
		uniqueLoginCounts.put(rank, pair);
	}
}
