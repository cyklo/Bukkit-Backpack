package com.afforess.backpack;

import java.io.File;

import net.minecraft.server.InventoryPlayer;

import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.Item;
import com.afforess.minecartmaniacore.MinecartManiaPlayer;

public class BackpackPlayer extends MinecartManiaPlayer{
	public BackpackPlayer(MinecartManiaPlayer player) {
		super(player.getName());
	}
	
	public ItemStack[] getInventoryPage(int page) {
		if (page == getCurrentInventoryPage()) {
			return getInventory().getContents();
		}
		ItemStack[] value = (ItemStack[]) getDataValue("Inventory Page " + page);
		if (value == null) {
			value = new ItemStack[36];
			setInventoryPage(page, value);
		}
		return value;
	}
	
	public void setInventoryPage(int page, ItemStack[] contents) {
		setDataValue("Inventory Page " + page, contents);
		if (page == getCurrentInventoryPage()) {
			updateInventoryWindow();
		}
		if (getDataValue("Active Updating") == null) {
			PlayerInventoryUpdater piu = new PlayerInventoryUpdater(this);
			setDataValue("Active Updating", Boolean.TRUE);
			Backpack.server.getScheduler().scheduleAsyncDelayedTask(Backpack.instance, piu);
		}
	}
	
	public int getCurrentInventoryPage() {
		Object value = getDataValue("Current Page");
		if (value == null) {
			setCurrentInventoryPage(0); //upon login we are always at slot 0
			return 0;
		}
		return (Integer) value;
	}
	
	public void setCurrentInventoryPage(int page) {
		setDataValue("Current Page", page);
	}
	
	public void updateInventoryWindow() {
		getInventory().setContents(getInventoryPage(getCurrentInventoryPage()));
	}
	
	public boolean isBackpackEnabled() {
		return getDataValue("Backpack Enabled") != null;
	}
	
	public void setBackpackEnabled(boolean b) {
		setDataValue("Backpack Enabled", b);
	}
	
	public boolean isInInventoryWindow() {
		return getDataValue("Dialog Window") != null;
	}
	
	public void setInInventorWindow(CraftInventory inventory, int page) {
		if (inventory == null) {
			setDataValue("Dialog Window", null);
		}
		else {
			Object[] o = {inventory, page};
			setDataValue("Dialog Window", o);
		}
	}
	
	public int getInventoryWindowPage() {
		if (isInInventoryWindow()) {
			Object[] o = (Object[]) getDataValue("Dialog Window");
			return ((Integer)o[1]).intValue();
		}
		return -1;
	}
	
	public CraftInventory getInventoryWindow() {
		if (isInInventoryWindow()) {
			Object[] o = (Object[]) getDataValue("Dialog Window");
			return ((CraftInventory)o[0]);
		}
		return null;
	}
	
	public String getDataFilePath() {
		return Backpack.dataDirectory + File.separator + getName() + ".data";
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

	//Called after a dialog window with our own 2 inventory pages is closed, used to update the contents of the inventory
	public void updateDialogWindow() {
		//Check and see if we need to update from the dialog window we just closed
		//Moving isn't a sure fire to detect a closed window, but in this case, the user had to use a command, so it works
		if (isInInventoryWindow()) {
			int page = getInventoryWindowPage();
			CraftInventory ci = getInventoryWindow();
			
			//CraftInventory gives us the armor slots too, which we need to dump
			ItemStack[] contents = ci.getContents();
			ItemStack[] realContents = new ItemStack[36];
			for (int i = 0; i < 36; i++) {
				realContents[i] = contents[i];
			}
			
			setInventoryPage(page, realContents);
			
			//clear the references
			InventoryPlayer ip = (InventoryPlayer) ci.getInventory();
			ip.a = null;
			ip.b = null;
			ip.d = true;
			setInInventorWindow(null, -1);
		}
	}
	
	public ItemStack[][] getInventoryContents() {
		ItemStack[][] contents = new ItemStack[getMaxInventoryPages()][36];
		for (int i = 0; i < getMaxInventoryPages(); i++) {
			contents[i] = getInventoryPage(i);
		}
		return contents;
	}
	
	public boolean contains(int type) {
		return first(type) != -1;
	}
	
	public boolean contains(Material m) {
		return contains(m.getId());
	}
	
	public boolean contains(Item m) {
		return first(m) != -1;
	}
	
	public int first(int type) {
		for (int i = 0; i < getMaxInventoryPages(); i++) {
			for (int j = 0; j < 36; j++) {
				ItemStack item = getInventoryPage(i)[j];
				if (item != null && item.getTypeId() == type) {
					return j + i * 36;
				}
			}
		}
		return -1;
	}
	
	public int first(Material m) {
		return first(m.getId());
	}
	
	public int first(Item m) {
		for (int i = 0; i < getMaxInventoryPages(); i++) {
			for (int j = 0; j < 36; j++) {
				ItemStack item = getInventoryPage(i)[j];
				if (item != null && item.getTypeId() == m.getId() && item.getDurability() == m.getData()) {
					return j + i * 36;
				}
			}
		}
		return -1;
	}
	
	public ItemStack getItem(int slot) {
		int page = slot / 36;
		slot -= page * 36;
		return getInventoryPage(page)[slot];
	}
	
	public void setItem(int slot, ItemStack item) {
		int page = slot / 36;
		slot -= page * 36;
		ItemStack[] contents = getInventoryPage(page);
		contents[slot] = item;
		setInventoryPage(page, contents);
		if (page == getCurrentInventoryPage()) {
			getInventory().setItem(slot, item);
		}
	}
}
