package me.antritus.minecraft_server.wormhole.antsfactions;

import me.antritus.minecraft_server.wormhole.astrolminiapi.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class FactionsPlugin extends JavaPlugin {
	private final CoreSettings coreSettings;
	private final MessageManager messageManager;
	private final Configuration config;
	private final CoreDatabase coreDatabase;

	protected FactionsPlugin() {
		config = new Configuration(this, "config.yml");
		coreSettings = new CoreSettings(this);
		coreDatabase = new CoreDatabase(this);
		messageManager = new MessageManager(this);
	}

	public void onEnable(){
		try {
			config.load();
		} catch (IOException | InvalidConfigurationException e) {
			throw new RuntimeException(e);
		}
		config.setIfNull("database.type", "mysql");
		config.setIfNull("database.password", "_123_!_Bad_Password_!_123_");
		config.setIfNull("database.user", "root");
		config.setIfNull("database.url", "https://database.example.com/db/wormhole");
		config.setIfNull("version", "version");
		coreSettings.load(new SimpleProperty<>("database-type", getConfig().getString("database.type", "database.type")));
		coreSettings.load(new SimpleProperty<>("database-password", getConfig().getString("database.password", "database.password")));
		coreSettings.load(new SimpleProperty<>("database-user", getConfig().getString("database.user", "database.user")));
		coreSettings.load(new SimpleProperty<>("database-url", getConfig().getString("database.url", "database.url")));
		coreSettings.load(new SimpleProperty<>("version", getConfig().getString("version", "version")));
		SimpleProperty<?> version = (SimpleProperty<?>) getCoreSettings().getKnownNonNull("version");
		if (version.getValue().getClass().isInstance("version")){
			String ver = (String) version.getValue();
			//noinspection UnstableApiUsage
			if (!ver.equalsIgnoreCase(getPluginMeta().getVersion())){
				//noinspection UnstableApiUsage
				updateConfig(ver.equalsIgnoreCase("version") ? null : ver, getPluginMeta().getVersion());
				//noinspection DataFlowIssue,UnstableApiUsage
				coreSettings.get("version").setValueObj(getPluginMeta().getVersion());
				// noinspection UnstableApiUsage
				config.set("version", getPluginMeta().getVersion());
			}
		}

		try {
			config.save();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void enableDatabase(){
		coreDatabase.connect();
	}

	public abstract void updateConfig(@Nullable String oldVersion, String newVersion);

	public abstract void enable();
	public abstract void disable();

	public CoreSettings getCoreSettings() {
		return coreSettings;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	@NotNull
	@Override
	public Configuration getConfig() {
		return config;
	}

	public CoreDatabase getCoreDatabase() {
		return coreDatabase;
	}
}
