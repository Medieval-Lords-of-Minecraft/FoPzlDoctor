package me.fopzl.doctor.listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.CreativeCategory;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.monitors.BlockMonitor;

public class BlockListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();

		World world = p.getWorld();
		Rank rank = Doctor.getPlayerRank(p);
		CreativeCategory category = e.getBlock().getType().getCreativeCategory();

		BlockMonitor.incBreak(world, rank, category);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();

		World world = p.getWorld();
		Rank rank = Doctor.getPlayerRank(p);
		CreativeCategory category = e.getBlock().getType().getCreativeCategory();

		BlockMonitor.incPlace(world, rank, category);
	}
}
