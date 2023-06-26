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
public class CMDBlock extends CoreCommand {
	public CMDBlock(){
		super("Tpdeny");
		setDescription(Wormhole.configuration.getString("commands.tpblock.description", "Allows player to block teleport requests of given player."));
		setUsage(Wormhole.configuration.getString("commands.tpblock.usage", "/tpdeny <online player>"));
		setAliases(Wormhole.configuration.getStringList("commands.tpblock.aliases"));
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		return false;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		return Collections.singletonList("");
	}

}
