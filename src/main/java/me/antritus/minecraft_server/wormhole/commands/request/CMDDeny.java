package me.antritus.minecraft_server.wormhole.commands.request;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.MessageManager;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.request.*;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDDeny extends CoreCommand {
	public CMDDeny(Wormhole wormhole){
		super(wormhole, "tpdeny");
		setPermission("wormhole.deny");
		setDescription(wormhole.getCommandConfig().getString("tpdeny.description", "tpdeny.description"));
		setUsage(wormhole.getConfig().getString("tpdeny.usage", "tpaccept.usage"));
		setAliases(wormhole.getConfig().getStringList("tpdeny.aliases"));
	}


	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		MessageManager messageManager = wormhole.getMessageManager();
		if (!(commandSender instanceof Player player)){
			messageManager.message(commandSender, "command-parse.player-only");
			return true;
		}
		if (args.length == 0){
			User user = wormhole.getUserDatabase().getKnownNonNull(player);
			user.findLatest();
			if (user.getLatestRequest() == null){
				messageManager.message(player, "deny.latest.no-requests");
				return true;
			}
			TeleportRequest request = user.getLatestRequest();
			Player sender = Bukkit.getPlayer(request.getWhoRequested());
			if (sender == null){
				messageManager.message(player, "deny.latest.sender-null");
				user.getReceivedRequests().remove(player.getUniqueId());
				return true;
			}
			User senderUser = wormhole.getUserDatabase().get(sender);
			if (senderUser == null){
				messageManager.message(player, "deny.latest.sender-null");
				user.getReceivedRequests().remove(player.getUniqueId());
				user.findLatest();
				throw new RuntimeException("Could not get user of: "+ sender.getName());
			}
			if (user.getReceivedRequests().get(request.getWhoRequested()).equals(request)){
				TpRequestAcceptEvent event = new TpRequestAcceptEvent(player, sender, request);
				event.callEvent();
				if (event.isCancelled()){
					return true;
				}
				user.getReceivedRequests().remove(request.getWhoRequested());
				senderUser.getSentRequests().remove(request.getRequested());
				user.findLatest();
				request.accepted = false;
				request.canceled = true;
				request.teleporting = -1;
				Wormhole.sendMessage(sender, player, "deny.denied-sender");
				Wormhole.sendMessage(player, sender, "deny.denied-requested");
			} else {
				messageManager.message(player, "deny.latest.no-request");
			}
			return true;
		}
		Player sender = Bukkit.getPlayer(args[0]);
		if (sender == null){
			Wormhole.sendMessage(player, args[0], "deny.manual.unknown-player", "%command%=tpdeny <name>");
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				messageManager.message(player, "deny.manual.self", "%command%=tpdeny <name>");
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent("tpdeny", player, sender);
		parseEvent.callEvent();
		if (parseEvent.isCancelled()){
			return true;
		}
		User user = wormhole.getUserDatabase().get(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		User senderUser = wormhole.getUserDatabase().get(sender);
		if (senderUser == null){
			throw new RuntimeException("Could not get user of: "+ sender.getName());
		}
		TeleportRequest requestPlayer = user.getReceivedRequests().get(sender.getUniqueId());
		TeleportRequest requestSender = senderUser.getSentRequests().get(player.getUniqueId());
		if (requestPlayer == null){
			Wormhole.sendMessage(player, sender, "commands.tpdeny.no-request-found", "%command%=tpdeny <name>");
			return true;
		}
		if (requestPlayer.equals(requestSender)){
			TpRequestAcceptEvent event = new TpRequestAcceptEvent(player, sender, requestSender);
			event.callEvent();
			if (event.isCancelled()){
				return true;
			}
			user.getReceivedRequests().remove(sender.getUniqueId());
			senderUser.getSentRequests().remove(player.getUniqueId());
			user.findLatest();
			requestPlayer.accepted = false;
			requestPlayer.canceled = true;
			requestPlayer.teleporting = -1;
			Wormhole.sendMessage(sender, player, "deny.denied-sender", "%command%=tpdeny <name>");
			Wormhole.sendMessage(player, sender, "deny.denied-requested", "%command%=tpdeny <name>");
		} else {
			Wormhole.sendMessage(player, sender, "deny.manual.no-requests", "%command%=tpdeny <name>");
			return true;
		}
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length == 1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			players.removeIf(player ->
					wormhole.getUserDatabase().get(sender).getSentRequests().get(player.getUniqueId()) == null
							||
							(wormhole.getUserDatabase().get(sender).getSentRequests().get(player.getUniqueId()) != null
									&&
									!wormhole.getUserDatabase().get(sender).getSentRequests().get(player.getUniqueId()).isValid()));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tpdeny", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			if (finalList.isEmpty()){
				finalList.add(wormhole.getMessageManager().messageConfig.getString(ColorUtils.translate("command-parse.no-tab.player"), "command-parse.no-tab.player"));
			}
			return finalList;
		}
		return Collections.singletonList("");
	}
}