package me.antritus.minecraft_server.wormhole.commands.request;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.events.request.*;
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
public class CMDDeny extends CoreCommand {
	public CMDDeny() {
		super("tpdeny", Wormhole.configuration.getLong("commands.tpdeny.cooldown", 0));
		setPermission("wormhole.deny");
		setDescription(Wormhole.configuration.getString("commands.tpdeny.description", "commands.tpdeny.description"));
		setUsage(Wormhole.configuration.getString("commands.tpdeny.usage", "commands.tpdeny.usage"));
		setAliases(Wormhole.configuration.getStringList("commands.tpdeny.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		if (args.length == 0){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpdeny.incorrect-format", "commands.tpdeny.incorrect-format")));
			return true;
		}
		Player sender = Bukkit.getPlayer(args[0]);
		if (sender == null){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpdeny.unknown-player", "commands.tpdeny.unknown-player")));
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(sender.getUniqueId())) {
				player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpdeny.request-self", "commands.tpdeny.request-self")));
				return true;
			}
		}
		TpPlayerAfterParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tpdeny", player, sender);
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
			Wormhole.sendMessage(player, sender, "commands.tpdeny.no-request-found");
			return true;
		}
		if (requestPlayer.equals(requestSender)){
			TpRequestDenyEvent event = TpRequestEventFactory.createDenyEvent(player, sender, requestSender);
			TpRequestEventFactory.trigger(event);
			if (event.isCancelled()){
				return true;
			}
			senderUser.removeRequest(requestSender);
			user.removeOther(requestPlayer);
			Wormhole.sendMessage(sender, player, "commands.tpdeny.denied-sender");
			Wormhole.sendMessage(player, sender, "commands.tpdeny.denied-requested");
		} else {
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpdeny.no-request-found", "commands.tpdeny.no-request-found")));
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
					Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) == null
							||
							(Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) != null
									&&
									!Wormhole.manager.getUser(sender).
											getRequest(player, TeleportRequest.Type.SENDER).isValid()));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tpdeny", sender, players);
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