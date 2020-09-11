package io.github.FKB.FKBAmongUs;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	private Game game;

	//public boolean running = false;
	
	 @Override
	    public void onEnable() {
		 getLogger().info("onEnable has been invoked!");
		 saveDefaultConfig();
		 game = new Game(this);
	    }
	    
	    @Override
	    public void onDisable() {
	    	getLogger().info("onDisable has been invoked!");
	    }
	    
}
