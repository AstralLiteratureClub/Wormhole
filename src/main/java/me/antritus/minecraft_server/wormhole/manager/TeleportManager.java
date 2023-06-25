package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import me.antritus.astrolapi.annotations.NotNull;
import me.antritus.astrolapi.annotations.Nullable;

import java.util.*;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class TeleportManager {
	private final Map<UUID, User> users = new HashMap<>();
	private final Wormhole main;
	private BukkitTask task;
	public TeleportManager(Wormhole main) {
		this.main = main;
	}
	public void onEnable(){
		task = new BukkitRunnable() {
			@Override
			public void run() {
				List<UUID> clearance = new ArrayList<>();
				users.forEach(
						(uuid, user)
								->
						{
							if (!user.isOnline() && user.getLastOnline()+1500 < System.currentTimeMillis()){
								clearance.add(uuid);
							} else if (user.isOnline()) {
								long now = System.currentTimeMillis();
								user.lastOnline = now;
								List<TeleportRequest> requestClearance = new ArrayList<>();
								user.requests().forEach(
										(
												(request) ->
												{
													if (request.teleportEnd < 0){
														requestClearance.add(request);
													} else {
														if (request.lastSentMessage-System.currentTimeMillis() < 0 && Wormhole.configuration.getBoolean("teleport-accepted.teleport-every-second.enabled", false)){
															Wormhole.sendMessage(Bukkit.getPlayer(request.getWhoRequested()), Bukkit.getPlayer(request.getRequested()), "teleport-accepted.teleport-every-second.enabled");
															request.lastSentMessage = System.currentTimeMillis()+500;
														}
													}
													if (now < request.getTimeEnd() && request.teleportEnd < 0){
														Wormhole.sendMessage(Bukkit.getPlayer(request.getWhoRequested()), Bukkit.getPlayer(request.getRequested()), "request-canceled.time-ran-out-sender");
														requestClearance.add(request);
													}
												}
										)
								);
								user.removeRequest(requestClearance);
								requestClearance.clear();
								user.others().forEach(
										(request) -> {
											if (now < request.getTimeEnd() && request.teleportEnd < 0){
												Wormhole.sendMessage(Bukkit.getPlayer(request.getWhoRequested()), Bukkit.getPlayer(request.getRequested()), "request-canceled.time-ran-out-requested");
												requestClearance.add(request);
											}
										}
								);
								user.removeOther(requestClearance);
							}
						}
				);
			}
		}.runTaskTimerAsynchronously(main, 20, 10);
	}
	public void onDisable() {
		if (task != null){
			task.cancel();
		}
	}

	@Nullable
	public User getUser(UUID user){
		return users.get(user);
	}
	@Nullable
	public User getUser(Player player){
		return users.get(player.getUniqueId());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		User user = users.get(event.getPlayer().getUniqueId());
		if (user != null){
			user.online = true;
			user.lastOnline = System.currentTimeMillis();
		} else {
			user = new User(event.getPlayer());
			users.put(event.getPlayer().getUniqueId(), user);
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		User user = users.get(event.getPlayer().getUniqueId());
		user.lastOnline = System.currentTimeMillis();
		user.online = false;
	}
}
