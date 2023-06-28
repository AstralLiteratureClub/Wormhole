package me.antritus.minecraft_server.wormhole.events.block;

import org.bukkit.entity.Player;

/**
 * @author Antritus
 * @since 1.0.0-snapshot
 */
public class TpUnblockEvent extends TpBlockEvent{
	/**
	 * @param who       who requested
	 * @param requested requested player
	 */
	public TpUnblockEvent(Player who, Player requested) {
		super(who, requested);
	}
}
