package org.darkstorm.minecraft.darkbot.world.block;

import static org.darkstorm.minecraft.darkbot.world.block.BlockType.Flag.*;
import static org.darkstorm.minecraft.darkbot.world.item.ToolType.*;

import org.darkstorm.minecraft.darkbot.world.*;
import org.darkstorm.minecraft.darkbot.world.item.ToolType;

public enum BlockType {
	UNKNOWN               (block(-1)),

	AIR                   (block(0).flags(0)),
	STONE                 (block(1).toolType(PICKAXE)),
	GRASS                 (block(2).toolType(SHOVEL)),
	DIRT                  (block(3).toolType(SHOVEL)),
	COBBLESTONE           (block(4).toolType(PICKAXE)),
	WOOD                  (block(5).toolType(AXE)),
	SAPLING               (block(6).flags(PLACEABLE)),
	BEDROCK               (block(7).flags(SOLID | PLACEABLE | INDESTRUCTABLE)),
	WATER                 (block(8).flags(INDESTRUCTABLE)),
	STATIONARY_WATER      (block(9).flags(INDESTRUCTABLE)),
	LAVA                  (block(10).flags(INDESTRUCTABLE)),
	STATIONARY_LAVA       (block(11).flags(INDESTRUCTABLE)),
	SAND                  (block(12).toolType(SHOVEL)),
	GRAVEL                (block(13).toolType(SHOVEL)),
	GOLD_ORE              (block(14).toolType(PICKAXE)),
	IRON_ORE              (block(15).toolType(PICKAXE)),
	COAL_ORE              (block(16).toolType(PICKAXE)),
	LOG                   (block(17).toolType(AXE)),
	LEAVES                (block(18).toolType(SHEARS)),
	SPONGE                (block(19)),
	GLASS                 (block(20).toolType(PICKAXE)),
	LAPIS_ORE             (block(21).toolType(PICKAXE)),
	LAPIS_BLOCK           (block(22).toolType(PICKAXE)),
	DISPENSER             (block(23).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	SANDSTONE             (block(24).toolType(PICKAXE)),
	NOTE_BLOCK            (block(25).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	BED_BLOCK             (block(26).flags(SOLID | INTERACTABLE | PLACEABLE)),
	POWERED_RAIL          (block(27).flags(PLACEABLE).toolType(PICKAXE)),
	DETECTOR_RAIL         (block(28).flags(PLACEABLE).toolType(PICKAXE)),
	PISTON_STICKY_BASE    (block(29).toolType(PICKAXE)),
	WEB                   (block(30).flags(PLACEABLE).toolType(SWORD)),
	LONG_GRASS            (block(31).flags(PLACEABLE)),
	DEAD_BUSH             (block(32).flags(PLACEABLE)),
	PISTON_BASE           (block(33).toolType(PICKAXE)),
	PISTON_EXTENSION      (block(34).toolType(PICKAXE)),
	WOOL                  (block(35).toolType(SWORD)),
	PISTON_MOVING_PIECE   (block(36)),
	YELLOW_FLOWER         (block(37).flags(PLACEABLE)),
	RED_ROSE              (block(38).flags(PLACEABLE)),
	BROWN_MUSHROOM        (block(39).flags(PLACEABLE)),
	RED_MUSHROOM          (block(40).flags(PLACEABLE)),
	GOLD_BLOCK            (block(41).toolType(PICKAXE)),
	IRON_BLOCK            (block(42).toolType(PICKAXE)),
	DOUBLE_STEP           (block(43).toolType(PICKAXE)),
	STEP                  (block(44).toolType(PICKAXE).factory(new StepBlockFactoryProvider())) {
		@Override public BlockFactory<StepBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	BRICK                 (block(45).toolType(PICKAXE)),
	TNT                   (block(46)),
	BOOKSHELF             (block(47).toolType(AXE)),
	MOSSY_COBBLESTONE     (block(48).toolType(PICKAXE)),
	OBSIDIAN              (block(49).toolType(PICKAXE)),
	TORCH                 (block(50).flags(PLACEABLE)),
	FIRE                  (block(51).flags(INDESTRUCTABLE)),
	MOB_SPAWNER           (block(52).toolType(PICKAXE)),
	WOOD_STAIRS           (block(53).toolType(AXE).factory(new StairBlockFactoryProvider(StairBlock.Material.OAK_WOOD))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	CHEST                 (block(54).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(AXE)),
	REDSTONE_WIRE         (block(55).flags(PLACEABLE)),
	DIAMOND_ORE           (block(56).toolType(PICKAXE)),
	DIAMOND_BLOCK         (block(57).toolType(PICKAXE)),
	WORKBENCH             (block(58).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(AXE)),
	CROPS                 (block(59).flags(PLACEABLE)),
	SOIL                  (block(60).toolType(SHOVEL)),
	FURNACE               (block(61).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	BURNING_FURNACE       (block(62).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	SIGN_POST             (block(63).flags(PLACEABLE).maxStack(16).toolType(AXE)),
	WOODEN_DOOR           (block(64).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(AXE)),
	LADDER                (block(65).flags(PLACEABLE)),
	RAILS                 (block(66).flags(PLACEABLE).toolType(PICKAXE)),
	COBBLESTONE_STAIRS    (block(67).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.COBBLESTONE))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	WALL_SIGN             (block(68).flags(PLACEABLE).toolType(AXE)),
	LEVER                 (block(69).flags(INTERACTABLE | PLACEABLE)),
	STONE_PLATE           (block(70).flags(PLACEABLE).toolType(PICKAXE)),
	IRON_DOOR_BLOCK       (block(71).toolType(PICKAXE)),
	WOOD_PLATE            (block(72).flags(PLACEABLE).toolType(AXE)),
	REDSTONE_ORE          (block(73).toolType(PICKAXE)),
	GLOWING_REDSTONE_ORE  (block(74).toolType(PICKAXE)),
	REDSTONE_TORCH_OFF    (block(75).flags(PLACEABLE)),
	REDSTONE_TORCH_ON     (block(76).flags(PLACEABLE)),
	STONE_BUTTON          (block(77).flags(INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	SNOW                  (block(78).flags(PLACEABLE).toolType(SHOVEL)),
	ICE                   (block(79).toolType(PICKAXE)),
	SNOW_BLOCK            (block(80).toolType(SHOVEL)),
	CACTUS                (block(81)),
	CLAY                  (block(82).toolType(SHOVEL)),
	SUGAR_CANE_BLOCK      (block(83).flags(PLACEABLE)),
	JUKEBOX               (block(84).toolType(PICKAXE)),
	FENCE                 (block(85).toolType(AXE).factory(new FenceBlockFactoryProvider(0.25, FenceBlockFactoryProvider.WOOD))) {
		@Override public BlockFactory<FenceBlock> getBlockFactory() { return getBlockFactoryTyped(); }
	},
	PUMPKIN               (block(86).toolType(AXE)),
	NETHERRACK            (block(87).toolType(PICKAXE)),
	SOUL_SAND             (block(88).toolType(SHOVEL)),
	GLOWSTONE             (block(89).toolType(PICKAXE)),
	PORTAL                (block(90).flags(PLACEABLE | INDESTRUCTABLE)),
	JACK_O_LANTERN        (block(91).toolType(AXE)),
	CAKE_BLOCK            (block(92)),
	DIODE_BLOCK_OFF       (block(93).flags(INTERACTABLE | PLACEABLE)),
	DIODE_BLOCK_ON        (block(94).flags(INTERACTABLE | PLACEABLE)),
	LOCKED_CHEST          (block(95).toolType(AXE)),
	TRAP_DOOR             (block(96).toolType(AXE)),
	MONSTER_EGGS          (block(97)),
	SMOOTH_BRICK          (block(98).toolType(PICKAXE)),
	HUGE_MUSHROOM_1       (block(99).toolType(AXE)),
	HUGE_MUSHROOM_2       (block(100).toolType(AXE)),
	IRON_FENCE            (block(101).toolType(PICKAXE)),
	THIN_GLASS            (block(102).toolType(PICKAXE)),
	MELON_BLOCK           (block(103).toolType(AXE)),
	PUMPKIN_STEM          (block(104).flags(PLACEABLE)),
	MELON_STEM            (block(105).flags(PLACEABLE)),
	VINE                  (block(106).flags(PLACEABLE)),
	FENCE_GATE            (block(107).toolType(AXE).factory(new FenceGateBlockFactoryProvider())) {
		@Override public BlockFactory<FenceGateBlock> getBlockFactory() { return getBlockFactoryTyped(); }
	},
	BRICK_STAIRS          (block(108).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.BRICK))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	SMOOTH_STAIRS         (block(109).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.STONE_BRICK))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	MYCEL                 (block(110).toolType(SHOVEL)),
	WATER_LILY            (block(111)),
	NETHER_BRICK          (block(112).toolType(PICKAXE)),
	NETHER_FENCE          (block(113).toolType(PICKAXE).factory(new FenceBlockFactoryProvider(0.25, FenceBlockFactoryProvider.NETHERBRICK))) {
		@Override public BlockFactory<FenceBlock> getBlockFactory() { return getBlockFactoryTyped(); }
	},
	NETHER_BRICK_STAIRS   (block(114).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.NETHER_BRICK))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	NETHER_WARTS          (block(115).flags(PLACEABLE)),
	ENCHANTMENT_TABLE     (block(116).toolType(PICKAXE)),
	BREWING_STAND         (block(117).toolType(PICKAXE)),
	CAULDRON              (block(118).toolType(PICKAXE)),
	ENDER_PORTAL          (block(119).flags(INDESTRUCTABLE)),
	ENDER_PORTAL_FRAME    (block(120).flags(INDESTRUCTABLE | SOLID | PLACEABLE)),
	ENDER_STONE           (block(121).toolType(PICKAXE)),
	DRAGON_EGG            (block(122).flags(SOLID | INTERACTABLE)),
	REDSTONE_LAMP_OFF     (block(123).toolType(PICKAXE)),
	REDSTONE_LAMP_ON      (block(124).toolType(PICKAXE)),
	WOOD_DOUBLE_STEP      (block(125).toolType(AXE)),
	WOOD_STEP             (block(126).toolType(AXE).factory(new StepBlockFactoryProvider())) {
		@Override public BlockFactory<StepBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	COCOA                 (block(127)),
	SANDSTONE_STAIRS      (block(128).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.SANDSTONE))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	EMERALD_ORE           (block(129).toolType(PICKAXE)),
	ENDER_CHEST           (block(130).flags(INDESTRUCTABLE | INTERACTABLE | SOLID | PLACEABLE)),
	TRIPWIRE_HOOK         (block(131).flags(INTERACTABLE | PLACEABLE)),
	TRIPWIRE              (block(132).flags(PLACEABLE)),
	EMERALD_BLOCK         (block(133).toolType(PICKAXE)),
	SPRUCE_WOOD_STAIRS    (block(134).toolType(AXE).factory(new StairBlockFactoryProvider(StairBlock.Material.SPRUCE_WOOD))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	BIRCH_WOOD_STAIRS     (block(135).toolType(AXE).factory(new StairBlockFactoryProvider(StairBlock.Material.BIRCH_WOOD))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	JUNGLE_WOOD_STAIRS    (block(136).toolType(AXE).factory(new StairBlockFactoryProvider(StairBlock.Material.JUNGLE_WOOD))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	COMMAND               (block(137)),
	BEACON                (block(138)),
	COBBLE_WALL           (block(139).toolType(PICKAXE).factory(new FenceBlockFactoryProvider(0.5, FenceBlockFactoryProvider.COBBLESTONE))) {
		@Override public BlockFactory<FenceBlock> getBlockFactory() { return getBlockFactoryTyped(); }
	},
	FLOWER_POT            (block(140)),
	CARROT                (block(141).flags(PLACEABLE)),
	POTATO                (block(142).flags(PLACEABLE)),
	WOOD_BUTTON           (block(143).flags(INTERACTABLE | PLACEABLE).toolType(AXE)),
	SKULL                 (block(144)),
	ANVIL                 (block(145).toolType(PICKAXE)),
	TRAPPED_CHEST         (block(146).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(AXE)),
	GOLD_PRESSURE_PLATE   (block(147).flags(PLACEABLE).toolType(PICKAXE)),
	IRON_PRESSURE_PLATE   (block(148).flags(PLACEABLE).toolType(PICKAXE)),
	LIGHT_SENSOR          (block(151).toolType(PICKAXE)),
	REDSTONE_BLOCK        (block(152).toolType(PICKAXE)),
	NETHER_QUARTZ_ORE     (block(153).toolType(PICKAXE)),
	HOPPER                (block(154).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	QUARTZ_BLOCK          (block(155).toolType(PICKAXE)),
	QUARTZ_STAIRS         (block(156).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.QUARTZ))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	ACTIVATOR_RAIL        (block(157).flags(PLACEABLE).toolType(PICKAXE)),
	DROPPER               (block(158).flags(SOLID | INTERACTABLE | PLACEABLE).toolType(PICKAXE)),
	STAINED_CLAY          (block(159).toolType(PICKAXE)),
	CARPET                (block(171).flags(PLACEABLE).toolType(SWORD)),
	HARDENED_CLAY         (block(172).toolType(PICKAXE)),
	COAL_BLOCK            (block(173).toolType(PICKAXE)),
	PACKED_ICE            (block(174).toolType(PICKAXE)),
	FLOWER_BLOCK          (block(175).flags(PLACEABLE)),
	RED_SANDSTONE         (block(179).toolType(PICKAXE)),
	RED_SANDSTONE_STAIRS  (block(180).toolType(PICKAXE).factory(new StairBlockFactoryProvider(StairBlock.Material.RED_SANDSTONE))) {
		@Override public BlockFactory<StairBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	},
	RED_SANDSTONE_STEP    (block(182).toolType(PICKAXE).factory(new StepBlockFactoryProvider())) {
		@Override public BlockFactory<StepBlock> getBlockFactory() { return super.getBlockFactoryTyped(); }
	};
	
	private final int id, maxStack, flags;
	private final float frictionCoefficient;
	private final ToolType toolType;
	private final BlockFactory<?> blockFactory;
	
	private BlockType(Builder builder) {
		this.id = builder.id;
		this.flags = builder.flags;
		this.maxStack = builder.maxStack;
		this.frictionCoefficient = builder.friction;
		this.toolType = builder.toolType;
		this.blockFactory = builder.factory.provide(this);
		
		if(id >= 0 && id < 256)
			Registry.registry[id] = this;
	}

	public int getId() {
		return id;
	}

	public int getMaxStack() {
		return maxStack;
	}

	public boolean isSolid() {
		return (flags & SOLID) == SOLID;
	}
	
	public boolean isOpaque() {
		return (flags & OPAQUE) == OPAQUE;
	}

	public boolean isInteractable() {
		return (flags & INTERACTABLE) == INTERACTABLE;
	}

	public boolean isPlaceable() {
		return (flags & PLACEABLE) == PLACEABLE;
	}

	public boolean isIndestructable() {
		return (flags & INDESTRUCTABLE) == INDESTRUCTABLE;
	}
	
	public float getFrictionCoefficient() {
		return frictionCoefficient;
	}

	public ToolType getToolType() {
		return toolType;
	}
	
	public BlockFactory<? extends Block> getBlockFactory() {
		return blockFactory;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends Block> BlockFactory<T> getBlockFactoryTyped() {
		return (BlockFactory<T>) blockFactory;
	}

	public static BlockType getById(int id) {
		if(id < 0 || id >= 256)
			return UNKNOWN;
		BlockType type = Registry.registry[id];
		return type != null ? type : UNKNOWN;
	}

	protected static final class Flag {
		public static final int SOLID = 0x01, INTERACTABLE = 0x02, PLACEABLE = 0x04, INDESTRUCTABLE = 0x08, OPAQUE = 0x10;
	}
	
	protected static final class Registry {
		public static final BlockType[] registry = new BlockType[256];
	}
	
	private static Builder block(int id) {
		return new Builder(id);
	}
	
	protected static final class Builder {
		private final int id;
		private int maxStack = 64;
		private int flags = SOLID | OPAQUE | PLACEABLE;
		private float friction = 1.0F;
		private ToolType toolType = null;
		private BlockFactoryProvider<?> factory = BlockFactoryProvider.DEFAULT;
		
		public Builder(int id) {
			this.id = id;
		}
		
		public Builder maxStack(int maxStack) {
			this.maxStack = maxStack;
			return this;
		}
		
		public Builder flags(int flags) {
			this.flags = flags;
			if((flags & SOLID) != SOLID)
				factory = BlockFactoryProvider.TRANSPARENT;
			return this;
		}
		
		public Builder toolType(ToolType toolType) {
			this.toolType = toolType;
			return this;
		}
		
		public Builder friction(float friction) {
			this.friction = friction;
			return this;
		}
		
		public Builder factory(BlockFactoryProvider<?> factory) {
			this.factory = factory;
			return this;
		}
	}
	
	private static BlockFactoryProvider<Block> singleBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		final BoundingBox bounds = BoundingBox.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
		return new BlockFactoryProvider<Block>() {
			@Override public Class<Block> getBlockClass() { return Block.class; }
			@Override public BlockFactory<Block> provide(BlockType type) {
				return RectangularBlockFactory.getInstance(type, bounds);
			}
		};
	}
	
	protected static interface BlockFactoryProvider<T extends Block> {
		BlockFactoryProvider<Block> DEFAULT = new BlockFactoryProvider<Block>() {
			@Override public Class<Block> getBlockClass() { return Block.class; }
			@Override public BlockFactory<Block> provide(BlockType type) {
				return RectangularBlockFactory.getInstance(type);
			}
		};
		BlockFactoryProvider<Block> TRANSPARENT = new BlockFactoryProvider<Block>() {
			@Override public Class<Block> getBlockClass() { return Block.class; }
			@Override public BlockFactory<Block> provide(BlockType type) {
				return TransparentBlockFactory.getInstance(type);
			}
		};
		public Class<T> getBlockClass();
		public BlockFactory<T> provide(BlockType type);
	}
	
	protected static class FenceBlockFactoryProvider implements BlockFactoryProvider<FenceBlock> {
		private static class FenceBlockImpl extends AbstractBlock implements FenceBlock {
			private final int x, y, z;
			private final BoundingBox unconnected, connectedPX, connectedNX, connectedPZ, connectedNZ;
			
			private FenceBlockImpl(World world, Chunk chunk, BlockLocation location, int id, int metadata, double diameter) {
				super(world, chunk, location, id, metadata);
				
				x = location.getX();
				y = location.getY();
				z = location.getZ();
				
				double minX = (1 - diameter) / 2, minY = 0, minZ = (1 - diameter) / 2;
				double maxX = 1 - minX, maxY = 1.5, maxZ = 1 - minZ;
				
				minX += x;
				minY += y;
				minZ += z;
				maxX += x;
				maxY += y;
				maxZ += z;
				
				unconnected = BoundingBox.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
				connectedPX = BoundingBox.getBoundingBox(minX, minY, minZ, x + 1, maxY, maxZ);
				connectedNX = BoundingBox.getBoundingBox(x, minY, minZ, maxX, maxY, maxZ);
				connectedPZ = BoundingBox.getBoundingBox(minX, minY, minZ, maxX, maxY, z + 1);
				connectedNZ = BoundingBox.getBoundingBox(minX, minY, z, maxX, maxY, maxZ);
			}
			
			@Override
			public BoundingBox[] getBoundingBoxes() {
				boolean px = connectsTo(1, 0, 0);
				boolean nx = connectsTo(-1, 0, 0);
				boolean pz = connectsTo(0, 0, 1);
				boolean nz = connectsTo(0, 0, -1);
				
				int size = 1 + (px ? 1 : 0) + (nx ? 1 : 0) + (pz ? 1 : 0) + (nz ? 1 : 0), idx = 0;
				BoundingBox[] boxes = new BoundingBox[size];
				boxes[idx++] = unconnected;
				if(px) boxes[idx++] = connectedPX;
				if(nx) boxes[idx++] = connectedNX;
				if(pz) boxes[idx++] = connectedPZ;
				if(nz) boxes[idx++] = connectedNZ;
				return boxes;
			}
			
			public boolean isConnected(Direction direction) {
				switch(direction) {
				case NORTH:
				case SOUTH:
				case EAST:
				case WEST:
					return connectsTo(direction.getBlockOffsetX(), direction.getBlockOffsetY(), direction.getBlockOffsetZ());
				default:
					return false;
				}
			}
			
			private boolean connectsTo(int offX, int offY, int offZ) {
				Block block = getWorld().getBlockAt(x + offX, y + offY, z + offZ);
				if(block == null)
					return false;
				
				BlockType type = block.getType();
				if(type == UNKNOWN)
					return true;
				
				if(type == getType())
					return true;
				
				if(type == BlockType.MELON_BLOCK || type == BlockType.PUMPKIN)
					return false;
				
				if(block instanceof FenceGateBlock)
					return true;
				
				return type.isSolid() && type.isOpaque();
			}
		}
		
		public static final int WOOD = 0, COBBLESTONE = 1, NETHERBRICK = 2;
		
		private final double diameter;
		//private final int material;
		
		public FenceBlockFactoryProvider(double diameter, int material) {
			this.diameter = diameter;
			//this.material = material;
		}
		
		@Override
		public Class<FenceBlock> getBlockClass() {
			return FenceBlock.class;
		}
		
		@Override
		public BlockFactory<FenceBlock> provide(final BlockType type) {
			return new BlockFactory<FenceBlock>() {
				@Override public BlockType getType() { return type; }
				
				@Override
				public FenceBlock createBlock(World world, Chunk chunk, BlockLocation location, int metadata) {
					return new FenceBlockImpl(world, chunk, location, type.getId(), metadata, diameter);
				}
			};
		}
	}
	
	protected static class FenceGateBlockFactoryProvider implements BlockFactoryProvider<FenceGateBlock> {
		private static class FenceGateBlockImpl extends AbstractBlock implements FenceGateBlock {
			private static final BoundingBox BOUNDS_X = BoundingBox.getBoundingBox(0, 0, 0.375, 1, 1.5, 0.625);
			private static final BoundingBox BOUNDS_Z = BoundingBox.getBoundingBox(0.375, 0, 0, 0.625, 1.5, 1);
			
			private final Direction direction;
			private final BoundingBox bounds;
			private final boolean open;
			
			public FenceGateBlockImpl(World world, Chunk chunk, BlockLocation location, int id, int metadata) {
				super(world, chunk, location, id, metadata);
				
				switch(metadata & 0x3) {
				default:
				case 0: direction = Direction.NORTH; break;
				case 1: direction = Direction.EAST; break;
				case 2: direction = Direction.SOUTH; break;
				case 3: direction = Direction.WEST; break;
				}
				open = (metadata & 0x4) != 0;
				
				if(!open) {
					switch(direction) {
					case NORTH:
					case SOUTH:
						bounds = BOUNDS_X.offset(location);
						break;
					case EAST:
					case WEST:
						bounds = BOUNDS_Z.offset(location);
						break;
					default:
						bounds = null;
					}
				} else
					bounds = null;
			}
			
			@Override
			public Direction getDirection() {
				return direction;
			}
			
			@Override
			public boolean isOpen() {
				return open;
			}
			
			@Override
			public BoundingBox[] getBoundingBoxes() {
				return bounds != null ? new BoundingBox[] { bounds } : new BoundingBox[0];
			}
		}
		
		@Override
		public Class<FenceGateBlock> getBlockClass() {
			return FenceGateBlock.class;
		}
		
		@Override
		public BlockFactory<FenceGateBlock> provide(final BlockType type) {
			return new BlockFactory<FenceGateBlock>() {
				@Override public BlockType getType() { return type; }
				
				@Override
				public FenceGateBlock createBlock(World world, Chunk chunk, BlockLocation location, int metadata) {
					return new FenceGateBlockImpl(world, chunk, location, type.getId(), metadata);
				}
			};
		}
	}
	
	protected static class StairBlockFactoryProvider implements BlockFactoryProvider<StairBlock> {
		private static class StairBlockImpl extends AbstractBlock implements StairBlock {
			private final int x, y, z;
			private final BoundingBox bottom, topPXPZ, topPXNZ, topNXPZ, topNXNZ;
			
			private final Material material;
			private final Direction direction;
			private final boolean upsideDown;
			
			
			private StairBlockImpl(World world, Chunk chunk, BlockLocation location, int id, int metadata, Material material) {
				super(world, chunk, location, id, metadata);
				
				this.material = material;
				switch(metadata & 0x3) {
				default:
				case 0: direction = Direction.NORTH; break;
				case 1: direction = Direction.SOUTH; break;
				case 2: direction = Direction.EAST; break;
				case 3: direction = Direction.WEST; break;
				}
				upsideDown = (metadata & 0x4) != 0;
				
				x = location.getX();
				y = location.getY();
				z = location.getZ();
				
				double by = y, ty = y + 0.5;
				if(upsideDown) {
					by = y + 0.5;
					ty = y;
				}
				
				bottom =  BoundingBox.getBoundingBox(x,       by, z,       x + 1,   by + 0.5, z + 1);
				topPXPZ = BoundingBox.getBoundingBox(x,       ty, z,       x + 0.5, ty + 0.5, z + 0.5);
				topPXNZ = BoundingBox.getBoundingBox(x,       ty, z + 0.5, x + 0.5, ty + 0.5, z + 1);
				topNXPZ = BoundingBox.getBoundingBox(x + 0.5, ty, z,       x + 1,   ty + 0.5, z + 0.5);
				topNXNZ = BoundingBox.getBoundingBox(x + 0.5, ty, z + 0.5, x + 1,   ty + 0.5, z + 1);
			}
			
			@Override
			public BoundingBox[] getBoundingBoxes() {
				boolean pxpz = true, pxnz = true, nxnz = false, nxpz = false;
				
				boolean connectedLeft = checkStair(Direction.WEST, Direction.NORTH);
				boolean connectedRight = checkStair(Direction.EAST, Direction.NORTH);
				if(!connectedLeft && checkStair(Direction.NORTH, Direction.EAST))
					pxpz = false;
				else if(!connectedRight && checkStair(Direction.NORTH, Direction.WEST))
					pxnz = false;
				else if(!connectedLeft && checkStair(Direction.SOUTH, Direction.WEST))
					nxpz = true;
				else if(!connectedRight && checkStair(Direction.SOUTH, Direction.EAST))
					nxnz = true;
				
				int rotation = (4 - rotation(direction) + 2) % 4;
				for(; rotation > 0; rotation--) {
					boolean temp = pxpz;
					pxpz = nxpz;
					nxpz = nxnz;
					nxnz = pxnz;
					pxnz = temp;
				}
				
				int size = 1 + (pxpz ? 1 : 0) + (pxnz ? 1 : 0) + (nxpz ? 1 : 0) + (nxnz ? 1 : 0), idx = 0;
				BoundingBox[] boxes = new BoundingBox[size];
				boxes[idx++] = bottom;
				if(pxpz) boxes[idx++] = topPXPZ;
				if(pxnz) boxes[idx++] = topPXNZ;
				if(nxpz) boxes[idx++] = topNXPZ;
				if(nxnz) boxes[idx++] = topNXNZ;
				return boxes;
			}
			private boolean checkStair(Direction direction, Direction facing) {
				direction = rotate(this.direction, rotation(direction));
				facing = rotate(this.direction, rotation(facing));
				
				Block block = getWorld().getBlockAt(x + direction.getBlockOffsetX(), y + direction.getBlockOffsetY(), z + direction.getBlockOffsetZ());
				if(block == null || !(block instanceof StairBlock) || upsideDown != ((StairBlock) block).isUpsideDown())
					return false;
				return facing == ((StairBlock) block).getDirection();
			}
			
			private int rotation(Direction direction) {
				switch(direction) {
				default:
				case NORTH: return 0;
				case EAST: return 1;
				case SOUTH: return 2;
				case WEST: return 3;
				}
			}
			private Direction rotate(Direction direction, int rotation) {
				for(; rotation > 0; rotation--) {
					switch(direction) {
					default:
					case NORTH: direction = Direction.EAST; break;
					case EAST: direction = Direction.SOUTH; break;
					case SOUTH: direction = Direction.WEST; break;
					case WEST: direction = Direction.NORTH; break;
					}
				}
				return direction;
			}
			
			@Override public Material getMaterial() { return material; }
			@Override public Direction getDirection() { return direction; }
			@Override public boolean isUpsideDown() { return upsideDown; }
		}
		
		private final StairBlock.Material material;
		
		public StairBlockFactoryProvider(StairBlock.Material material) {
			this.material = material;
		}
		
		@Override
		public Class<StairBlock> getBlockClass() {
			return StairBlock.class;
		}
		
		@Override
		public BlockFactory<StairBlock> provide(final BlockType type) {
			return new BlockFactory<StairBlock>() {
				@Override public BlockType getType() { return type; }
				
				@Override
				public StairBlock createBlock(World world, Chunk chunk, BlockLocation location, int metadata) {
					return new StairBlockImpl(world, chunk, location, type.getId(), metadata, material);
				}
			};
		}
	}
	
	protected static class StepBlockFactoryProvider implements BlockFactoryProvider<StepBlock> {
		private static class StepBlockImpl extends AbstractBlock implements StepBlock {
			private static final BoundingBox BOUNDS_LOWER = BoundingBox.getBoundingBox(0, 0, 0, 1, 0.5, 1);
			private static final BoundingBox BOUNDS_UPPER = BoundingBox.getBoundingBox(0, 0.5, 0, 1, 1, 1);
			
			private final Material material;
			private final BoundingBox bounds;
			private final boolean upper;
			
			public StepBlockImpl(World world, Chunk chunk, BlockLocation location, int id, int metadata) {
				super(world, chunk, location, id, metadata);
				
				switch(metadata & 0x7) {
				default:
				case 0x0: material = Material.OAK_WOOD; break;
				case 0x1: material = Material.SPRUCE_WOOD; break;
				case 0x2: material = Material.BIRCH_WOOD; break;
				case 0x3: material = Material.JUNGLE_WOOD; break;
				case 0x4: material = Material.ACACIA_WOOD; break;
				case 0x5: material = Material.DARK_OAK; break;
				}
				upper = (metadata & 0x8) != 0;
				bounds = (upper ? BOUNDS_UPPER : BOUNDS_LOWER).offset(location);
			}
			
			@Override
			public Material getMaterial() {
				return material;
			}
			
			@Override
			public boolean isUpper() {
				return upper;
			}
			
			@Override
			public BoundingBox[] getBoundingBoxes() {
				return new BoundingBox[] { bounds };
			}
		}
		
		@Override
		public Class<StepBlock> getBlockClass() {
			return StepBlock.class;
		}
		
		@Override
		public BlockFactory<StepBlock> provide(final BlockType type) {
			return new BlockFactory<StepBlock>() {
				@Override public BlockType getType() { return type; }
				
				@Override
				public StepBlock createBlock(World world, Chunk chunk, BlockLocation location, int metadata) {
					return new StepBlockImpl(world, chunk, location, type.getId(), metadata);
				}
			};
		}
	}
}
