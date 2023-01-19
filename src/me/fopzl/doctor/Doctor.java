package me.fopzl.doctor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.fopzl.doctor.listeners.*;

public class Doctor extends JavaPlugin {
	public void onEnable() {
		super.onEnable();
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new BlockListener(), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new ChunkListener(), this);
		pm.registerEvents(new CommandListener(), this);
		pm.registerEvents(new EntityListener(), this);
		pm.registerEvents(new HopperListener(), this);
		pm.registerEvents(new HurtListener(), this);
		pm.registerEvents(new MoneyListener(), this);
		pm.registerEvents(new OnlineListener(), this);
		pm.registerEvents(new QuestListener(), this);
		pm.registerEvents(new VoteListener(), this);
		
		Bukkit.getServer().getLogger().info("FoPzlDoctor Enabled");
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlDoctor Enabled");
		
		super.onDisable();
	}
}
