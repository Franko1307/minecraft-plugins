package io.github.JoseGurrola.FKBAchievementRace;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by JG 
 */
public class HiCommand implements CommandExecutor {
	private final Main plugin;
	
	public HiCommand(Main plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
	}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.RED + " Hola " + sender.getName() + ". The plugin was installed correctly :)");
        } else {
        	sender.sendMessage("You must be a player!");
            return false;
        }
        
        /*if (player.hasPermission("some.pointless.permission")) {
        	   //Do something
        	} else {
        	   //Do something else
        	}*/
        
        /*if (args.length > 4) {
            sender.sendMessage("Too many arguments!");
            return false;
        } 
        if (args.length < 2) {
            sender.sendMessage("Not enough arguments!");
            return false;
        }*/
        return true;
    }
}
