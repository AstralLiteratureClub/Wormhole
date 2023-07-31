package me.antritus.minecraft_server.wormhole.events;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class PlayerTabCompleteRequestEvent extends WormholeEvent  {
	private static final HandlerList HANDLERS = new HandlerList();
	private final String cmdName;
	private final List<Player> players;
	private final Player player;
	public PlayerTabCompleteRequestEvent(@NotNull Wormhole wormhole, @NotNull String cmdName, @NotNull Player who, @NotNull List<Player> players) {
		super(wormhole);
		this.player = who;
		this.players = players;
		this.cmdName = cmdName;
	}
	@NotNull
	public String getCommandName(){
		return cmdName;
	}
	@NotNull
	public List<Player> getPlayers() {
		return players;
	}
	@NotNull
	public Player getPlayer() {
		return player;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
	}
}
