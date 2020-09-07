package io.github.FKB.FKBAmongUs;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import io.github.FKB.FKBAmongUs.FKBAmongUsPlayer.PlayerRole;


public class HandlerCommand /*extends BukkitRunnable*/ implements CommandExecutor {
	private final Game game;
	private final Main plugin;
	private boolean running = false;

	//private Vector<Player> players = new Vector<Player>(); 
	//private int counter;
	
	public HandlerCommand(Game _game, Main _plugin) {
		this.game = _game; // Store the plugin in situations where you need it.
		this.plugin = _plugin;
		//this.counter = 100;
	}

	public boolean isRunning() {
		return running;
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
		case "setlobby":
			setLobby(sender);
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
		FKBAmongUsPlayer player = new FKBAmongUsPlayer((Player)sender);
		if(isPlayerIn(player) == -1) {
			try {
				player.getPlayer().saveData(); //Guarda los datos del jugador que quiere ingresar a partida, esto se usa para que al terminar la partida se regrese al lugar donde estaba
				int maxPlayers = plugin.getConfig().getInt("MaxPlayers") != 0 ? plugin.getConfig().getInt("MaxPlayers") : 10; //Se obtiene numero máximo de jugadores, si no existe, se pone 10 por default
				
				if(game.players.size() > maxPlayers) { //Si la partida está llena, no meter al jugador.
					sender.sendMessage(this.game.pluginName + ChatColor.RED + "The match is full.");
					return;
				}
				//Se obtiene el mundo y las coordenadas del lobby
				String w = plugin.getConfig().get("map.world.name").toString();
				double x = (double) plugin.getConfig().get("map.world.lobby.x");
				double y = (double) plugin.getConfig().get("map.world.lobby.y");
				double z = (double) plugin.getConfig().get("map.world.lobby.z");
				double d = (double) plugin.getConfig().get("map.world.lobby.d");
				plugin.getLogger().info("w = " + w + ", x = " + x + ", y = " + y + ", z = " + z);
				Location loc = new Location(plugin.getServer().getWorld(w), x, y, z);
				loc.setYaw((float)d); //Se pone la dirección de mirada al jugador.
				
				player.getPlayer().teleport(loc); //teletransporta jugador al lobby
				game.players.addElement(player);  //Se ingresa al arreglo de jugadores.
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
		FKBAmongUsPlayer _player = new FKBAmongUsPlayer((Player)sender);
		int aux = isPlayerIn(_player);
		if(aux != -1) { //Si el jugador está en partida no iniciada:
			game.players.get(aux).getPlayer().loadData(); //Carga la información que tenia antes de entrar a la partida. (Lo tepea a donde estaba antes de iniciar)
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
		if(running == true) return;
		int minPlayers = plugin.getConfig().getInt("MinPlayers") != 0 ? plugin.getConfig().getInt("MinPlayers") : 4; //Se obtiene numero minimo de jugadores, si no existe, se pone 4 por default
		int numImpostors = plugin.getConfig().getInt("NumberOfImpostor") != 0 ? plugin.getConfig().getInt("NumberOfImpostor") : 1; //Se obtiene numero de impostores, si no existe, se pone 1 por default
		
		if(game.players.size() < minPlayers) { //No empieza si no hay minimo de jugadores
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.RED + "Not enough players for start the game. Are needed at least " + minPlayers + " players.");
			return;
		}
		
		//plugin.innocents = plugin.players; //Se copian todos los jugadores al arreglo de inocentes
		int i = 0;
		while(i < numImpostors) { //elegir n veces impostor
			//FKBAmongUsPlayer impostor = plugin.players2.elementAt(ThreadLocalRandom.current().nextInt(plugin.players2.size())); 
			int random = ThreadLocalRandom.current().nextInt(game.players.size());
			if(game.players.elementAt(random).getRole() != PlayerRole.IMPOSTOR) {
				game.players.elementAt(ThreadLocalRandom.current().nextInt(game.players.size())).setRole(PlayerRole.IMPOSTOR); //Se obtiene un impostor del arreglo de jugadores al azar
				i++;
			}
			//plugin.innocents.remove(impostor); //Se elimina del arreglo de inocentes
		}
		
		//Se informa a los jugadores el rol que tienen
		for(i=0; i < game.players.size(); i++) {	
			FKBAmongUsPlayer p = game.players.get(i);
			if(p.getRole() == PlayerRole.INNOCENT)
				p.getPlayer().sendTitle(ChatColor.BLUE + "Innocent", ChatColor.GRAY + "There are " + ChatColor.RED + numImpostors + " Impostor(s) " + ChatColor.GRAY + " among us", 5, 100, 5);
			else
				p.getPlayer().sendTitle(ChatColor.DARK_RED + "Impostor", ChatColor.GRAY + "Kill them!", 5, 100, 5);
		}

		
		//TEPEAR AL SPAWN DEL MAPA A LOS JUGADORES.
		
		
		running = true;
		plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.GREEN + "has started.");
	}
	
	private void stopGame() {
		if(running != false) {
			running = false;
			this.game.players.removeAllElements();
			plugin.getServer().broadcastMessage(this.game.pluginName + ChatColor.RED + "has stopped.");
		}
	}
	
	/*
	 * Se establece el lobby de espera antes de iniciar el juego. Se guarda en config.yml (se guarda el nombre del mundo, coordenadas x,y,z y la dirección a la que mira el jugador) 
	 * */
	private boolean setLobby(CommandSender sender) {
		Player _player = (Player)sender;
		try {
			double x = _player.getLocation().getX();
			double y = _player.getLocation().getY();
			double z = _player.getLocation().getZ();
			String w = _player.getWorld().getName();
			double d = (double) _player.getLocation().getYaw();
			
			plugin.getConfig().set("map.world.name", w);
			plugin.getConfig().set("map.world.lobby.x", x);
			plugin.getConfig().set("map.world.lobby.y", y);
			plugin.getConfig().set("map.world.lobby.z", z);
			plugin.getConfig().set("map.world.lobby.d", d);
			
	        plugin.saveConfig();
	        sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Established lobby in world: " + w + "(" + x + ", " + y +", " + z + ").");
	        return true;
		}catch(Exception e) {
			sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "Could not establish lobby");
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
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN + "Number of impostors established (" + i + ")");
			}catch(Exception e) {
				sender.sendMessage(this.game.pluginName + ChatColor.GREEN  + "Could not establish number of impostors");
				return false;
			}
		}else {
			sender.sendMessage(this.game.pluginName + ChatColor.RED + "Number of impostors can only be 1, 2 or 3.");
			return false;
		}
		
	return true;
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
