package io.github.FKB.FKBAmongUs;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class Game  implements Listener {
	public enum Status{WAITING, IN_GAME, TALKING, VOTING;}
	public String pluginName = ChatColor.AQUA + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.AQUA + ChatColor.BOLD  + "AmongUs" + ChatColor.AQUA  + "] ";
	
	private Main plugin;
	public boolean running;
	public Vector<FKBAmongUsPlayer> players;
	public Vector<FKBAmongUsPlayer> impostors;
	public Vector<FKBAmongUsPlayer> innocents;
	public int timeInGame; //segundos
	public Status status;
	public HandlerCommand handlerCommand;
	public ItemStack impostorItem = new ItemStack(Material.IRON_SWORD);
	
	
	public Game(Main _plugin){
		this.plugin = _plugin; // Store the plugin in situations where you need it.
		this.timeInGame = 0;
		this.players = new Vector<FKBAmongUsPlayer>();
		this.impostors = new Vector<FKBAmongUsPlayer>();
		this.innocents = new Vector<FKBAmongUsPlayer>();
		this.running = false;
		this.status = Status.WAITING;
		this.handlerCommand = new HandlerCommand(this, this.plugin);
		
		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		
		for (Player player : _plugin.getServer().getOnlinePlayers()) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); //remover scoreboard
		}
		
		//Commands
		 this.plugin.getCommand("hi").setExecutor(new HiCommand(this.plugin));
		 this.plugin.getCommand("FKBAmongUs").setExecutor(handlerCommand);
		 
		 //Recipes

		 
		 //ThreadGame
		 BukkitScheduler scheduler = plugin.getServer().getScheduler();
	        scheduler.scheduleSyncRepeatingTask(this.plugin, new Runnable() {
	            @Override
	            public void run() {
	            	switch(status) {
	            	case WAITING:
	            		waiting();
	            		break;
	            	case IN_GAME:
	            		inGame();    		
	            		break;
	            	case TALKING:
	            		break;
	            	case VOTING:
	            		break;
	            	}
	            	
	            }
	        }, 0L, 20L); //20L = 1 seg
	        
	}
	
	private void waiting() {
		
		for(int i=0; i < players.size(); i++) {
			FKBAmongUsPlayer _p = players.get(i);
			_p.fkbScoreboard.setStatus(Game.Status.WAITING);
			_p.getPlayer().setScoreboard(_p.fkbScoreboard.getScoreboard());
			
		}
	}

	private void inGame() {
		
		//verifica el cuarto donde se encuentran los jugadores
		checkPlayersLocations();
		//checkNearbyPlayers();
	}
	
	private void checkPlayersLocations() {
		for(int i=0; i < players.size(); i++) {
			FKBAmongUsPlayer _p = players.get(i);
			_p.fkbScoreboard.setStatus(Game.Status.IN_GAME);
			_p.getPlayer().setScoreboard(_p.fkbScoreboard.getScoreboard());
			
			String room = getRoom(_p.getPlayer());
			if(!room.isBlank() && !_p.isInRoom(room)) { 

				_p.getPlayer().sendTitle("", ChatColor.WHITE + "" + ChatColor.BOLD + room , 5, 60, 5);
				_p.setRoom(room);
			}
		}
		timeInGame++;
	}
	
	/*private void checkNearbyPlayers() {
		
		
		for(int i=0; i < impostors.size(); i++) {
			FKBAmongUsPlayer p1 = players.get(i);
			for(int j=0; j < innocents.size(); j++) {
				FKBAmongUsPlayer p2 = players.get(j);
				if(!p1.getPlayer().equals(p2.getPlayer())) {
					if(p1.getPlayer().getLocation().distance(p2.getPlayer().getLocation()) <= 4) {
					    ItemStack item1 = p1.getPlayer().getInventory().getItemInMainHand();
					    if(!item1.equals(this.impostorItem)){
					    	p1.getPlayer().getInventory().setItem(1, this.impostorItem);
					    }
						//plugin.getServer().broadcast-Message(ChatColor.GREEN  + "jugador " + p2.getPlayer().getName() + " cerca de " + p1.getPlayer().getName());
					}else {
						p1.getPlayer().getInventory().setItem(1, new ItemStack(Material.AIR));
					}
				}
			}
		}
	}*/
	
	
    public String getRoom(Player player){
    	String name;
        double[] dimX = new double[2];
        double[] dimZ = new double[2];
        
       //Set<String> locationsNames = plugin.getConfig().getKeys(false);
        
       Set<String> list = plugin.getConfig().getConfigurationSection("map.world.rooms").getKeys(false);
        
        for(int i=0; i <list.toArray().length; i++) {
        	name = list.toArray()[i].toString();
        	
        	Location location = plugin.getConfig().getLocation("map.world.rooms." + name + ".location");
        	int ratio = plugin.getConfig().getInt("map.world.rooms." + name + ".r") + 1;
            
            dimX[0] = location.getX() - ratio;
            dimX[1] = location.getX() + ratio;
            dimZ[0] = location.getZ() - ratio;
            dimZ[1] = location.getZ() + ratio;
            
            if(player.getLocation().getX() > dimX[0] && player.getLocation().getX() < dimX[1] && player.getLocation().getZ() > dimZ[0] && player.getLocation().getZ() < dimZ[1])
                return name;
        	
        }
        
    return "";   
    }
    
    public FKBAmongUsPlayer getFKBAmongUsPlayer(Player p) {
    	for(int i=0; i < players.size(); i++) {
    		if(players.get(i).getPlayer().equals(p)) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    @EventHandler
    public void OnDrop(PlayerDropItemEvent event) {
    	event.setCancelled(true);
    }
    @EventHandler
    public void OnDrag(InventoryDragEvent event) {
    	event.setCancelled(true);
    }
    @EventHandler
    public void OnInventoryClickEvent(InventoryClickEvent event) {
    	event.setCancelled(true);
    }
    @EventHandler
    public void OnPvp(EntityDamageByEntityEvent event) {
    	boolean isImpostor = false;
    	boolean isInnocent = true;
    	if(!(event.getEntity() instanceof Player)) return;
    	Player p1 = (Player) event.getEntity();
    	Player p2 = (Player) event.getDamager();

    	for(int i=0; i < impostors.size(); i++) {
    		if(impostors.get(i).getPlayer().equals(p1)) {
    			isImpostor = true;
    		}
    		if(impostors.get(i).getPlayer().equals(p2)) {
    			isInnocent = false;
    		}
    	}
    	if(!isImpostor) return;
    	if(!isInnocent) return;
    	
    	if(p1.getInventory().getItemInMainHand() == impostorItem) {
    		p2.setGameMode(GameMode.SPECTATOR);
    		FKBAmongUsPlayer pAux = getFKBAmongUsPlayer(p2);
    		pAux.setAlive(false);
    	}
    	
    }
}
