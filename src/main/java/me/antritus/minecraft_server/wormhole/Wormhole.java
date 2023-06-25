package me.antritus.minecraft_server.wormhole;

import me.antritus.astrolapi.minecraft.ColorUtils;
import me.antritus.astrolapi.minecraft.Configuration;
import me.antritus.minecraft_server.wormhole.commands.CMDTpa;
import me.antritus.minecraft_server.wormhole.commands.CoreCommand;
import me.antritus.minecraft_server.wormhole.manager.TeleportManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public class Wormhole extends JavaPlugin {
	public static long TPA_TIME = 30000;
	public static Configuration configuration = new Configuration(null, new File(""));
	public static TeleportManager manager;

	@Override
	public void onEnable() {
		manager = new TeleportManager(this);
		manager.onEnable();
		CoreCommand.registerCommand(this, "tpa", new CMDTpa());
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
	public static void sendMessage(Player player, String  who, String key){
		String msg = configuration.getString(key, key);
		msg = msg.replace("%who%", who);
		player.sendMessage(ColorUtils.translateComp(msg));
	}
}
