package com.afforess.backpack;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.MinecartManiaWorld;

public class BackpackEntityListener extends EntityListener{
    public void onEntityDeath(EntityDeathEvent event) {
    	if (event.getEntity() instanceof Player) {
    		BackpackPlayer player = new BackpackPlayer(MinecartManiaWorld.getMinecartManiaPlayer((Player)event.getEntity()));
    		List<ItemStack> loot = event.getDrops();
    		for (int i = 0; i < player.getMaxInventoryPages(); i++) {
    			if (i != player.getCurrentInventoryPage()) {
    				ItemStack[] page = player.getInventoryPage(i);
    				if (page != null) {
	    				for (int j = 0; j < 36; j++) {
	    					if (page[j] != null && page[j].getType() != Material.AIR) {
	    						loot.add(page[j]);
	    					}
	    				}
	    				player.setInventoryPage(i, new ItemStack[36]);
    				}
    			}
    		}
    	}
    }
}
