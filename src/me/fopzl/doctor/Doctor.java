package me.fopzl.doctor;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.fopzl.doctor.listeners.BlockListener;
import me.fopzl.doctor.listeners.ChatListener;
import me.fopzl.doctor.listeners.ChunkListener;
import me.fopzl.doctor.listeners.EntityListener;
import me.fopzl.doctor.listeners.HurtListener;
import me.fopzl.doctor.listeners.LoginListener;
import me.fopzl.doctor.listeners.MoneyListener;
import me.fopzl.doctor.listeners.QuestListener;
import me.fopzl.doctor.listeners.VoteListener;
import me.fopzl.doctor.monitors.BlockMonitor;
import me.fopzl.doctor.monitors.ChatMonitor;
import me.fopzl.doctor.monitors.ChunkMonitor;
import me.fopzl.doctor.monitors.EntityMonitor;
import me.fopzl.doctor.monitors.HurtMonitor;
import me.fopzl.doctor.monitors.LagMonitor;
import me.fopzl.doctor.monitors.LoginMonitor;
import me.fopzl.doctor.monitors.MoneyMonitor;
import me.fopzl.doctor.monitors.OnlineMonitor;
import me.fopzl.doctor.monitors.QuestMonitor;
import me.fopzl.doctor.monitors.QuestStatMonitor;
import me.fopzl.doctor.monitors.TownyStatMonitor;
import me.fopzl.doctor.monitors.VoteMonitor;
import me.neoblade298.neocore.bukkit.NeoCore;
import me.neoblade298.neocore.bukkit.scheduler.ScheduleInterval;
import net.milkbowl.vault.permission.Permission;

public class Doctor extends JavaPlugin {
	private static Permission perms;
	
	@Override
	public void onEnable() {
		super.onEnable();
		
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		
		loadConfig();
		
		setupListeners();
		setupMonitors();
		
		Bukkit.getServer().getLogger().info("FoPzlDoctor Enabled");
	}
	
	@Override
	public void onDisable() {
		Bukkit.getServer().getLogger().info("FoPzlDoctor Disabled");
		
		super.onDisable();
	}

	private void loadConfig() {
		// Save config if doesn't exist
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			saveResource("config.yml", false);
		}
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);

		IOManager.loadConfig(config);
	}
	
	private void setupListeners() {
		PluginManager pm = getServer().getPluginManager();
		switch (NeoCore.getInstanceType()) {
		case TOWNY:
			pm.registerEvents(new BlockListener(), this);
			pm.registerEvents(new ChatListener(), this);
			pm.registerEvents(new ChunkListener(), this);
			pm.registerEvents(new EntityListener(), this);
			pm.registerEvents(new HurtListener(), this);
			pm.registerEvents(new LoginListener(), this);
			pm.registerEvents(new MoneyListener(), this);
			pm.registerEvents(new VoteListener(), this); // temporary
			return;
		case QUESTS:
			pm.registerEvents(new ChatListener(), this);
			pm.registerEvents(new ChunkListener(), this);
			pm.registerEvents(new EntityListener(), this);
			pm.registerEvents(new HurtListener(), this);
			pm.registerEvents(new LoginListener(), this);
			pm.registerEvents(new MoneyListener(), this);
			pm.registerEvents(new QuestListener(), this);
			return;
		case SESSIONS:
			pm.registerEvents(new ChatListener(), this);
			pm.registerEvents(new HurtListener(), this);
			pm.registerEvents(new QuestListener(), this);
			return;
		case CREATIVE:
			pm.registerEvents(new BlockListener(), this);
			pm.registerEvents(new ChatListener(), this);
			pm.registerEvents(new LoginListener(), this);
			pm.registerEvents(new MoneyListener(), this);
			return;
		case HUB:
			pm.registerEvents(new ChatListener(), this);
			pm.registerEvents(new LoginListener(), this);
			pm.registerEvents(new MoneyListener(), this);
			return;
		case DEV:
			return;
		case OTHER:
			return;
		}
	}
	
	private void setupMonitors() {
		switch (NeoCore.getInstanceType()) {
		case TOWNY:
			new BlockMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new ChatMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new ChunkMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new EntityMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new HurtMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LagMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LoginMonitor(ScheduleInterval.DAILY);
			new MoneyMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new OnlineMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new TownyStatMonitor(ScheduleInterval.DAILY);
			new VoteMonitor(ScheduleInterval.FIFTEEN_MINUTES); // temporary
			return;
		case QUESTS:
			new BlockMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new ChatMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new ChunkMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new EntityMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new HurtMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LagMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LoginMonitor(ScheduleInterval.DAILY);
			new MoneyMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new OnlineMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new QuestMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new QuestStatMonitor(ScheduleInterval.DAILY);
			return;
		case SESSIONS:
			new ChatMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new HurtMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LagMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LoginMonitor(ScheduleInterval.DAILY);
			return;
		case CREATIVE:
			new BlockMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new ChatMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LagMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LoginMonitor(ScheduleInterval.DAILY);
			new MoneyMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new OnlineMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			return;
		case HUB:
			new ChatMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LagMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new LoginMonitor(ScheduleInterval.DAILY);
			new MoneyMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			new OnlineMonitor(ScheduleInterval.FIFTEEN_MINUTES);
			return;
		case DEV:
			return;
		case OTHER:
			return;
		}
	}
	
	public enum Rank {
		PAID, STAFF, NONE;
		
		public static Rank getPlayerRank(Player p) {
			if (perms.playerHas(p, "Vote.Staff")) {
				return Rank.STAFF;
			} else if (perms.playerHas(p, "Vote.Diamond") || perms.playerHas(p, "Vote.Emerald") || perms.playerHas(p, "Vote.Sapphire")
					|| perms.playerHas(p, "Vote.Ruby")) {
				return Rank.PAID;
			} else {
				return Rank.NONE;
			}
		}
	}

	public enum QuestLevelGroup {
		T2LOW("1-9"), T2MID("10-19"), T2HIGH("20-29"), T3LOW("30-39"), T3MID("40-49"), T3HIGH("50-59"), MAX("60"); // will need to update when T4 comes out in 10 years

		private final String text;

		QuestLevelGroup(final String text) {
			this.text = text;
		}

		public static QuestLevelGroup fromLevel(int lvl) {
			if (lvl < 10)
				return T2LOW;
			if (lvl < 20)
				return T2MID;
			if (lvl < 30)
				return T2HIGH;
			if (lvl < 40)
				return T3LOW;
			if (lvl < 50)
				return T3MID;
			if (lvl < 60)
				return T3HIGH;
			return MAX;
		}

		@Override
		public String toString() {
			return text;
		}
	}
}
