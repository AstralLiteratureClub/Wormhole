package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Main;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDAccept extends CoreCommand{

	private final Main main;
	protected CMDAccept(Main main) {
		super("tpaccept");
		this.main = main;
		setDescription(Main.configuration.getString("tpaccept.description", "Allows players to accept teleport of given player."));
		setUsage(Main.configuration.getString("tpaccept.usage", "/tpaccept <online player>"));
		setAliases(Main.configuration.getStringList("tpaccept.aliases"));
	}


	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
		return true;
	}


	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		return null;
	}
}