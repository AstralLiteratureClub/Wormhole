package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDDeny extends CoreCommand {

	private final Main main;

	protected CMDDeny(Main main) {
		super("tpdeny");
		this.main = main;
		setDescription(Main.configuration.getString("tpdeny.description", "Allows player to deny teleport of given player."));
		setUsage(Main.configuration.getString("tpdeny.usage", "/tpdeny <online player>"));
		setAliases(Main.configuration.get("tpdeny.aliases", "tpdeny"));
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
