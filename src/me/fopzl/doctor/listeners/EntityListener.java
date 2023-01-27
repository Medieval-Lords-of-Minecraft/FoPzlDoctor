package me.fopzl.doctor.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import me.fopzl.doctor.monitors.EntityMonitor;

public class EntityListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntitySpawn(EntitySpawnEvent e) {
		String w = e.getEntity().getWorld().getName();
		EntityType type = e.getEntityType();

		EntityMonitor.inc(w, type);
	}
}
