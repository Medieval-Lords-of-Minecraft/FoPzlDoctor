package me.fopzl.doctor.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onVote(VotifierEvent e) {
		String site = e.getVote().getServiceName();
		
		// TODO
	}
}
