package me.antritus.minecraft_server.wormhole.antsfactions;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CoreDatabase {
	private final FactionsPlugin main;

	private Connection connection;

	@SuppressWarnings("unchecked")
	public CoreDatabase(FactionsPlugin main) {
		this.main = main;

	}
	public void connect(){
		try {
			if (connection != null && !connection.isClosed()){
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		try {
			// todo other db types.
			this.connection = DriverManager.getConnection(((SimpleProperty<String>) main.getCoreSettings().getKnownNonNull("database-url")).getValue(), ((SimpleProperty<String>) main.getCoreSettings().getKnownNonNull("database-user")).getValue(), ((SimpleProperty<String>) main.getCoreSettings().getKnownNonNull("database-password")).getValue());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			connection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}