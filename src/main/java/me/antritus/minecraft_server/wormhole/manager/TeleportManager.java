package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class TeleportManager implements Listener {
	private final Map<UUID, User> users = new HashMap<>();
	private final Wormhole main;
	private BukkitTask task;
	public TeleportManager(Wormhole main) {
		this.main = main;
	}
	public void onEnable(){
		if (Bukkit.getOnlinePlayers().size()>0){
			Bukkit.getOnlinePlayers().forEach((player)->{
				User user = new User(player);
				users.put(player.getUniqueId(), user);
			});
		}
		task = new BukkitRunnable() {
			@Override
			public void run() {
				List<User> removeList = new ArrayList<>();
				users.forEach((uniqueId, user)->{
					// Check all users
					// Unload them after 45 seconds. This is because players can relog to the server within ~30 seconds.
					if (!user.isOnline() && System.currentTimeMillis()-user.lastOnline>45000){
						removeList.add(user);
					}else {
						Player pl = Bukkit.getPlayer(uniqueId);
						if (pl == null){
							throw new RuntimeException("Could not find player: "+ uniqueId);
						}
						if (user.requests().size() > 0) {
							for (TeleportRequest request : user.requests()) {
								if (request.accepted) {
									if (request.teleporting<0){
										user.removeRequest(request);
										continue;
									}
									// Every second send message if setting applies
									if (request.teleporting<=System.currentTimeMillis()){
										user.teleport(request);
									}
									if (System.currentTimeMillis()-request.lastSentMessage>1000) {
										Player req = Bukkit.getPlayer(request.getRequested());
										if (req == null){
											throw new RuntimeException("Could not find player: "+ request.getRequested());
										}
										Wormhole.sendMessage(pl, req, "teleport-accepted.teleport-every-second.message", "%second%="+request.seconds+"");
										request.lastSentMessage=System.currentTimeMillis();
									}
								} else {
									if (request.getTimeEnd()<=System.currentTimeMillis()){
										user.removeRequest(request);
										Wormhole.sendMessage(pl, "time-ran-out-sender");
									}
								}
							}
						}
						if (user.others().size() > 0) {
							for (TeleportRequest request : user.others()) {
								if (request.getTimeEnd()<=System.currentTimeMillis()){
									user.removeOther(request);
									Wormhole.sendMessage(pl, "time-ran-out-requested");
								}
							}
						}
					}
				});
				for (User user : removeList) {
					users.remove(user.getUniqueId());
				}
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
