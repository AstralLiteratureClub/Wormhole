package me.antritus.minecraft_server.wormhole.commands.block;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDToggle extends CoreCommand {

	public CMDToggle() {
		super("tptoggle");
		setDescription(Wormhole.configuration.getString("commands.tptoggle.description", "Allows player toggle between receiving and not receiving."));
		setUsage(Wormhole.configuration.getString("commands.tptoggle.usage", "/tptoggle"));
		setAliases(Wormhole.configuration.getStringList("commands.tptoggle.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		user.acceptingRequests = !user.acceptingRequests;
		if (user.acceptingRequests) {
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tptoggle.toggled-off", "commands.tptoggle.toggled-off")));
		} else {
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tptoggle.toggled-on", "commands.tptoggle.toggled-on")));
		}
		return true;
	}


	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		return Collections.singletonList("");
	}
}
