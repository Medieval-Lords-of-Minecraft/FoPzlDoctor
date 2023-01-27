package me.fopzl.doctor.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.monitors.HurtMonitor;

public class HurtListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();

		String world = p.getWorld().getName();
		Rank rank = Rank.getPlayerRank(p);
		EntityDamageEvent.DamageCause cause = p.getLastDamageCause().getCause();

		HurtMonitor.incDeath(world, rank, cause);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerHurt(EntityDamageEvent e) {
		if (e.getEntityType() != EntityType.PLAYER)
			return;

		Player p = (Player) e.getEntity();

		String world = p.getWorld().getName();
		Rank rank = Rank.getPlayerRank(p);
		EntityDamageEvent.DamageCause cause = e.getCause();
		double dmg = e.getFinalDamage();

		HurtMonitor.addDamage(world, rank, cause, dmg);
	}
}
