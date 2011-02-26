package com.afforess.backpack;

import java.io.File;

import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.MinecartManiaPlayer;

public class BackpackPlayer extends MinecartManiaPlayer{
	public final MinecartManiaPlayer player;
	public BackpackPlayer(MinecartManiaPlayer player) {
		super(player.getName());
		this.player = player;
	}
	
	public ItemStack[] getInventoryPage(int page) {
		return (ItemStack[]) player.getDataValue("Inventory Page " + page);
	}
	
	public void setInventoryPage(int page, ItemStack[] contents) {
		ItemStack[] prev = getInventoryPage(page);
		boolean dirty = false;
		if (prev != null) {
			for (int i = 0; i < prev.length; i++) {
				if (!BackpackManager.isEqual(prev[i], contents[i])) {
					dirty = true;
					break;
				}
			}
		}
		player.setDataValue("Inventory Page " + page, contents);
		if (dirty) {
			PlayerInventoryUpdater piu = new PlayerInventoryUpdater(this);
			Backpack.server.getScheduler().scheduleAsyncDelayedTask(Backpack.instance, piu);
		}
	}
	
	public int getCurrentInventoryPage() {
		Object value =  player.getDataValue("Current Page");
		if (value == null) {
			setCurrentInventoryPage(0); //upon login we are always at slot 0
			return 0;
		}
		return (Integer) value;
	}
	
	public void setCurrentInventoryPage(int page) {
	player.setDataValue("Current Page", page);
	}
	
	public boolean isBackpackEnabled() {
		return player.getDataValue("Backpack Enabled") != null;
	}
	
	public void setBackpackEnabled(boolean b) {
		player.setDataValue("Backpack Enabled", b);
	}
	
	public String getDataFilePath() {
		return Backpack.dataDirectory + File.separator + player.getName() + ".data";
	}
}
