package me.antritus.minecraft_server.wormhole.commands.request;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.Nullable;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
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
		super("tpdeny");
		setDescription(Wormhole.configuration.getString("commands.tpdeny.description", "Allows player to deny teleport of given player."));
		setUsage(Wormhole.configuration.getString("commands.tpdeny.usage", "/tpdeny <online player>"));
		setAliases(Wormhole.configuration.getStringList("commands.tpdeny.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		commandSender.sendMessage("Totally denied!");
		return false;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.removeIf(player -> Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER) != null && Wormhole.manager.getUser(sender).getRequest(player, TeleportRequest.Type.SENDER).isValid());
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(sender, players);
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
