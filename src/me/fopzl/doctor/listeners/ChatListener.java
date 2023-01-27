package me.fopzl.doctor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.ChatChannelEvent;
import org.mineacademy.chatcontrol.api.PrePrivateMessageEvent;

import me.fopzl.doctor.Doctor.Rank;
import me.fopzl.doctor.monitors.ChatMonitor;

public class ChatListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(ChatChannelEvent e) {
		if (!(e.getSender() instanceof Player))
			return;
		Player sender = (Player) e.getSender();

		Rank rank = Rank.getPlayerRank(sender);
		String channel = e.getChannel().getName();

		ChatMonitor.inc(rank, channel);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMessage(PrePrivateMessageEvent e) {
		if (!(e.getSender() instanceof Player))
			return;

		Player sender = (Player) e.getSender();
		Rank rank = Rank.getPlayerRank(sender);

		ChatMonitor.inc(rank, "message");
	}
}
