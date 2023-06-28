package me.antritus.minecraft_server.wormhole;

import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.Configuration;
import me.antritus.minecraft_server.wormhole.astrolminiapi.CoreCommand;
import me.antritus.minecraft_server.wormhole.commands.admin.CMDReload;
import me.antritus.minecraft_server.wormhole.commands.block.CMDBlock;
import me.antritus.minecraft_server.wormhole.commands.block.CMDToggle;
import me.antritus.minecraft_server.wormhole.commands.block.CMDUnblock;
import me.antritus.minecraft_server.wormhole.commands.request.CMDAccept;
import me.antritus.minecraft_server.wormhole.commands.request.CMDDeny;
import me.antritus.minecraft_server.wormhole.commands.request.CMDRequests;
import me.antritus.minecraft_server.wormhole.commands.request.CMDTpa;
import me.antritus.minecraft_server.wormhole.manager.TeleportManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class Wormhole extends JavaPlugin {
	private static Wormhole wormhole;
	public static boolean DEBUG = true;
	public static boolean CANCEL_TELEPORT_ON_MOVE = true;
	public static long REQUEST_TIME = 30000;
	public static long TELEPORT_TIME;
	public static Configuration configuration;
	public static TeleportManager manager;

	@Override
	public void onEnable() {
		wormhole = this;
		configuration = new Configuration(this, new File(this.getDataFolder(), "config.yml"));
		manager = new TeleportManager(this);
		manager.onEnable();
		getServer().getPluginManager().registerEvents(manager, this);
		CoreCommand.registerCommand(this, "tpa", new CMDTpa());
		CoreCommand.registerCommand(this, "tpaccept", new CMDAccept());
		CoreCommand.registerCommand(this, "tpdeny", new CMDDeny());
		CoreCommand.registerCommand(this, "tptoggle", new CMDToggle());
		CoreCommand.registerCommand(this, "tpblock", new CMDBlock());
		CoreCommand.registerCommand(this, "tpunblock", new CMDUnblock());
		CoreCommand.registerCommand(this, "tprequests", new CMDRequests());
		CoreCommand.registerCommand(this, "tpreload", new CMDReload());
	}
	@Override
	public void onDisable() {
		manager.onDisable();
	}

	public static void sendMessage(Player player, Player who, String key){
		String msg = configuration.getString(key, key);
		msg = msg.replace("%who%", ColorUtils.deseriazize(who.name()));
		player.sendMessage(ColorUtils.translateComp(msg));
	}
	public static void sendMessage(Player player, Player who, String key, String... replacements){
		String msg = configuration.getString(key, key);
		msg = msg.replace("%who%", ColorUtils.deseriazize(who.name()));
		for (String replacement : replacements) {
			String[] split = replacement.split("=");
			msg = msg.replace(split[0], split[1]);
		}
		player.sendMessage(ColorUtils.translateComp(msg));
	}
	public static void sendMessage(Player player, String  who, String key){
		String msg = configuration.getString(key, key);
		msg = msg.replace("%who%", who);
		player.sendMessage(ColorUtils.translateComp(msg));
	}

	public static void sendKey(Player player, String key){
		String msg = configuration.getString(key, key);
		player.sendMessage(ColorUtils.translateComp(msg));
	}
	public static void sendMessage(Player player, String msg){
		Component component = ColorUtils.translateComp(msg);
		player.sendMessage(component);
	}

	public static void reload(){
		if (manager != null){
			manager.onDisable();
		}
		configuration = new Configuration(wormhole, new File(wormhole.getDataFolder(), "config.yml"));
		manager = new TeleportManager(wormhole);
		manager.onEnable();

		DEBUG = configuration.getBoolean("debug", false);
		REQUEST_TIME = configuration.getLong("settings.tpatime", 30000);
		CANCEL_TELEPORT_ON_MOVE = configuration.getBoolean("settings.cancel-teleport-on-movement", false);
		TELEPORT_TIME = configuration.getLong("settings.teleport-time", 3000);
	}
}
