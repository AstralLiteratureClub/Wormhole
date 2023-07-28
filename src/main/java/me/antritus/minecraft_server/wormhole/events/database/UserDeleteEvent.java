package me.antritus.minecraft_server.wormhole.events.database;

import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class UserDeleteEvent extends UserEvent {
	private static final HandlerList HANDLER_LIST = new HandlerList();

	public UserDeleteEvent(@NotNull User user) {
		super(user);
	}

	public static HandlerList getHandlerList(){
		return HANDLER_LIST;
	}
	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}
}
