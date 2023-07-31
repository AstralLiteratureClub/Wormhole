package me.antritus.minecraft_server.wormhole.events.request;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.events.TpAbstractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TpRequestAcceptEvent extends TpAbstractEvent implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
	private final Request request;
	private boolean isCancelled = false;
	/**
	 * @param who       who requested
	 * @param requested requested player
	 */
	public TpRequestAcceptEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player requested, @NotNull Request request) {
		super(wormhole, who, requested);
		this.request = request;
	}

	/**
	 * Returns the teleport request instance.
	 * @return request instance
	 */
	public Request getRequest() {
		return request;
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
