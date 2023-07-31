package me.antritus.minecraft_server.wormhole.commands.request.to;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.MessageManager;
import me.antritus.minecraft_server.wormhole.api.Request;
import me.antritus.minecraft_server.wormhole.api.TeleportManager;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestSendEvent;
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
 * @author antritus
 */
public class CMDTpa extends CoreCommand {
	public CMDTpa(Wormhole wormhole) {
		super(wormhole, "tpa");
		setPermission("wormhole.request");
		setDescription(wormhole.getCommandConfig().getString("tpa.description", "tpa.description"));
		setUsage(wormhole.getCommandConfig().getString("tpa.usage", "tpa.usage"));
		setAliases(wormhole.getCommandConfig().getStringList("tpa.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		MessageManager messageManager = wormhole.getMessageManager();
		if (!(commandSender instanceof Player player)){
			messageManager.message(commandSender, "command-parse.player-only");
			return true;
		}
		if (args.length == 0){
			messageManager.message(player, "command-parse.incorrect-format", "%command%=/tpa <player>");
			return true;
		}
		Player requested = Bukkit.getPlayer(args[0]);
		if (requested == null){
			Wormhole.sendMessage(player, args[0], "request.unknown-player");
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(requested.getUniqueId())) {
				messageManager.message(player, "request.self");
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent(wormhole, "tpa", player, requested);
		parseEvent.callEvent();
		if (parseEvent.isCancelled()){
			return true;
		}
		User user = wormhole.getUserDatabase().get(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		User requestUser = wormhole.getUserDatabase().get(requested);
		if (requestUser == null){
			throw new RuntimeException("Could not get user of: "+ requested.getName());
		}
		if (requestUser.isBlocked(player)){
			Wormhole.sendMessage(player, requested, "request.blocked", "%command%=tpa <player>");
			return true;
		}
		if (!requestUser.isAcceptingRequests) {
			Wormhole.sendMessage(player, requested, "request.disabled", "%command%=tpa <player>");
			return true;
		}
		TeleportManager manager = wormhole.getTeleportManager();
		if (manager.hasRequested(player, requested)){
			Wormhole.sendMessage(player, requested, "request.duplicate", "%command%=tpa <player>");
			return true;
		} else {
			Request request = new Request(wormhole, player.getUniqueId(), requested.getUniqueId());
			TpRequestSendEvent event = new TpRequestSendEvent(wormhole, player, requested, request.getEnd());
			event.callEvent();
			if (event.isCancelled()){
				return true;
			}
			manager.request(request);
			Wormhole.sendMessage(player, requested, "request.sent-sender", "%command%=tpcancel "+requested.getName());
			Wormhole.sendMessage(requested, player, "request.sent-requested", "%command-accept%=tpaccept "+player.getName(), "%command-deny%=tpdeny "+player.getName());
		}
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
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(wormhole, "tpa", sender, players);
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
