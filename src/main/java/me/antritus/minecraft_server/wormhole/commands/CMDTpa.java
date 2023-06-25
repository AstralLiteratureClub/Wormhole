package me.antritus.minecraft_server.wormhole.commands;

import me.antritus.astrolapi.annotations.NotNull;
import me.antritus.astrolapi.annotations.Nullable;
import me.antritus.astrolapi.minecraft.ColorUtils;
import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.events.TpRequestEventFactory;
import me.antritus.minecraft_server.wormhole.events.request.TpRequestPlayerPrepareParseEvent;
import me.antritus.minecraft_server.wormhole.manager.TeleportRequest;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDTpa extends CoreCommand{
	public CMDTpa() {
		super("tpa");
		setDescription(Wormhole.configuration.getString("commands.tpa.description", "Allows player to ask a teleport to specific player"));
		setUsage(Wormhole.configuration.getString("commands.tpa.usage", "/tpa <online player>"));
		setAliases(Wormhole.configuration.getStringList("commands.tpa.aliases"));
	}

	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args) {
		if (!(commandSender instanceof Player player)){
			playerOnly();
			return true;
		}
		if (args.length == 0){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.incorrect-format", "commands.tpa.incorrect-format")));
			return true;
		}
		Player requested = Bukkit.getPlayer(args[0]);
		if (requested == null){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.unknown-player", "commands.tpa.unknown-player")));
			return true;
		}
		TpRequestPlayerPrepareParseEvent parseEvent = TpRequestEventFactory.createSendPrepareEvent("tpa", player, requested);
		if (parseEvent.isCancelled()){
			return true;
		}
		User user = Wormhole.manager.getUser(player);
		if (user == null){
			throw new RuntimeException("Could not get user of: "+ player.getName());
		}
		User requestUser = Wormhole.manager.getUser(requested);
		if (requestUser == null){
			throw new RuntimeException("Could not get user of: "+ requested.getName());
		}
		if (requestUser.isBlocked(player)){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.blocked", "commands.tpa.blocked")));
			return true;
		}
		TeleportRequest request = user.getRequest(requested);
		if (request != null && request.isValid()){
			player.sendMessage(ColorUtils.translateComp(Wormhole.configuration.getString("commands.tpa.request-already-sent", "commands.tpa.request-already-sent")));
			return true;
		} else {
			request = user.teleportRequest(requested);
			requestUser.receiveRequest(request);
			Wormhole.sendMessage(player, requested, "commands.tpa.sent-sender");
			Wormhole.sendMessage(requested, player, "commands.tpa.sent-requested");
		}
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		return null;
	}
}
