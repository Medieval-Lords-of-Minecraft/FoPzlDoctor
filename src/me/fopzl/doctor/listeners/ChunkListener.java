package me.fopzl.doctor.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChunkLoad(ChunkLoadEvent e) {
		World w = e.getWorld();
		int chunkX = e.getChunk().getX();
		int chunkZ = e.getChunk().getZ();
		
		// TODO
	}
}
