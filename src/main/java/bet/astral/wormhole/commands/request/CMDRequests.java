package bet.astral.wormhole.commands.request;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.antsfactions.MessageManager;
import bet.astral.wormhole.api.Request;
import bet.astral.wormhole.api.TeleportManager;
import bet.astral.wormhole.astrolminiapi.CoreCommand;
import bet.astral.wormhole.events.PlayerTabCompleteRequestEvent;
import bet.astral.wormhole.events.TpPlayerAfterParseEvent;
import bet.astral.wormhole.manager.User;
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
public class CMDRequests extends CoreCommand {
	public CMDRequests(Wormhole wormhole){
		super(wormhole, "tprequests");
		setPermission("wormhole.requests");
		setDescription(wormhole.getCommandConfig().getString("tprequests.description", "tprequests.description"));
		setUsage(wormhole.getConfig().getString("tprequests.usage", "tprequests.usage"));
		setAliases(wormhole.getConfig().getStringList("tprequests.aliases"));
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		MessageManager messageManager = wormhole.getMessageManager();
		if (!(commandSender instanceof Player player)){
			messageManager.message(commandSender, "command-parse.player-only");
			return true;
		}
		User user = wormhole.getUserDatabase().get(player);
		Player other = null;
		if (args.length > 0){
			other = Bukkit.getPlayer(args[0]);
			if (other == null){
				if (!player.hasPermission("wormhole.requests.others")){
					messageManager.message(player, "requests.others.no-perm");
					return true;
				}
				return true;
			}
			if (!player.hasPermission("wormhole.requests.others")){
				Wormhole.sendMessage(player, other, "commands.tprequests.no-perms-check-others");
				return true;
			}
			TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent(wormhole, "tprequests", player, other);
			parseEvent.callEvent();
			if (parseEvent.isCancelled()){
				return true;
			}
			user = wormhole.getUserDatabase().get(other);
			if (user == null){
				throw new RuntimeException("Could not get user of: "+ other.getName());
			}
		}
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		TeleportManager manager = wormhole.getTeleportManager();
		StringBuilder sent = new StringBuilder();
		StringBuilder requested = new StringBuilder();
		for (Request request : manager.getAllRequests(player).values()) {
			if (sent.length() != 0) {
				sent.append(", ");
			}
			sent.append(Bukkit.getPlayer(request.getRequested()).getName());
		}
		for (Request request : manager.getAllReceivedRequests(player).values()) {
			if (requested.length() != 0) {
				requested.append(", ");
			}
			requested.append(Bukkit.getPlayer(request.getWhoAsked()).getName());
		}
		if (sent.isEmpty()){
			sent.append("None");
		}
		if (requested.isEmpty()){
			requested.append("None");
		}
		if (other != null){
			Wormhole.sendMessage(player, other.getName(), "requests.format-other", "%received%="+requested,
					"%sent%="+sent,
					"%latest%="+(user.getLatestRequest() != null ? Bukkit.getPlayer(user.getLatestRequest().getWhoAsked()) : "None"));
		} else {
			messageManager.message(player, "requests.format-self", "%received%="+requested,
					"%sent%="+sent,
					"%latest%="+(user.getLatestRequest() != null ? Bukkit.getPlayer(user.getLatestRequest().getWhoAsked()) : "None"));
		}
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
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(wormhole,"tprequests", sender, players);
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
