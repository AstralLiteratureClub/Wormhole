package bet.astral.wormhole.commands.block;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.astrolminiapi.CoreCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class CMDBlocked extends CoreCommand {
	protected CMDBlocked(Wormhole wormhole) {
		super(wormhole, "tpblocked");
		setPermission("wormhole.blocked");
		setDescription(wormhole.getCommandConfig().getString("tpblocked.description", "tpblocked.description"));
		setUsage(wormhole.getConfig().getString("tpblocked.usage", "tpblocked.usage"));
		setAliases(wormhole.getConfig().getStringList("tpblocked.aliases"));
	}
	/**
	 * Executes the command, returning its success
	 *
	 * @param sender       Source object which is executing this command
	 * @param commandLabel The alias of the command used
	 * @param args         All arguments passed to the command, split via ' '
	 * @return true if the command was successful, otherwise false
	 */
	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		//todo
		// never
		return true;
	}
}