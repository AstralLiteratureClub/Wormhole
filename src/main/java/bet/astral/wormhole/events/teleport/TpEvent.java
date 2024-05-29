package bet.astral.wormhole.events.teleport;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.api.Teleport;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TpEvent extends InternalTeleportEvent implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();

	boolean isCancelled;

	public TpEvent(boolean aSync, @NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(aSync, wormhole, player, to, teleport);
	}

	public TpEvent(@NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(wormhole, player, to, teleport);
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
