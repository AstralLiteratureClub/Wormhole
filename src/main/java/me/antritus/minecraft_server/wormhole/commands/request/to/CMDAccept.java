package me.antritus.minecraft_server.wormhole.commands.request.to;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.MessageManager;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.api.TeleportManager;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestAcceptEvent;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
		TeleportManager manager = wormhole.getTeleportManager();
		if (manager.sizeOfReceivedRequests(player)==0){
			messageManager.message(player, "accept.no-requests");
			return true;
		}
		if (args.length == 0) {
			User user = wormhole.getUserDatabase().getKnownNonNull(player);
			user.findLatest();
			Request request = user.getLatestRequest();
			if (request == null){
				messageManager.message(player, "accept.latest.no-request");
				return true;
			}
			OfflinePlayer sender = Bukkit.getOfflinePlayer(request.getWhoAsked());
			if (!sender.isOnline()) {
				messageManager.message(player, "accept.latest.sender-null");
				return true;
			}
			TeleportManager teleportManager = wormhole.getTeleportManager();
			TpRequestAcceptEvent event = new TpRequestAcceptEvent(player,
					Objects.requireNonNull(sender.getPlayer()),
					request);
			event.callEvent();
			if (event.isCancelled()) {
				return true;
			}
			request.setAccepted(true);
			request.setDenied(false);
			teleportManager.beginTeleport(sender.getPlayer(), player);
			Wormhole.sendMessage(sender.getPlayer(), player, "accept.accepted-sender");
			Wormhole.sendMessage(player, sender.getPlayer(), "accept.accepted-requested");
			return true;
		}
		OfflinePlayer sender = Bukkit.getOfflinePlayer(args[0]);
		if (!sender.isOnline()){
			Wormhole.sendMessage(player, args[0], "accept.manual.unknown-player", "%command%=tpaccept <name>");
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				messageManager.message(player, "accept.manual.self", "%command%=tpaccept <name>");
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent("tpaccept", player, Objects.requireNonNull(sender.getPlayer()));
		parseEvent.callEvent();
		if (parseEvent.isCancelled()){
			return true;
		}

		if (!manager.hasRequested(sender, player)) {
			Wormhole.sendMessage(player, sender.getName(), "commands.tpaccept.no-request-found", "%command%=tpaccept <name>");
			return true;
		}
		Request request = manager.getRequest(sender.getPlayer(), player);
		// This should never happen.
		if (request == null) {
			return true;
		}
		TpRequestAcceptEvent event = new TpRequestAcceptEvent(player, sender.getPlayer(), request);
		event.callEvent();
		if (event.isCancelled()){
			return true;
		}
		request.setAccepted(true);
		request.setDenied(false);
		Wormhole.sendMessage(sender.getPlayer(), player, "accept.accept-sender");
		Wormhole.sendMessage(player, sender.getPlayer(), "accept.accept-requested");
		// Automatically teleports if 0 secs
		// Send the messages before teleport!
		manager.beginTeleport(sender.getPlayer(), player);
		return true;
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			TeleportManager manager = wormhole.getTeleportManager();
			players.removeIf(player -> !manager.hasRequested(sender, player));
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