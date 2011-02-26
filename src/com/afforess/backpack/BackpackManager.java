package com.afforess.backpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.bukkit.inventory.ItemStack;

public class BackpackManager {
	
	public static void initialize() {
		//initialize data folders
		File dir = new File(Backpack.directory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		dir = new File(Backpack.dataDirectory);
		if (!dir.exists()) {
			dir.mkdir();
		}
		
	}

	public static void initializeBackPack(BackpackPlayer player) {
		player.setBackpackEnabled(true);
		//Check for previous data
		File prevData = new File(player.getDataFilePath());
		if (prevData.exists()){
			Scanner input;
			try {
				input = new Scanner(prevData);
				int page = 0;
				ItemStack[] contents = new ItemStack[player.getContents().length];
				int item = 0;
				while(input.hasNext()) {
					String line = input.nextLine();
					//Move to next page
					if (line.contains("page:")) {
						page = Integer.parseInt(line.split(":")[1]);
						player.setInventoryPage(page, contents);
						contents = new ItemStack[player.getContents().length];
						item = 0;
					}
					//Parse this pages contents
					else {
						ItemStack i = deserializeItemStackString(line);
						contents[item] = i;
						item ++;
					}
				}
				input.close();
			} catch (FileNotFoundException e) {
			}
		}
		for (int i = 0; i < 9; i++) {
			if (player.getInventoryPage(i) == null)
				player.setInventoryPage(i, new ItemStack[player.getContents().length]);
		}
	}
	
	public static String serializeItemStack(ItemStack i) {
		if (i== null) {
			return "null";
		}
		StringBuilder s = new StringBuilder();
		s.append(i.getTypeId());
		s.append(":");
		s.append(i.getAmount());
		s.append(":");
		s.append(i.getDurability());
		return s.toString();
	}
	
	public static ItemStack deserializeItemStackString(String s) {
		if (s.equals("null")) {
			return null;
		}
		String[] split = s.split(":");
		ItemStack item = new ItemStack(Integer.parseInt(split[0]), Integer.parseInt(split[1]), (short) Integer.parseInt(split[2]));
		return item;
	}

}