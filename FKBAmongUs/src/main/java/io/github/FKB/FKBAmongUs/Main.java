package io.github.FKB.FKBAmongUs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

public class Main extends JavaPlugin{
	public String pluginName = ChatColor.AQUA + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.AQUA + ChatColor.BOLD  + "AmongUs" + ChatColor.AQUA  + "] ";
	public boolean running = false;
	public int count = 0; //segundos

	public HandlerCommand handlerCommand = new HandlerCommand(this);
	public Vector<FKBAmongUsPlayer> players2 = new Vector<FKBAmongUsPlayer>();
	
	 @Override
	    public void onEnable() {
		 saveDefaultConfig();
		 getLogger().info("onEnable has been invoked!");
		 //for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		//	    playerList.put(player.getName(), playerData(player));
		//	}
		 //Commands
		 this.getCommand("hi").setExecutor(new HiCommand(this));
		 this.getCommand("FKBAmongUs").setExecutor(handlerCommand);
		 //Recipes

		 //ThreadGame
		 BukkitScheduler scheduler = getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	            @Override
	            public void run() {
	            	running = handlerCommand.isRunning();
	            	if(running) {
	            		
	            		//players = handlerCommand.getPlayers();
	            		count++;
	            		//hacer algo 
	            	}else {
	            		count = 0;
	            	}
	            }
	        }, 0L, 20L); //20L = 1 seg
 
	    }
	    
	    @Override
	    public void onDisable() {
	    	getLogger().info("onDisable has been invoked!");
	    }
	    
}
