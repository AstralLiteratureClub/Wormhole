package me.antritus.minecraft_server.wormhole.events;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

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
