package io.github.FKB.FKBAmongUs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;



public class FKBAmongUsPlayer{
	private Player player;
	private Main plugin;
	public enum PlayerRole{INNOCENT, IMPOSTOR;}
	private String pluginName = ChatColor.AQUA + "[" + ChatColor.DARK_RED + "" + ChatColor.BOLD  + "F" + ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "K" + ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "B" + ChatColor.AQUA + ChatColor.BOLD  + "AmongUs" + ChatColor.AQUA  + "] ";
	
	public boolean alive;
	public PlayerRole role;
	public String room;
	
	public FKBScoreboard fkbScoreboard;

	public FKBAmongUsPlayer(Main _plugin, Player _player) {
		this.player = _player;
		this.plugin = _plugin;	
		this.alive = true;
		this.role = PlayerRole.INNOCENT;
		this.room = "";
		
		this.fkbScoreboard = new FKBScoreboard(this.plugin, "    " + pluginName + "    ");
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public void setRole(PlayerRole _role) {
		this.role = _role;
		this.fkbScoreboard.setRole(_role);
		
	}

	public PlayerRole getRole(){
		return this.role;
	}
	
	public void setPluginName(String _pn){
		this.pluginName = _pn;
	}
	
	public void setRoom(String _room) {
		this.room = _room;
	}
	
	public boolean isInRoom(String _room) {
		return this.room.equals(_room);
	}
	
	public boolean isImpostor() {
		return this.role == PlayerRole.IMPOSTOR;
	}
	
	public void setAlive(boolean _alive) {
		this.alive = _alive;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
}
