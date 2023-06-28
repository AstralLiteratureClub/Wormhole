package me.antritus.minecraft_server.wormhole.events;

import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


/**
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpAbstractEvent extends PlayerEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final Player requested;
	private boolean isCancelled;

	/**
	 * @param who who requested
	 * @param requested requested player
	 */
	public TpAbstractEvent(@NotNull Player who, @NotNull Player requested) {
		super(who);
		this.requested = requested;
	}
	@NotNull
	public Player getRequested() {
		return requested;
	}


	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
