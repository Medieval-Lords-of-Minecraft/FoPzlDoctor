package me.fopzl.doctor.monitors;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class TownyStatMonitor extends Monitor {
	private static TownyAPI towny;

	public TownyStatMonitor(ScheduleInterval i) {
		super(i);
		towny = TownyAPI.getInstance();
	}

	@Override
	protected void update() {
		int numTowns = towny.getTowns().size();
		int numNations = towny.getNations().size();

		int townPlots = 0;
		int nationPlots = 0;
		for(Town t : towny.getTowns()) {
			townPlots += t.getNumTownBlocks();
		}
		for(Nation n : towny.getNations()) {
			nationPlots += n.getNumTownblocks();
		}
		// TODO: send counts to sql
	}
}
