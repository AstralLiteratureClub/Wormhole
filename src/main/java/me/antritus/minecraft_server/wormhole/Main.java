package me.antritus.minecraft_server.wormhole;

import me.antritus.minecraft_server.astrolminiapi.configuration.yaml.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {
	public static long TPA_TIME = 30000;
	public static Configuration configuration = new Configuration(null, new File(""));
}
