package com.afforess.backpack;

import java.io.File;
import java.util.logging.Logger;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.InventoryPlayer;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.StringUtils;


public class Backpack extends JavaPlugin{

	public static Logger log = Logger.getLogger("Minecraft");
	public static Server server;
	public static PluginDescriptionFile description;
	public static final String directory = "plugins" + File.separator + "Backpack";
	public static final String dataDirectory = directory + File.separator + "data";
	public static final BackpackPlayerListener bpl = new BackpackPlayerListener();
	public static final BackpackEntityListener bpe = new BackpackEntityListener();
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
			getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, bpl, Priority.Lowest, this);
			getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DEATH, bpe, Priority.Lowest, this);
			BackpackManager.initialize();
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		
		BackpackPlayer player = new BackpackPlayer(MinecartManiaWorld.getMinecartManiaPlayer((Player)sender));
		if (commandLabel.equalsIgnoreCase("open")) {
			if (args.length == 1) {
				try {
					//minus one because what users see as page #1 is actually page 0, etc...
					int page = Integer.parseInt(StringUtils.getNumber(args[0])) - 1;
					if (page != player.getCurrentInventoryPage() && page < player.getMaxInventoryPages()) {
						EntityPlayer ep = ((CraftPlayer)player.getPlayer()).getHandle();
						InventoryPlayer otherPage = new InventoryPlayer(ep);
						
						//proceed to abuse CraftBukkit's built in methods
						CraftInventory ci = new CraftInventory(otherPage);
						ci.setContents(player.getInventoryPage(page));
						
						//Alert player move we are looking at a dialog window
						//Because we will need to update the fake inventory page with the new fake info from the fake dialog
						player.setInInventorWindow(ci, page);
						
						//And launch the dialog!
						ep.a(otherPage);
						
						return true;
					}
				}
				catch (Exception e) {
				}
			}
		}
		
		return false;
	}
}
