package me.fopzl.doctor.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.mineacademy.chatcontrol.api.ChatChannelBungeeEvent;
import org.mineacademy.chatcontrol.api.PrePrivateMessageEvent;

public class ChatListener implements Listener {
	
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onChat(ChatChannelBungeeEvent e) {
		String senderName = e.getSenderName();
		String msg = e.getMessage();
		
		// TODO
	}
	
	// whisper cmds: /w /whisper /msg /message /tell /r /reply /pm
	// handles /tc, /nc, etc.?
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMessage(PrePrivateMessageEvent e) {
		if(!(e.getSender() instanceof Player)) return;
		
		Player sender = (Player)e.getSender();
		Player receiver = (Player)e.getReceiver();
		String msg = e.getMessage();
		
		// TODO
	}
}
