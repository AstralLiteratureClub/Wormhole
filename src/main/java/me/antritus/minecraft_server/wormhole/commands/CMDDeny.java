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
public class CMDDeny extends CoreCommand {
	protected CMDDeny() {
		super("tpdeny");
		setDescription(Wormhole.configuration.getString("commands.tpdeny.description", "Allows player to deny teleport of given player."));
		setUsage(Wormhole.configuration.getString("commands.tpdeny.usage", "/tpdeny <online player>"));
		setAliases(Wormhole.configuration.getStringList("commands.tpdeny.aliases"));
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
