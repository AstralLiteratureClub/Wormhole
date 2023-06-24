package me.antritus.minecraft_server.wormhole.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class TeleportRequest {
	private UUID whoRequested;
	private UUID requested;
	private long timeEnd;
	public long teleportEnd = -1;
	public long lastSentMessage = -1;
	public TeleportRequest(Player who, Player requested, long end) {
		this.whoRequested = who.getUniqueId();
		this.requested = requested.getUniqueId();
		this.timeEnd = end;
	}
	public TeleportRequest(UUID who, UUID requested, long end) {
		this.whoRequested = who;
		this.requested = requested;
		this.timeEnd = end;
	}
	public boolean isValid(){
		return (Bukkit.getPlayer(whoRequested) != null && Bukkit.getPlayer(requested) != null && timeEnd > System.currentTimeMillis());
	}

	public UUID getWhoRequested() {
		return whoRequested;
	}

	public UUID getRequested() {
		return requested;
	}

	public long getTimeEnd() {
		return timeEnd;
	}
}
