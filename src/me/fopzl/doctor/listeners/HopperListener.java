package me.fopzl.doctor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

public class HopperListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHopperTick(InventoryMoveItemEvent e) {
		if(e.getInitiator().getType() != InventoryType.HOPPER) return;
		
		// TODO
	}
}
