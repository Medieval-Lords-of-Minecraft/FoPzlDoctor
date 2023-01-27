package me.fopzl.doctor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.fopzl.doctor.monitors.LoginMonitor;
import me.fopzl.doctor.monitors.OnlineMonitor;
import me.neoblade298.neocore.bukkit.InstanceType;
import me.neoblade298.neocore.bukkit.NeoCore;

public class LoginListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if (p.hasPlayedBefore()) {
			LoginMonitor.tryIncPlayer(p.getUniqueId());
		} else if (NeoCore.getInstanceType() == InstanceType.HUB) {
			OnlineMonitor.incNewbie();
		}
	}
}
