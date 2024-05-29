package bet.astral.wormhole.api;

import bet.astral.wormhole.Wormhole;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class Teleport {
	private final Wormhole wormhole;
	protected final Player who;
	protected final Player to;
	protected final Location whoLocation;
	private long end;
	public long lastTick;

	public Teleport(Wormhole wormhole, Player who, Player to) {
		this.who = who;
		this.to = to;
		this.wormhole = wormhole;
		this.end = System.currentTimeMillis()+((long) wormhole.getCoreSettings().getKnownNonNull("teleport-time").getValue());
		whoLocation = who.getLocation().clone();
	}

	public long getEnd(){
		return end;
	}
	public Player getWho() {
		return who;
	}

	public Player getTo() {
		return to;
	}

	public Location getWhoLocation() {
		return whoLocation;
	}

	public Wormhole getWormhole() {
		return wormhole;
	}

}
