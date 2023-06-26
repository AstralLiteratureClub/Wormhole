package me.antritus.minecraft_server.wormhole.events;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.events.request.*;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class TpRequestEventFactory {
	public static void trigger(TpRequestEvent event){
		Bukkit.getPluginManager().callEvent(event);
	}
	public static TpRequestPlayerPrepareParseEvent createSendPrepareEvent(String name, Player player, Player requested){
		return new TpRequestPlayerPrepareParseEvent(name, player, requested);
	}
	public static TpRequestSendEvent createSendEvent(Player player, Player requested){
		return new TpRequestSendEvent(player, requested, Wormhole.REQUEST_TIME);
	}
	public static TpRequestAcceptEvent createAcceptEvent(Player player, Player requested, TeleportRequest request){
		return new TpRequestAcceptEvent(player, requested, request);
	}
	public static TpRequestDenyEvent createDenyEvent(Player player, Player requested, TeleportRequest request){
		return new TpRequestDenyEvent(player, requested, request);
	}
}
