package me.antritus.minecraft_server.wormhole.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is sent right before sending if player exists or not.
 * @author antritus
 * @since 1.0.0-snapshot
 */
public class TpPlayerAfterParseEvent extends TpAbstractEvent implements Cancellable {
	private static HandlerList HANDLERS = new HandlerList();
	private final Player requested;
	private boolean isCancelled;
	private final String command;
	/**
	 * @param who who requested
	 * @param requested requested player
	 */
	public TpPlayerAfterParseEvent(String command, @NotNull Player who, @NotNull Player requested) {
		super(who, who);
		this.requested = requested;
		this.command =command;
	}

	/**
	 * Returns the player who was requested
	 * @return requested play
	 */
	@NotNull
	public Player getRequested() {
		return requested;
	}

	/**
	 * The name of the command.
	 * @return command name
	 */
	public String getCommand() {
		return command;
	}

	public static HandlerList getHandlerList(){
		return HANDLERS;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLERS;
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
