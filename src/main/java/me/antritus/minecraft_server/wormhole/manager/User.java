package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.IUser;
import me.antritus.minecraft_server.wormhole.antsfactions.Property;
import me.antritus.minecraft_server.wormhole.antsfactions.SimpleProperty;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.api.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class User implements IUser {
	private final Map<String, SimpleProperty<?>> settings = new LinkedHashMap<>();
	private final List<String> blockedUsers;
	private final Wormhole main;
	private final UUID uniqueId;
	private Request latestRequest;
	private Request latestSentRequest;
	private final String name;
	public boolean online;
	public long lastOnline;
	public boolean isAcceptingRequests;
	public User(@NotNull Wormhole main, @NotNull Player player){
		this(main, player.getUniqueId());
	}
	public User(@NotNull Wormhole main, @NotNull UUID uniqueId){
		this.blockedUsers = new ArrayList<>();
		this.main = main;
		this.uniqueId = uniqueId;
		this.name = Bukkit.getOfflinePlayer(uniqueId).getName();
	}


	@NotNull
	public Wormhole getMain(){
		return main;
	}

	public boolean isBlocked(Player player) {
		String playerUUIDString = player.getUniqueId().toString();
		return blockedUsers.stream().anyMatch(id -> id.equals(playerUUIDString));
	}

	public void block(Player player) {
		String playerUUIDString = player.getUniqueId().toString();
		if (blockedUsers.stream().anyMatch(id -> id.equals(playerUUIDString))) {
			return;
		}
		blockedUsers.add(playerUUIDString);
	}

	public void unblock(Player player) {
		String playerUUIDString = player.getUniqueId().toString();
		blockedUsers.remove(playerUUIDString);
	}

	public List<String> blocked() {
		return blockedUsers;
	}

	public void findLatest(){
		TeleportManager manager = main.getTeleportManager();
		HashMap<UUID, Request> requests = manager.getAllReceivedRequests(uniqueId);
		if (requests.isEmpty()){
			latestRequest = null;
			return;
		}
		List<Request> requestList = new ArrayList<>(requests.values());
		requestList.sort((a, b) -> Long.compare(b.getEnd(), a.getEnd()));
		latestRequest = requestList.get(0);
	}
	public void findLatestSent(){
		TeleportManager manager = main.getTeleportManager();
		HashMap<UUID, Request> requests = manager.getAllRequests(uniqueId);
		if (requests.isEmpty()){
			latestSentRequest = null;
			return;
		}
		List<Request> requestList = new ArrayList<>(requests.values());
		requestList.sort((a, b) -> Long.compare(b.getEnd(), a.getEnd()));
		latestSentRequest = requestList.get(0);
	}

	public Request getLatestSentRequest() {
		return latestSentRequest;
	}

	@Override
	@Nullable
	public Property<String, ?> get(@NotNull String key) {
		return settings.get(key);
	}

	@Override
	public @NotNull Map<String, SimpleProperty<?>> get() {
		return settings;
	}

	@Override
	public void setting(@NotNull String key, Object value) {
		settings.putIfAbsent(key, new SimpleProperty<>(key, value));
		settings.get(key).setValueObj(value);
	}

	public List<String> getBlockedUsers() {
		return blockedUsers;
	}

	public UUID getUniqueId() {
		return uniqueId;
	}

	public Request getLatestRequest() {
		return latestRequest;
	}

	public String getName() {
		return name;
	}
}
