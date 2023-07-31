package me.antritus.minecraft_server.wormhole.api;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager implements Listener {
	private final Map<String, Request> requestMap = new LinkedHashMap<>();
	private final Map<String, Teleport> teleportMap = new LinkedHashMap<>();
	public final Wormhole wormhole;
	private BukkitTask taskRequest;
	private BukkitTask taskTeleport;

	public TeleportManager(Wormhole wormhole) {
		this.wormhole = wormhole;
	}

	public void run() {
		taskRequest = new BukkitRunnable() {
			@Override
			public void run() {
				requestMap.keySet().forEach(key -> {
					Request request = requestMap.get(key);
					if (request.isAccepted() || request.isDenied()) {
						requestMap.remove(key);
					}
					long end = request.getEnd() - System.currentTimeMillis();
					if (end < 0) {
						requestMap.remove(toId(request.getWhoAsked(), request.getRequested()));
						OfflinePlayer player = wormhole.getServer().getOfflinePlayer(request.getWhoAsked());
						OfflinePlayer requested = wormhole.getServer().getOfflinePlayer(request.getWhoAsked());
						if (player.isOnline()) {
							Wormhole.sendMessage(((Player) player), requested.getName(), "time-ran-out.to.player");
						}
						if (requested.isOnline()) {
							Wormhole.sendMessage(((Player) requested), player.getName(), "time-ran-out.to.requested");
						}
						requestMap.remove(key);
					}
				});
			}
		}.runTaskTimerAsynchronously(wormhole, 0, 20);
		taskTeleport = new BukkitRunnable() {
			@Override
			public void run() {
				teleportMap.keySet().forEach(key -> {
					Teleport teleport = teleportMap.get(key);
					Location location = teleport.getWhoLocation();
					Player player = teleport.getWho();
					Player to = teleport.getTo();
					if (!player.isOnline()) {
						requestMap.remove(toId(player, to));
						if (!to.isOnline()) {
							return;
						}
						Wormhole.sendMessage(to, player, "teleporting.to.player-left");
					} else {
						if (!to.isOnline()) {
							requestMap.remove(toId(player, to));
							Wormhole.sendMessage(player, to, "teleporting.to.offline-to");
							return;
						}
					}
					if ((boolean) wormhole.getCoreSettings().getKnownNonNull("cancel-on-movement").getValue()) {
						if (location.distance(player.getLocation()) > (double) wormhole.getCoreSettings().getKnownNonNull("max-movement-distance").getValue()) {
							Wormhole.sendMessage(to, player, "teleporting.to.");
						}
					}
					long end = teleport.getEnd() - System.currentTimeMillis();
					if (end < 0) {
						teleport(teleport.getWho(), teleport.to);
						requestMap.remove(key);
					} else if (end < 5001) {
						if (teleport.lastTick == -1) {
							teleport.lastTick = System.currentTimeMillis();
							String left = formatSeconds(millisecondsToSeconds(end));
							Wormhole.sendMessage(player, to, "teleporting.to.second", "%seconds%="+left);
							return;
						}
						long time = System.currentTimeMillis();
						long diff = time - teleport.lastTick;
						if (diff>=985){
							teleport.lastTick = System.currentTimeMillis();
							String left = formatSeconds(millisecondsToSeconds(end));
							Wormhole.sendMessage(player, to, "teleporting.to.second", "%seconds%="+left);
						}
					}
				});
			}
		}.runTaskTimerAsynchronously(wormhole, 0, 20);
	}

	public void end() {
		taskRequest.cancel();
		taskTeleport.cancel();
	}

	public void request(@NotNull Player player, @NotNull Player to) {
		String id = toId(player, to);
		Request request = new Request(wormhole, player.getUniqueId(), to.getUniqueId());
		requestMap.put(id, request);
	}

	public void request(@NotNull Request request) {
		requestMap.put(toId(request.getWhoAsked(), request.getRequested()), request);
	}

	public void beginTeleport(@NotNull Player player, @NotNull Player to) {
		Teleport teleport = new Teleport(wormhole, player, to);
		long x = System.currentTimeMillis() - teleport.getEnd();
		if (Wormhole.DEBUG) {
			wormhole.getLogger().warning("[DEBUG] Difference between end and now (Teleport Begin): " + x);
		}
		if (x == 0) {
			teleport(player, to);
			return;
		}
		teleportMap.put(toId(player, to), teleport);
	}

	private void teleport(@NotNull Player player, @NotNull Player to) {
		teleportMap.remove(toId(player, to));
		if (!player.isOnline()) {
			Wormhole.sendMessage(to, player, "teleporting.to.player-left");
		}
		Wormhole.sendMessage(player, to, "teleporting.to.final");
		player.teleportAsync(to.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
	}

	private String toId(Player player, Player to) {
		return player.getUniqueId() + "<->" + to.getUniqueId();
	}

	private String toId(UUID player, UUID to) {
		return player + "<->" + to;
	}

	public boolean hasRequested(@NotNull OfflinePlayer player, @NotNull OfflinePlayer who) {
		String id = toId(player.getUniqueId(), who.getUniqueId());
		Request request = requestMap.get(id);
		return request != null;
	}

	public boolean isTeleporting(@NotNull OfflinePlayer player, @NotNull OfflinePlayer who) {
		String id = toId(player.getUniqueId(), who.getUniqueId());
		Teleport teleport = teleportMap.get(id);
		return teleport != null;
	}

	@NotNull
	public HashMap<UUID, Request> getAllRequests(@NotNull OfflinePlayer player) {
		return getAllRequests(player.getUniqueId());
	}

	@NotNull
	public HashMap<UUID, Request> getAllRequests(@NotNull UUID uuid) {
		HashMap<UUID, Request> requests = new HashMap<>();
		requestMap.keySet().stream().filter(key -> key.startsWith(uuid.toString())).forEach(key -> {
			requests.put(UUID.fromString(key.split("<->")[1]), requestMap.get(key));
				}
		);
		return requests;
	}

	@Nullable
	public Request getRequest(@NotNull OfflinePlayer player, @NotNull OfflinePlayer who) {
		return requestMap.get(toId(player.getUniqueId(), who.getUniqueId()));
	}

	@Nullable
	public Request getRequest(@NotNull UUID player, @NotNull UUID who) {
		return requestMap.get(toId(player, who));
	}

	public int sizeOfSentRequests(@NotNull OfflinePlayer player) {
		return requestMap.keySet().stream().filter(key -> key.startsWith(player.getUniqueId().toString())).toList().size();
	}

	public int sizeOfReceivedRequests(@NotNull OfflinePlayer player) {
		return requestMap.keySet().stream().filter(key -> key.endsWith(player.getUniqueId().toString())).toList().size();
	}

	@NotNull
	public HashMap<UUID, Request> getAllReceivedRequests(@NotNull OfflinePlayer player) {
		return getAllReceivedRequests(player.getUniqueId());
	}

	@NotNull
	public HashMap<UUID, Request> getAllReceivedRequests(@NotNull UUID uuid) {
		HashMap<UUID, Request> requests = new HashMap<>();
		requestMap.keySet().stream().filter(key -> key.endsWith(uuid.toString())).forEach(key -> {
					requests.put(UUID.fromString(key.split("<->")[1]), requestMap.get(key));
				}
		);
		return requests;
	}

	private double millisecondsToSeconds(long milliseconds) {
		return (double) milliseconds / 1000.0;
	}

	private String formatSeconds(double seconds) {
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		return decimalFormat.format(seconds);
	}
}