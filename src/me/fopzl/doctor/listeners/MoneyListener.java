package me.fopzl.doctor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.fopzl.doctor.Doctor;
import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.monitors.MoneyMonitor;
import net.essentialsx.api.v2.events.TransactionEvent;

public class MoneyListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onTransact(TransactionEvent e) {
		Player fromP = e.getRequester().getPlayer();
		@SuppressWarnings("deprecation")
		Player toP = e.getTarget().getBase();
		
		Rank fromRank = Doctor.getPlayerRank(fromP);
		Rank toRank = Doctor.getPlayerRank(toP);
		
		double amt = e.getAmount().doubleValue();
		
		MoneyMonitor.addSender(fromRank, amt);
		MoneyMonitor.addReceiver(toRank, amt);
	}
}
