package io.github.FKB.FKBAmongUs;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;


public class HandlerCommand /*extends BukkitRunnable*/ implements CommandExecutor {
	private final Main plugin;
	private boolean running = false;

	//private Vector<Player> players = new Vector<Player>(); 
	//private int counter;
	
	public HandlerCommand(Main plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
		//this.counter = 100;
	}

	public boolean isRunning() {
		return running;
	}
	

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
            sender.sendMessage(this.plugin.pluginName + ChatColor.RED + "Not enough arguments!");
            return false;
        }
		switch (args[0].toLowerCase()) {
		case "join":
			playerJoin(sender);
			break;
		case "leave":
			playerLeave(sender);
			break;
		case "list":
			printPlayers();
			break;
		case "start":
			if(plugin.players.size() < 2)
				plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.RED + "Not enough players for start the game.");
			else
				startGame();
			break;
		case "stop":
			stopGame();
			break;
		case "setlobby":
			setLobby(sender);
			break;
		default:
			sender.sendMessage("Error in comand!.");
			return false;
		}
	
		return true;
	}
	
	private void playerJoin(CommandSender sender) {
		Player player = (Player) sender;
		if(!this.plugin.players.contains(player)) {
			try {
				player.saveData();
				String w = plugin.getConfig().get("map.world.name").toString();
				double x = (double) plugin.getConfig().get("map.world.lobby.x");
				double y = (double) plugin.getConfig().get("map.world.lobby.y");
				double z = (double) plugin.getConfig().get("map.world.lobby.z");
				double d = (double) plugin.getConfig().get("map.world.lobby.d");
				plugin.getLogger().info("w = " + w + ", x = " + x + ", y = " + y + ", z = " + z);
				Location loc = new Location(plugin.getServer().getWorld(w), x, y, z);
				loc.setYaw((float)d);
				player.teleport(loc);
				
				plugin.players.addElement(player); 
				plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.YELLOW + player.getName() + ChatColor.AQUA + " joined (" + this.plugin.players.size() + ").");
				
			}catch(Exception e) {
				sender.sendMessage(this.plugin.pluginName + ChatColor.RED + "There is no lobby to join, contact an admin.");
				plugin.getLogger().info("There is no lobby to join FKB Among Us. Configure a lobby please. " + e.getStackTrace());
			}	
		}else {
			sender.sendMessage(this.plugin.pluginName + ChatColor.RED + "You are already in the game!.");
		}
	}
	
	private void playerLeave(CommandSender sender) {
		Player _player = (Player)sender;
		if(this.plugin.players.contains(_player)) {
			_player.loadData();
			plugin.players.remove(_player);
			
			//Location loc = new Location(_player.getMetadata("lastLocation").get(0));
			//_player.teleport((Location) _player.getMetadata("lastLocation"));
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.YELLOW + _player.getName() + ChatColor.AQUA + " left (" + this.plugin.players.size() + ").");
		}else {
			sender.sendMessage(this.plugin.pluginName + ChatColor.RED + "You are not in a game.");
		}
	}
	
	public void printPlayers() {
		if(plugin.players.size() > 0) {
		plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.GREEN + "Players:");
		for(int i=0; i < plugin.players.size();i++) {
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.GREEN + plugin.players.elementAt(i).getName());
		}
		}else {
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.GREEN + "No players in game.");
		}
	}
	
	private void startGame() {
		if(running == true) return;
		
		Player impostor = plugin.players.elementAt(ThreadLocalRandom.current().nextInt(plugin.players.size()));
		plugin.impostors.addElement(impostor); 
		plugin.innocents = plugin.players;
		plugin.innocents.remove(impostor);
		

		plugin.getLogger().info("[" + plugin.innocents.size() + "] innocents:");
		for(int i=0; i < plugin.innocents.size(); i++) {	
			plugin.innocents.get(i).sendTitle(ChatColor.BLUE + "Innocent", ChatColor.GRAY + "There are " + ChatColor.RED + plugin.impostors.size() + " Impostor(s) " + ChatColor.GRAY + " among us", 5, 100, 5);
		}
		
		plugin.getLogger().info("[" + plugin.impostors.size() + "] impostors:");
		for(int i=0; i < plugin.impostors.size(); i++) {
			plugin.impostors.get(i).sendTitle(ChatColor.DARK_RED + "Impostor", ChatColor.GRAY + "Kill them!", 5, 100, 5);
		}
		
		running = true;
		plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.GREEN + "has started.");
	}
	
	private void stopGame() {
		if(running != false) {
			running = false;
			this.plugin.players.removeAllElements();
			this.plugin.innocents.removeAllElements();
			this.plugin.impostors.removeAllElements();
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.RED + "has stopped.");
		}
	}
	
	private void setLobby(CommandSender sender) {
		Player _player = (Player)sender;
		double x = _player.getLocation().getX();
		double y = _player.getLocation().getY();
		double z = _player.getLocation().getZ();
		String w = _player.getWorld().getName();
		double d = (double) _player.getLocation().getYaw();
		
		plugin.getConfig().set("map.world.name", w);
		plugin.getConfig().set("map.world.lobby.x", x);
		plugin.getConfig().set("map.world.lobby.y", y);
		plugin.getConfig().set("map.world.lobby.z", z);
		plugin.getConfig().set("map.world.lobby.d", d);
		
        plugin.saveConfig();
        sender.sendMessage(this.plugin.pluginName + ChatColor.GREEN + "Established lobby in world: " + w + "(" + x + ", " + y +", " + z + ").");
	}
	
	
}
