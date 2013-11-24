package org.darkstorm.darkbot.minecraftbot.world.entity;

import org.darkstorm.darkbot.minecraftbot.util.IntHashMap;
import org.darkstorm.darkbot.minecraftbot.world.block.TileEntity;

public final class EntityList {
	private static final IntHashMap<Class<? extends LivingEntity>> livingIds;
	private static final IntHashMap<Class<? extends Entity>> objectIds;
	private static final IntHashMap<Class<? extends TileEntity>> tileIds;

	static {
		livingIds = new IntHashMap<Class<? extends LivingEntity>>();
		livingIds.put(50, CreeperEntity.class);
		livingIds.put(51, SkeletonEntity.class);
		livingIds.put(52, SpiderEntity.class);
		livingIds.put(54, ZombieEntity.class);
		livingIds.put(57, PigZombieEntity.class);
		livingIds.put(58, EndermanEntity.class);
		livingIds.put(59, CaveSpiderEntity.class);
		livingIds.put(61, BlazeEntity.class);
		livingIds.put(90, PigEntity.class);
		livingIds.put(91, SheepEntity.class);
		livingIds.put(92, CowEntity.class);
		livingIds.put(93, ChickenEntity.class);
		livingIds.put(94, SquidEntity.class);
		livingIds.put(95, WolfEntity.class);
		livingIds.put(96, MushroomCowEntity.class);
		livingIds.put(98, OcelotEntity.class);

		objectIds = new IntHashMap<Class<? extends Entity>>();
		objectIds.put(1, BoatEntity.class);
		objectIds.put(2, ItemEntity.class);
		objectIds.put(10, MinecartEntity.class);
		objectIds.put(11, StorageMinecartEntity.class);
		objectIds.put(12, PoweredMinecartEntity.class);
		objectIds.put(60, ArrowEntity.class);
		objectIds.put(61, SnowballEntity.class);
		objectIds.put(63, FireballEntity.class);
		objectIds.put(66, WitherSkullEntity.class);
		objectIds.put(71, ItemFrameEntity.class);
		objectIds.put(90, FishingBobEntity.class);

		tileIds = new IntHashMap<Class<? extends TileEntity>>();
	}

	private EntityList() {
	}

	public static Class<? extends LivingEntity> getLivingEntityClass(int id) {
		return livingIds.get(id);
	}

	public static Class<? extends Entity> getObjectEntityClass(int id) {
		return objectIds.get(id);
	}

	public static Class<? extends TileEntity> getTileEntityClass(int id) {
		return tileIds.get(id);
	}
}
