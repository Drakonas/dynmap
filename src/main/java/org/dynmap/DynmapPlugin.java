package org.dynmap;

import java.util.logging.Logger;
import java.io.IOException;

import java.io.File;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;
import org.bukkit.util.config.Configuration;
import org.dynmap.debug.BukkitPlayerDebugger;

public class DynmapPlugin extends JavaPlugin {

	protected static final Logger log = Logger.getLogger("Minecraft");

	private WebServer server = null;
	private MapManager mgr = null;
	
	private BukkitPlayerDebugger debugger = new BukkitPlayerDebugger(this);
	
	public static File dataRoot;

	public DynmapPlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		dataRoot = folder;
	}

	public World getWorld() {
		return getServer().getWorlds()[0];
	}

	public void onEnable() {
		Configuration configuration = new Configuration(new File(this.getDataFolder(), "configuration.txt"));
		configuration.load();
		
		debugger.enable();
		mgr = new MapManager(getWorld(), debugger, configuration);
		mgr.startManager();

		try {
			server = new WebServer(mgr, getServer(), debugger, configuration);
		} catch(IOException e) {
			log.info("position failed to start WebServer (IOException)");
		}
		
		registerEvents();
	}

	public void onDisable() {
		mgr.stopManager();

		if(server != null) {
			server.shutdown();
			server = null;
		}
		debugger.disable();
	}

	public void registerEvents() {
		BlockListener blockListener = new DynmapBlockListener(mgr);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, new DynmapPlayerListener(mgr), Priority.Normal, this);
		//getServer().getPluginManager().registerEvent(Event.Type.BLOCK_DESTROYED, listener, Priority.Normal, this);
	/*	etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_CREATED, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, PluginListener.Priority.MEDIUM);

		etc.getInstance().addCommand("/map_wait", " [wait] - set wait between tile renders (ms)");
		etc.getInstance().addCommand("/map_stat", " - query number of tiles in render queue");
		etc.getInstance().addCommand("/map_regen", " - regenerate entire map");
		etc.getInstance().addCommand("/map_debug", " - send map debugging messages");
		etc.getInstance().addCommand("/map_nodebug", " - disable map debugging messages");
		etc.getInstance().addCommand("/addsign", " [name] - adds a named sign to the map");
		etc.getInstance().addCommand("/removesign", " [name] - removes a named sign to the map");
		etc.getInstance().addCommand("/listsigns", " - list all named signs");
		etc.getInstance().addCommand("/tpsign", " [name] - teleport to a named sign");
		*/
		
	}
}
