package me.antritus.minecraft_server.wormhole.events.teleport;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Teleport;
import me.antritus.minecraft_server.wormhole.events.WormholeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class InternalTeleportEvent extends WormholeEvent {
	private final Player player;
	private final Player to;
	private final Teleport teleport;
	public InternalTeleportEvent(boolean aSync, @NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(aSync, wormhole);
		this.player = player;
		this.to = to;
		this.teleport = teleport;
	}
	public InternalTeleportEvent(@NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(true, wormhole);
		this.player = player;
		this.to = to;
		this.teleport = teleport;
	}
	@NotNull
	public Player getPlayer() {
		return player;
	}
	@NotNull
	public Player getTo() {
		return to;
	}
	@NotNull
	public Teleport getTeleport() {
		return teleport;
	}
}
