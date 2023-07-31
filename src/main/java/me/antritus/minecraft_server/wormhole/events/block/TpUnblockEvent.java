package me.antritus.minecraft_server.wormhole.events.block;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.0.0-snapshot
 */
public class TpUnblockEvent extends TpBlockEvent{
	/**
	 * @param who       who requested
	 * @param unblocked requested player
	 */
	public TpUnblockEvent(@NotNull Wormhole wormhole, @NotNull Player who, @NotNull Player unblocked) {
		super(wormhole, who, unblocked);
	}
}
