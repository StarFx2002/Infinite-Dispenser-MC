package me.starplugin.infinitedispenser;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import me.starplugin.infinitedispenser.events.rightClick;

public class Main extends JavaPlugin {
    public static ArrayList<rightClick.Dispenser> dispenserList = new ArrayList<rightClick.Dispenser>();
	
	
	public void onEnable() {
	
		getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[InfiniteDispenser]&f plugin has been enabled"));
		getServer().getPluginManager().registerEvents(new rightClick(), this);
		
	}
	
	public void onDisable() {
		
		getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[InfiniteDispenser]&f plugin has been disabled"));
		
	}
}
