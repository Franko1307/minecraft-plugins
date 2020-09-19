//§
package io.github.FKB.FKBAmongUs;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import io.github.FKB.FKBAmongUs.utils.Sewer;

import java.util.AbstractMap;
import java.util.HashMap;
public class Game  implements Listener {
	public enum Status{WAITING, IN_GAME, TALKING, VOTING;}
	public String pluginName = ChatColor.AQUA + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.AQUA + ChatColor.BOLD  + "AmongUs" + ChatColor.AQUA  + "] ";
	BukkitScheduler scheduler;
	
	private Main plugin;
	public boolean running;
	public Vector<FKBAmongUsPlayer> players;
	public Vector<FKBAmongUsPlayer> impostors;
	public Vector<FKBAmongUsPlayer> innocents;
	public Vector<ArmorStand> recentDead;
	public int timeInGame; //segundos
	public Status status;
	public HandlerCommand handlerCommand;
	public ItemStack impostorItem;
	public ItemStack meetingItem;
	public ItemStack sewersItem;
	
	public ItemStack leftItem;
	public ItemStack rightItem;
	public ItemStack exitItem;
	
	public int meetingTime;
	public int votingTime;
	public int cooldown;
	public Inventory votingInventory;
	public HashMap<String, Integer> votations;
	public Sewer sewer;
	
	public Game(Main _plugin){
		this.plugin = _plugin; // Store the plugin in situations where you need it.
		this.timeInGame = 0;
		this.players = new Vector<FKBAmongUsPlayer>();
		this.impostors = new Vector<FKBAmongUsPlayer>();
		this.innocents = new Vector<FKBAmongUsPlayer>();
		this.recentDead = new Vector<ArmorStand>();
		this.running = false;
		this.status = Status.WAITING;
		this.handlerCommand = new HandlerCommand(this, this.plugin);
		this.votations = new HashMap<String, Integer>();
		this.meetingTime = plugin.getConfig().getInt("MeetingTime") != 0 ? plugin.getConfig().getInt("MeetingTime") : 60;
		this.votingTime = plugin.getConfig().getInt("VotingTime") != 0 ? plugin.getConfig().getInt("VotingTime") : 20;
		this.cooldown = plugin.getConfig().getInt("VotingTime") != 0 ? plugin.getConfig().getInt("Cooldown") : 30;
		
		sewer = new Sewer(this.plugin);
		
		setGameItems();

		this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
		
		for (Player player : _plugin.getServer().getOnlinePlayers()) {
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); //remover scoreboard
		}
		
		//Commands
		 this.plugin.getCommand("hi").setExecutor(new HiCommand(this.plugin));
		 this.plugin.getCommand("FKBAmongUs").setExecutor(handlerCommand);
		 
		 //Recipes

		 
		 //ThreadGame
		 this.scheduler = plugin.getServer().getScheduler();
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
	            		talking();
	            		break;
	            	case VOTING:
	            		voting();
	            		break;
	            	}
	            	
	            }
	        }, 0L, 20L); //20L = 1 seg
	        
	}
	
	private void waiting() {
		for(FKBAmongUsPlayer p:players) {
			if(!p.getPlayer().isOnline()) continue;
			p.fkbScoreboard.setStatus(Game.Status.WAITING);
			p.getPlayer().setScoreboard(p.fkbScoreboard.getScoreboard());
			
		}
	}

	private void inGame() {
		//verifica el cuarto donde se encuentran los jugadores
		checkPlayersLocations();
		
		checkNearbyDeads();
		
		checkIfImpostorOnSewers();

	}
	


	private void talking() {
		if(meetingTime < 0) {
			status = Status.VOTING;
			meetingTime = plugin.getConfig().getInt("MeetingTime") != 0 ? plugin.getConfig().getInt("MeetingTime") : 60;
			openVotation();
			return;
		}
		
		for(FKBAmongUsPlayer p:players) {
			if(!p.getPlayer().isOnline()) continue;
			p.fkbScoreboard.setStatus(Game.Status.TALKING);
			p.fkbScoreboard.setCountDown(Integer.toString(meetingTime));
			p.getPlayer().setScoreboard(p.fkbScoreboard.getScoreboard());
		}
		meetingTime--;
	}
	
	private void openVotation() {
		int i=0;
		this.votingInventory = Bukkit.createInventory(null, 9, "Vota seleccionando su cabeza");
		for(FKBAmongUsPlayer p:players) {
			if(!p.isAlive()) continue;
			
			ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
	    	SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
	    	playerHeadMeta.setOwningPlayer(p.getPlayer());
	    	playerHeadMeta.setDisplayName(p.getPlayer().getName());
	    	playerHead.setItemMeta(playerHeadMeta);
	    	
	    	this.votingInventory.setItem(i, playerHead);
			i++;
		}
		
		 ItemStack skip = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		 ItemMeta skipMeta = skip.getItemMeta();
		 skipMeta.setDisplayName("Saltar voto");
		 skip.setItemMeta(skipMeta);
		 this.votingInventory.setItem(i,skip);
		 votations.put("Skip", 0);
		for(FKBAmongUsPlayer p:players) {
			if(!p.isAlive()) continue;
			p.getPlayer().openInventory(votingInventory);
			votations.put(p.getPlayer().getName(), 0);
			//Parar movimiento de jugadores
			
			//De aqui se va a EventHandler OnInventoryClick
		}
		
	} 
	
	private void voting() {
		
		
		if(votingTime < 0) { //Cuando se acaba el tiempo de la votación
			votingResults();
			
			return;
		}

		
		for(FKBAmongUsPlayer p:players) {
			if(!p.getPlayer().isOnline()) continue;
			//p.getPlayer().openInventory(votingInventory);
			p.fkbScoreboard.setStatus(Game.Status.VOTING);
			p.fkbScoreboard.setCountDown(Integer.toString(votingTime));
			p.getPlayer().setScoreboard(p.fkbScoreboard.getScoreboard());
			
		}
		votingTime--;
	}
	
	private void votingResults() {
		Location loc;
		Vector<ArmorStand> holograms = new Vector<ArmorStand>();
		FKBAmongUsPlayer eliminated = null;
		
		status = Status.IN_GAME;
		votingTime = plugin.getConfig().getInt("VotingTime") != 0 ? plugin.getConfig().getInt("VotingTime") : 20;
		for(FKBAmongUsPlayer p:players) {
			p.getPlayer().closeInventory();
		}
		
		//Conteo de votos
		Map.Entry<String, Integer> maxVotes = new AbstractMap.SimpleEntry<String, Integer>("Skip", 0);
		for(Map.Entry<String, Integer>  hp :votations.entrySet()) {
			if(hp.getValue() > maxVotes.getValue()) {
				maxVotes = hp;
			}

			//plugin.getServer().broadcastMessage(pluginName +  hp.getKey() + " tiene " + hp.getValue() + " voto(s).");
		}
		
		loc = plugin.getConfig().getLocation("map.world.rooms.MeetingRoom.location");
		loc.setY(loc.getY() + players.size()*0.3);

		ArmorStand votationTitle = (ArmorStand) players.get(0).getPlayer().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		holograms.add(votationTitle);
		votationTitle.setGravity(false);
		votationTitle.setInvulnerable(true);
		votationTitle.setCanPickupItems(false);
		votationTitle.setCustomName(ChatColor.GREEN + "--==== VOTACIONES ====--");
		votationTitle.setCustomNameVisible(true);
		votationTitle.setVisible(false);
		
		loc.setY(loc.getY()-0.3);
		
		for(Map.Entry<String, Integer>  hp :votations.entrySet()) {
			
			ArmorStand stand = (ArmorStand) players.get(0).getPlayer().getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		
			String votes = "";
			for(int j = 0; j < hp.getValue(); j++) {
				votes = votes + "⚡ ";
			}
			/*for(FKBAmongUsPlayer p:players) {
				if(p.isAlive()) {
					votes = votes + "◻ ";
				}
			}*/
			
			holograms.add(stand);
			
			stand.setGravity(false);
    		stand.setInvulnerable(true);
    		stand.setCanPickupItems(false);
    		stand.setCustomName(ChatColor.AQUA + " | " + ChatColor.YELLOW + hp.getKey() + ChatColor.AQUA + " | " + votes + ChatColor.AQUA + " |");
    		stand.setCustomNameVisible(true);
    		stand.setVisible(false);
    		
    		loc.setY(loc.getY()-0.3);
		}

		
		//busca el jugador que tiene mas votos
		for(FKBAmongUsPlayer p:players) {
			if(maxVotes.getKey().equals(p.getPlayer().getName())) {
				eliminated = p;
				//elimina al jugador del juego
				
	    		break;
			} 
		}
		
		//se usan variables finales porque si no el hilo no las acepta.
		final FKBAmongUsPlayer eliminated_ = eliminated;
		final Vector<ArmorStand> holograms_ = holograms;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable(){
			
			public void run(){
				
				//avisar a todos a quien sacaron
				for(FKBAmongUsPlayer p:players) {
					if(eliminated_ != null) {
						p.getPlayer().sendTitle("", ChatColor.WHITE + "" + ChatColor.BOLD + eliminated_.getPlayer().getName() + " a sido funado." , 5, 60, 5);
					}else {
						p.getPlayer().sendTitle("", "Nadie ha sido funado." , 5, 60, 5);
					}
				}
				
				if(eliminated_ != null) {
					eliminated_.getPlayer().setGameMode(GameMode.SPECTATOR);
					eliminated_.setAlive(false);
				}
				
				for(ArmorStand a:holograms_) {
					a.remove();
				}
				holograms_.removeAllElements();
				
				
			}
		}, 150L);
		
		votations.clear();
		
		
		//TODO: activar movimiento
	}
	
	private void checkPlayersLocations() {
		for(int i=0; i < players.size(); i++) {
			FKBAmongUsPlayer _p = players.get(i);
			_p.fkbScoreboard.setStatus(Game.Status.IN_GAME);
			_p.fkbScoreboard.setCountDown("--:--");
			_p.getPlayer().setScoreboard(_p.fkbScoreboard.getScoreboard());
			
			String room = getRoom(_p.getPlayer());
			if(!room.isEmpty() && !_p.isInRoom(room)) { 

				_p.getPlayer().sendTitle("", ChatColor.WHITE + "" + ChatColor.BOLD + room , 5, 60, 5);
				_p.setRoom(room);
			}
		}
		timeInGame++;
	}
	
	private void checkNearbyDeads() {	
		ItemStack air = new ItemStack(Material.AIR);
		
		for(ArmorStand rd:recentDead) {
			Location loc = rd.getLocation();
			
			for (FKBAmongUsPlayer player:players) {
				//plugin.getServer().getLogger().info(player.getPlayer().getName() + " is alive: " + player.isAlive());
				if(!player.isAlive()) continue;
				
				ItemStack item1 = player.getPlayer().getInventory().getItem(0);
				if(item1 == null) item1 = air;
				
				if(player.getPlayer().getLocation().distance(rd.getLocation()) <= 4) {
					plugin.getLogger().info("Hay un muerto cerca de " + player.getPlayer().getName());
					if(item1.equals(meetingItem)) continue;
					player.getPlayer().getInventory().setItem(0, meetingItem);
				}else {
					if(item1.equals(meetingItem)) {
						player.getPlayer().getPlayer().getInventory().setItem(0, air);
					}
				}
			}
		}
	}
	
	private void checkIfImpostorOnSewers() {
		// TODO Auto-generated method stub
		
		for (FKBAmongUsPlayer p:impostors) {
			if ( p.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.IRON_TRAPDOOR) {				
				p.getPlayer().getInventory().setItem(4, sewersItem);
			}
				
			 else 
				 p.getPlayer().getInventory().setItem(4, new ItemStack(Material.AIR));			
		}
		
	}
	
	
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
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
    	FKBAmongUsPlayer FKBPlayer = getFKBAmongUsPlayer(e.getPlayer());
    	if (FKBPlayer == null) return;
    	
    }
    
    @EventHandler
    public void OnDrop(PlayerDropItemEvent event) {
    	if(status == Status.WAITING) return;
    	
    	event.setCancelled(true);
    }
    @EventHandler
    public void OnDrag(InventoryDragEvent event) {
    	if(status == Status.WAITING) return;
    	
    	event.setCancelled(true);
    }
    @EventHandler
    public void OnPvp(EntityDamageByEntityEvent event) {
    	if(status == Status.WAITING) return;
    	
    	boolean isImpostor = false;
    	boolean isInnocent = true;
    	if(!(event.getEntity() instanceof Player)) return;
    	Player p2 = (Player) event.getEntity(); //recibe daño
    	Player p1 = (Player) event.getDamager();  //hace daño
    	
    	plugin.getLogger().info("atacante: " + p1.getName());
    	plugin.getLogger().info("vistima: " + p2.getName());

    	for(int i=0; i < impostors.size(); i++) {
    		if(impostors.get(i).getPlayer().equals(p1)) {
    			isImpostor = true;
    		}
    		if(impostors.get(i).getPlayer().equals(p2)) {
    			isInnocent = false;
    		}
    	}
    	if(!isImpostor) {
    		event.setCancelled(true);
    		return;
    	}
    	if(!isInnocent) {
    		event.setCancelled(true);
    		return;
    	}
    	//plugin.getLogger().info("p1.getInventory().getItemInMainHand(): " + p1.getInventory().getItemInMainHand());
    	if(p1.getInventory().getItemInMainHand().equals(impostorItem)) {
    		//plugin.getLogger().info("el impostor golpeo con la espada");
    		p2.setGameMode(GameMode.SPECTATOR);
    		FKBAmongUsPlayer pAux = getFKBAmongUsPlayer(p2);
    		pAux.setAlive(false);
    		setPlayerDead(p2);
    		
    	}else {
    		event.setCancelled(true);
    	}
    }
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
    	if(status == Status.WAITING) return;
    	
    	event.setCancelled(true);
    	
    	Block block = event.getBlockPlaced();
    	if(block.getType().equals(meetingItem.getType())) {
    		plugin.getServer().broadcastMessage(pluginName + event.getPlayer().getName() + " ha convocado una reunión.");
    		status = Status.TALKING;
    		
    		for(FKBAmongUsPlayer p:players) {
    			p.getPlayer().getInventory().setItem(3, new ItemStack(Material.AIR)); //quitar item de reportar.
				//TODO: tepear alrededor de un bloque
    			p.getPlayer().teleport(plugin.getConfig().getLocation("map.world.rooms.MeetingRoom.location"));
				//TODO: desactivar movimiento
    		}
    	}
    	for(ArmorStand a:recentDead) {
			a.remove();
		}
    	recentDead.removeAllElements();
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
    	
    	if(status == Status.WAITING) return;	

    	event.setCancelled(true);
    	
	    Player clicker = (Player) event.getWhoClicked(); // clickeador
	    ItemStack clicked = event.getCurrentItem(); // item clickeado
	    Inventory inventory = event.getInventory(); // inventario que clickeo
	    if (!inventory.equals(votingInventory)) return; //Si clickeó en votación
    	
	    //ItemStack playerHead = clicked;
    	if(clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
    		votations.replace("Skip", votations.get("Skip") + 1);
    		plugin.getLogger().info(clicker.getName() + " saltó su voto.");
    		//event.setCancelled(true); // cancelar el evento
    		clicker.closeInventory(); //cierra el inventario
    		return;
    	}
    	
    	SkullMeta playerHeadMeta = (SkullMeta) clicked.getItemMeta();

    	for(FKBAmongUsPlayer p:players) {
    		String pName = p.getPlayer().getName();
    		if (playerHeadMeta.getOwningPlayer().getName().equals(pName)) { // detectar a quien votó
	    		//event.setCancelled(true); // cancelar el evento
    			
    			votations.replace(pName, votations.get(pName) + 1);
	    		plugin.getLogger().info(clicker.getName() + " votó por " + pName + " [" + votations.get(pName) + "]");
	    		break;
	    	}
    	}
    	clicker.closeInventory(); //cierra el inventario
	    
    }
    public void setPlayerDead(Player p) {    	
    	Location loc = p.getLocation();
    	loc.setY(loc.getY()-1);
    	
    	ArmorStand stand = (ArmorStand) p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
    	ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD, 1);
    	SkullMeta playerHeadMeta = (SkullMeta) playerHead.getItemMeta();
    	playerHeadMeta.setOwningPlayer(p);
    	playerHead.setItemMeta(playerHeadMeta);
    	
    	stand.getEquipment().setHelmet(playerHead);
    	stand.setGravity(false);
    	stand.setInvulnerable(true);
    	stand.setCanPickupItems(false);
    	stand.setCustomName(p.getName() + " §4ha sido descuartizado!");
    	stand.setCustomNameVisible(true);
    	
    	recentDead.add(stand);  //se añade la ubicación al arreglo de muertos recientes
    }
    
    private void setGameItems() {
    	this.impostorItem = new ItemStack(Material.IRON_SWORD);  //PORHACER: item configurable
		ItemMeta impostorItemMeta = this.impostorItem.getItemMeta();
		impostorItemMeta.setUnbreakable(true);
		impostorItemMeta.setDisplayName("§c§lCuchillo");
		impostorItem.setItemMeta(impostorItemMeta);
		
		this.meetingItem = new ItemStack(Material.REDSTONE_BLOCK); //PORHACER: item config.
		ItemMeta meetingItemMeta = this.meetingItem.getItemMeta();
		meetingItemMeta.setDisplayName("§4§l¡Reportar!");
		meetingItem.setItemMeta(meetingItemMeta);
		
		this.sewersItem = new ItemStack(Material.IRON_TRAPDOOR); //PORHACER: item config.
		ItemMeta sewersItemMeta = this.sewersItem.getItemMeta();
		sewersItemMeta.setDisplayName("§4§l¡Usar Alcantarilla!");
		this.sewersItem.setItemMeta(sewersItemMeta);
		
		this.leftItem = new ItemStack(Material.BLUE_STAINED_GLASS); //PORHACER: item config.
		ItemMeta leftItemMeta = this.leftItem.getItemMeta();
		leftItemMeta.setDisplayName("§4§l¡ Izquierda !");
		this.leftItem.setItemMeta(leftItemMeta);
		
		this.rightItem = new ItemStack(Material.GREEN_STAINED_GLASS); //PORHACER: item config.
		ItemMeta rightItemMeta = this.rightItem.getItemMeta();
		rightItemMeta.setDisplayName("§4§l¡ Derecha !");
		this.rightItem.setItemMeta(rightItemMeta);
		
		this.exitItem = new ItemStack(Material.RED_STAINED_GLASS); //PORHACER: item config.
		ItemMeta exitItemMeta = this.exitItem.getItemMeta();
		exitItemMeta.setDisplayName("§4§l¡ SALIR !");
		this.exitItem.setItemMeta(exitItemMeta);
		
		
    }
    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
    	
    	if (this.status != Status.IN_GAME) return;
    	
    	Player player = event.getPlayer();
        
    	FKBAmongUsPlayer fkb =  getFKBAmongUsPlayer(player);
    	
    	if (!fkb.alive) return;
    	if (!fkb.isImpostor()) return;
    		    	
        Action action = event.getAction();
        ItemStack item = event.getItem();

         if ( action.equals( Action.RIGHT_CLICK_AIR ) || action.equals( Action.RIGHT_CLICK_BLOCK ) ) {
             if ( item != null && item.getType().equals(sewersItem.getType()) ) {
                 player.sendMessage( this.pluginName + ChatColor.RED + "¡ Te has metido en una alcantarilla !" );
                 for (FKBAmongUsPlayer p : players) {
                	 p.getPlayer().hidePlayer(this.plugin,player);                	 
                 }
                 
                 player.getInventory().clear();
                 player.getInventory().setItem(0, leftItem);
                 player.getInventory().setItem(2, rightItem);
                 player.getInventory().setItem(1, exitItem);
                 player.setWalkSpeed(0f);
                 player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250, false, false, false));

             }
             if ( item != null && item.getType().equals(leftItem.getType()) ) {
                 player.sendMessage( this.pluginName + ChatColor.RED + "¡ Izquierda !" );                    
                 Location loc = sewer.goLeft(player.getLocation());                 
                 if (loc != null) player.teleport(new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()));                             
             } 
             if ( item != null && item.getType().equals(rightItem.getType()) ) {
                 player.sendMessage( this.pluginName + ChatColor.RED + "¡ Derecha !" );
                 Location loc = sewer.goRight(player.getLocation());
                 
                 if (loc != null) player.teleport(new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ()));                                 
             } 
             if ( item != null && item.getType().equals(exitItem.getType()) ) {
                 player.sendMessage( this.pluginName + ChatColor.RED + "¡ Saliendo !" );                
                 
                 for (FKBAmongUsPlayer p : players) {
                	 p.getPlayer().showPlayer(this.plugin,player);                	 
                 }                 
                 player.getInventory().clear();                 
                 player.setWalkSpeed(0.2f);
                 player.removePotionEffect(PotionEffectType.JUMP);
                 player.getInventory().setItem(1, impostorItem);
                                 
             } 

         }

    }
    
    @EventHandler
    public void onDisconnectPlayer(InventoryClickEvent event) {
    
    }
}
