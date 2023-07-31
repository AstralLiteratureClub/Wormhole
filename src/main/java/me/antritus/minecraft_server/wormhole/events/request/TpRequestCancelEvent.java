package me.antritus.minecraft_server.wormhole.events.request;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.events.TpAbstractOfflineEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TpRequestCancelEvent extends TpAbstractOfflineEvent {
	/**
	 * @param who       who requested
	 * @param requested requested player
	 * @param request   requested player
	 */
	public TpRequestCancelEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull OfflinePlayer requested, @NotNull Request request) {
		super(wormhole, who, requested, request);
	}

	private static final HandlerList HANDLERS = new HandlerList();

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
