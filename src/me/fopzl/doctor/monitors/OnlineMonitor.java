package me.fopzl.doctor.monitors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class OnlineMonitor extends Monitor {
	private static Essentials ess;

	private static int newPlayers = 0;

	public OnlineMonitor(ScheduleInterval i) {
		super(i);
		ess = Bukkit.getServicesManager().getRegistration(Essentials.class).getProvider();
	}

	@Override
	protected void update() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			World w = p.getWorld();
			Rank r = Doctor.getPlayerRank(p);
			boolean afk = ess.getUser(p).isAfk();
		}
		// TODO: send counts to sql
		reset();
	}

	private static void reset() {
		newPlayers = 0;
	}

	public static void incNewbie() {
		newPlayers++;
	}
}
