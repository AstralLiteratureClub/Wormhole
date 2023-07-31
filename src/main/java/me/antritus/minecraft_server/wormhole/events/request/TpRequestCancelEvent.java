package me.antritus.minecraft_server.wormhole.events.request;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Request;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpRequestCancelEvent extends TpRequestDenyEvent {
	/**
	 * @param who       who requested
	 * @param requested requested player
	 * @param request   requested player
	 */
	public TpRequestCancelEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player requested, @NotNull Request request) {
		super(wormhole, who, requested, request);
	}
}
