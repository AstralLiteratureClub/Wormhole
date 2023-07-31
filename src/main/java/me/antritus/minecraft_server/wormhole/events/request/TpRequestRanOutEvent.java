package me.antritus.minecraft_server.wormhole.events.request;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.events.TpAbstractOfflineEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TpRequestRanOutEvent extends TpAbstractOfflineEvent {
	private static final HandlerList HANDLERS = new HandlerList();

	/**
	 * @param who       who requested
	 * @param requested requested player
	 * @param request   request
	 */
	public TpRequestRanOutEvent(@NotNull Wormhole wormhole, @NotNull OfflinePlayer who, @NotNull OfflinePlayer requested, @NotNull Request request) {
		super(wormhole, who, requested, request);
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
