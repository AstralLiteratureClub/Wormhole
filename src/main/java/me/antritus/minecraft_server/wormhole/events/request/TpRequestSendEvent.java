package me.antritus.minecraft_server.wormhole.events.request;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import me.antritus.astrolapi.annotations.NotNull;
import me.antritus.astrolapi.annotations.Nullable;

/**
 * This event is right before sending teleport request to requested
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpRequestSendEvent extends TpRequestEvent implements Cancellable {

	private static HandlerList HANDLERS = new HandlerList();
	private final Player requested;
	private final long requestTime;
	private boolean isCancelled;

	/**
	 * @param who who requested
	 * @param requested requested
	 * @param requestTime how long is the request
	 */
	public TpRequestSendEvent(@NotNull Player who, @NotNull Player requested, long requestTime) {
		super(who, requested);
		this.requested = requested;
		this.requestTime = requestTime;
	}

	/**
	 * Returns who was requested
	 * @return NotNull requested player
	 */
	@NotNull
	@Override
	public Player getRequested() {
		return requested;
	}

	/**
	 * Returns how long the request is going to be.
	 * @return request length
	 */
	public long getRequestTime() {
		return requestTime;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		isCancelled = b;
	}
}
