package me.antritus.minecraft_server.wormhole.antsfactions;

import me.antritus.minecraft_server.wormhole.astrolminiapi.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public interface ISettings {
	@NotNull
	String name();
	@Nullable
	Property<?, ?> get(String name);
	Configuration getConfiguration();
	void save();
	default void reload(){
		try {
			getConfiguration().load();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
