package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDTpa extends CoreCommand{
	private final Main main;
	protected CMDTpa(Main main) {
		super("tpa");
		this.main = main;
		setDescription(Main.configuration.getString("tpa.description", "Allows player to ask a teleport to specific player"));
		setUsage(Main.configuration.getString("tpa.usage", "/tpa <online player>"));
		setAliases(Main.configuration.getStringList("tpa.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		return null;
	}
}
