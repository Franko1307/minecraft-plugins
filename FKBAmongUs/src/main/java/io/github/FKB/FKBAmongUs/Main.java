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
		 getLogger().info("antes de game");
		 game = new Game(this);
		 getLogger().info("despues de game");
	    }
	    
	    @Override
	    public void onDisable() {
	    	getLogger().info("onDisable has been invoked!");
	    }
	    
}
