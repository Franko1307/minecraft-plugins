package io.github.JoseGurrola.FKBRandomEffects;

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
	public String pluginName = ChatColor.DARK_PURPLE + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.DARK_PURPLE + "RandomEffects] ";
	public boolean running = false;
	public int count = 0;
	private int time = 1;
	private int duration = 10;
	Vector<String> effectsApplied = new Vector<String>();
	public HandlerCommand handlerCommand = new HandlerCommand(this);
	PotionEffectType[] effects = PotionEffectType.values();
	
	 @Override
	    public void onEnable() {
		 getLogger().info("onEnable has been invoked!");
		 //for (Player player : Bukkit.getServer().getOnlinePlayers()) {
		//	    playerList.put(player.getName(), playerData(player));
		//	}
		 //Commands
		 this.getCommand("hi").setExecutor(new HiCommand(this));
		 this.getCommand("randomeffects").setExecutor(handlerCommand);
		 //Recipe
		 Bukkit.addRecipe(getRecipe());

		 //ThreadGame
		 BukkitScheduler scheduler = getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
	            @Override
	            public void run() {
	            	running = handlerCommand.isRunning();
	            	if(running) {
	            		time = handlerCommand.getTime();
	            		duration = handlerCommand.getDuration();
	            		count++;
	            		if(count == 144000) handlerCommand.finishEvent();
	            		int aux = count % time;
	            		if(aux == time-1 || aux == time-2 || aux == time-3)
	            			getServer().broadcastMessage(pluginName + ChatColor.YELLOW + "Effect will be applied in " + ((time - aux)) + " seconds");
	            		
	            		if(count % time == 0) 
	            			getServer().broadcastMessage(pluginName + ChatColor.YELLOW + "Effect [" + applyEffect() + "] applied.");
	            	}else {
	            		effectsApplied.removeAllElements();
	            		count = 0;
	            	}
	            }
	        }, 0L, 20L); //20L = 1 seg
 
	    }
	    
	    @Override
	    public void onDisable() {
	    	getLogger().info("onDisable has been invoked!");
	    }
	    
		public String applyEffect() {
	    	PotionEffectType effect = null;
	    	
	    	effect = effects[ThreadLocalRandom.current().nextInt(effects.length)];
	    	
	    	String effectName = effect.getName();
	    	int amplifier = 0;

			Enumeration<String> enumeration = effectsApplied.elements();
	    	while(enumeration.hasMoreElements()) {
	    		 String element=(String)enumeration.nextElement();
	             if(element.equals(effectName)){
	            	 amplifier++;
	             }
	    	}

	    	for (Player player : getServer().getOnlinePlayers()) {
	    		player.addPotionEffect(new PotionEffect(effect, duration*20, amplifier)); //6000 = 5 min	
	    	}
	    	
	    	effectsApplied.addElement(effectName);
	    	return effectName + ", LEVEL=" + (amplifier + 1);
	    }

		public ShapedRecipe getRecipe() {
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
		}
}
