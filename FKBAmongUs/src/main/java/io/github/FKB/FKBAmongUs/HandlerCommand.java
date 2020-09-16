package io.github.FKB.FKBAmongUs;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import io.github.FKB.FKBAmongUs.FKBAmongUsPlayer.PlayerRole;
import io.github.FKB.FKBAmongUs.Game.Status;


public class HandlerCommand /*extends BukkitRunnable*/ implements CommandExecutor {
	private final Game game;
	private final Main plugin;

	//private Vector<Player> players = new Vector<Player>(); 
	//private int counter;
	
	public HandlerCommand(Game _game, Main _plugin) {
		this.game = _game; // Store the plugin in situations where you need it.
		this.plugin = _plugin;
		//this.counter = 100;
	}

	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
            sender.sendMessage(this.game.pluginName + ChatColor.RED + "Not enough arguments!");
            return false;
        }
		switch (args[0].toLowerCase()) {
		case "join":
			playerJoin(sender);
			break;
		case "leave":
			playerLeave(sender);
			break;
		case "list":
			printPlayers();
			break;
		case "start":
			startGame();
			break;
		case "stop":
			stopGame();
			break;
		case "setmaxplayers":
			if(args.length < 2) return false;	
			setMaxPlayers(sender, Integer.parseInt(args[1]));
			break;
		case "setminplayers":
			if(args.length < 2) return false;	
			setMinPlayers(sender, Integer.parseInt(args[1]));
			break;
		case "setnumberimpostors":
			if(args.length < 2) return false;	
			setNumberImpostors(sender, Integer.parseInt(args[1]));
			break;
		case "setmeetingtime":
			if(args.length < 2) return false;	
			setMeetingTime(sender, Integer.parseInt(args[1]));
			break;
		case "setvotingtime":
			if(args.length < 2) return false;	
			setVotingTime(sender, Integer.parseInt(args[1]));
			break;
		case "setlobby":
			setLobby(sender);
			break;
		case "setmapcoords":
			if(args.length < 3) return false;
			setMapCoords(sender, args[1], Integer.parseInt(args[2]));
			break;
		case "getplayers":
			printPlayers(sender);
			break;
		default:
			sender.sendMessage("Error in comand!.");
			return false;
		}
	
		return true;
	}
	/*
	 * Ingresa a un jugador a la partida, siempre y cuando se cumplan las condiciones.
	 */
	private void playerJoin(CommandSender sender) {
		FKBAmongUsPlayer player = new FKBAmongUsPlayer(this.plugin, (Player)sender);
		player.getPlayer().saveData(); //Guarda los datos del jugador que quiere ingresar a partida, esto se usa para que al terminar la partida se regrese al lugar donde estaba
		if(isPlayerIn(player) == -1) {
			try {
				int maxPlayers = plugin.getConfig().getInt("MaxPlayers") != 0 ? plugin.getConfig().getInt("MaxPlayers") : 10; //Se obtiene numero máximo de jugadores, si no existe, se pone 10 por default
				
				if(game.players.size() > maxPlayers) { //Si la partida está llena, no meter al jugador.
					sender.sendMessage(this.game.pluginName + ChatColor.RED + "The match is full.");
					return;
				}
				//Se obtiene el mundo y las coordenadas del lobby
				Location loc = plugin.getConfig().getLocation("map.world.lobby.location");
				player.getPlayer().setHealth(20);
				player.setRole(FKBAmongUsPlayer.PlayerRole.INNOCENT);
				game.players.addElement(player);  //Se ingresa al arreglo de jugadores.
				player.getPlayer().teleport(loc); //teletransporta jugador al lobby
				plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.YELLOW + player.getPlayer().getName() + ChatColor.AQUA + " joined (" + this.game.players.size() + "/"+ maxPlayers + ").");
				
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.RED + "There is no lobby to join, contact an admin.");
				plugin.getLogger().info("There is no lobby to join FKB Among Us. Configure a lobby please. " + e.getStackTrace());
			}	
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "You are already in the game!.");
		}
	}
	
	/*
	 * PORHACER: funcionalidad para cuando este la partida en curso y se salga.
	 * Quita a un jugador de la partida
	 * */
	private void playerLeave(CommandSender sender) {
		int maxPlayers = plugin.getConfig().getInt("MaxPlayers") != 0 ? plugin.getConfig().getInt("MaxPlayers") : 10; //Se obtiene numero máximo de jugadores, si no existe, se pone 10 por default
		FKBAmongUsPlayer _player = new FKBAmongUsPlayer(this.plugin, (Player)sender);
		int aux = isPlayerIn(_player);
		if(aux != -1) { //Si el jugador está en partida no iniciada:
			game.getFKBAmongUsPlayer(game.players.get(aux).getPlayer()).getPlayer().loadData(); //Carga la información que tenia antes de entrar a la partida. (Lo tepea a donde estaba antes de iniciar)
			
			_player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); //remover scoreboard
			game.players.remove(aux); //Quita al jugador del arreglo de jugadores.
			
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.YELLOW + _player.getPlayer().getName() + ChatColor.AQUA + " left (" + this.game.players.size() + "/" + maxPlayers + ")."); //informa que se fue un jugador
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "You are not in a game.");
		}
	}
	
	public void printPlayers() {
		if(game.players.size() > 0) {
		plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.GREEN + "Players:");
		for(int i=0; i < game.players.size();i++) {
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.GREEN + game.players.elementAt(i).getPlayer().getName());
		}
		}else {
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.GREEN + "No players in game.");
		}
	}
	
	/*
	 * Empieza el juego, seleccionando impostores y los tepea al spawn del mapa. 
	 */
	private void startGame() {		
		if(game.status != Status.WAITING) return;
		int minPlayers = plugin.getConfig().getInt("MinPlayers") != 0 ? plugin.getConfig().getInt("MinPlayers") : 4; //Se obtiene numero minimo de jugadores, si no existe, se pone 4 por default
		int numImpostors = plugin.getConfig().getInt("NumberOfImpostor") != 0 ? plugin.getConfig().getInt("NumberOfImpostor") : 1; //Se obtiene numero de impostores, si no existe, se pone 1 por default
		
		if(game.players.size() < minPlayers) { //No empieza si no hay minimo de jugadores
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.RED + "Not enough players for start the game. Are needed at least " + minPlayers + " players.");
			return;
		}
		
		try {
			//plugin.innocents = plugin.players; //Se copian todos los jugadores al arreglo de inocentes
			int i = 0;
			while(i < numImpostors) { //elegir n veces impostor
				//FKBAmongUsPlayer impostor = plugin.players2.elementAt(ThreadLocalRandom.current().nextInt(plugin.players2.size())); 
				int random = ThreadLocalRandom.current().nextInt(game.players.size());
				if(game.players.get(random).getRole() != PlayerRole.IMPOSTOR) {
					game.players.elementAt(random).setRole(PlayerRole.IMPOSTOR); //Se obtiene un impostor del arreglo de jugadores al azar
					i++;
				}
				//plugin.innocents.remove(impostor); //Se elimina del arreglo de inocentes
			}
			
			//Se informa a los jugadores el rol que tienen
			for(i=0; i < game.players.size(); i++) {
				FKBAmongUsPlayer p = game.getFKBAmongUsPlayer(game.players.get(i).getPlayer());
				p.setAlive(true);
				p.getPlayer().setGameMode(GameMode.SURVIVAL); //se ponen en survival
				p.getPlayer().getInventory().clear(); //se les limpia el inventario
				if(p.getRole() == PlayerRole.INNOCENT) { //Si son inocentes
					game.innocents.add(p);
					p.getPlayer().sendTitle(ChatColor.BLUE + "Innocent", ChatColor.GRAY + "There are " + ChatColor.RED + numImpostors + " Impostor(s) " + ChatColor.GRAY + " among us", 5, 100, 5);
				}else {			//Si son impostores
					p.getPlayer().getInventory().setItem(1, this.game.impostorItem); //Se les da la espada
					game.impostors.add(p);
					p.getPlayer().sendTitle(ChatColor.DARK_RED + "Impostor", ChatColor.GRAY + "Kill them!", 5, 100, 5);
				}
				//Se obtiene el mundo y las coordenadas del lobby
				Location loc = plugin.getConfig().getLocation("map.world.rooms.MeetingRoom.location");
				p.getPlayer().teleport(loc);
			}
			
			for(i=0; i < this.game.recentDead.size(); i++) {
				this.game.recentDead.get(i).remove();
			}
			
			game.status = Status.IN_GAME; //Se pasa el estado del juego a in-game
			
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.GREEN + "has started.");
			
		}catch(Exception e) {
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.RED + "There is no MeetingRoom to start the game, contact an admin.");
			plugin.getLogger().info("There is no MeetingRoom to start the game to join FKB Among Us. Configure a lobby please. " + e.getStackTrace());
		}
	}
	
	private void stopGame() {
		if(game.status != Status.WAITING) {
			game.status = Status.WAITING;
			for(FKBAmongUsPlayer p: game.players) {
				//FKBAmongUsPlayer p = this.game.getFKBAmongUsPlayer(this.game.players.get(i).getPlayer());
				p.setAlive(false);
				
				p.getPlayer().setGameMode(GameMode.SURVIVAL); //se ponen en survival
				p.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()); //remover scoreboard
				p.getPlayer().loadData(); //Carga la información que tenia antes de entrar a la partida. (Lo tepea a donde estaba antes de iniciar)
			
			}
			
			for(ArmorStand a:this.game.recentDead) {
				a.remove();
			}
			
			this.game.players.removeAllElements();
			this.game.impostors.removeAllElements();
			this.game.innocents.removeAllElements();
			this.game.recentDead.removeAllElements();
			this.game.timeInGame = 0;
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.RED + "has stopped.");
		}
	}
	
	/*
	 * Se establece el lobby de espera antes de iniciar el juego. Se guarda en config.yml (se guarda el nombre del mundo, coordenadas x,y,z y la dirección a la que mira el jugador) 
	 * */
	private boolean setLobby(CommandSender sender) {
		Player _player = (Player)sender;
		try {
			Location loc =_player.getLocation();
			
			plugin.getConfig().set("map.world.lobby.location", loc);

	        plugin.saveConfig();
	        sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Established lobby in world: " + loc.getWorld().getName() + "(" + loc.getX() + ", " + loc.getY() +", " + loc.getZ() + ").");
	        return true;
		}catch(Exception e) {
			sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "Could not establish lobby");
			return false;
		}
	}
	
	/*
	 * Se establece la sala de reuniones del mapa antes de iniciar el juego. Se guarda en config.yml (se guarda el nombre del mundo, coordenadas x,y,z y la dirección a la que mira el jugador) 
	 * */
	private boolean setMapCoords(CommandSender sender, String _roomName, int ratio) {
		Player _player = (Player)sender;
		if(_roomName.isEmpty() || ratio == 0) return false;
		try {
			
			Location loc =_player.getLocation();
			
			plugin.getConfig().set("map.world.rooms." + _roomName + ".r", ratio);
			plugin.getConfig().set("map.world.rooms." + _roomName + ".location", loc);
						
	        plugin.saveConfig();
	        sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Established " + _roomName +" in world: " + loc.getWorld().getName() + "(" + loc.getX() + ", " + loc.getY() +", " + loc.getZ() + ").");
	        return true;
		}catch(Exception e) {
			sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "Could not establish " + _roomName);
			return false;
		}
	}
	
	/*
	 * PORHACER: vaildar cuando no se mande un numero si no letras a lo pendejo
	 * Establece el numero maximo de jugadores por partida. Se guarda en config.yml
	 * */
	private boolean setMaxPlayers(CommandSender sender, int i) {
		if(i >= 4) {
			try {
				plugin.getConfig().set("MaxPlayers", i);
				plugin.saveConfig();
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Maximum number of players set (" + i + ")");
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "Could not establish maximun number of players");
				return false;
			}
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "Maximum number of players must be at least 4");
			return false;
		}
		return true;
	}
	
	/*
	 * PORHACER: validar cuando no se mande un numero si no letras a lo pendejo
	 * Establece el numero minimo de jugadores por partida. Se guarda en config.yml
	 * */
	private boolean setMinPlayers(CommandSender sender, int i) {
		if(i >= 4) {
			try {
				plugin.getConfig().set("MinPlayers", i);
				plugin.saveConfig();
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Minimum number of players set (" + i + ")");
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "Could not establish Minimum number of players");
				return false;
			}
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "Minimum number of players must be at least 4");
			return false;
		}
		return true;
	}
	
	
	/*
	 * Establece el numero de impostores por partida (solo se aceptan 1, 2 o 3). Se guarda en config.yml
	 * */
	private boolean setNumberImpostors(CommandSender sender, int i) {
		if(i == 1 || i == 2 || i == 3) {
			try {
				plugin.getConfig().set("NumberOfImpostor", i);
				plugin.saveConfig();
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Número de impostores establecido (" + i + ").");
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "No se pudo establecer el número de impostores");
				return false;
			}
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "Numero de impostores tiene que ser 1, 2 o 3.");
			return false;
		}
		
	return true;
	}
	
	private boolean setMeetingTime(CommandSender sender, int i) {
		if(i >= 10) {
			try {
				plugin.getConfig().set("MeetingTime", i);
				plugin.saveConfig();
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Tiempo para hablar establecido (" + i + " segundos).");
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "No se pudo establecer el tiempo para hablar.");
				return false;
			}
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "El minimo son 10 segundos");
			return false;
		}
		return true;
	}
	
	private boolean setVotingTime(CommandSender sender, int i) {
		if(i >= 10) {
			try {
				plugin.getConfig().set("VotingTime", i);
				plugin.saveConfig();
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Tiempo para votar establecido (" + i + " segundos).");
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "No se pudo establecer el tiempo para votar.");
				return false;
			}
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "El minimo son 10 segundos");
			return false;
		}
		return true;
	}
	
	public void printPlayers(CommandSender sender) {
    	for(int i=0; i < game.players.size();i++) {
    		FKBAmongUsPlayer p = game.players.get(i);
    		sender.sendMessage(this.game.pluginName + ChatColor.AQUA  + p.getPlayer().getName() + " §b[" + (p.isImpostor() ?  "§4Impostor" : "§aInnocent") + "§b] [" + (p.isAlive() ? "§aAlive" : "§4Dead") +"§b]");
    	}
    } 

	/*=============================================================================================================
	 * 
	 * 								FUNCIONES AUXILIARES
	 * 
	 *============================================================================================================= */
	private int isPlayerIn(FKBAmongUsPlayer _player) {
		for(int i = 0; i < game.players.size(); i++) {
			if(game.players.get(i).getPlayer().getName() == _player.getPlayer().getName()) {
				return i;
			}
		}
		return -1;
	}
}
