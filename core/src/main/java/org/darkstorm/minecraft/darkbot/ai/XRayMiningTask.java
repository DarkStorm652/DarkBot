package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.block.*;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.world.item.ItemStack;
import org.darkstorm.minecraft.darkbot.world.item.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class XRayMiningTask extends AbstractTask implements EventListener {
    private EatTask eatTask;
    private boolean running = false;

    private int ticksWait;

    private World world;
    private MainPlayerEntity player;

    private int offsetX, offsetY, offsetZ;
    private BlockLocation playerBlock, finalTarget, lastPlaceBlock, lastBreakBlock;
    private List<BlockLocation> permBlockBlacklist = new ArrayList<>();
    private List<BlockLocation> tempBlockBlacklist = new ArrayList<>();
    private int collectedDiamonds;

    /* UNIFORM COORDINATES
    will be automatically transformed according to orientation

    +X = right
    -X = left
    -Z = backward
    +Z = forward
    +Y = up
    -Y = down
    */

    private BlockLocation FromUniformRelativeLocation(BlockLocation location) {
        if(offsetZ == 1) // Z is forward
            return playerBlock.offset(location);

        if(offsetZ == -1) // Z is backward
            return playerBlock.offset(-location.getX(), location.getY(), -location.getZ());

        if(offsetX == 1) // X is forward
            return playerBlock.offset(location.getZ(), location.getY(), -location.getX());

        if(offsetX == -1) // X is backward
            return playerBlock.offset(-location.getZ(), location.getY(), location.getX());

        return null;
    }


    public XRayMiningTask(final MinecraftBot bot) {
        super(bot);
        bot.getEventBus().register(this);
    }

    @Override
    public synchronized boolean isPreconditionMet() {
        return running;
    }

    @Override
    public synchronized boolean start(String... options) {
        TaskManager taskManager = bot.getTaskManager();
        eatTask = taskManager.getTaskFor(EatTask.class);

        UpdateVariables();

        List<BlockLocation> diamonds = FindDiamonds();
        BlockLocation closestDiamond = GetClosestDiamond(diamonds);
        if (closestDiamond == null) {
            stop();
            System.out.println("No diamonds in vicinity!");
            return false;
        }

        finalTarget = closestDiamond;
        UpdateDirectionOffsets();
        running = true;
        return true;
    }

    @Override
    public synchronized void stop() {
        running = false;
        finalTarget = null;
        world = null;
        player = null;
        playerBlock = null;
        tempBlockBlacklist.clear();
        lastPlaceBlock = null;
        lastBreakBlock = null;
    }

    private void UpdateVariables() {
        world = bot.getWorld();
        player = bot.getPlayer();
        playerBlock = player.getBlockLocation();
    }

    private List<BlockLocation> FindDiamonds() {
        int chunkRenderDistance = 16;
        List<BlockLocation> diamonds = new ArrayList<>();

        Chunk playerChunk = world.getChunkAt(playerBlock.getX() / 16,playerBlock.getY() / 16,playerBlock.getZ() / 16);
        ChunkLocation playerChunkLocation = playerChunk.getLocation();

        int chunkX = playerChunkLocation.getX() - chunkRenderDistance;

        for (; chunkX < playerChunkLocation.getX() + chunkRenderDistance; chunkX++) {
            int chunkY = playerChunkLocation.getY() - chunkRenderDistance;
            if (chunkY < 0)
                chunkY = 0; // can't go below bedrock
            for (; chunkY < playerChunkLocation.getY() + chunkRenderDistance; chunkY++) {
                int chunkZ = playerChunkLocation.getZ() - chunkRenderDistance;
                for (; chunkZ < playerChunkLocation.getZ() + chunkRenderDistance; chunkZ++) {
                    Chunk testChunk = world.getChunkAt(chunkX, chunkY, chunkZ);
                    if (testChunk == null)
                        continue;

                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < +16; z++) {
                                int testId = testChunk.getBlockIdAt(x, y, z);
                                if (testId == -1)
                                    continue;
                                if (testId == 56)
                                    diamonds.add(new BlockLocation(x + testChunk.getBlockBaseLocation().getX(),
                                            y + testChunk.getBlockBaseLocation().getY(),
                                            z + testChunk.getBlockBaseLocation().getZ()));
                            }
                        }
                    }
                }
            }
        }

        return diamonds;
    }

    private BlockLocation GetClosestDiamond(List<BlockLocation> diamonds) {
        double smallestDistance = -1;
        BlockLocation closestDiamond = null;

        for (BlockLocation diamond : diamonds) {
            if(permBlockBlacklist.contains(diamond))
                continue;
            double distance = playerBlock.getDistanceTo(diamond);
            if (distance < smallestDistance || smallestDistance == -1) {
                smallestDistance = distance;
                closestDiamond = diamond;
            }
        }
        return closestDiamond;
    }

    private void UpdateDirectionOffsets() {
        offsetX = finalTarget.getX() > playerBlock.getX() ? 1 : -1;
        offsetZ = finalTarget.getZ() > playerBlock.getZ() ? 1 : -1;

        BlockLocation firstStep = playerBlock.offset(offsetX, 0, 0);
        BlockLocation firstStep2 = playerBlock.offset(0, 0, offsetZ);

        if (firstStep2.getDistanceTo(finalTarget) < firstStep.getDistanceTo(finalTarget))
            offsetX = 0;
        else
            offsetZ = 0;
    }

    @Override
    public synchronized void run() {
        if (eatTask.isActive())
            return;
        if (ticksWait > 0) {
            ticksWait--;
            return;
        }

        if(collectedDiamonds > 10) {
            collectedDiamonds = 0;
            permBlockBlacklist.clear();
        }

        UpdateVariables();
        UpdateDirectionOffsets();
        tempBlockBlacklist.clear();

        if(permBlockBlacklist.contains(finalTarget)) {
            stop();
            start();
        }

        if ((world.getBlockIdAt(finalTarget) < 1)) {
            stop();
            System.out.println("Mined target! Restarting.");
            collectedDiamonds++;
            start();
        } else {
            int playerY = (int) player.getLocation().getY();
            if(playerY > finalTarget.getY())
                DoSequence(XRayMiningMatrices.RequiredBlocksDown, XRayMiningMatrices.DigBlocksDown, XRayMiningMatrices.StepLocationDown);
            else if(playerY < finalTarget.getY())
                DoSequence(XRayMiningMatrices.RequiredBlocksUp, XRayMiningMatrices.DigBlocksUp, XRayMiningMatrices.StepLocationUp);
            else
                DoSequence(XRayMiningMatrices.RequiredBlocksStraight, XRayMiningMatrices.DigBlocksStraight, XRayMiningMatrices.StepLocationStraight);
        }
    }

    // check if player blocks are filled (e.g. sand/gravel) to prevent suffocation
    private boolean CheckPlayerBlocks() {
        BlockLocation[] playerBlocksFixed = new BlockLocation[XRayMiningMatrices.PlayerBlocks.length];
        for (int i = 0; i < XRayMiningMatrices.PlayerBlocks.length; i++) {
            playerBlocksFixed[i] = FromUniformRelativeLocation(XRayMiningMatrices.PlayerBlocks[i]);
        }

        for(BlockLocation playerBlock : playerBlocksFixed) {
            if(!isEmpty(world.getBlockIdAt(playerBlock))) {
                //TODO: Better handling
                breakBlock(playerBlock);
                return true;
            }
        }
        return false;
    }

    private void DoSequence(BlockLocation[] requiredBlocks, BlockLocation[] digBlocks, BlockLocation stepLocation) {
        if (CheckPlayerBlocks())
            return;

        BlockLocation[] requiredBlocksFixed = new BlockLocation[requiredBlocks.length];
        for (int i = 0; i < requiredBlocks.length; i++) {
            requiredBlocksFixed[i] = FromUniformRelativeLocation(requiredBlocks[i]);
        }

        for (BlockLocation requiredBlock : requiredBlocksFixed) {
            if (isEmpty(world.getBlockIdAt(requiredBlock))) {
                if (!placeBlockAt(requiredBlock)) {
                    continue;
                }
                return;
            }
        }

        // nothing to place, start digging
        BlockLocation[] digBlocksFixed = new BlockLocation[digBlocks.length];
        for (int i = 0; i < digBlocks.length; i++) {
            digBlocksFixed[i] = FromUniformRelativeLocation(digBlocks[i]);
        }

        for (BlockLocation digBlock : digBlocksFixed) {

            for (BlockLocation adjacentBlock : digBlock.getAdjacentBlocks()) {
                if (isDangerous(world.getBlockIdAt(adjacentBlock))) {
                    if (!placeBlockAt(adjacentBlock)) {
                        permBlockBlacklist.add(digBlock); //TODO: Currently unused
                        permBlockBlacklist.add(finalTarget);
                        return;
                    }
                    return;
                }
            }
            int digBlockId = world.getBlockIdAt(digBlock);
            if (!isEmpty(digBlockId)) {
                if (BlockType.getById(digBlockId).isIndestructable() || !breakBlock(digBlock)) {
                    permBlockBlacklist.add(digBlock); //TODO: Currently unused
                    permBlockBlacklist.add(finalTarget);
                    return;
                }
                return;
            }
        }

        BlockLocation stepLocationFixed = FromUniformRelativeLocation(stepLocation);
        player.face(stepLocationFixed);
        setActivity(new WalkActivity(bot, stepLocationFixed));
    }

    @Override
    public synchronized boolean isActive() {
        return running;
    }

    private boolean breakBlock(BlockLocation location) {
        if (lastBreakBlock != null && lastBreakBlock.equals(location)) {
            lastBreakBlock = null;
            return false;
        }
        int x = location.getX(), y = location.getY(), z = location.getZ();
        MainPlayerEntity player = bot.getPlayer();
        World world = bot.getWorld();
        if (player == null)
            return false;
        int face = getBreakBlockFaceAt(location);
        if (face == -1)
            return false;
        player.face(x, y, z);
        int idAbove = world.getBlockIdAt(x, y + 1, z);
        if (idAbove == 12 || idAbove == 13) { // wait for sand/gravel to fall
            ticksWait = 10;
        }
        if (player.breakBlock(location)) {
            lastBreakBlock = location;
            return true;
        }
        return false;
    }

    private int getBreakBlockFaceAt(BlockLocation location) {
        int x = location.getX(), y = location.getY(), z = location.getZ();
        World world = bot.getWorld();
        if (isEmpty(world.getBlockIdAt(x - 1, y, z))) {
            return 4;
        } else if (isEmpty(world.getBlockIdAt(x, y, z + 1))) {
            return 3;
        } else if (isEmpty(world.getBlockIdAt(x, y, z - 1))) {
            return 2;
        } else if (isEmpty(world.getBlockIdAt(x, y + 1, z))) {
            return 1;
        } else if (isEmpty(world.getBlockIdAt(x, y - 1, z))) {
            return 0;
        } else if (isEmpty(world.getBlockIdAt(x + 1, y, z))) {
            return 5;
        } else
            return -1;
    }

    private boolean isEmpty(int id) {
        BlockType type = BlockType.getById(id);
        return !type.isSolid();
    }

    private boolean isDangerous(int id) {
        BlockType type = BlockType.getById(id);
        if(type == BlockType.LAVA || type == BlockType.STATIONARY_LAVA ||
                type == BlockType.WATER || type == BlockType.STATIONARY_WATER)
            return true;
        return false;
    }

    private boolean placeBlockAt(BlockLocation location) {
        if (lastPlaceBlock != null && lastPlaceBlock.equals(location)) {
            lastPlaceBlock = null;
            return false;
        }
        MainPlayerEntity player = bot.getPlayer();
        if (player == null)
            return false;
        PlayerInventory inventory = player.getInventory();
        int slot = -1;
        for (int i = 0; i < 36; i++) {
            ItemStack item = inventory.getItemAt(i);
            if (item == null)
                continue;
            int id = item.getId();
            if (id == 1 || id == 3 || id == 4) {
                slot = i;
                break;
            }
        }
        if (slot == -1 || !player.switchHeldItems(slot) || inventory.hasActionsQueued())
            return false;
        if (player.placeBlock(location)) {
            lastPlaceBlock = location;
            ticksWait = 5;
            return true;
        }
        return false;
    }

    @Override
    public TaskPriority getPriority() {
        return TaskPriority.NORMAL;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }

    @Override
    public boolean ignoresExclusive() {
        return false;
    }

    @Override
    public String getName() {
        return "XRayMine";
    }

    @Override
    public String getOptionDescription() {
        return "";
    }
}
