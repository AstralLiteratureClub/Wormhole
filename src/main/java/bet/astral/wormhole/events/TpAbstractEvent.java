package bet.astral.wormhole.events;

import bet.astral.wormhole.Wormhole;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;


/**
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpAbstractEvent extends WormholeEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	private final Player player;
	private final Player requested;
	/**
	 * @param who       who requested
	 * @param requested requested player
	 */

	public TpAbstractEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player requested) {
		super(wormhole);
		this.player = who;
		this.requested = requested;
	}
	@NotNull
	public Player getRequested() {
		return requested;
	}

	public Player getPlayer() {
		return player;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
