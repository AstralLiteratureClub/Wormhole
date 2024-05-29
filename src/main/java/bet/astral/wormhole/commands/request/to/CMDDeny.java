package bet.astral.wormhole.commands.request.to;


import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.antsfactions.MessageManager;
import bet.astral.wormhole.api.Request;
import bet.astral.wormhole.api.TeleportManager;
import bet.astral.wormhole.astrolminiapi.ColorUtils;
import bet.astral.wormhole.astrolminiapi.CoreCommand;
import bet.astral.wormhole.events.PlayerTabCompleteRequestEvent;
import bet.astral.wormhole.events.TpPlayerAfterParseEvent;
import bet.astral.wormhole.events.request.TpRequestAcceptEvent;
import me.antritus.minecraft_server.wormhole.events.request.*;
import bet.astral.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDDeny extends CoreCommand {
	public CMDDeny(Wormhole wormhole){
		super(wormhole, "tpdeny");
		setPermission("wormhole.deny");
		setDescription(wormhole.getCommandConfig().getString("tpdeny.description", "tpdeny.description"));
		setUsage(wormhole.getConfig().getString("tpdeny.usage", "tpdeny.usage"));
		setAliases(wormhole.getConfig().getStringList("tpdeny.aliases"));
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
			messageManager.message(player, "deny.no-requests");
			return true;
		}
		if (args.length == 0) {
			User user = wormhole.getUserDatabase().getKnownNonNull(player);
			user.findLatest();
			Request request = user.getLatestRequest();
			if (request == null){
				messageManager.message(player, "deny.latest.no-request");
				return true;
			}
			OfflinePlayer sender = Bukkit.getOfflinePlayer(request.getWhoAsked());
			if (!sender.isOnline()) {
				messageManager.message(player, "deny.latest.sender-null");
				return true;
			}
			TeleportManager teleportManager = wormhole.getTeleportManager();
			TpRequestAcceptEvent event = new TpRequestAcceptEvent(wormhole, player,
					Objects.requireNonNull(sender.getPlayer()),
					request);
			event.callEvent();
			if (event.isCancelled()) {
				return true;
			}
			request.setAccepted(true);
			request.setDenied(false);
			teleportManager.beginTeleport(sender.getPlayer(), player);
			Wormhole.sendMessage(sender.getPlayer(), player, "deny.deny-sender");
			Wormhole.sendMessage(player, sender.getPlayer(), "deny.deny-requested");
			return true;
		}
		OfflinePlayer sender = Bukkit.getOfflinePlayer(args[0]);
		if (!sender.isOnline()){
			Wormhole.sendMessage(player, args[0], "accept.manual.unknown-player", "%command%=tpdeny <name>");
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				messageManager.message(player, "deny.manual.self", "%command%=tpdeny <name>");
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent(wormhole, "tpdeny", player, Objects.requireNonNull(sender.getPlayer()));
		parseEvent.callEvent();
		if (parseEvent.isCancelled()){
			return true;
		}

		if (!manager.hasRequested(sender, player)) {
			Wormhole.sendMessage(player, sender.getName(), "deny.manual.no-request", "%command%=tpdeny <name>");
			return true;
		}
		Request request = manager.getRequest(sender.getPlayer(), player);
		// This should never happen.
		if (request == null) {
			return true;
		}
		TpRequestAcceptEvent event = new TpRequestAcceptEvent(wormhole, player, sender.getPlayer(), request);
		event.callEvent();
		if (event.isCancelled()){
			return true;
		}
		request.setAccepted(false);
		request.setDenied(true);
		Wormhole.sendMessage(sender.getPlayer(), player, "deny.deny-sender");
		Wormhole.sendMessage(player, sender.getPlayer(), "deny.deny-requested");
		return true;
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			TeleportManager manager = wormhole.getTeleportManager();
			players.removeIf(player -> manager.hasRequested(sender, player));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(wormhole, "tpaccept", sender, players);
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