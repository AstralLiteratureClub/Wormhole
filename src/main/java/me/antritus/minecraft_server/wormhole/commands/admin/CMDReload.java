package me.antritus.minecraft_server.wormhole.commands.admin;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import org.bukkit.command.CommandSender;

public class CMDReload extends CoreCommand {
	public CMDReload() {
		super("tpreload");
		setDescription(Wormhole.configuration.getString("commands.tpreload.description", "Allows reloading on Wormhole."));
		setUsage(Wormhole.configuration.getString("commands.tpreload.usage", "/tpreload"));
		setAliases(Wormhole.configuration.getStringList("commands.tpreload.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		sendMessage(commandSender, Wormhole.configuration.getString("commands.tpreload.reloading"));
		Wormhole.reload();
		sendMessage(commandSender, Wormhole.configuration.getString("commands.tpreload.reloaded"));
		return true;
	}
}
