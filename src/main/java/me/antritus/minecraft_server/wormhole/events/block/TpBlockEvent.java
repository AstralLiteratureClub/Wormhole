package me.antritus.minecraft_server.wormhole.events.block;

import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.events.TpAbstractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

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
	 * @param requested requested player
	 */
	public TpBlockEvent(Player who, Player requested) {
		super(who, requested);
	}
}
