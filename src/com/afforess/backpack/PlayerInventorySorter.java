package com.afforess.backpack;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerInventorySorter implements Runnable {
	
	List<Entity> entityList;
	BackpackPlayer player;
	Vector location;
	public PlayerInventorySorter(BackpackPlayer player, List<Entity> entities) {
		this.player = player;
		this.entityList = entities;
		this.location = player.getPlayer().getLocation().toVector();
	}

	@Override
	public void run() {
		for (Entity e : entityList) {
			if (e instanceof Item && e.getLocation().toVector().distanceSquared(location) <= 2.0D) {
				ItemStack item = ((Item)e).getItemStack();
				if (!player.canAddItem(item)) {
					for (int i = 0; i < player.getMaxInventoryPages(); i++) {
						if (i != player.getCurrentInventoryPage()) {
							ItemStack[] page = player.getInventoryPage(i);
							
							//First pass attempt to merge with existing items
							for (int j = 0; j < 36; j++) {
								if (page[j] != null && page[j].getTypeId() == item.getTypeId() && page[j].getDurability() == item.getDurability()) {
									if (page[j].getAmount() + item.getAmount() <= 64) {
										page[j].setAmount(page[j].getAmount() + item.getAmount());
										item = null;
										break;
									}
									else {
										int diff = page[j].getAmount() + item.getAmount() - 64;
										page[j].setAmount(64);
										item.setAmount(diff);
									}
								}
							}
							
							//second pass look for empty slot
							if (item != null) {
								for (int j = 0; j < 36; j++) {
									if (page[j] == null || page[j].getTypeId() == Material.AIR.getId()) {
										page[j] = item;
										item = null;
										break;
									}
								}
							}
							
							if (item == null) {
								break;
							}
						}
					}
					if (item == null) {
						e.remove();
					}
				}
			}
		}
		Runnable r = new Runnable() {
			public void run() {
				player.setDataValue("Active Sorting", null);
			}
		};
		Backpack.server.getScheduler().scheduleSyncDelayedTask(Backpack.instance, r, 40);
		
	}
}
