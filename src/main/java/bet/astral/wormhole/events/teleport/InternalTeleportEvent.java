package bet.astral.wormhole.events.teleport;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.api.Teleport;
import bet.astral.wormhole.events.WormholeEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public abstract class InternalTeleportEvent extends WormholeEvent {
	private final Player player;
	private final Player to;
	private final Teleport teleport;
	public InternalTeleportEvent(boolean aSync, @NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(aSync, wormhole);
		this.player = player;
		this.to = to;
		this.teleport = teleport;
	}
	public InternalTeleportEvent(@NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(true, wormhole);
		this.player = player;
		this.to = to;
		this.teleport = teleport;
	}
	@NotNull
	public Player getPlayer() {
		return player;
	}
	@NotNull
	public Player getTo() {
		return to;
	}
	@NotNull
	public Teleport getTeleport() {
		return teleport;
	}
}
