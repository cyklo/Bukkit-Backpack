package com.afforess.backpack;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class PlayerInventorySorter implements Runnable {
	
	List<Entity> entityList;
	BackpackPlayer player;
	public PlayerInventorySorter(BackpackPlayer player, List<Entity> entities) {
		this.player = player;
		entityList = entities;
	}

	@Override
	public void run() {
		for (Entity e : entityList) {
			if (e instanceof Item) {
				if (e.getLocation().toVector().distanceSquared(player.getPlayer().getLocation().toVector()) <= 2.5D){
					Item drop = (Item)e;
					ItemStack[] contents = player.getContents();
					if (!player.addItem(drop.getItemStack())) {
						ItemStack item = drop.getItemStack();
						for (int i = 0; i < 9; i++) {
							ItemStack[] page = player.getInventoryPage(i);
							//First pass attempt to merge with existing items
							for (int j = 0; j < 36; j++) {
								if (page[j] != null && page[j].getTypeId() == drop.getItemStack().getTypeId() && page[j].getDurability() == drop.getItemStack().getDurability()) {
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
						if (item == null) {
							CraftEntity ce = ((CraftEntity)drop);
							ce.getHandle().C();
						}
					}
					else {
						player.getInventory().setContents(contents);
					}
				}
			}
		}
		Runnable r = new Runnable() {
			public void run() {
				player.setDataValue("Active Sorting", null);
			}
		};
		Backpack.server.getScheduler().scheduleSyncDelayedTask(Backpack.instance, r, 8);
		
	}
}
