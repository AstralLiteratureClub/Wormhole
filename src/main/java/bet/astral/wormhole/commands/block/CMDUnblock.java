package bet.astral.wormhole.commands.block;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.antsfactions.MessageManager;
import bet.astral.wormhole.astrolminiapi.ColorUtils;
import bet.astral.wormhole.astrolminiapi.CoreCommand;
import bet.astral.wormhole.events.PlayerTabCompleteRequestEvent;
import bet.astral.wormhole.events.TpPlayerAfterParseEvent;
import bet.astral.wormhole.manager.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class CMDUnblock extends CoreCommand {
	public CMDUnblock(Wormhole wormhole){
		super(wormhole, "tpunblock");
		setPermission("wormhole.unblock");
		setDescription(wormhole.getCommandConfig().getString("tpunblock.description", "tpunblock.description"));
		setUsage(wormhole.getConfig().getString("tpunblock.usage", "tpunblock.usage"));
		setAliases(wormhole.getConfig().getStringList("tpunblock.aliases"));
	}
	@Override
	public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] args){
		MessageManager messageManager = wormhole.getMessageManager();
		if (!(commandSender instanceof Player player)){
			messageManager.message(commandSender, "command-parse.player-only");
			return true;
		}
		if (args.length == 0){
			messageManager.message(player, "command-parse.incorrect-format", "%command%=tpunblock <player>");
			return true;
		}
		User user = wormhole.getUserDatabase().get(player);
		if (user == null) {
			throw new RuntimeException("Could not get user of: " + player.getName());
		}
		Player other = Bukkit.getPlayer(args[0]);
		if (other == null){
			Wormhole.sendMessage(player, args[0], "unblock.unknown-player", "%command%=tpunblock <player>");
			return true;
		}
		TpPlayerAfterParseEvent parseEvent = new TpPlayerAfterParseEvent(wormhole, "tpunblock", player, other);
		parseEvent.callEvent();
		if (parseEvent.isCancelled()){
			return true;
		}
		if (!user.isBlocked(other)){
			Wormhole.sendMessage(player, other, "unblock.already", "%command%=tpunblock <player>");
			return true;
		}

		user.unblock(other);
		Wormhole.sendMessage(player, other, "unblock.unblocked");
		return true;
	}
	public @NotNull List<String> tabComplete(@NotNull CommandSender commandSender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
		if (args.length == 1) {
			Player sender = (Player) commandSender;
			List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
			players.remove(sender);
			players.removeIf(player -> wormhole.getUserDatabase().getKnownNonNull(sender).isBlocked(player));
			PlayerTabCompleteRequestEvent e = new PlayerTabCompleteRequestEvent(wormhole, "tpunblock", sender, players);
			Bukkit.getServer().getPluginManager().callEvent(e);
			List<String> finalList = new ArrayList<>();
			for (Player player : players) {
				finalList.add(player.getName());
			}
			if (finalList.isEmpty()) {
				finalList.add(wormhole.getMessageManager().messageConfig.getString(ColorUtils.translate("command-parse.no-tab.player"), "command-parse.no-tab.player"));
			}
			finalList.add("-list");
			return finalList;
		}
		return Collections.singletonList("");
	}
}