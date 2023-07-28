package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.IUser;
import me.antritus.minecraft_server.wormhole.antsfactions.Property;
import me.antritus.minecraft_server.wormhole.antsfactions.SimpleProperty;
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
	private final Map<UUID, TeleportRequest> requestsSent;
	private final Map<UUID, TeleportRequest> requestsReceived;
	private final List<String> blockedUsers;
	private final Wormhole main;
	private final UUID uniqueId;
	private TeleportRequest latestRequest;
	private final String name;
	public boolean online;
	public long lastOnline;
	public boolean isAcceptingRequests;
	public User(@NotNull Wormhole main, @NotNull Player player){
		this(main, player.getUniqueId());
	}
	public User(@NotNull Wormhole main, @NotNull UUID uniqueId){
		this.requestsSent = new LinkedHashMap<>();
		this.requestsReceived = new LinkedHashMap<>();
		this.blockedUsers = new ArrayList<>();
		this.main = main;
		this.uniqueId = uniqueId;
		this.name = Bukkit.getOfflinePlayer(uniqueId).getName();
	}

	@NotNull
	public Map<UUID, TeleportRequest> getSentRequests(){
		return requestsSent;
	}

	@NotNull
	public Map<UUID, TeleportRequest> getReceivedRequests(){
		return requestsSent;
	}

	@NotNull
	public Wormhole getMain(){
		return main;
	}

	public boolean isBlocked(@NotNull Player player) {
		return blockedUsers.stream().anyMatch(id->id.equalsIgnoreCase(player.getUniqueId().toString()));
	}

	public void block(Player player) {
		if (blockedUsers.stream().anyMatch(id->id.equalsIgnoreCase(player.getUniqueId().toString())))
			return;
		blockedUsers.add(player.getUniqueId().toString());
	}
	public void unblock(Player player) {
		blockedUsers.remove(player.getUniqueId().toString());
	}

	public boolean findMatch(TeleportRequest request, boolean sent) {
		if (sent){
			return requestsSent.values().stream().anyMatch(tp->tp.equals(request));
		}
		return requestsReceived.values().stream().anyMatch(tp->tp.equals(request));
	}

	public List<String> blocked(){
		return blockedUsers;
	}

	public void findLatest(){
		List<TeleportRequest> requests = new ArrayList<>(requestsReceived.values().stream().toList());

		requests.sort((a, b) -> {
			if (a.getTimeEnd() > b.getTimeEnd()) {
				return -1; // a should be sorted before b
			} else if (a.getTimeEnd() < b.getTimeEnd()) {
				return 1; // b should be sorted before a
			} else {
				return 0; // the order of a and b doesn't matter
			}
		});
		latestRequest = requests.get(0);
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

	public TeleportRequest getLatestRequest() {
		return latestRequest;
	}

	public String getName() {
		return name;
	}
}
