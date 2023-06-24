package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.wormhole.Main;
import net.luckperms.api.model.user.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDBlock extends CoreCommand{
	private final Main main;

	protected CMDBlock(Main main){
		super("Tpdeny");
		setDescription(Main.configuration.getString("tpblock.description", "Allows player to block teleport requests of given player."));
		setUsage(Main.configuration.getString("tpblock.usage", "/tpdeny <online player>"));
		setAliases(Main.configuration.getStringList("tpblock.aliases"));
		this.main = main;
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		return false;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		return null;
	}
}
