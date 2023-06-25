package me.antritus.minecraft_server.wormhole.events.request;

import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * This event is fired when deny is about to happen
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpRequestDenyEvent extends TpRequestAcceptEvent{
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
	 * @param request requested player
	 */
	public TpRequestDenyEvent(@NotNull Player who, @NotNull Player requested, @NotNull TeleportRequest request) {
		super(who, requested, request);
	}
}
