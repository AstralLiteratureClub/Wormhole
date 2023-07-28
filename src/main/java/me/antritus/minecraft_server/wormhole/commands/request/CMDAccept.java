package me.antritus.minecraft_server.wormhole.commands.request;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.MessageManager;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestAcceptEvent;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDAccept extends CoreCommand {

	public CMDAccept(Wormhole wormhole){
		super(wormhole, "tpaccept");
		setPermission("wormhole.accept");
		setDescription(wormhole.getCommandConfig().getString("tpaccept.description", "tpaccept.description"));
		setUsage(wormhole.getConfig().getString("tpaccept.usage", "tpaccept.usage"));
		setAliases(wormhole.getConfig().getStringList("tpaccept.aliases"));
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
				messageManager.message(player, "accept.latest.no-requests");
				return true;
			}
			TeleportRequest request = user.getLatestRequest();
			Player sender = Bukkit.getPlayer(request.getWhoRequested());
			if (sender == null){
				messageManager.message(player, "accept.latest.sender-null");
				user.getReceivedRequests().remove(player.getUniqueId());
				return true;
			}
			User senderUser = wormhole.getUserDatabase().get(sender);
			if (senderUser == null){
				messageManager.message(player, "accept.latest.sender-null");
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
				user.getReceivedRequests().remove(player.getUniqueId());
				user.findLatest();
				request.accepted = true;
				request.teleporting = System.currentTimeMillis()+((long) wormhole.getCoreSettings().get("teleport-time").getValue());
				Wormhole.sendMessage(sender, player, "accept.accepted-sender");
				Wormhole.sendMessage(player, sender, "accept.accepted-requested");
			} else {
				messageManager.message(player, "accept.latest.no-request");
			}
			return true;
		}
		Player sender = Bukkit.getPlayer(args[0]);
		if (sender == null){
			Wormhole.sendMessage(player, args[0], "accept.manual.unknown-player", "%command%=tpaccept <name>");
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				messageManager.message(player, "accept.manual.self", "%command%=tpaccept <name>");
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent("tpaccept", player, sender);
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
			Wormhole.sendMessage(player, sender, "commands.tpaccept.no-request-found", "%command%=tpaccept <name>");
			return true;
		}
		if (requestPlayer.equals(requestSender)){
			TpRequestAcceptEvent event = new TpRequestAcceptEvent(player, sender, requestSender);
			event.callEvent();
			if (event.isCancelled()){
				return true;
			}
			requestPlayer.accepted = true;
			requestPlayer.teleporting = System.currentTimeMillis()+((long) wormhole.getCoreSettings().get("teleport-time").getValue());
			Wormhole.sendMessage(sender, player, "accept.accepted-sender", "%command%=tpaccept <name>");
			Wormhole.sendMessage(player, sender, "accept.accepted-requested", "%command%=tpaccept <name>");
		} else {
			Wormhole.sendMessage(player, sender, "accept.manual.no-requests", "%command%=tpaccept <name>");
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
					wormhole.getUserDatabase().get(sender).getSentRequests().get(player.getUniqueId()) == null
							||
							wormhole.getUserDatabase().get(sender).getSentRequests().get(player.getUniqueId()) != null
									&&
									!wormhole.getUserDatabase().get(sender).getSentRequests().get(player.getUniqueId()).isValid());
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tpaccept", sender, players);
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