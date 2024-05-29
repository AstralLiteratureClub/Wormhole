package bet.astral.wormhole.events;

import bet.astral.wormhole.Wormhole;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class WormholeReloadEvent extends WormholeEvent{
	private static final HandlerList HANDLERS = new HandlerList();

	public WormholeReloadEvent(@NotNull Wormhole wormhole) {
		super(wormhole);
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
