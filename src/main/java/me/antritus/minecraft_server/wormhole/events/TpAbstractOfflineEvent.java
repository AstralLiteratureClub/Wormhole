package me.antritus.minecraft_server.wormhole.events;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Request;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;


/**
 * @author antritus
 * @since 1.0.0-snapshot
 */
public abstract class TpAbstractOfflineEvent extends WormholeEvent {
	private final OfflinePlayer player;
	private final OfflinePlayer requested;
	private final Request request;

	/**
	 * @param who       who requested
	 * @param requested requested player
	 * @param request   request
	 */

	public TpAbstractOfflineEvent(@NotNull Wormhole wormhole, @NotNull OfflinePlayer who, @NotNull OfflinePlayer requested, @NotNull Request request) {
		super(wormhole);
		this.player = who;
		this.requested = requested;
		this.request = request;
	}
	public TpAbstractOfflineEvent(boolean aSync, @NotNull Wormhole wormhole, @NotNull OfflinePlayer who, @NotNull OfflinePlayer requested, @NotNull Request request) {
		super(aSync, wormhole);
		this.player = who;
		this.requested = requested;
		this.request = request;
	}
	@NotNull

	public OfflinePlayer getPlayer() {
		return player;
	}
	@NotNull
	public OfflinePlayer getRequested() {
		return requested;
	}

	@NotNull
	public Request getRequest() {
		return request;
	}
}

