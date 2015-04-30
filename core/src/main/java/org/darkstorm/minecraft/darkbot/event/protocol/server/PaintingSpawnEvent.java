package org.darkstorm.minecraft.darkbot.event.protocol.server;

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
		private final int direction;

		public PaintingSpawnLocation(double x, double y, double z, int direction) {
			super(x, y, z);

			this.direction = direction;
		}

		public int getDirection() {
			return direction;
		}
	}
}
