package me.antritus.minecraft_server.wormhole.astrolminiapi;


import me.antritus.minecraft_server.wormhole.Wormhole;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * @since 1.0.0-snapshot
 * @author antritus
 */
public abstract class CoreCommand extends BukkitCommand {
	public static void registerCommand(JavaPlugin plugin, String label, CoreCommand command) {
		try {
			Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			commandMapField.setAccessible(true);
			CommandMap commandMap = (CommandMap) commandMapField.get(Bukkit.getServer());
			commandMap.register(label, plugin.getName(), command);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	protected final Wormhole wormhole;

	protected CoreCommand(Wormhole wormhole, @NotNull String name) {
		super(name);
		this.wormhole = wormhole;
	}

	protected String build(int pos, String[] args){
		StringBuilder builder = new StringBuilder();
		for (int i = pos; i < args.length; i++){
			if (builder.length() > 0)
				builder.append(" ");
			builder.append(args[i]);
		}
		return builder.toString();
	}
}
