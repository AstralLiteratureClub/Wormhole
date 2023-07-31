package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CMDShrug extends CoreCommand {
	public CMDShrug(Wormhole wormhole) {
		super(wormhole, "shrug");
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		if (commandSender instanceof Player player){
			player.chat("¯\\_(ツ)_/¯");
		}
		return true;
	}
}
