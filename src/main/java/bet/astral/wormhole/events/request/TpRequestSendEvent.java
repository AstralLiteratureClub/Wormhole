package bet.astral.wormhole.events.request;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.events.TpAbstractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is right before sending teleport request to request
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpRequestSendEvent extends TpAbstractEvent implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Player requested;
	private final long requestTime;
	private boolean isCancelled;

	/**
	 * @param who who requested
	 * @param requested requested
	 * @param requestTime how long is the request
	 */
	public TpRequestSendEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player requested, long requestTime) {
		super(wormhole, who, requested);
		this.requested = requested;
		this.requestTime = requestTime;
	}

	/**
	 * Returns who were requested
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
