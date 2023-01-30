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
import me.fopzl.doctor.util.tuples.Triplet;
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
		Map<Triplet<World, Rank, Boolean>, Integer> activityCounts = new HashMap<Triplet<World, Rank, Boolean>, Integer>();
		for (Player p : Bukkit.getOnlinePlayers()) {
			World w = p.getWorld();
			Rank r = Rank.getPlayerRank(p);
			boolean afk = ess.getUser(p).isAfk();

			Triplet<World, Rank, Boolean> triplet = Triplet.with(w, r, afk);
			activityCounts.put(triplet, activityCounts.getOrDefault(triplet, 0) + 1);
		}

		List<String> sqls = new ArrayList<String>();
		String server = NeoCore.getInstanceKey();
		for (Entry<Triplet<World, Rank, Boolean>, Integer> entry : activityCounts.entrySet()) {
			String world = entry.getKey().getValue0().getName();
			String rank = entry.getKey().getValue1().toString();
			int afk = entry.getKey().getValue2() ? 1 : 0;
			int count = entry.getValue();

			sqls.add(
					"insert into fopzldoctor_onlineMonitor_activity (server, world, rank, afk, count) values ('" + server + "', '" + world + "', '" + rank
							+ "', " + afk + ", " + count + ");"
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
