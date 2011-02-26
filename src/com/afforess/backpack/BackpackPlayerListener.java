package com.afforess.backpack;

import java.util.List;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;

import com.afforess.minecartmaniacore.MinecartManiaWorld;

public class BackpackPlayerListener extends PlayerListener{
	
	@SuppressWarnings("deprecation")
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		BackpackPlayer player = new BackpackPlayer(MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer()));
		if (!player.isBackpackEnabled()) {
			BackpackManager.initializeBackPack(player);
		}
		boolean sneaking = false; //isSneaking is broken :(
		net.minecraft.server.Entity e = ((CraftEntity)player.getPlayer()).getHandle();
		sneaking = e.U();
		int page = Math.min(event.getNewSlot(), player.getMaxInventoryPages() - 1);
		if (sneaking && page != player.getCurrentInventoryPage()) {
			ItemStack[] newInventory = player.getInventoryPage(page);
			ItemStack[] oldInventory = player.getInventory().getContents();
			player.setInventoryPage(player.getCurrentInventoryPage(), oldInventory);
			player.getInventory().setContents(newInventory);
			player.getPlayer().updateInventory();
			player.setCurrentInventoryPage(page);
		}
	}
	
    public void onPlayerMove(PlayerMoveEvent event) {
    	if (event.isCancelled()) {
    		return;
    	}
    	
    	if (!event.getFrom().equals(event.getTo())) {
    		//Check to see if we can pick up items and add them to other inventory pages if this one is full
    		BackpackPlayer player = new BackpackPlayer(MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer()));
    		List<Entity> entities = event.getPlayer().getWorld().getEntities();
    		PlayerInventorySorter sort = new PlayerInventorySorter(player, entities);
    		Backpack.server.getScheduler().scheduleAsyncDelayedTask(Backpack.instance, sort);
    	}
    }

}
