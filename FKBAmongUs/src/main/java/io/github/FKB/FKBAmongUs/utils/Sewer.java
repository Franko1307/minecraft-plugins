package io.github.FKB.FKBAmongUs.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;

import io.github.FKB.FKBAmongUs.Main;

public class Sewer {
	
	List<List<Location>> sewers;
	Main plugin;
	@SuppressWarnings("unchecked")
	public Sewer(Main plugin) {
		this.plugin = plugin;
	}
	
	public void setSewers(String node, Location loc) {
		
		@SuppressWarnings("unchecked")
		List<Location> current_sewers = (List<Location>) this.plugin.getConfig().getList("map.world.sewers."+node);
		
		if (current_sewers == null) {
			current_sewers = new ArrayList<Location>();
		}
		
		current_sewers.add(loc);
		
		this.plugin.getConfig().set("map.world.sewers."+node, current_sewers);
		
		this.plugin.saveConfig();
		
	}

	public Location goLeft(Location loc) {
		
		Set<String> list = plugin.getConfig().getConfigurationSection("map.world.sewers").getKeys(false);
		String node;
		
		
		
		for(int i=0; i <list.toArray().length; i++) {
		 	node = list.toArray()[i].toString();
        	
    		@SuppressWarnings("unchecked")
			List<Location> current_sewers = (List<Location>) this.plugin.getConfig().getList("map.world.sewers."+node);
    		
    		for (int j = 0; j < current_sewers.size(); j++) {
    			if (current_sewers.get(j).distance(loc) < 2) {    			
    				if (j == 0) 
    					return current_sewers.get(current_sewers.size()-1);
    				else return current_sewers.get(j-1);
    			}
    		}	
        }

		return null;
	}
	
	public Location goRight(Location loc) {
		
		Set<String> list = plugin.getConfig().getConfigurationSection("map.world.sewers").getKeys(false);
		String node;
		
		for(int i=0; i <list.toArray().length; i++) {
		 	node = list.toArray()[i].toString();
        	
    		@SuppressWarnings("unchecked")
			List<Location> current_sewers = (List<Location>) this.plugin.getConfig().getList("map.world.sewers."+node);
		
    		for (int j = 0; j < current_sewers.size(); j++) {
    			if (current_sewers.get(j).distanceSquared(loc) < 2) {
    				if (j == current_sewers.size()-1) 
    					return current_sewers.get(0);
    				else return current_sewers.get(j+1);
    			}
    		}	
        }

		return null;
	}
}