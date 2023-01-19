package me.fopzl.doctor;

import org.bukkit.entity.Player;

public class Util {
	public enum Rank {
		PAID, STAFF, NONE
	}
	
	public static Rank getPlayerRank(Player p) {
		if (p.hasPermission("Vote.Staff")) {
				return Rank.STAFF;
		} else if(p.hasPermission("Vote.Diamond") || 
			p.hasPermission("Vote.Emerald") || 
			p.hasPermission("Vote.Sapphire") || 
			p.hasPermission("Vote.Ruby") ) {
			return Rank.PAID;
		} else {
			return Rank.NONE;
		}
	}
}