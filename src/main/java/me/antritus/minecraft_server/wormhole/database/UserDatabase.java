package me.antritus.minecraft_server.wormhole.database;

import me.antritus.minecraft_server.wormhole.Wormhole;
import me.antritus.minecraft_server.wormhole.antsfactions.Property;
import me.antritus.minecraft_server.wormhole.antsfactions.ShushIDE;
import me.antritus.minecraft_server.wormhole.events.database.UserDeleteEvent;
import me.antritus.minecraft_server.wormhole.events.database.UserLoadEvent;
import me.antritus.minecraft_server.wormhole.events.database.UserSaveEvent;
import me.antritus.minecraft_server.wormhole.manager.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.sql.*;
import java.util.*;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class UserDatabase implements Listener {
	@ShushIDE
	private static final String CREATE_QUERY_USER = "CREATE TABLE IF NOT EXISTS wormhole_users ("
			+ "uniqueId VARCHAR(36) NOT NULL, "
			+ "enabled BOOLEAN, "
			+ "blocked JSON, "
			+ "PRIMARY KEY (uniqueId)"
			+ ");";
	@ShushIDE
	private final String INSERT_QUERY_USER = "INSERT INTO wormhole_users (uniqueId, enabled, blocked) VALUES (?, ?, ?)";
	@ShushIDE
	private final String UPDATE_QUERY_USER = "UPDATE wormhole_users SET enabled = ?, blocked = ? WHERE uniqueId = ?";
	@ShushIDE
	private final String SELECT_QUERY_USER = "SELECT * FROM wormhole_users WHERE uniqueId = ?";
	private final Map<UUID, User> cached = new HashMap<>();
	private final Wormhole main;

	public UserDatabase(Wormhole main) {
		this.main = main;
		createStatement();
	}

	@Nullable
	public User get(UUID uuid) {
		if (cached.get(uuid) == null) {
			load(uuid);
		}
		return cached.get(uuid);
	}

	@Nullable
	public User get(Player player) {
		if (cached.get(player.getUniqueId()) == null) {
			load(player.getUniqueId());
		}
		return get(player.getUniqueId());
	}

	@NotNull
	public User getKnownNonNull(UUID uniqueId) {
		return Objects.requireNonNull(get(uniqueId));
	}

	@NotNull
	public User getKnownNonNull(Player player) {
		return getKnownNonNull(player.getUniqueId());
	}

	private void createStatement() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = main.getCoreDatabase().getConnection();
				try {
					Statement statement = connection.createStatement();
					statement.executeUpdate(CREATE_QUERY_USER); // Use executeUpdate() for DDL queries
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskAsynchronously(main);
	}

	public void checkAndDropTable() {
		boolean shouldDropTable = main.getConfig().getBoolean("delete-all-data", false);
		if (shouldDropTable) {
			new BukkitRunnable() {
				@Override
				public void run() {
					main.getConfig().set("delete-all-data", false);
					try (Statement statement = main.getCoreDatabase().getConnection().createStatement()) {
						statement.execute("DROP TABLE IF EXISTS wormhole_users");
						main.getLogger().info("Table wormhole_users has been dropped.");
					} catch (SQLException e) {
						throw new RuntimeException("Failed to drop table wormhole_users.", e);
					}
				}
			}.runTaskAsynchronously(main);
		}
	}
	public void delete(User user) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = main.getCoreDatabase().getConnection();
				String statementQuery = "DELETE FROM wormhole_users WHERE uniqueId = ?";
				try (PreparedStatement statement = connection.prepareStatement(statementQuery)) {
					statement.setString(1, user.getUniqueId().toString());
					statement.executeUpdate();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				UserDeleteEvent event = new UserDeleteEvent(main, user);
				event.callAsync(main);
			}
		}.runTaskAsynchronously(main);
	}

	private String toJSON(User user){
		StringBuilder builder = new StringBuilder("[");
		for (String blockedUser : user.getBlockedUsers()) {
			if (blockedUser == null){
				continue;
			}
			if (builder.length()>1){
				builder.append(",");
			}
			builder.append("\"").append(blockedUser).append("\"");
		}
		return builder.append("]").toString();
	}

	private List<String> fromJSON(String string){
		List<String> list = new ArrayList<>();
		try {
			JSONArray array = (JSONArray) new JSONParser().parse(string);
			for (Object o : array) {
				if (o instanceof String i){
					list.add(i);
				}
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return list;
	}

	public void save(@NotNull User user) {
		new BukkitRunnable() {
			@Override
			public void run() {
				UserSaveEvent event = new UserSaveEvent(main, user);
				event.callAsync(main);
				Connection connection = main.getCoreDatabase().getConnection();
				Property<String, ?> property = user.get("isNew");
				if (property == null || !((Boolean) property.getValue())) {
					try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY_USER)) {
						statement.setBoolean(1, user.isAcceptingRequests);
						statement.setString(2, toJSON(user));
						statement.setString(3, user.getUniqueId().toString());
						statement.executeUpdate();
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				} else {
					try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY_USER)) {
						statement.setString(1, user.getUniqueId().toString());
						statement.setBoolean(2, user.isAcceptingRequests);
						statement.setString(3, toJSON(user));
						statement.executeUpdate();
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}.runTaskAsynchronously(main);
	}

	public void load(UUID uuid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = main.getCoreDatabase().getConnection();
				try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY_USER)) {
					statement.setString(1, uuid.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						if (resultSet.next()) {
							User user = new User(main, uuid);
							UserLoadEvent event = new UserLoadEvent(main, user);
							event.callAsync(main);
							user.isAcceptingRequests = resultSet.getBoolean("enabled");
							user.blocked().addAll(fromJSON(resultSet.getString("blocked")));

							cached.put(user.getUniqueId(), user);
						} else {
							User user = new User(main, uuid);
							user.setting("isNew", true);
							user.isAcceptingRequests = true;
							cached.put(user.getUniqueId(), user);
							UserLoadEvent event = new UserLoadEvent(main, user);
							event.callAsync(main);
							save(user);

						}
					}
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskAsynchronously(main);
	}

	public void join(UUID uuid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = main.getCoreDatabase().getConnection();
				try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY_USER)) {
					statement.setString(1, uuid.toString());
					try (ResultSet resultSet = statement.executeQuery()) {
						if (resultSet.next()) {
							User user = new User(main, uuid);
							UserLoadEvent event = new UserLoadEvent(main, user);
							event.callAsync(main);
							user.isAcceptingRequests = resultSet.getBoolean("enabled");
							user.blocked().addAll(fromJSON(resultSet.getString("blocked")));
							user.lastOnline = -1;
							user.online = true;
							save(user);
							cached.put(user.getUniqueId(), user);
						} else {
							User user = new User(main, uuid);

							UserLoadEvent event = new UserLoadEvent(main, user);
							event.callAsync(main);

							user.setting("isNew", true);
							user.isAcceptingRequests = true;
							user.lastOnline = -1;
							user.online = true;
							cached.put(user.getUniqueId(), user);
							save(user);
						}
					}
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}.runTaskAsynchronously(main);
	}

	public void unload(Player player) {
		cached.remove(player.getUniqueId());
	}
}