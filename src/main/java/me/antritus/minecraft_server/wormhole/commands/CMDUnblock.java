package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDUnblock extends CoreCommand {

	private final Main main;

	protected CMDUnblock(Main main){
		super("tpdeny");
		setDescription(Main.configuration.getString("tpunblock.description", "Allows player to unblock teleport requests of given player."));
		setUsage(Main.configuration.getString("tpunblock.usage", "/tpunblock <online player>"));
		setAliases(Main.configuration.getString("tpunblock.aliases", "tpunblock"));

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
