package me.antritus.minecraft_server.wormhole.commands.request;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestAcceptEvent;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestPlayerPrepareParseEvent;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDAccept extends CoreCommand {

	public CMDAccept() {
		super("tpaccept", Wormhole.configuration.getLong("commands.tpaccept.cooldown", 0));
		setPermission("wormhole.accept");
		setDescription(Wormhole.configuration.getString("commands.tpaccept.description", "commands.tpaccept.description"));
		setUsage(Wormhole.configuration.getString("commands.tpaccept.usage", "commands.tpaccept.usage"));
		setAliases(Wormhole.configuration.getStringList("commands.tpaccept.aliases"));
	}


	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		if (args.length == 0){
			User user = Wormhole.manager.getUser(player);
			if (user.latestRequest == null){
				player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpaccept.no-latest-requests", "commands.tpaccept.no-latest-requests")));
				return true;
			}
			TeleportRequest request = user.latestRequest;
			Player sender = Bukkit.getPlayer(request.getWhoRequested());
			if (sender == null){
				player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("settings.player-receive-error", "settings.player-receive-error")));
				user.latestRequest = null;
				return true;
			}
			User senderUser = Wormhole.manager.getUser(sender);
			if (senderUser == null){
				user.latestRequest = null;
				user.findLatest();
				throw new RuntimeException("Could not get user of: "+ sender.getName());
			}
			if (user.getRequest(player, TeleportRequest.Type.SENDER).equals(request)){
				TpRequestAcceptEvent event = TpRequestEventFactory.createAcceptEvent(player, sender, request);
				TpRequestEventFactory.trigger(event);
				if (event.isCancelled()){
					return true;
				}
				user.latestRequest = null;
				user.findLatest();
				senderUser.teleport(request);
				Wormhole.sendMessage(sender, player, "commands.tpaccept.accepted-sender");
				Wormhole.sendMessage(player, sender, "commands.tpaccept.accepted-requested");
				cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+super.cooldown);
			} else {
				player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpaccept.no-request-found", "commands.tpaccept.no-request-found")));
			}
			return true;
		}
		Player sender = Bukkit.getPlayer(args[0]);
		if (sender == null){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpaccept.unknown-player", "commands.tpaccept.unknown-player")));
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpaccept.request-self", "commands.tpaccept.request-self")));
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tpaccept", player, sender);
		TpRequestEventFactory.trigger(parseEvent);
		if (parseEvent.isCancelled()){
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		User senderUser = Wormhole.manager.getUser(sender);
		if (senderUser == null){
			throw new RuntimeException("Could not get user of: "+ sender.getName());
		}
		TeleportRequest requestPlayer = user.getRequest(sender, TeleportRequest.Type.REQUESTED);
		TeleportRequest requestSender = senderUser.getRequest(player, TeleportRequest.Type.SENDER);
		if (requestPlayer == null){
			Wormhole.sendMessage(player, sender, "commands.tpaccept.no-request-found");
			return true;
		}
		Bukkit.broadcastMessage(requestPlayer.toString());
		Bukkit.broadcastMessage(requestSender.toString());
		if (requestPlayer.equals(requestSender)){
			TpRequestAcceptEvent event = TpRequestEventFactory.createAcceptEvent(player, sender, requestSender);
			TpRequestEventFactory.trigger(event);
			if (event.isCancelled()){
				return true;
			}
			senderUser.teleport(requestPlayer);
			Wormhole.sendMessage(sender, player, "commands.tpaccept.accepted-sender");
			Wormhole.sendMessage(player, sender, "commands.tpaccept.accepted-requested");
			cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+super.cooldown);
		} else {
			Wormhole.sendMessage(player, sender, "commands.tpaccept.no-request-found");
			return true;
		}
		return true;
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			players.removeIf(player ->
					Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) == null
							||
							(Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) != null
									&&
									!Wormhole.manager.getUser(sender).
											getRequest(player, TeleportRequest.Type.SENDER).isValid()));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tpaccept", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			if (finalList.isEmpty()){
				finalList.add(Wormhole.configuration.getString("settings.no-player-tab-completion", "settings.no-player-tab-completion"));
			}
			return finalList;
		}
		return Collections.singletonList("");
	}
}