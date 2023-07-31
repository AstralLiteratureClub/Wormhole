package me.antritus.minecraft_server.wormhole.events;

import me.antritus.minecraft_server.wormhole.Wormhole;
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
