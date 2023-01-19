package me.fopzl.doctor.listeners;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CommandListener implements Listener {
	@SuppressWarnings("serial")
	// key = command/alias, value = primary alias
	private Map<String, String> commandAliases = new HashMap<String, String>(){{
		put("warp", "warp");
		put("pwarp", "pwarp");
		put("pw", "pwarp");
		put("faq", "faq");
		put("newbie", "newbie");
		put("help", "help");
		put("rules", "rules");
		put("features", "features");
		put("tpaccept", "tpaccept");
		put("fast", "fast");
		put("f", "fast");
		put("dis", "disguise");
		put("disg", "disguise");
		put("disguise", "disguise");
		put("pv", "playervault");
		put("playervault", "playervault");
		put("vc", "playervault");
		put("ec", "enderchest");
		put("enderchest", "enderchest");
		put("home", "home");
		put("wb", "workbench");
		put("kit", "kit");
		put("paint", "paint");
		put("skill", "skills");
		put("skills", "skills");
		put("research", "research");
		put("attr", "attr");
		put("attrs", "attr");
		put("rattr", "rattr");
		put("anvil", "anvil");
		put("back", "back");
		put("return", "back");
		put("bottle", "bottle");
		put("rtp", "randomtp");
		put("randomtp", "randomtp");
		put("wild", "randomtp");
		put("shop", "shop");
	}};
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		String cmd = e.getMessage().substring(0, e.getMessage().indexOf(' ')).toLowerCase();
		String mainAlias = commandAliases.get(cmd);
		if(mainAlias == null) return;
		
		// TODO
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onServerCommand(ServerCommandEvent e) {
		String cmd = e.getCommand();
		
		String[] words = cmd.split(" ");
		
		if(words[0].equalsIgnoreCase("fix")) { // repair key used
			Player p = Bukkit.getPlayer(words[1]);
			
			// TODO
		} else if(words[0].equalsIgnoreCase("prof") && words[1].equalsIgnoreCase("artifact")) { // artifact created
			Player p = Bukkit.getPlayer(words[2]);
			
			// TODO
		}
	}
}
