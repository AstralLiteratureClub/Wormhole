package bet.astral.wormhole.events.request;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.api.Request;
import bet.astral.wormhole.events.TpAbstractOfflineEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TpRequestRanOutEvent extends TpAbstractOfflineEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	/**
	 * @param who       who requested
	 * @param requested requested player
	 * @param request   request
	 */
	public TpRequestRanOutEvent(@NotNull Wormhole wormhole, @NotNull OfflinePlayer who, @NotNull OfflinePlayer requested, @NotNull Request request) {
		super(true, wormhole, who, requested, request);
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
