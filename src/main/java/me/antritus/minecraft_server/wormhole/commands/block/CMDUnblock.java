package me.antritus.minecraft_server.wormhole.commands.block;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDUnblock extends CoreCommand {
	public CMDUnblock(){
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
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		return Collections.singletonList("");
	}

}
