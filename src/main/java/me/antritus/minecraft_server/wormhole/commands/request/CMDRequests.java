package me.antritus.minecraft_server.wormhole.commands.request;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
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
public class CMDRequests extends CoreCommand {
	public CMDRequests(){
		super("tprequests", Wormhole.configuration.getLong("commands.tprequests.cooldown", 0));
		setPermission("wormhole.requests");
		setDescription(Wormhole.configuration.getString("commands.tprequests.description", "commands.tprequests.description"));
		setUsage(Wormhole.configuration.getString("commands.tprequests.usage", "commands.tprequest.usage"));
		setAliases(Wormhole.configuration.getStringList("commands.tprequests.aliases"));
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		Player other = null;
		if (args.length > 0){
			other = Bukkit.getPlayer(args[0]);
			if (other == null){
				if (!player.hasPermission("wormhole.requests.others")){
					player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tprequests.no-perms-check")));
					return true;
				}
				return true;
			}
			if (!player.hasPermission("wormhole.requests.others")){
				Wormhole.sendMessage(player, other, "commands.tprequests.no-perms-check-others");
				return true;
			}
			TpPlayerAfterParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tprequests", player, other);
			TpRequestEventFactory.trigger(parseEvent);
			if (parseEvent.isCancelled()){
				return true;
			}
			user = Wormhole.manager.getUser(other);
			if (user == null){
				throw new RuntimeException("Could not get user of: "+ other.getName());
			}
		}
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		StringBuilder sent = new StringBuilder();
		StringBuilder requested = new StringBuilder();
		for (TeleportRequest request : user.requests()) {
			if (request.isValid()){
				if (sent.length() != 0){
					sent.append(", ");
				}
				sent.append(Bukkit.getPlayer(request.getRequested()).getName());
			}
		}
		for (TeleportRequest request : user.others()) {
			if (request.isValid()){
				if (requested.length() != 0){
					requested.append(", ");
				}
				requested.append(Bukkit.getPlayer(request.getWhoRequested()).getName());
			}
		}
		if (sent.isEmpty()){
			sent.append("None");
		}
		if (requested.isEmpty()){
			requested.append("None");
		}
		String msgFormat = Wormhole.configuration.getString("commands.tprequests.format-self", "commands.tprequests.format-self").replace("%sent%", sent.toString()).replace("%received%", requested.toString());
		if (other != null){
			msgFormat = Wormhole.configuration.getString("commands.tprequests.format-other", "commands.tprequests.format-other").replace("%sent%", sent.toString()).replace("%received%", requested.toString()).replace("%who%", other.getName());
		}
		msgFormat = msgFormat.replace("%latest%", user.latestRequest != null ? Bukkit.getPlayer(user.latestRequest.getWhoRequested()).getName() : "None");
		commandSender.sendMessage(ColorUtils.translateComp(msgFormat));
		cooldowns.put(player.getUniqueId(), System.currentTimeMillis()+super.cooldown);
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			if (!sender.hasPermission("wormhole.requests.others")){
				return Collections.singletonList("");
			}
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tprequests", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			return finalList;
		}
		return Collections.singletonList("");
	}

}
