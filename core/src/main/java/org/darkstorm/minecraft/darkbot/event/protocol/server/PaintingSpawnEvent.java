package org.darkstorm.minecraft.darkbot.event.protocol.server;

import com.github.steveice10.mc.protocol.data.game.entity.type.object.HangingDirection;

public class PaintingSpawnEvent extends EntitySpawnEvent {
	private final String title;

	public PaintingSpawnEvent(int entityId, PaintingSpawnLocation location, String title) {
		super(entityId, location);

		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public PaintingSpawnLocation getLocation() {
		return (PaintingSpawnLocation) super.getLocation();
	}

	public static class PaintingSpawnLocation extends SpawnLocation {
		private final HangingDirection direction;

		public PaintingSpawnLocation(double x, double y, double z, HangingDirection direction) {
			super(x, y, z);

			this.direction = direction;
		}

		public HangingDirection getDirection() {
			return direction;
		}
	}
}
