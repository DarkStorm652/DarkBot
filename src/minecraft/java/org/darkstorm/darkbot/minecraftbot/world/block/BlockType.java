package org.darkstorm.darkbot.minecraftbot.world.block;

public enum BlockType {
	UNKNOWN(-1),

	AIR(0, false),
	STONE(1),
	GRASS(2),
	DIRT(3),
	COBBLESTONE(4),
	WOOD(5),
	SAPLING(6, false),
	BEDROCK(7),
	WATER(8, false),
	STATIONARY_WATER(9, false),
	LAVA(10, false),
	STATIONARY_LAVA(11, false),
	SAND(12),
	GRAVEL(13),
	GOLD_ORE(14),
	IRON_ORE(15),
	COAL_ORE(16),
	LOG(17),
	LEAVES(18),
	SPONGE(19),
	GLASS(20),
	LAPIS_ORE(21),
	LAPIS_BLOCK(22),
	DISPENSER(23),
	SANDSTONE(24),
	NOTE_BLOCK(25),
	BED_BLOCK(26),
	POWERED_RAIL(27, false),
	DETECTOR_RAIL(28, false),
	PISTON_STICKY_BASE(29),
	WEB(30, false),
	LONG_GRASS(31, false),
	DEAD_BUSH(32, false),
	PISTON_BASE(33),
	PISTON_EXTENSION(34),
	WOOL(35),
	PISTON_MOVING_PIECE(36),
	YELLOW_FLOWER(37, false),
	RED_ROSE(38, false),
	BROWN_MUSHROOM(39, false),
	RED_MUSHROOM(40, false),
	GOLD_BLOCK(41),
	IRON_BLOCK(42),
	DOUBLE_STEP(43),
	STEP(44),
	BRICK(45),
	TNT(46),
	BOOKSHELF(47),
	MOSSY_COBBLESTONE(48),
	OBSIDIAN(49),
	TORCH(50, false),
	FIRE(51, false),
	MOB_SPAWNER(52),
	WOOD_STAIRS(53),
	CHEST(54),
	REDSTONE_WIRE(55, false),
	DIAMOND_ORE(56),
	DIAMOND_BLOCK(57),
	WORKBENCH(58),
	CROPS(59, false),
	SOIL(60),
	FURNACE(61),
	BURNING_FURNACE(62),
	SIGN_POST(63, 64, false),
	WOODEN_DOOR(64),
	LADDER(65, false),
	RAILS(66, false),
	COBBLESTONE_STAIRS(67),
	WALL_SIGN(68, 64, false),
	LEVER(69, false),
	STONE_PLATE(70, false),
	IRON_DOOR_BLOCK(71),
	WOOD_PLATE(72, false),
	REDSTONE_ORE(73),
	GLOWING_REDSTONE_ORE(74),
	REDSTONE_TORCH_OFF(75, false),
	REDSTONE_TORCH_ON(76, false),
	STONE_BUTTON(77, false),
	SNOW(78, false),
	ICE(79),
	SNOW_BLOCK(80),
	CACTUS(81),
	CLAY(82),
	SUGAR_CANE_BLOCK(83, false),
	JUKEBOX(84),
	FENCE(85),
	PUMPKIN(86),
	NETHERRACK(87),
	SOUL_SAND(88),
	GLOWSTONE(89),
	PORTAL(90, false),
	JACK_O_LANTERN(91),
	CAKE_BLOCK(92, 64),
	DIODE_BLOCK_OFF(93, false),
	DIODE_BLOCK_ON(94, false),
	LOCKED_CHEST(95),
	TRAP_DOOR(96),
	MONSTER_EGGS(97),
	SMOOTH_BRICK(98),
	HUGE_MUSHROOM_1(99),
	HUGE_MUSHROOM_2(100),
	IRON_FENCE(101),
	THIN_GLASS(102),
	MELON_BLOCK(103),
	PUMPKIN_STEM(104, false),
	MELON_STEM(105, false),
	VINE(106, false),
	FENCE_GATE(107),
	BRICK_STAIRS(108),
	SMOOTH_STAIRS(109),
	MYCEL(110),
	WATER_LILY(111),
	NETHER_BRICK(112),
	NETHER_FENCE(113),
	NETHER_BRICK_STAIRS(114),
	NETHER_WARTS(115, false),
	ENCHANTMENT_TABLE(116),
	BREWING_STAND(117),
	CAULDRON(118),
	ENDER_PORTAL(119, false),
	ENDER_PORTAL_FRAME(120),
	ENDER_STONE(121),
	DRAGON_EGG(122),
	REDSTONE_LAMP_OFF(123),
	REDSTONE_LAMP_ON(124),
	WOOD_DOUBLE_STEP(125),
	WOOD_STEP(126),
	COCOA(127),
	SANDSTONE_STAIRS(128),
	EMERALD_ORE(129),
	ENDER_CHEST(130),
	TRIPWIRE_HOOK(131, false),
	TRIPWIRE(132, false),
	EMERALD_BLOCK(133),
	SPRUCE_WOOD_STAIRS(134),
	BIRCH_WOOD_STAIRS(135),
	JUNGLE_WOOD_STAIRS(136),
	COMMAND(137),
	BEACON(138),
	COBBLE_WALL(139),
	FLOWER_POT(140),
	CARROT(141),
	POTATO(142),
	WOOD_BUTTON(143, false),
	SKULL(144),
	ANVIL(145);

	private final int id, maxStack;
	private final boolean solid;

	private BlockType(int id) {
		this(id, 64);
	}

	private BlockType(int id, boolean solid) {
		this(id, 64, solid);
	}

	private BlockType(int id, int maxStack) {
		this(id, maxStack, true);
	}

	private BlockType(int id, int maxStack, boolean solid) {
		this.id = id;
		this.maxStack = maxStack;
		this.solid = solid;
	}

	public int getId() {
		return id;
	}

	public int getMaxStack() {
		return maxStack;
	}

	public boolean isSolid() {
		return solid;
	}

	public static BlockType getById(int id) {
		for(BlockType type : values())
			if(type.getId() == id)
				return type;
		return UNKNOWN;
	}
}
