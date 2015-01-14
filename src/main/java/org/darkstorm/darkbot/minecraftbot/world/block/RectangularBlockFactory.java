package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.world.*;

public class RectangularBlockFactory implements BlockFactory {
	private static class BasicBlock extends AbstractBlock {
		private final BoundingBox boundingBox;
		
		public BasicBlock(World world, Chunk chunk, BlockLocation location, int id, int metadata, BoundingBox boundingBox) {
			super(world, chunk, location, id, metadata);
			
			this.boundingBox = boundingBox;
		}
		
		@Override
		public BoundingBox[] getBoundingBoxes() {
			return new BoundingBox[] { boundingBox };
		}
	}
	
	private static final BoundingBox DEFAULT = BoundingBox.getBoundingBox(0, 0, 0, 1, 1, 1);
	
	private final BlockType type;
	private final BoundingBox boundingBox;
	
	protected RectangularBlockFactory(BlockType type, BoundingBox boundingBox) {
		this.type = type;
		this.boundingBox = boundingBox;
	}
	
	@Override
	public Block createBlock(World world, Chunk chunk, BlockLocation location, int metadata) {
		BoundingBox bounds = boundingBox.offset(location.getX(), location.getY(), location.getZ());
		return new BasicBlock(world, chunk, location, type.getId(), metadata, bounds);
	}
	
	@Override
	public BlockType getType() {
		return type;
	}
	
	public static RectangularBlockFactory getInstance(BlockType type) {
		return new RectangularBlockFactory(type, DEFAULT);
	}
	
	public static RectangularBlockFactory getInstance(BlockType type, BoundingBox boundingBox) {
		if(boundingBox.equals(DEFAULT))
			return getInstance(type );
		return new RectangularBlockFactory(type, boundingBox);
	}
}
