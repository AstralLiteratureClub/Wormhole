package me.antritus.minecraft_server.wormhole.events.block;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.events.TpAbstractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.0.0-snapshot
 */
public class TpBlockEvent extends TpAbstractEvent {
	private static final HandlerList HANDLERS = new HandlerList();
	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}

	/**
	 * @param who       who requested
	 * @param blocked requested player
	 */
	public TpBlockEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player blocked) {
		super(wormhole, who, blocked);
	}
}
