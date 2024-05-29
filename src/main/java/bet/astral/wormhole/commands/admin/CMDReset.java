package bet.astral.wormhole.commands.admin;

import bet.astral.wormhole.Wormhole;
import bet.astral.wormhole.astrolminiapi.CoreCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antritus
 * @since 1.0.0-snapshot
 */
public class CMDReset extends CoreCommand {

	protected CMDReset(Wormhole wormhole) {
		super(wormhole, "tpreset");
		setPermission("wormhole.reset");
		setDescription(wormhole.getCommandConfig().getString("tpreset.description", "tpreset.description"));
		setUsage(wormhole.getConfig().getString("tpreset.usage", "tpreset.usage"));
		setAliases(wormhole.getConfig().getStringList("tpreset.aliases"));
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
