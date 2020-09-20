package io.github.FKB.FKBAmongUs.utils;

import org.bukkit.Location;

public class Room {
	public Location location1;
	public Location location2;
	public String name;
	
	public Room(String _name, Location _location1, Location _location2) {
		this.name = _name;
		this.location1 = _location1;
		this.location2 = _location2;	
	}
}
