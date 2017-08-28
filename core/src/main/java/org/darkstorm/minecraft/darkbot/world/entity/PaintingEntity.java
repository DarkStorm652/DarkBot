package org.darkstorm.minecraft.darkbot.world.entity;

import com.github.steveice10.mc.protocol.data.game.entity.type.object.HangingDirection;
import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.protocol.client.EntityHitEvent;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.ArtType;

public class PaintingEntity extends Entity {
	private final ArtType artType;
	private HangingDirection direction;

	public PaintingEntity(World world, int id, ArtType artType) {
		super(world, id);
		this.artType = artType;
	}

	public ArtType getArtType() {
		return artType;
	}

	public int getBlockX() {
		return (int) getX();
	}

	public int getBlockY() {
		return (int) getY();
	}

	public int getBlockZ() {
		return (int) getZ();
	}

	public HangingDirection getDirection() {
		return direction;
	}

	@Override
	public void setX(double x) {
		super.setX((int) x);
	}

	@Override
	public void setY(double y) {
		super.setY((int) y);
	}

	@Override
	public void setZ(double z) {
		super.setZ((int) z);
	}

	public void setDirection(HangingDirection direction) {
		this.direction = direction;
	}

	public void breakPainting() {
		MinecraftBot bot = world.getBot();
		bot.getEventBus().fire(new EntityHitEvent(this));
	}
}
