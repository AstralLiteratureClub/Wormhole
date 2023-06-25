package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.astrolapi.annotations.NotNull;
import me.antritus.astrolapi.annotations.Nullable;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDUnblock extends CoreCommand {
	protected CMDUnblock(Wormhole main){
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
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		return null;
	}
}
