package bet.astral.wormhole.events.request;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.api.Request;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when deny is about to happen
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpRequestDenyEvent extends TpRequestAcceptEvent{
	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
	/**
	 * @param who       who requested
	 * @param requested requested player
	 * @param request requested player
	 */
	public TpRequestDenyEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player requested, @NotNull Request request) {
		super(wormhole, who, requested, request);
	}
}
