package me.antritus.minecraft_server.wormhole.commands.block;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class CMDBlocked extends CoreCommand {
	protected CMDBlocked(Wormhole wormhole) {
		super(wormhole, "tpblocked");
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		//todo
		return true;
	}
}
