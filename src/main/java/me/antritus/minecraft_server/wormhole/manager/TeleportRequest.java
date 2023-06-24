package me.antritus.minecraft_server.wormhole.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeleportRequest {
	private UUID whoRequested;
	private UUID requested;
	private long timeEnd;
	public TeleportRequest(Player who, Player requested, long end) {
		this.whoRequested = who.getUniqueId();
		this.requested = requested.getUniqueId();
		this.timeEnd = end;

	}
	public boolean isValid(){
		return (Bukkit.getPlayer(whoRequested) != null && Bukkit.getPlayer(requested) != null && timeEnd > System.currentTimeMillis());
	}

}
