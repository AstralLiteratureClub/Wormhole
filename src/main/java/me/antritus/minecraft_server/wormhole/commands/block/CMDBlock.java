package me.antritus.minecraft_server.wormhole.commands.block;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDBlock extends CoreCommand {
	public CMDBlock(){
		super("tpblock", Wormhole.configuration.getLong("commands.tpblock.cooldown", 0));
		setPermission("wormhole.block");
		setDescription(Wormhole.configuration.getString("commands.tpblock.description", "commands.tpblock.description"));
		setUsage(Wormhole.configuration.getString("commands.tpblock.usage", "commands.tpblock.usage"));
		setAliases(Wormhole.configuration.getStringList("commands.tpblock.aliases"));
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		if (args.length == 0){
			sendMessage(player, Wormhole.configuration.getString("commands.tpblock.incorrect-format", "commands.tpblock.incorrect-format"));
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		if (args[0].equalsIgnoreCase("-list")){
			if (user.blocked().size() == 0){
				sendMessage(player, Wormhole.configuration.getString("commands.tpblock.list.no-blocked", "commands.tpblock.list.no-blocked"));
				return true;
			}
			StringBuilder builder = new StringBuilder();
			String blocked = Wormhole.configuration.getString("commands.tpblock.list.blocked", "commands.tpblock.list.blocked");
			String split = Wormhole.configuration.getString("commands.tpblock.list.comma", "commands.tpblock.list.comma");
			for (String s1 : user.blocked()) {
				if (builder.length()>0){
					builder.append(split);
				}
				builder.append(blocked.replace("%name%", Bukkit.getPlayer(UUID.fromString(s1)).getName()));
			}
			String msg = Wormhole.configuration.getString("commands.tpblock.list.format", "commands.tpblock.list.format");
			msg = msg.replace("%amount%", String.valueOf(user.blocked().size()));
			msg = msg.replace("%blocked%", builder.toString());
			sendMessage(commandSender, msg);
			return true;
		}
		Player other = Bukkit.getPlayer(args[0]);
		if (other == null){
			sendMessage(player, Wormhole.configuration.getString("commands.tpblock.unknown-player", "commands.tpblock.unknown-player"));
			return true;
		}
		TpPlayerAfterParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tpblock", player, other);
		TpRequestEventFactory.trigger(parseEvent);
		if (parseEvent.isCancelled()){
			return true;
		}
		if (user.isBlocked(other)){
			sendMessage(player, Wormhole.configuration.getString("commands.tpblock.already-blocked", "commands.tpblock.already-blocked"));
			return true;
		}

		user.block(other, true);
		sendMessage(player, Wormhole.configuration.getString("commands.tpblock.blocked", "commands.tpblock.blocked"));
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			players.removeIf(player -> Wormhole.manager.getUser(sender).isBlocked(player));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent("tpblock", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			if (finalList.isEmpty()){
				finalList.add(Wormhole.configuration.getString("settings.no-player-tab-completion", "settings.no-player-tab-completion"));
			}
			finalList.add("-list");
			return finalList;
		}
		return Collections.singletonList("");
	}
}
