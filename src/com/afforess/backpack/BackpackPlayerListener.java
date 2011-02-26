package com.afforess.backpack;

import net.minecraft.server.Entity;

import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.MinecartManiaWorld;

public class BackpackPlayerListener extends PlayerListener{
	
	@SuppressWarnings("deprecation")
	public void onItemHeldChange(PlayerItemHeldEvent event) {
		BackpackPlayer player = new BackpackPlayer(MinecartManiaWorld.getMinecartManiaPlayer(event.getPlayer()));
		if (!player.isBackpackEnabled()) {
			BackpackManager.initializeBackPack(player);
		}
		boolean sneaking = false; //isSneaking is broken :(
		Entity e = ((CraftEntity)player.getPlayer()).getHandle();
		sneaking = e.U();
		if (sneaking && event.getNewSlot() != player.getCurrentInventoryPage()) {
			ItemStack[] newInventory = player.getInventoryPage(event.getNewSlot());
			ItemStack[] oldInventory = player.getInventory().getContents();
			player.setInventoryPage(player.getCurrentInventoryPage(), oldInventory);
			player.getInventory().setContents(newInventory);
			player.getPlayer().updateInventory();
			player.setCurrentInventoryPage(event.getNewSlot());
		}
	}

}
