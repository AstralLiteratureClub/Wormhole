package me.antritus.minecraft_server.wormhole.commands.admin;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.0.0-snapshot
 */
public class CMDReload extends CoreCommand {
	public CMDReload(Wormhole wormhole){
		super(wormhole, "tpreload");
		setPermission("wormhole.reload");
		setDescription(wormhole.getCommandConfig().getString("tpreload.description", "tpreload.description"));
		setUsage(wormhole.getConfig().getString("tpreload.usage", "tpreload.usage"));
		setAliases(wormhole.getConfig().getStringList("tpreload.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		wormhole.getMessageManager().message(commandSender, "reload.reloading");
		Wormhole.reload();
		wormhole.getMessageManager().message(commandSender, "reload.reloaded");
		return true;
	}
}
