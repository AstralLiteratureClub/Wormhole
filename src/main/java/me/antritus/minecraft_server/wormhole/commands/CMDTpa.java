package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.minecraft_server.astrolminiapi.api.chat.ColorUtils;
import me.antritus.minecraft_server.wormhole.Main;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestPlayerPrepareParseEvent;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CMDTpa extends CoreCommand{
	protected CMDTpa() {
		super("tpa");
		setDescription(Main.configuration.getString("commands.tpa.description", "Allows player to ask a teleport to specific player"));
		setUsage(Main.configuration.getString("commands.tpa.usage", "/tpa <online player>"));
		setAliases(Main.configuration.getStringList("tpa.aliases"));
		playerOnly = true;
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		if (args.length == 0){
			commandSender.sendMessage(ColorUtils.translate(Main.configuration.getString("commands.tpa.incorrect-format", "commands.tpa.incorrect-format")));
			return true;
		}
		Player requested = Bukkit.getPlayer(args[0]);
		if (player == null){
			commandSender.sendMessage(ColorUtils.translate(Main.configuration.getString("commands.tpa.unknown-player")));
		}
		// make event so we can access it in other plugins... Ex: astroldp
		TpRequestPlayerPrepareParseEvent parse = TpRequestEventFactory.createSendPrepareEvent(player, requested);
		if (parse.isCancelled()){
			return true;
		}

		User user = Main.manager.getUser(player);
		TeleportRequest request = user.teleportRequest(requested);
		user.receiveRequest(request);
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		return null;
	}
}
