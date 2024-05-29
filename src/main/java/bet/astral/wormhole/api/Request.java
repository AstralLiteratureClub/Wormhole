package bet.astral.wormhole.api;

import bet.astral.wormhole.Wormhole;

import java.util.UUID;

/**
 * @author Antritus
 * @since 1.1-SNAPSHOT
 */
public class Request {
	protected final UUID whoAsked;
	protected final UUID requested;
	protected final long end;
	protected boolean denied;
	protected boolean accepted;

	public Request(Wormhole wormhole, UUID whoAsked, UUID requested) {
		this.whoAsked = whoAsked;
		this.requested = requested;
		this.end = System.currentTimeMillis()+(long) wormhole.getCoreSettings().get("request-time").getValue();
	}

	public void setDenied(boolean denied) {
		this.denied = denied;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public UUID getWhoAsked() {
		return whoAsked;
	}

	public UUID getRequested() {
		return requested;
	}

	public long getEnd() {
		return end;
	}

	public boolean isDenied() {
		return denied;
	}

	public boolean isAccepted() {
		return accepted;
	}
}
