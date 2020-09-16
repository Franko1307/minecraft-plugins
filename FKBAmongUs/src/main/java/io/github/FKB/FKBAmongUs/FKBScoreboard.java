package io.github.FKB.FKBAmongUs;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import io.github.FKB.FKBAmongUs.FKBAmongUsPlayer.PlayerRole;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FKBScoreboard {
	private Main plugin;
	private Scoreboard scoreboard;
	private String pluginName;
	private  Objective objective;
	private Score s0, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10;
	protected String status;
	protected String role;
	protected String countDown;
	
	public FKBScoreboard(Main _plugin, String _pluginName) {
		this.plugin = _plugin;
		this.pluginName = _pluginName;
		this.scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		this.status = "status";
		this.role = "role";
		this.countDown = "--:--";
		
		objective = this.scoreboard.registerNewObjective("scoreboard", "dummy", "displayname");
	    objective.setDisplayName(this.pluginName);
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	public Scoreboard getScoreboard() {
	    
		s0 = objective.getScore(" ");
	    s1 = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD +"STATUS:");
	    s2 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC + status);
	    s3 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC + "        ");
	    s4 = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "ROLE:");
	    s5 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC  + role);
	    s6 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC  + "          ");
	    s7 = objective.getScore(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "TIME:");
	    s8 = objective.getScore(ChatColor.YELLOW + countDown);
	    s9 = objective.getScore(ChatColor.AQUA + "             ");
	    s10 = objective.getScore(ChatColor.AQUA + "            ");
	    
	    s0.setScore(10);
	    s1.setScore(9);
	    s2.setScore(8);
	    s3.setScore(7);
	    s4.setScore(6);
	    s5.setScore(5);
	    s6.setScore(4);
	    s7.setScore(3);
	    s8.setScore(2);
	    s9.setScore(1);
	    s10.setScore(0);
		return this.scoreboard;
	}
	
	public void setStatus(Game.Status _status) {
		objective.unregister();
		objective = this.scoreboard.registerNewObjective("scoreboard", "dummy", "displayname");
		objective.setDisplayName(this.pluginName);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		switch(_status) {
		case WAITING:
			this.status = "Esperando jugadores...";
			s2 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC + status);
			s2.setScore(8);
			break;
		case IN_GAME:
			this.status = "En juego";
			s2 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC + status);
			s2.setScore(8);
    		break;
    	case TALKING:
    		this.status = "Hablando";
			s2 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC + status);
			s2.setScore(8);
    		break;
    	case VOTING:
    		this.status = "Votando";
			s2 = objective.getScore(ChatColor.AQUA + "" + ChatColor.ITALIC + status);
			s2.setScore(8);
    		break;
		default:
			break;
		}
		
		//s2.setScore(8);
	}	
	
	public void setRole(FKBAmongUsPlayer.PlayerRole _role) {
		objective.unregister();
		objective = this.scoreboard.registerNewObjective("scoreboard", "dummy", "displayname");
		objective.setDisplayName(this.pluginName);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		this.role = (_role == PlayerRole.IMPOSTOR) ? (ChatColor.DARK_RED + "Impostor") : (ChatColor.GREEN + "Innocent");
		
		s5 = objective.getScore(this.role);
		//s5.setScore(5);
	}
	
	public void setCountDown(String _countDown) {
		objective.unregister();
		objective = this.scoreboard.registerNewObjective("scoreboard", "dummy", "displayname");
		objective.setDisplayName(this.pluginName);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		this.countDown = _countDown;
		s8 = objective.getScore(ChatColor.YELLOW + this.countDown);
		//s8.setScore(2);
		
	}
}
