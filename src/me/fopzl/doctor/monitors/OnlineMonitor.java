package me.fopzl.doctor.monitors;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.rowset.serial.SerialBlob;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.earth2me.essentials.IEssentials;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.IOManager;
import me.fopzl.doctor.util.tuples.Pair;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class OnlineMonitor extends Monitor {
	private static IEssentials ess;

	private static int newPlayers = 0;

	public OnlineMonitor(ScheduleInterval i) {
		super(i);
		ess = (IEssentials) Bukkit.getPluginManager().getPlugin("Essentials");
	}

	@Override
	protected void update() {
		// value item0 is afk count, item1 is active/non-afk count
		Map<Pair<World, Rank>, Pair<Integer, Integer>> activityCounts = new HashMap<Pair<World, Rank>, Pair<Integer, Integer>>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			World w = p.getWorld();
			Rank r = Rank.getPlayerRank(p);
			boolean afk = ess.getUser(p).isAfk();

			Pair<World, Rank> key = Pair.with(w, r);
			Pair<Integer, Integer> value = activityCounts.getOrDefault(key, Pair.with(0, 0));
			if (afk) {
				value.setAt0(value.getValue0() + 1);
			} else {
				value.setAt1(value.getValue1() + 1);
			}
			activityCounts.put(key, value);
		}

		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		for (Entry<Pair<World, Rank>, Pair<Integer, Integer>> entry : activityCounts.entrySet()) {
			String world = entry.getKey().getValue0().getName();
			String rank = entry.getKey().getValue1().toString();
			int afkCount = entry.getValue().getValue0();
			int activeCount = entry.getValue().getValue1();

			sqls.add(
					"insert into fopzldoctor_onlineMonitor_activity (server, world, rank, afkCount, activeCount) values ('" + server + "', '" + world + "', '"
							+ rank + "', " + afkCount + ", " + activeCount + ");"
			);
		}

		sqls.add("insert into fopzldoctor_onlineMonitor_newPlayers (server, count) values ('" + server + "', " + newPlayers + ");");

		permSaveData(sqls);
		reset();
	}
	
	@Override
	protected void saveData() {
		try {
			Map<String, Blob> blobs = new HashMap<String, Blob>();

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			new ObjectOutputStream(bytes).writeObject(newPlayers);
			blobs.put("newPlayers", new SerialBlob(bytes.toByteArray()));

			bytes.close();

			IOManager.saveBlobs(getClass().getName(), blobs);
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception saving BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	@Override
	protected void loadData() {
		try {
			Map<String, Blob> blobs = IOManager.loadBlobs(getClass().getName());
			if (blobs == null || blobs.isEmpty())
				return;
			
			newPlayers = (Integer) (new ObjectInputStream(blobs.get("newPlayers").getBinaryStream()).readObject());
		} catch (Exception e) {
			Bukkit.getLogger().warning("[DOCTOR] Exception loading BLOBs for " + getClass().getName() + ":");
			e.printStackTrace();
		}
	}

	private static void reset() {
		newPlayers = 0;
	}

	public static void incNewbie() {
		newPlayers++;
	}
}
