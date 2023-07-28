package me.antritus.minecraft_server.wormhole.antsfactions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IUser {
	@Nullable
	Property<String, ?> get(@NotNull String key);
	@NotNull
	Map<String, SimpleProperty<?>> get();
	void setting(@NotNull String key, @Nullable Object value);
}
