package bet.astral.wormhole.events.block;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.events.WormholeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class TpToggleEvent extends WormholeEvent {
	private final Player player;
	public TpToggleEvent(@NotNull Wormhole wormhole, @NotNull Player player) {
		super(wormhole);
		this.player = player;
	}
	public Player getPlayer(){
		return player;
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
