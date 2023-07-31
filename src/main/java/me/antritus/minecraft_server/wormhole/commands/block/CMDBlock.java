package me.antritus.minecraft_server.wormhole.commands.block;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.MessageManager;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.events.PlayerTabCompleteRequestEvent;
import me.antritus.minecraft_server.wormhole.events.TpPlayerAfterParseEvent;
import me.antritus.minecraft_server.wormhole.events.block.TpBlockEvent;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDBlock extends CoreCommand {
	public CMDBlock(Wormhole wormhole){
		super(wormhole, "tpblock");
		setPermission("wormhole.block");
		setDescription(wormhole.getCommandConfig().getString("tpblock.description", "tpblock.description"));
		setUsage(wormhole.getConfig().getString("tpblock.usage", "tpblock.usage"));
		setAliases(wormhole.getConfig().getStringList("tpblock.aliases"));
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		MessageManager messageManager = wormhole.getMessageManager();
		if (!(commandSender instanceof Player player)){
			messageManager.message(commandSender, "command-parse.player-only");
			return true;
		}
		if (args.length == 0){
			messageManager.message(player, "command-parse.incorrect-format", "%command%=tpblock <player>");
			return true;
		}
		User user = wormhole.getUserDatabase().get(player);
		if (user == null) {
			throw new RuntimeException("Could not get user of: " + player.getName());
		}
		Player other = Bukkit.getPlayer(args[0]);
		if (other == null){
			Wormhole.sendMessage(player, args[0], "block.unknown-player", "%command%=tpblock <player>");
			return true;
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent(wormhole, "tpblock", player, other);
		parseEvent.callEvent();
		if (parseEvent.isCancelled()){
			return true;
		}
		if (user.isBlocked(other)){
			Wormhole.sendMessage(player, other, "block.already", "%command%=tpblock <player>");
			return true;
		}
		TpBlockEvent event = new TpBlockEvent(wormhole, player, other);
		event.callEvent();
		user.block(other);
		Wormhole.sendMessage(player, other, "block.blocked");
		return true;
	}

	@Override
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length==1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			players.removeIf(player -> wormhole.getUserDatabase().getKnownNonNull(sender).isBlocked(player));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(wormhole, "tpblock", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			if (finalList.isEmpty()){
				finalList.add(wormhole.getMessageManager().messageConfig.getString(ColorUtils.translate("command-parse.no-tab.player"), "command-parse.no-tab.player"));
			}
			finalList.add("-list");
			return finalList;
		}
		return Collections.singletonList("");
	}
}
