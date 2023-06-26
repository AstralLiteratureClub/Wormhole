package me.antritus.minecraft_server.wormhole.commands;


import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.astrolminiapi.ColorUtils;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
		}	}
	private static List<CoreCommand> commands = new ArrayList<>();
	protected Integer cooldown = null;
	protected Component cooldownMessage = null;
	protected String cooldownBypassPermission = null;
	protected boolean playerOnly = true;

	protected CoreCommand(@NotNull String name) {
		super(name);
	}

	protected CoreCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
		super(name, description, usageMessage, aliases);
	}
	protected void sendMessage(CommandSender sender, String msg){
		MiniMessage message = MiniMessage.miniMessage();
		Component component = message.deserialize(msg).decoration(TextDecoration.ITALIC, false);
		sender.sendMessage(component);
	}

	private void  defaults(){
		MiniMessage miniMessage = MiniMessage.miniMessage();
		String string = "<red>You are in cooldown for this command!";
		cooldownMessage = miniMessage.deserialize(string);
	}

	private String consoleMsg = Wormhole.configuration.getString("settings.player-only-command", "settings.player-only-command");
	public void playerOnly() {
		Bukkit.getConsoleSender().sendMessage(ColorUtils.translateComp(consoleMsg));
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
