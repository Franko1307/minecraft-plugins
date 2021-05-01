package io.github.JoseGurrola.FKBAchievementRace;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;


public class HandlerCommand /*extends BukkitRunnable*/ implements CommandExecutor {
	private final Main plugin;
	private boolean running = false;

	public Vector<FKBAchievementRacePlayer> FKBplayers;
	
	public HandlerCommand(Main plugin) {
		this.plugin = plugin; // Store the plugin in situations where you need it.
		this.FKBplayers = new Vector<FKBAchievementRacePlayer>();
	}

	public boolean isRunning() {
		return running;
	}
	
	public Vector<FKBAchievementRacePlayer> getFKBPlayers() {
		return this.FKBplayers;
	}
	
	public void finishEvent(){
		plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.RED + "The time is over.");
		running = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
            sender.sendMessage("Not enough arguments!");
            return false;
        }
		
		if(args[0].equals("start")) {			
			running = true;
			for(Player p : Bukkit.getServer().getOnlinePlayers()) {
				this.FKBplayers.add(new FKBAchievementRacePlayer(this.plugin, p));
				plugin.getLogger().info("Jugador " + p.getName() + "insertado");
			}
			 

			for (FKBAchievementRacePlayer player : this.FKBplayers) {
				setFKBScoreboard(player);
			}

			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.GREEN + "has started.");
			
		}else if(args[0].equals("stop")){
			running = false;
			for(FKBAchievementRacePlayer p: this.FKBplayers) {
				p.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			}
			plugin.getServer().broadcastMessage(this.plugin.pluginName + ChatColor.RED + "has stopped.");
			
		}else {
			sender.sendMessage("Error in comand!.");
			return false;
		}
		return true;
	}
	
	private void setFKBScoreboard(FKBAchievementRacePlayer _player) {
		int i=15;
		Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("FKBCraft", "dummy", "Carrera de logros");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (FKBAchievementRacePlayer FKBplayer : this.FKBplayers) {
        	Score achievements = obj.getScore(FKBplayer.getPlayer().getName() + " [" + FKBplayer.getNumAchievements() + "]");
        	achievements.setScore(i);
        	i--;
        	if(i < 0) break;
        }
        _player.getPlayer().setScoreboard(board);
	}
	
	public void updateFKBScoreboard(){
		 for (FKBAchievementRacePlayer _player : this.FKBplayers) {
			int i=15;
			Scoreboard board = _player.getPlayer().getScoreboard();
			Objective obj =board.getObjective(DisplaySlot.SIDEBAR);
			for (FKBAchievementRacePlayer FKBplayer : this.FKBplayers) {
	        	Score achievements = obj.getScore(FKBplayer.getPlayer().getName() + " [" + FKBplayer.getNumAchievements() + "]");
	        	achievements.setScore(i);
	        	i--;
	        	if(i < 0) break;
	        }
	        _player.getPlayer().setScoreboard(board);
		 }
	}
	
	
}
