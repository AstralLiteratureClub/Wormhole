package me.antritus.minecraft_server.wormhole.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerTabCompleteRequestEvent extends PlayerEvent  {
	private static final HandlerList HANDLERS = new HandlerList();
	private final String cmdName;
	private final List<Player> players;
	public PlayerTabCompleteRequestEvent(String cmdName, @NotNull Player who, List<Player> players) {
		super(who);
		this.players = players;
		this.cmdName = cmdName;
	}

	public String getCommandName(){
		return cmdName;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
