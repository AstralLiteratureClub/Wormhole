package bet.astral.wormhole.events.database;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.antsfactions.FactionsPlugin;
import bet.astral.wormhole.events.WormholeEvent;
import bet.astral.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public abstract class UserEvent extends WormholeEvent {
	private final User user;
	public UserEvent(@NotNull Wormhole wormhole, @NotNull User user){
		super(true, wormhole);
		this.user = user;
	}
	public UserEvent(boolean async, @NotNull Wormhole wormhole, @NotNull User user){
		super(async, wormhole);
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
