package me.antritus.minecraft_server.wormhole.commands.block;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.astrolminiapi.Nullable;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus, lunarate
 */
public class CMDToggle extends CoreCommand {

	public CMDToggle(){
		super("tptoggle");
		setDescription(Wormhole.configuration.getString("commands.tptoggle.description", "Allows player toggle between receiving and not receiving."));
		setUsage(Wormhole.configuration.getString("commands.tptoggle.usage", "/tptoggle"));
		setAliases(Wormhole.configuration.getStringList("commands.tptoggle.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		return false;
	}
}
