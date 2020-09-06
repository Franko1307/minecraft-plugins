package io.github.FKB.FKBAmongUs;
import org.bukkit.entity.Player;



public class FKBAmongUsPlayer{
	private Player player;
	public enum PlayerRole{INNOCENT, IMPOSTOR;}
	
	public boolean alive = true;
	public PlayerRole role = PlayerRole.INNOCENT;

	public FKBAmongUsPlayer(Player _player) {
		this.player = _player;
	}
	
	public void setRole(PlayerRole _role) {
		this.role = _role;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public PlayerRole getRole(){
		return this.role;
	}
}
