package me.antritus.minecraft_server.wormhole.events.database;

import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserSaveEvent extends UserEvent {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	public UserSaveEvent(@NotNull User user) {
		super(user);
	}

	public static HandlerList getHandlerList(){
		return HANDLER_LIST;
	}
	@Override
	public @NotNull HandlerList getHandlers() {
		return getHandlerList();
	}
}
