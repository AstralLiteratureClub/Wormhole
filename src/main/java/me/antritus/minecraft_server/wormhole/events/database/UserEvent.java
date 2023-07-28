package me.antritus.minecraft_server.wormhole.events.database;

import me.antritus.minecraft_server.wormhole.antsfactions.FactionsPlugin;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public abstract class UserEvent extends Event {
	private final User user;
	public UserEvent(@NotNull User user){
		super(true);
		this.user = user;
	}

	@NotNull
	public User getFaction() {
		return user;
	}

	public void callAsync(FactionsPlugin main){
		Event event = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(event);
				cancel();
			}
		}.runTaskAsynchronously(main);
	}
}
