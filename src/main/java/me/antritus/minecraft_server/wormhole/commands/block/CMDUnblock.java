package me.antritus.minecraft_server.wormhole.commands.block;

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
public class CMDUnblock extends CoreCommand {
	public CMDUnblock(){
		super("tpdeny");
		setDescription(Wormhole.configuration.getString("commands.tpunblock.description", "Allows player to unblock teleport requests of given player."));
		setUsage(Wormhole.configuration.getString("commands.tpunblock.usage", "/tpunblock <online player>"));
		setAliases(Wormhole.configuration.getStringList("commands.tpunblock.aliases"));

	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		return false;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
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
