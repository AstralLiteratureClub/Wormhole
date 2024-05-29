package bet.astral.wormhole.api;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.events.request.TpRequestRanOutEvent;
import bet.astral.wormhole.events.teleport.TpBeginEvent;
import bet.astral.wormhole.events.teleport.TpEvent;
import bet.astral.wormhole.events.teleport.TpMoveEvent;
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
import java.util.*;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TeleportManager implements Listener {
	private final Map<String, Request> requestMap = new LinkedHashMap<>();
	private final Map<String, Teleport> teleportMap = new LinkedHashMap<>();
	private final Map<String, Teleport> ignoreMap = new LinkedHashMap<>();
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
				List<String> deleteList = new ArrayList<>();
				requestMap.keySet().forEach(key -> {
					Request request = requestMap.get(key);
					if (request.isAccepted() || request.isDenied()) {
						deleteList.add(key);
					} else {
						long end = request.getEnd() - System.currentTimeMillis();
						if (end < 0) {
							deleteList.add(key);
							OfflinePlayer player = wormhole.getServer().getOfflinePlayer(request.getWhoAsked());
							OfflinePlayer requested = wormhole.getServer().getOfflinePlayer(request.getWhoAsked());
							TpRequestRanOutEvent event = new TpRequestRanOutEvent(wormhole, player, requested, request);
							event.callEvent();
							if (player.isOnline()) {
								Wormhole.sendMessage(((Player) player), requested.getName(), "time-ran-out.to.player");
							}
							if (requested.isOnline()) {
								Wormhole.sendMessage(((Player) requested), player.getName(), "time-ran-out.to.requested");
							}
						}
					}
				});
				deleteList.forEach(requestMap::remove);
			}
		}.runTaskTimerAsynchronously(wormhole, 0, 20);
		taskTeleport = new BukkitRunnable() {
			@Override
			public void run() {
				List<String> deleteList = new ArrayList<>();
				teleportMap.keySet().forEach(key -> {
					if (ignoreMap.get(key) == null) {
						Teleport teleport = teleportMap.get(key);
						Location location = teleport.getWhoLocation();
						Player player = teleport.getWho();
						Player to = teleport.getTo();
						if (!player.isOnline()) {
							deleteList.add(key);
							if (!to.isOnline()) {
								return;
							}
							Wormhole.sendMessage(to, player, "teleporting.to.player-left");
						} else {
							if (!to.isOnline()) {
								deleteList.add(key);
								Wormhole.sendMessage(player, to, "teleporting.to.offline-to");
								return;
							}
						}
						boolean moved = false;
						if ((boolean) wormhole.getCoreSettings().getKnownNonNull("cancel-on-movement").getValue()) {
							if (location.distance(player.getLocation()) > (double) wormhole.getCoreSettings().getKnownNonNull("max-movement-distance").getValue()) {
								TpMoveEvent event = new TpMoveEvent(wormhole, player, to, teleport);
								if (!event.isCancelled()) {
									Wormhole.sendMessage(to, player, "teleporting.to.to-moved");
									Wormhole.sendMessage(player, to, "teleporting.to.player-moved");
									deleteList.add(key);
									moved = true;
								}
							}
						}
						if (!moved) {
							long end = teleport.getEnd() - System.currentTimeMillis();
							if (end < 0) {
								teleport(teleport.getWho(), teleport.to);
								ignoreMap.put(key, teleport);
							} else if (end < 5001) {
								if (teleport.lastTick == -1) {
									teleport.lastTick = System.currentTimeMillis();
									String left = formatSeconds(millisecondsToSeconds(end));
									Wormhole.sendMessage(player, to, "teleporting.to.second", "%seconds%=" + left);
									return;
								}
								long time = System.currentTimeMillis();
								long diff = time - teleport.lastTick;
								if (diff >= 950) {
									teleport.lastTick = System.currentTimeMillis();
									String left = formatSeconds(millisecondsToSeconds(end));
									Wormhole.sendMessage(player, to, "teleporting.to.second", "%seconds%=" + left);
								}
							}
						}
					}
				});
				deleteList.forEach(teleportMap::remove);
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
		TpBeginEvent event = new TpBeginEvent(false, wormhole, player, to, teleport);
		event.callEvent();
		if (event.isCancelled()){
			return;
		}
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
		new BukkitRunnable() {
			@Override
			public void run() {
				TpEvent event = new TpEvent(true, wormhole, player, to, teleportMap.get(toId(player, to)));
				event.callEvent();
				teleportMap.remove(toId(player, to));
				ignoreMap.remove(toId(player, to));
				if (event.isCancelled()){
					return;
				}
				if (!player.isOnline()) {
					Wormhole.sendMessage(to, player, "teleporting.to.player-left");
				}
				Wormhole.sendMessage(player, to, "teleporting.to.final");
				player.teleportAsync(to.getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
			}
		}.runTaskAsynchronously(wormhole);
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
		DecimalFormat decimalFormat = new DecimalFormat("0.0");
		String format = decimalFormat.format(seconds);
		return format.equalsIgnoreCase("0.0") ? format+"*" : format;
	}

	public void cancelRequest(Request request) {
	}
}