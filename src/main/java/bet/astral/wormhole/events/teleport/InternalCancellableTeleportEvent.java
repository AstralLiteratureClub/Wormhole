package bet.astral.wormhole.events.teleport;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.api.Teleport;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public abstract class InternalCancellableTeleportEvent extends InternalTeleportEvent implements Cancellable {
	private boolean isCancelled;
	public InternalCancellableTeleportEvent(boolean aSync, @NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(aSync, wormhole, player, to, teleport);
	}

	public InternalCancellableTeleportEvent(@NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(wormhole, player, to, teleport);
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean v) {
		isCancelled = v;
	}
}
