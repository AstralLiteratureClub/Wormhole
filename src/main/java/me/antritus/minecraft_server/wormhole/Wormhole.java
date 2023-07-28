package me.antritus.minecraft_server.wormhole;

import me.antritus.minecraft_server.wormhole.antsfactions.*;
import me.antritus.minecraft_server.wormhole.astrolminiapi.Configuration;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.commands.admin.CMDReload;
import me.antritus.minecraft_server.wormhole.commands.block.CMDBlock;
import me.antritus.minecraft_server.wormhole.commands.block.CMDToggle;
import me.antritus.minecraft_server.wormhole.commands.block.CMDUnblock;
import me.antritus.minecraft_server.wormhole.commands.request.*;
import me.antritus.minecraft_server.wormhole.database.UserDatabase;
import me.antritus.minecraft_server.wormhole.manager.TeleportManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class Wormhole extends FactionsPlugin  {
	private static Wormhole wormhole;
	public static boolean DEBUG = true;
	private MessageManager messageManager;
	private UserDatabase userDatabase;
	private Configuration commandConfig;

	public TeleportManager userManager;

	@Override
	public void enable() {
		loadCoreSettings();
		reload();
		wormhole = this;
		messageManager = new MessageManager(this);
		commandConfig = new Configuration(this, "commands.yml");
		try {
			commandConfig.load();
		} catch (IOException | InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}

		userDatabase = new UserDatabase(this);
		userManager = new TeleportManager(this);
		userManager.onEnable();

		getServer().getPluginManager().registerEvents(userManager, this);
		CoreCommand.registerCommand(this, "tpa", new CMDTpa(this));
//		CoreCommand.registerCommand(this, "tpcancel", new CMDCancel());
		CoreCommand.registerCommand(this, "tpaccept", new CMDAccept(this));
		CoreCommand.registerCommand(this, "tpdeny", new CMDDeny(this));
		CoreCommand.registerCommand(this, "tptoggle", new CMDToggle(this));
		CoreCommand.registerCommand(this, "tpblock", new CMDBlock(this));
		CoreCommand.registerCommand(this, "tpunblock", new CMDUnblock(this));
		CoreCommand.registerCommand(this, "tprequests", new CMDRequests());
		CoreCommand.registerCommand(this, "tpreload", new CMDReload(this));

	}
	@Override
	public void disable() {
		userManager.onDisable();
	}

	@Override
	public void updateConfig(@Nullable String oldVersion, String newVersion) { }


	public static void sendMessage(Player player, Player who, String key) {
		wormhole.messageManager.message(player, key, "%player%=" + player.getName(), "%who%=" + who.getName());
	}

	public static void sendMessage(Player player, Player who, String key, String... replacements) {
		String[] args = new String[replacements.length + 2];
		System.arraycopy(replacements, 0, args, 0, replacements.length);
		args[replacements.length] = "%who%=" + who.getName();
		args[replacements.length + 1] = "%player%=" + player.getName();
		wormhole.messageManager.message(player, key, args);
	}

	public static void sendMessage(Player player, String who, String key) {
		wormhole.messageManager.message(player, key, "%player%=" + player.getName(), "%who%=" + who);
	}

	public static void sendMessage(Player player, String who, String key, String... replacements) {
		String[] args = new String[replacements.length + 2];
		System.arraycopy(replacements, 0, args, 0, replacements.length);
		args[replacements.length] = "%who%=" + who;
		args[replacements.length + 1] = "%player%=" + player.getName();
		wormhole.messageManager.message(player, key, args);
	}


	public MessageManager getMessageManager() {
		return messageManager;
	}

	public static void reload(){
		if (wormhole.userManager != null){
			wormhole.userManager.onDisable();
		} else {
			wormhole.userManager = new TeleportManager(wormhole);
		}
		wormhole.userManager.onEnable();

		DEBUG = (boolean) Objects.requireNonNullElse(Objects.requireNonNull(wormhole.getCoreSettings().get("debug")).getValue(), false);
	}

	private void loadCoreSettings() {
		CoreSettings coreSettings = getCoreSettings();
		Configuration configuration = getConfig();
		configuration.setIfNull("debug", false);
		coreSettings.load(new SimpleProperty<>("debug", configuration.getBoolean("debug", false)));
		configuration.setIfNull("request-time", "30s");
		coreSettings.load(new SimpleProperty<>("request-time", TimeFormatter.formatTime(configuration.getString("request-time", "30s"))));
		configuration.setIfNull("teleport-time", "0s");
		coreSettings.load(new SimpleProperty<>("teleport-time", TimeFormatter.formatTime(configuration.getString("teleport-time", "0s"))));
		configuration.setIfNull("cancel-teleport-on-move", false);
		coreSettings.load(new SimpleProperty<>("teleport-move", configuration.getBoolean("cancel-teleport-on-move", false)));

		try {
			configuration.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public UserDatabase getUserDatabase() {
		return userDatabase;
	}

	public Configuration getCommandConfig() {
		return commandConfig;
	}
}
