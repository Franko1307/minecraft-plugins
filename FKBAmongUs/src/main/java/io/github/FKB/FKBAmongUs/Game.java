package io.github.FKB.FKBAmongUs;

import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitScheduler;

public class Game {
	public String pluginName = ChatColor.AQUA + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.AQUA + ChatColor.BOLD  + "AmongUs" + ChatColor.AQUA  + "] ";
	private Main plugin;
	public enum Status{WAITING, IN_GAME, TALKING, VOTING;}
	public boolean running;
	public Vector<FKBAmongUsPlayer> players;
	public int count = 0; //segundos

	public HandlerCommand handlerCommand;
	
	public Game(Main _plugin) {
		//this.plugin.getLogger().info("0");
		this.plugin = _plugin; // Store the plugin in situations where you need it.
		this.players = new Vector<FKBAmongUsPlayer>();
		this.running = false;
		this.handlerCommand = new HandlerCommand(this, this.plugin);
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		//	    playerList.put(player.getName(), playerData(player));
		//	}
		
		//Commands
		 this.plugin.getCommand("hi").setExecutor(new HiCommand(this.plugin));
		 this.plugin.getCommand("FKBAmongUs").setExecutor(handlerCommand);
		 
		 //Recipes

		 
		 //ThreadGame
		 BukkitScheduler scheduler = plugin.getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(this.plugin, new Runnable() {
	            @Override
	            public void run() {
	            	running = handlerCommand.isRunning();
	            	if(running) {
	            		count++;
	            		//hacer algo 
	            	}else {
	            		count = 0;
	            	}
	            }
	        }, 0L, 20L); //20L = 1 seg
	        
	}
}
