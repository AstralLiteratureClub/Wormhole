package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class User {
	private final HashMap<UUID, TeleportRequest> requests = new HashMap<>();
	private final HashMap<UUID, TeleportRequest> others = new HashMap<>();
	private final UUID uniqueId;
	public boolean online = false;
	public long lastOnline = 0;
	public String name;

	public User(Player player) {
		this.uniqueId = player.getUniqueId();
	}
	public @Nullable TeleportRequest getRequest(@NotNull UUID uniqueId){
		return requests.get(uniqueId);
	}
	public @Nullable TeleportRequest getRequest(@NotNull Player player){
		return requests.get(player.getUniqueId());
	}
	public TeleportRequest teleportRequest(@NotNull Player player){
		name = player.getName();
		long time = Main.TPA_TIME+System.currentTimeMillis();
		TeleportRequest request = new TeleportRequest(uniqueId, player.getUniqueId(), time);
		this.requests.put(player.getUniqueId(), request);
		return request;
	}
	public void receiveRequest(TeleportRequest request) {
		others.put(request.getWhoRequested(), request);
	}

	public void teleport(@NotNull TeleportRequest request){
		Player player = Bukkit.getPlayer(request.getWhoRequested());
		if (player == null){
			throw new RuntimeException("Could not teleport null player!");
		}
		Player requested = Bukkit.getPlayer(request.getRequested());
		if (requested == null){
			throw new RuntimeException("Could not teleport to null player!");
		}
		player.teleportAsync(requested.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	public List<TeleportRequest> requests(){
		return new ArrayList<>(requests.values());
	}
	public List<TeleportRequest> others(){
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
	public void removeRequest(List<TeleportRequest> requests){
		for (TeleportRequest request : requests) {
			this.requests.remove(request.getRequested());
		}
	}
	public void removeOther(List<TeleportRequest> others){
		for (TeleportRequest request : others) {
			this.others.remove(request.getWhoRequested());
		}
	}
	public void removeRequest(TeleportRequest request){
		this.requests.remove(request.getRequested());
	}
	public void removeOther(TeleportRequest other){
		this.others.remove(other.getWhoRequested());
	}
}
