package me.fopzl.doctor.listeners;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import me.fopzl.doctor.Util;

public class HurtListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = (Player)e.getEntity();
		
		World world = p.getWorld();
		Util.Rank rank = Util.getPlayerRank(p);
		EntityDamageEvent.DamageCause cause = p.getLastDamageCause().getCause();
		
		// TODO
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerHurt(EntityDamageEvent e) {
		if(e.getEntityType() != EntityType.PLAYER) return;
		
		Player p = (Player)e.getEntity();
		
		World world = p.getWorld();
		Util.Rank rank = Util.getPlayerRank(p);
		EntityDamageEvent.DamageCause cause = e.getCause();
		double dmg = e.getFinalDamage();
		
		// TODO
	}
}
