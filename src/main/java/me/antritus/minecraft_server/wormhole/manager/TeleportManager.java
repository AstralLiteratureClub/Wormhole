package me.antritus.minecraft_server.wormhole.manager;

import me.antritus.minecraft_server.wormhole.Main;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class TeleportManager {
	private final Main main;
	private BukkitTask task;
	protected TeleportManager(Main main) {
		this.main = main;
	}
	public void onEnable(){
		task = new BukkitRunnable() {
			@Override
			public void run() {

			}
		}.runTaskTimerAsynchronously(main, 20, 10);
	}
	public void onDisable() {
		if (task != null){
			task.cancel();
		}
	}
}
