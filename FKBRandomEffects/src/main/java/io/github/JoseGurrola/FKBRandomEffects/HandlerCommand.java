package io.github.JoseGurrola.FKBRandomEffects;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class HandlerCommand /*extends BukkitRunnable*/ implements CommandExecutor {
	private final Main plugin;
	private boolean running = false;
	private int time = 0;
	private int duration = 0;
	//private int counter;
	
	public HandlerCommand(Main plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
		//this.counter = 100;
	}

	public boolean isRunning() {
		return running;
	}
	
	public int getTime() {
		return time;
	}
	
	public int getDuration() {
		return duration;
	}
	public void finishEvent(){
		plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.RED + "The time is over.");
		running = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
            sender.sendMessage("Not enough arguments!");
            return false;
        }
		
		if(args[0].equals("start")) {
			if(args.length < 3) {
				sender.sendMessage(this.plugin.pluginName + ChatColor.RED + "You need to specified the time and duration!.");
				return false;
			}
			if(!args[1].isEmpty() && !args[2].isEmpty()) {
				time = Integer.valueOf(args[1]);
				duration = Integer.valueOf(args[2]);
				running = true;
			}
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.GREEN + "has started.");
			
		}else if(args[0].equals("stop")){
			running = false;
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.RED + "has stopped.");
			
		}else {
			sender.sendMessage("Error in comand!.");
			return false;
		}
		return true;
	}
	
}
