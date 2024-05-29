package bet.astral.wormhole;

import bet.astral.wormhole.antsfactions.*;
import bet.astral.wormhole.api.TeleportManager;
import bet.astral.wormhole.astrolminiapi.Configuration;
import bet.astral.wormhole.astrolminiapi.CoreCommand;
import bet.astral.wormhole.commands.admin.CMDReload;
import bet.astral.wormhole.commands.block.CMDBlock;
import bet.astral.wormhole.commands.block.CMDToggle;
import bet.astral.wormhole.commands.block.CMDUnblock;
import bet.astral.wormhole.commands.request.to.CMDAccept;
import bet.astral.wormhole.commands.request.to.CMDCancel;
import bet.astral.wormhole.commands.request.to.CMDDeny;
import bet.astral.wormhole.commands.request.CMDRequests;
import bet.astral.wormhole.commands.request.to.CMDTpa;
import bet.astral.wormhole.database.UserDatabase;
import bet.astral.wormhole.events.WormholeReloadEvent;
import bet.astral.wormhole.events.request.TpRequestSendEvent;
import bet.astral.wormhole.manager.UserManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class Wormhole extends FactionsPlugin {
	private static Wormhole wormhole;
	public static boolean DEBUG = true;
	private MessageManager messageManager;
	private UserDatabase userDatabase;
	private UserManager userManager;
	private Configuration commandConfig;

	private TeleportManager teleportManager;

	@Override
	public void enable() {
		wormhole = this;
		enableDatabase();
		loadCoreSettings();
		messageManager = new MessageManager(this);
		commandConfig = new Configuration(this, "commands.yml");
		try {
			commandConfig.load();
		} catch (IOException | InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}

		userDatabase = new UserDatabase(this);
		userDatabase.checkAndDropTable();
		teleportManager = new TeleportManager(this);
		teleportManager.run();

		userManager = new UserManager(this);
		userManager.onEnable();
		TpRequestSendEvent event;
		getServer().getPluginManager().registerEvents(userManager, this);
		CoreCommand.registerCommand(this, "tpa", new CMDTpa(this));
		CoreCommand.registerCommand(this, "tpcancel", new CMDCancel(this));
		CoreCommand.registerCommand(this, "tpaccept", new CMDAccept(this));
		CoreCommand.registerCommand(this, "tpdeny", new CMDDeny(this));
		CoreCommand.registerCommand(this, "tptoggle", new CMDToggle(this));
		CoreCommand.registerCommand(this, "tpblock", new CMDBlock(this));
		CoreCommand.registerCommand(this, "tpunblock", new CMDUnblock(this));
		CoreCommand.registerCommand(this, "tprequests", new CMDRequests(this));
		CoreCommand.registerCommand(this, "tpreload", new CMDReload(this));
		getLogger().info("Wormhole has started.");
	}

	// this is used when the core (factions plugin) does not disable connection between the database.
	@Override
	public void startDisable() {
		userManager.onDisable();
		teleportManager.end();
	}

	@Override
	public void disable() {
		getLogger().info("Wormhole has disabled.");
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
		WormholeReloadEvent event = new WormholeReloadEvent(wormhole);
		event.callEvent();

		wormhole.reloadConfig();

		wormhole.userDatabase = new UserDatabase(wormhole);

		if (wormhole.userManager != null){
			wormhole.userManager.onDisable();
		} else {
			wormhole.teleportManager = new TeleportManager(wormhole);
		}
		wormhole.messageManager = new MessageManager(wormhole);
		wormhole.userManager = new UserManager(wormhole);
		wormhole.userManager.onEnable();

		DEBUG = (boolean) Objects.requireNonNullElse(Objects.requireNonNull(wormhole.getCoreSettings().get("debug")).getValue(), false);
		wormhole.loadCoreSettings();
 	}

	private void loadCoreSettings() {
		CoreSettings coreSettings = getCoreSettings();
		Configuration configuration = getConfig();
		configuration.setIfNull("debug", false);
		coreSettings.load(new SimpleProperty<>("debug", configuration.getBoolean("debug", false)));
		configuration.setIfNull("request-time", "30s");
		coreSettings.load(new SimpleProperty<>("request-time", TimeFormatter.formatTime(configuration.getString("request-time", "30s")).toMillis()));
		configuration.setIfNull("teleport-time", "0s");
		coreSettings.load(new SimpleProperty<>("teleport-time", TimeFormatter.formatTime(configuration.getString("teleport-time", "0s")).toMillis()));
		configuration.setIfNull("cancel-on-movement", false);
		coreSettings.load(new SimpleProperty<>("cancel-on-movement", configuration.getBoolean("cancel-on-movement", false)));
		configuration.setIfNull("max-movement-distance", 0.5D);
		coreSettings.load(new SimpleProperty<>("max-movement-distance", configuration.getDouble("max-movement-distance", 0.5D)));

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

	public TeleportManager getTeleportManager() {
		return teleportManager;
	}
	public UserManager getUserManager(){
		return userManager;
	}
}
