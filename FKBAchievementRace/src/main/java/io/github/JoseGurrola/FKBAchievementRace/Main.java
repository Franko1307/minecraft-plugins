package io.github.JoseGurrola.FKBAchievementRace;

import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin{
	public String pluginName = ChatColor.DARK_PURPLE + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.DARK_PURPLE + "AchievementRace] ";
	public boolean running = false;
	public HandlerCommand handlerCommand = new HandlerCommand(this);
	
	 @Override
	    public void onEnable() {
		 getLogger().info("onEnable has been invoked!");
		 //for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		//	    playerList.put(player.getName(), playerData(player));
		//	}
		 //Commands
		 this.getCommand("hi").setExecutor(new HiCommand(this));
		 this.getCommand("AchievementRace").setExecutor(handlerCommand);
		 //Recipe
		// Bukkit.addRecipe(getRecipe());

		 //ThreadGame
		 BukkitScheduler scheduler = getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	            @Override
	            public void run() {
	            	running = handlerCommand.isRunning();
	            	if(running) {
	            		handlerCommand.updateFKBScoreboard();         		
	            	}else {
	            		
	            	}
	            }
	        }, 0L, 20L); //20L = 1 seg
 
	    }
	    
	    @Override
	    public void onDisable() {
	    	getLogger().info("onDisable has been invoked!");
	    }
	    
	    
	    /*
	    
	    
	    AQUI ESTA EL PEDO XDXD
	    
	    EL EVENTO DE ABAJO NO JALA NO SE PORQUE
	    
	    
	    
	    
	    */
	    @EventHandler
	    public void PlayerAdvancementDoneEvent(Player p, Advancement adv) {
	    	
			getServer().broadcastMessage(pluginName + ChatColor.GREEN + p.getName() + " ha obtenido el logro"); 
			//for (FKBAchievementRacePlayer FKBplayer : ) {	
			//	if(p.equals(FKBplayer.getPlayer())){
			//		FKBplayer.addAchievement();
			//	}
			//}
	    }
	    
		/*public ShapedRecipe getRecipe() {
			ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
			item.addEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName( ChatColor.RED + "F" + ChatColor.DARK_BLUE + "K" + ChatColor.LIGHT_PURPLE + "B" + ChatColor.AQUA + "Sword");
			List<String> loreList = new ArrayList<String>();
			loreList.add(ChatColor.GRAY + "Una espada para ayudar a los mancos xd");
			itemMeta.setLore(loreList);
			item.setItemMeta(itemMeta);
			
			NamespacedKey key = new NamespacedKey(this, "diamond_sword");
			ShapedRecipe recipe = new ShapedRecipe(key, item);
			
			recipe.shape("GDG","GDG", "GSG");
			recipe.setIngredient('G', Material.GOLD_INGOT);
			recipe.setIngredient('D', Material.DIAMOND);
			recipe.setIngredient('S', Material.STICK);
			return recipe;
		}*/
}
