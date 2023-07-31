package me.antritus.minecraft_server.wormhole.commands.request.to;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.MessageManager;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.api.TeleportManager;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestCancelEvent;
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
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class CMDCancel extends CoreCommand {
	public CMDCancel(Wormhole wormhole) {
		super(wormhole, "tpcancel");
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		MessageManager messageManager = wormhole.getMessageManager();
		if (!(commandSender instanceof Player player)) {
			messageManager.message(commandSender, "command-parse.player-only");
			return true;
		}
		TeleportManager manager = wormhole.getTeleportManager();
		if (manager.sizeOfSentRequests(player) == 0) {
			messageManager.message(player, "cancel.no-requests");
			return true;
		}
		if (args.length == 0) {
			User user = wormhole.getUserDatabase().getKnownNonNull(player);
			user.findLatestSent();
			Request request = user.getLatestSentRequest();
			if (request == null) {
				messageManager.message(player, "cancel.latest.no-request");
				return true;
			}
			OfflinePlayer sender = Bukkit.getOfflinePlayer(request.getWhoAsked());
			TpRequestCancelEvent event = new TpRequestCancelEvent(wormhole, player, sender, user.getLatestSentRequest());
			event.callEvent();
			request.setAccepted(true);
			request.setDenied(false);
			manager.cancelRequest(request);
			Wormhole.sendMessage(player, sender.getName(), "cancel.cancel-sender");
			if (!sender.isOnline()) {
				return true;
			} else {
				Wormhole.sendMessage(sender.getPlayer(), player, "cancel.cancel-sender");
			}
			return true;
		}


		OfflinePlayer sender = Bukkit.getOfflinePlayer(args[0]);
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				messageManager.message(player, "cancel.manual.self", "%command%=tpcancel <name>");
				return true;
			}
		}

		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent(wormhole, "tpcancel", player, Objects.requireNonNull(sender.getPlayer()));
		parseEvent.callEvent();
		if (parseEvent.isCancelled()) {
			return true;
		}
		Request request = manager.getRequest(player, sender);
		if (request == null) {
			return true;
		}
		if (!manager.hasRequested(player, sender)){
			Wormhole.sendMessage(player, sender.getName(), "cancel.manual.no-request", "%command%=tpcancel <name>");
			return true;
		}
		TpRequestCancelEvent event = new TpRequestCancelEvent(wormhole, player, sender, request);
		event.callEvent();
		request.setAccepted(true);
		request.setDenied(false);
		manager.cancelRequest(request);
		Wormhole.sendMessage(player, sender.getName(), "cancel.cancel-sender");
		if (!sender.isOnline()) {
			return true;
		} else {
			Wormhole.sendMessage(sender.getPlayer(), player, "cancel.cancel-sender");
		}
		return true;
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length == 1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			TeleportManager manager = wormhole.getTeleportManager();
			players.removeIf(player -> manager.hasRequested(sender, player));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(wormhole, "tpcancel", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			if (finalList.isEmpty()) {
				finalList.add(wormhole.getMessageManager().messageConfig.getString(ColorUtils.translate("command-parse.no-tab.player"), "command-parse.no-tab.player"));
			}
			return finalList;
		}
		return Collections.singletonList("");
	}
}
