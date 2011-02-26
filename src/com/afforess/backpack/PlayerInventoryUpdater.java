package com.afforess.backpack;

import java.io.File;
import java.io.PrintWriter;

import org.bukkit.inventory.ItemStack;

public class PlayerInventoryUpdater implements Runnable{
	
	ItemStack[][] contents = new ItemStack[9][36];
	String path;
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
	}

	@Override
	public void run() {
		try {
			PrintWriter pw = new PrintWriter(new File(path));
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < contents[i].length; j++) {
					pw.append(BackpackManager.serializeItemStack(contents[i][j]));
					pw.append("\n");
				}
				pw.append("page:" + i);
				pw.append("\n");
			}
			pw.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
