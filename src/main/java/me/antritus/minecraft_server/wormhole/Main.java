package me.antritus.minecraft_server.wormhole;

import me.antritus.minecraft_server.astrolminiapi.api.chat.ColorUtils;
import me.antritus.minecraft_server.astrolminiapi.configuration.yaml.Configuration;
import me.antritus.minecraft_server.wormhole.manager.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class Main extends JavaPlugin {
	public static HashMap<String, Object> messages = new HashMap<>();
	public static long TPA_TIME = 30000;
	public static Configuration configuration = new Configuration(null, new File(""));
	public static TeleportManager manager;

	@Override
	public void onEnable() {
		manager = new TeleportManager(this);
		manager.onEnable();
	}
	@Override
	public void onDisable() {
		manager.onDisable();
	}

	public static void sendMessage(Player player, Player who, String key){
		String msg = (String) messages.get(key);
		msg = msg.replace("%who%", ColorUtils.deseriazize(who.name()));
		player.sendMessage(ColorUtils.translateComp(msg));
	}
	public static void sendMessage(Player player, String  who, String key){
		String msg = (String) messages.get(key);
		msg = msg.replace("%who%", who);
		player.sendMessage(ColorUtils.translateComp(msg));
	}
}
