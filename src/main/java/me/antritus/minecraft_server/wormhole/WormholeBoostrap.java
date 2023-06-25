package me.antritus.minecraft_server.wormhole;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import me.antritus.minecraft_server.wormhole.astrolminiapi.NotNull;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("UnstableApiUsage")
public class WormholeBoostrap implements PluginBootstrap {
	@Override
	public void bootstrap(@NotNull BootstrapContext bootstrapContext) {
	}

	@Override
	public @NotNull JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
		return new Wormhole();
	}
}
