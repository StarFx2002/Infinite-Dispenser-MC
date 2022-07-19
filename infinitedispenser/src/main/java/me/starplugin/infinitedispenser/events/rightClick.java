package me.starplugin.infinitedispenser.events;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.starplugin.infinitedispenser.Main;

public class rightClick implements Listener{
	public class Dispenser {
		Block _stone = null;
		Block _clay = null;
		Block _chest = null;
		Integer taskID = null;
		public Location location = null;
		Dispenser disp = this;
	
		ItemStack _item = null;
		Player _p = null;
		
		public Dispenser(Block stone, Block clay, Block chest, Player p, ItemStack item) {
			_stone = stone;
			_clay = clay;
			_chest = chest;
			_p = p;
			_item = item.clone();
			location = _stone.getLocation();
			startRunnable();
		}
		void startRunnable()
		{
			new BukkitRunnable() {
				@Override
				public void run() {
					taskID = getTaskId();

					if (!(_stone.getType().equals(Material.STONE) && _clay.getType().equals(Material.CLAY) && _chest.getType().equals(Material.CHEST))) {
						Main.dispenserList.remove(disp);
						this.cancel();
						return;
					}
					
					Chest chestState = (Chest) _chest.getState();
					ItemStack[] chestContent = chestState.getBlockInventory().getContents();
					boolean yesItems = false;
					for (ItemStack e : chestContent) {
						if (e == null) continue;
						if(!e.getType().equals(Material.AIR)) {
							yesItems = true;
						}
					}	
					if (yesItems == false) {
						Main.dispenserList.remove(disp);
						this.cancel();
					}
					
					Location dir = _clay.getLocation().clone().subtract(_chest.getLocation());
					Location locToDrop = _stone.getLocation().clone().add(dir.multiply(0.75));
					Vector vec = dir.toVector().multiply(0.6);
					_p.getWorld().dropItemNaturally(locToDrop, _item).setVelocity(vec);
	
				}
			}.runTaskTimer(Main.getPlugin(Main.class), 0, 10);
		}
		
	}

    @EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (event.getClickedBlock().getType().equals(Material.STONE) && event.getHand().equals(EquipmentSlot.HAND)) {
			    for (Dispenser dispenser : Main.dispenserList) {
			    	if (event.getClickedBlock().getLocation().equals(dispenser.location)) return;
				}
				Location clay = event.getClickedBlock().getLocation().add(0, -1, 0);
				if (clay.getBlock().getType().equals(Material.CLAY)) {
					Location[] chests = {clay.clone().add(0, 0, 1), clay.clone().add(0, 0, -1), clay.clone().add(1, 0, 0), clay.clone().add(-1, 0, 0)} ;
					Location chestLoc = null;
					boolean foundChest = false;
					for (Location location : chests) {
						if (location.getBlock().getType().equals(Material.CHEST)) {
						    if (chestLoc == null) {
						        chestLoc = location;
						        foundChest = true;
						    } else {
							    p.sendMessage(ChatColor.RED + "Error too many chests...");
							    return;
							}
						}
					}
					if (foundChest) {
						Chest chest = (Chest) chestLoc.getBlock().getState();
						ItemStack[] items = chest.getBlockInventory().getContents();
						ItemStack item = null;
						
						int greedyDetector = 0;
						for(int i = 0; i < items.length; i++) {
							if (items[i] == null) continue;
							if (items[i].getType().equals(Material.AIR)) continue;

							if (item == null) item = items[i];
							if (greedyDetector == 0) greedyDetector++;
							if (!items[i].getType().equals(item.getType())) greedyDetector++;
						}
						if (greedyDetector == 0) {
							p.sendMessage(ChatColor.RED + "No items in the chest...");
						}
						else if(greedyDetector == 1) {
							p.sendMessage(ChatColor.GREEN + "Dispenser Started!");
							Main.dispenserList.add(new Dispenser(event.getClickedBlock(), clay.getBlock(), chest.getBlock(), p, item));
						}else if (greedyDetector >= 2) {
							p.getWorld().createExplosion(chestLoc, 2f, true, true);
							p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&4Greedy"), ">:(", 10, 30, 10);
						}
					}else {
						p.sendMessage(ChatColor.RED + "No chest detected!");
					}
				}
			}
		}
	}
}
