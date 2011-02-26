package com.afforess.backpack;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class Backpack extends JavaPlugin{

	public static Logger log = Logger.getLogger("Minecraft");
	public static Server server;
	public static PluginDescriptionFile description;
	public static final String directory = "plugins" + File.separator + "Backpack";
	public static final String dataDirectory = directory + File.separator + "data";
	public static final BackpackPlayerListener bpl = new BackpackPlayerListener();
	public static Plugin instance;
	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		server = this.getServer();
		description = this.getDescription();
		instance = this;
		PluginDescriptionFile pdfFile = this.getDescription();
		Plugin MinecartMania = server.getPluginManager().getPlugin("Minecart Mania Core");
		if (MinecartMania == null) {
			log.severe(pdfFile.getName() + " requires Minecart Mania Core to function!");
			log.severe(pdfFile.getName() + " is disabled!");
			this.setEnabled(false);
		}
		else {	
			log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_ITEM_HELD, bpl, Priority.Lowest, this);
			BackpackManager.initialize();
		}
	}
}
