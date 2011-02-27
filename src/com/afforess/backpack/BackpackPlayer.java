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
		player.setDataValue("Inventory Page " + page, contents);
		if (player.getDataValue("Active Updating") == null) {
			PlayerInventoryUpdater piu = new PlayerInventoryUpdater(this);
			player.setDataValue("Active Updating", Boolean.TRUE);
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
	
	public int getMaxInventoryPages() {
		int defaultVal = 9;
		if (BackpackManager.config.get(this.getName()) == null) {
			if (BackpackManager.config.get("default") == null) {
				return defaultVal;
			}
			else {
				return (Integer)BackpackManager.config.get("default");
			}
		} 
		else {
			return (Integer)BackpackManager.config.get(this.getName());
		}
	}
	
	public boolean canAddItem(ItemStack item) {
		ItemStack[] contents = getContents();
		int amt = item.getAmount();
		for (int i = 0; i < 36; i++){
			if (contents[i] == null || contents[i].getTypeId() == 0) {
				return true;
			}
			if (contents[i].getTypeId() == item.getTypeId() && contents[i].getDurability() == item.getDurability()) {
				amt = contents[i].getAmount() + amt - 64;
			}
		}
		return amt <= 0;
	}
}
