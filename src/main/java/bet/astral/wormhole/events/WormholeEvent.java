package bet.astral.wormhole.events;

import bet.astral.wormhole.Wormhole;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public abstract class WormholeEvent extends Event {
	private final Wormhole wormhole;
	public WormholeEvent(@NotNull Wormhole wormhole) {
		this.wormhole = wormhole;
	}

	public WormholeEvent(boolean isAsync, @NotNull Wormhole wormhole) {
		super(isAsync);
		this.wormhole = wormhole;
	}

	@NotNull
	public Wormhole getWormhole() {
		return wormhole;
	}
}
