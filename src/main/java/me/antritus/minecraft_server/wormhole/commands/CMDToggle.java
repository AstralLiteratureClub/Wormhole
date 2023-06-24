package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDToggle extends CoreCommand{
	private final Main main;

	protected CMDToggle(Main main){
		super("tptoggle");
		this.main = main;
		setDescription(Main.configuration.getString("tptoggle.description", "Allows player toggle between receiving and not receiving."));
		setUsage(Main.configuration.getString("tptoggle.usage", "/tptoggle"));

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
