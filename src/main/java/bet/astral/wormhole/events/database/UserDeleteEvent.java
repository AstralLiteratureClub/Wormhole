package bet.astral.wormhole.events.database;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.manager.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class UserDeleteEvent extends UserEvent {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	public UserDeleteEvent(@NotNull Wormhole wormhole, @NotNull User user) {
		super(wormhole, user);
	}

	public static HandlerList getHandlerList(){
		return HANDLER_LIST;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return getHandlerList();
	}
}
