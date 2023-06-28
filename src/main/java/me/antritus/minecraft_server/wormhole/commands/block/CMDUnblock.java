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

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDUnblock extends CoreCommand {
	public CMDUnblock() {
		super("tpunblock", Wormhole.configuration.getLong("commands.tpunblock.cooldown", 0));
		setPermission("wormhole.unblock");
		setDescription(Wormhole.configuration.getString("commands.tpunblock.description", "commands.tpunblock.description"));
		setUsage(Wormhole.configuration.getString("commands.tpunblock.usage", "commands.tpunblock.usage"));
		setAliases(Wormhole.configuration.getStringList("commands.tpunblock.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)) {
			playerOnly();
			return true;
		}
		if (args.length == 0) {
			sendMessage(player, Wormhole.configuration.getString("commands.tpunblock.incorrect-format", "commands.tpunblock.incorrect-format"));
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		if (user == null) {
			throw new RuntimeException("Could not get user of: " + player.getName());
		}
		Player other = Bukkit.getPlayer(args[0]);
		if (other == null) {
			sendMessage(player, Wormhole.configuration.getString("commands.tpunblock.unknown-player", "commands.tpunblock.unknown-player"));
			return true;
		}
		TpPlayerAfterParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tpunblock", player, other);
		TpRequestEventFactory.trigger(parseEvent);
		if (parseEvent.isCancelled()) {
			return true;
		}
		if (user.isBlocked(other)) {
			sendMessage(player, Wormhole.configuration.getString("commands.tpunblock.already-unblocked", "commands.tpunblock.already-unblocked"));
			return true;
		}

		user.block(other, false);
		sendMessage(player, Wormhole.configuration.getString("commands.tpunblock.unblocked", "commands.tpunblock.unblocked"));
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length == 1) {
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
			if (finalList.isEmpty()) {
				finalList.add(Wormhole.configuration.getString("settings.no-player-tab-completion", "settings.no-player-tab-completion"));
			}
			finalList.add("-list");
			return finalList;
		}
		return Collections.singletonList("");
	}
}