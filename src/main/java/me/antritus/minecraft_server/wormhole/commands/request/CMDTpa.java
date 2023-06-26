package me.antritus.minecraft_server.wormhole.commands.request;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestPlayerPrepareParseEvent;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestSendEvent;
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
 * @author antritus
 */
public class CMDTpa extends CoreCommand {
	public CMDTpa() {
		super("tpa");
		setDescription(Wormhole.configuration.getString("commands.tpa.description", "Allows player to ask a teleport to specific player"));
		setUsage(Wormhole.configuration.getString("commands.tpa.usage", "/tpa <online player>"));
		setAliases(Wormhole.configuration.getStringList("commands.tpa.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		if (args.length == 0){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.incorrect-format", "commands.tpa.incorrect-format")));
			return true;
		}
		Player requested = Bukkit.getPlayer(args[0]);
		if (requested == null){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.unknown-player", "commands.tpa.unknown-player")));
			return true;
		}
		if (!Wormhole.DEBUG) {
			if (player.getUniqueId().equals(requested.getUniqueId())) {
				player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.request-self", "commands.tpa.request-self")));
				return true;
			}
		}
		TpRequestPlayerPrepareParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tpa", player, requested);
		TpRequestEventFactory.trigger(parseEvent);
		if (parseEvent.isCancelled()){
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		User requestUser = Wormhole.manager.getUser(requested);
		if (requestUser == null){
			throw new RuntimeException("Could not get user of: "+ requested.getName());
		}
		if (requestUser.isBlocked(player)){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.blocked", "commands.tpa.blocked")));
			return true;
		}
		if (!requestUser.acceptingRequests){
			Wormhole.sendMessage(player, requested, "commands.tpa.requests-off");
			return true;
		}
		TeleportRequest request = user.getRequest(requested, TeleportRequest.Type.SENDER);
		if (request != null && request.isValid()){
			Wormhole.sendMessage(player, requested, "commands.tpa.request-already-sent");
			return true;
		} else {
			TpRequestSendEvent event = TpRequestEventFactory.createSendEvent(player, requested);
			TpRequestEventFactory.trigger(event);
			if (event.isCancelled()){
				return true;
			}
			request = user.teleportRequest(requested);
			requestUser.receiveRequest(request);
			Wormhole.sendMessage(player, requested, "commands.tpa.sent-sender");
			Wormhole.sendMessage(requested, player, "commands.tpa.sent-requested");
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
					Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) != null
							||
							Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) != null
									&&
									Wormhole.manager.getUser(sender).
											getRequest(player, TeleportRequest.Type.SENDER).isValid());
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tpa", sender, players);
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
