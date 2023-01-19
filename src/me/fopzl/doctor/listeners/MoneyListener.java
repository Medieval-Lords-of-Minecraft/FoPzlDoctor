package me.fopzl.doctor.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.TechsCode.UltraEconomy.events.account.AccountAddBalanceEvent;
import me.TechsCode.UltraEconomy.events.account.AccountPayEvent;
import me.TechsCode.UltraEconomy.events.account.AccountRemoveBalanceEvent;
import me.fopzl.doctor.Util;

public class MoneyListener implements Listener {
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPay(AccountPayEvent e) {
		if(!e.getCurrency().getName().equalsIgnoreCase("gold")) return;
		
		Player fromP = Bukkit.getServer().getPlayer(e.getFromAccount().getUuid());
		Player toP = Bukkit.getServer().getPlayer(e.getToAccount().getUuid());
		
		Util.Rank fromRank = Util.getPlayerRank(fromP);
		Util.Rank toRank = Util.getPlayerRank(toP);
		float amt = e.getSentValue();
		
		// TODO
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAddBalance(AccountAddBalanceEvent e) {
		if(!e.getCurrency().getName().equalsIgnoreCase("gold")) return;
		
		Player p = Bukkit.getServer().getPlayer(e.account.getUuid());
		
		Util.Rank rank = Util.getPlayerRank(p);
		float amt = e.getNewBalance() - e.getOldBalance();
		
		// TODO
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onRemoveBalance(AccountRemoveBalanceEvent e) {
		if(!e.getCurrency().getName().equalsIgnoreCase("gold")) return;
		
		Player p = Bukkit.getServer().getPlayer(e.account.getUuid());
		
		Util.Rank rank = Util.getPlayerRank(p);
		float amt = e.getOldBalance() - e.getNewBalance();
		
		// TODO
	}
}
