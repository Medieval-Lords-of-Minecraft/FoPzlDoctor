package me.fopzl.doctor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Doctor extends JavaPlugin {
	public void onEnable() {
		super.onEnable();
		
		Bukkit.getServer().getLogger().info("FoPzlDoctor Enabled");
	}
	
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlDoctor Enabled");
		
		super.onDisable();
	}
}
