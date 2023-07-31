package me.antritus.minecraft_server.wormhole.events.teleport;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.api.Teleport;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TpMoveEvent extends TpBeginEvent{
	public TpMoveEvent(@NotNull Wormhole wormhole, @NotNull Player player, @NotNull Player to, @NotNull Teleport teleport) {
		super(true, wormhole, player, to, teleport);
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
