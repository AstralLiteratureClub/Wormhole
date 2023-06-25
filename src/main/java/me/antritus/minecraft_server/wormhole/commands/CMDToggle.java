package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import me.antritus.astrolapi.annotations.NotNull;
import me.antritus.astrolapi.annotations.Nullable;

import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDToggle extends CoreCommand{

	protected CMDToggle(){
		super("tptoggle");
		setDescription(Wormhole.configuration.getString("commands.tptoggle.description", "Allows player toggle between receiving and not receiving."));
		setUsage(Wormhole.configuration.getString("commands.tptoggle.usage", "/tptoggle"));
		setAliases(Wormhole.configuration.getStringList("commands.tptoggle.aliases"));
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
