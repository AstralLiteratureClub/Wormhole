package me.antritus.minecraft_server.wormhole.events.teleport;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Teleport;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

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
