package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ASyncTeleportThread {
	private BukkitTask task;
	private final TeleportRequest request;
	private final Wormhole wormhole;

	public ASyncTeleportThread(Wormhole wormhole, TeleportRequest request) {
		this.wormhole = wormhole;
		this.request = request;
	}

	private void cancel(){
		task.cancel();
	}

	public void run() {
		if (task == null) {
			task = new BukkitRunnable() {
				double counter = 0;

				@Override
				public void run() {
					if (request.isValid()) {
						if (request.accepted && request.teleporting != -1) {
							counter += 1;
							// Check if teleporting time is over 30 seconds
							if (counter >= 30) {
								teleportPlayers();
								cancel();
							}
						} else if (request.accepted) {
							ASyncTeleportThread.this.cancel();
							cancel();
						} else if (request.getTimeEnd() < System.currentTimeMillis()) {
							ASyncTeleportThread.this.cancel();
							cancel();
							handleTimeOut();
						}
					} else {
						ASyncTeleportThread.this.cancel();
						cancel();
					}
				}
			}.runTaskTimerAsynchronously(wormhole, 0, 100); // Execute every 5 seconds (100 ticks)
		}
	}

	private void teleportPlayers() {
		Player player = Bukkit.getPlayer(request.getWhoRequested());
		Player requested = Bukkit.getPlayer(request.getRequested());
		if (player != null && requested != null) {
			player.sendMessage("You have been teleported!");
			requested.sendMessage("You have been teleported!");
		}
	}

	private void handleTimeOut() {
		OfflinePlayer playerOffline = Bukkit.getOfflinePlayer(request.getWhoRequested());
		OfflinePlayer requestedOffline = Bukkit.getOfflinePlayer(request.getRequested());
		Player player = null;
		Player requested = null;
		if (playerOffline.isOnline()) {
			player = (Player) playerOffline;
		}
		if (requestedOffline.isOnline()) {
			requested = (Player) requestedOffline;
		}
		request.accepted = false;
		request.canceled = true;
		if (player == null) {
			if (requested == null) {
				return;
			}
			Wormhole.sendMessage(requested, playerOffline.getName(), "request-canceled.time-ran-out-requested");
		} else {
			if (requested == null) {
				Wormhole.sendMessage(player, requestedOffline.getName(), "request-canceled-time-ran-out-sender");
				return;
			}
			Wormhole.sendMessage(player, requested, "request-canceled-time-ran-out-sender");
			Wormhole.sendMessage(requested, player, "request-canceled.time-ran-out-requested");
		}
	}
}