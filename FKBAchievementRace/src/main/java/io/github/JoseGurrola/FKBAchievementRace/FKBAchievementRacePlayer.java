package io.github.JoseGurrola.FKBAchievementRace;

import org.bukkit.entity.Player;

public class FKBAchievementRacePlayer {
	private Player player;
	private Main plugin;
	
	private int achievements;
	
	public FKBAchievementRacePlayer(Main _plugin, Player _player) {
		this.player = _player;
		this.plugin = _plugin;	
		this.achievements = 0;
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public int getNumAchievements() {
		return this.achievements;
	}
	
	public void addAchievement() {
		this.achievements++;
	}
}
