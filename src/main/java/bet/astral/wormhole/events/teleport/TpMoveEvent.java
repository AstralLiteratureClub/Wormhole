package bet.astral.wormhole.events.teleport;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.api.Teleport;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TpMoveEvent extends TpBeginEvent{
	public TpMoveEvent(@NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(true, wormhole, player, to, teleport);
	}
	private static final HandlerList HANDLERS = new HandlerList();
	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
