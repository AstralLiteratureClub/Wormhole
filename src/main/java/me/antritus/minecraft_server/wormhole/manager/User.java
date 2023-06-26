package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class User {
	private final HashMap<String, Boolean> blockedUsers = new HashMap<>();
	private final HashMap<UUID, TeleportRequest> requests = new HashMap<>();
	private final HashMap<UUID, TeleportRequest> others = new HashMap<>();
	private final UUID uniqueId;
	public boolean online = false;
	public boolean acceptingRequests = true;
	public long lastOnline = 0;
	public String name;

	public User(Player player) {
		this.uniqueId = player.getUniqueId();
	}

	public @Nullable TeleportRequest getRequest(@NotNull UUID uniqueId, TeleportRequest.Type type) {
		if (type.equals(TeleportRequest.Type.SENDER)) {
			return requests.get(uniqueId);
		} else {
			return others.get(uniqueId);
		}
	}

	public @Nullable TeleportRequest getRequest(@NotNull Player player, TeleportRequest.Type type) {
		return getRequest(player.getUniqueId(), type);
	}

	public TeleportRequest teleportRequest(@NotNull Player player) {
		name = player.getName();
		long time = Wormhole.REQUEST_TIME + System.currentTimeMillis();
		TeleportRequest request = new TeleportRequest(uniqueId, player.getUniqueId(), time);
		this.requests.put(player.getUniqueId(), request);
		return request;
	}

	public void receiveRequest(TeleportRequest request) {
		others.put(request.getWhoRequested(), request);
	}

	public void teleport(@NotNull TeleportRequest request) {
		Player player = Bukkit.getPlayer(request.getWhoRequested());
		if (player == null) {
			throw new RuntimeException("Could not teleport null player!");
		}
		Player requested = Bukkit.getPlayer(request.getRequested());
		if (requested == null) {
			throw new RuntimeException("Could not teleport to null player!");
		}
		if (request.teleportEnd < 0){
			this.requests.remove(request.getRequested());
		}
		User user = Wormhole.manager.getUser(request.getRequested());
		user.removeOther(request);
		player.teleportAsync(requested.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	public List<TeleportRequest> requests() {
		return new ArrayList<>(requests.values());
	}

	public List<TeleportRequest> others() {
		return new ArrayList<>(others.values());
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public boolean isOnline() {
		return online;
	}

	public long getLastOnline() {
		return lastOnline;
	}

	public void removeRequest(List<TeleportRequest> requests) {
		for (TeleportRequest request : requests) {
			this.requests.remove(request.getRequested());
		}
	}

	public void removeOther(List<TeleportRequest> others) {
		for (TeleportRequest request : others) {
			this.others.remove(request.getWhoRequested());
		}
	}

	public void removeRequest(TeleportRequest request) {
		this.requests.remove(request.getRequested());
	}

	public void removeOther(TeleportRequest other) {
		this.others.remove(other.getWhoRequested());
	}

	public boolean isBlocked(@NotNull Player player) {
		return blockedUsers.get(player.getUniqueId().toString().toLowerCase()) != null;
	}

	public void block(Player player, boolean v) {
		if (v) {
			blockedUsers.put(player.getUniqueId().toString(), true);
		} else {
			blockedUsers.remove(player.getUniqueId().toString());
		}
	}

	public boolean findMatch(TeleportRequest request) {
		for (TeleportRequest value : others.values()) {
			if (value.equals(request)){
				return true;
			}
		}
		return false;
	}
}
