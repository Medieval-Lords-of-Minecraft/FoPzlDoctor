package me.fopzl.doctor.monitors;

import java.util.ArrayList;
import java.util.List;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;

import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;

public class TownyStatMonitor extends Monitor {
	private static TownyAPI towny;
	
	public TownyStatMonitor(ScheduleInterval i) {
		super(i);
		towny = TownyAPI.getInstance();
	}
	
	@Override
	protected void update() {
		List<String> sqls = new ArrayList<String>();
		
		int numTowns = towny.getTowns().size();
		int numNations = towny.getNations().size();
		
		int townPlots = 0;
		int nationPlots = 0;
		for (Town t : towny.getTowns()) {
			townPlots += t.getNumTownBlocks();
		}
		for (Nation n : towny.getNations()) {
			nationPlots += n.getNumTownblocks();
		}
		
		String server = NeoCore.getInstanceKey();
		sqls.add(
				"insert into fopzldoctor_townyStatMonitor (server, numTowns, numNations, townPlots, nationPlots) values ('" + server + "', " + numTowns + ", "
						+ numNations + ", " + townPlots + ", " + nationPlots + ");"
		);
		permSaveData(sqls);
	}

	@Override
	protected void saveData(boolean async) {
	}
	
	@Override
	protected void loadData() {
	}
}
