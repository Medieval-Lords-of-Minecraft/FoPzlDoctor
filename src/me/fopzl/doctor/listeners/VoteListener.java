package me.fopzl.doctor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

import me.fopzl.doctor.monitors.VoteMonitor;

public class VoteListener implements Listener {
	// TODO: eventually listen for custom fopzlvote event, to avoid duplicate calls
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVote(VotifierEvent e) {
		String site = e.getVote().getServiceName();

		VoteMonitor.inc(site);
	}
}
