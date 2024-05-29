package bet.astral.wormhole.manager;

import bet.astral.wormhole.Wormhole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class UserManager implements Listener {
	private final Wormhole main;
	private BukkitTask task;
	public UserManager(Wormhole main) {
		this.main = main;
	}
	public void onEnable(){
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Bukkit.getOnlinePlayers().size() > 0) {
					Bukkit.getOnlinePlayers().forEach((player) -> {
						main.getUserDatabase().join(player.getUniqueId());
					});
				}
			}
		}.runTaskAsynchronously(main);
		task = new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getOnlinePlayers().forEach(player->{
					main.getUserDatabase().save(main.getUserDatabase().get(player));
				});
			}
			// 30 minutes = 20 * 60 * 30 = 36_000
		}.runTaskTimerAsynchronously(main, 1000, 30*60*20);
	}
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			User user = main.getUserDatabase().getKnownNonNull(player);
			user.lastOnline = System.currentTimeMillis();
			user.online = false;
		});
		main.getUserDatabase().disable();
		task.cancel();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event){
		new BukkitRunnable() {
			@Override
			public void run() {
				Player player = event.getPlayer();
				main.getUserDatabase().join(player.getUniqueId());
			}
		}.runTaskAsynchronously(main);
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event){
		if (main.getUserDatabase().get(event.getPlayer()) == null){
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				User user = main.getUserDatabase().getKnownNonNull(event.getPlayer());
				user.lastOnline = System.currentTimeMillis();
				user.online = false;
				main.getUserDatabase().save(user);
				main.getUserDatabase().unload(event.getPlayer());
			}
		}.runTaskAsynchronously(main);
	}
}
