package com.afforess.backpack;

import java.io.File;
import java.io.PrintWriter;

import org.bukkit.inventory.ItemStack;

public class PlayerInventoryUpdater implements Runnable{
	
	ItemStack[][] contents = new ItemStack[9][36];
	String path;
	BackpackPlayer player;
	int currentPage;
	public PlayerInventoryUpdater(BackpackPlayer player) {
		for (int i = 0; i < 9; i++) {
			contents[i] = player.getInventoryPage(i);
		}
		//fill in nulls if the page hasn't been created
		for (int i = 0; i < 9; i++) {
			if (contents[i] == null) {
				contents[i] = new ItemStack[36];
			}
		}
		path = player.getDataFilePath();
		this.player = player;
		currentPage = player.getCurrentInventoryPage();
	}

	@Override
	public void run() {
		try {
			PrintWriter pw = new PrintWriter(new File(path));
			pw.append("Current Page:"+currentPage);
			pw.append("\n");
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < contents[i].length; j++) {
					pw.append(BackpackManager.serializeItemStack(contents[i][j]));
					pw.append("\n");
				}
				pw.append("page:" + i);
				pw.append("\n");
			}
			pw.close();
			
			if (player.isOnline()) {
				PlayerInventoryUpdater piu = new PlayerInventoryUpdater(player);
				//20 ticks a second, 60 seconds in a minute (20* 60)
				//Backpack.log.info("Saved " + player.getName() + "'s inventory data!");
				Backpack.server.getScheduler().scheduleAsyncDelayedTask(Backpack.instance, piu, 1200);
			}
			else {
				player.setDataValue("Active Updating", null);
			}
		}
		catch (Exception e) {
			player.setDataValue("Active Updating", null);
		}
	}
}
