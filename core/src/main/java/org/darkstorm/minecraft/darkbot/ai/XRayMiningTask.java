package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.event.EventListener;
import org.darkstorm.minecraft.darkbot.world.BasicWorld;
import org.darkstorm.minecraft.darkbot.world.World;
import org.darkstorm.minecraft.darkbot.world.WorldLocation;
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
    private int digState = 0; // digging stairs is always a 3-state process

    private World world;
    private MainPlayerEntity player;

    private int offsetX, offsetY, offsetZ;
    private BlockLocation playerBlock, finalTarget;
    private List<BlockLocation> permBlockBlacklist = new ArrayList<>();
    private List<BlockLocation> tempBlockBlacklist = new ArrayList<>();

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
        BlockLocation closestDiamond = FindClosestDiamond(diamonds);
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
    }

    private void UpdateVariables() {
        world = bot.getWorld();
        player = bot.getPlayer();
        // feet, above standing block
        playerBlock = new BlockLocation((int) player.getLocation().getX(), (int) player.getLocation().getY(), (int) player.getLocation().getZ());
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

    private BlockLocation FindClosestDiamond(List<BlockLocation> diamonds) {
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
            start();
        } else {
            int playerY = (int) player.getLocation().getY();
            if(playerY > finalTarget.getY())
                DoSequence(XRayMiningMatrices.ShellBlocksDown, XRayMiningMatrices.DigBlocksDown, XRayMiningMatrices.StepLocationDown);
            else if(playerY < finalTarget.getY())
                DoSequence(XRayMiningMatrices.ShellBlocksUp, XRayMiningMatrices.DigBlocksUp, XRayMiningMatrices.StepLocationUp);
            else
                DoSequence(XRayMiningMatrices.ShellBlocksStraight, XRayMiningMatrices.DigBlocksStraight, XRayMiningMatrices.StepLocationStraight);
        }
    }

    private void DoSequence(BlockLocation[] shellBlocks, BlockLocation[] digBlocks, BlockLocation stepLocation) {
        BlockLocation[] playerBlocksFixed = new BlockLocation[XRayMiningMatrices.PlayerBlocks.length];
        for (int i = 0; i < XRayMiningMatrices.PlayerBlocks.length; i++) {
            playerBlocksFixed[i] = FromUniformRelativeLocation(XRayMiningMatrices.PlayerBlocks[i]);
        }

        for(BlockLocation playerBlock : playerBlocksFixed) {
            if(!isEmpty(world.getBlockIdAt(playerBlock))) {
                //TODO: Better handle sand/gravel on player
                breakBlock(playerBlock);
                return;
            }
        }

        BlockLocation[] shellBlocksFixed = new BlockLocation[shellBlocks.length];
        for (int i = 0; i < shellBlocks.length; i++) {
            shellBlocksFixed[i] = FromUniformRelativeLocation(shellBlocks[i]);
        }

        boolean actionTaken = false;
        for(BlockLocation shellBlock : shellBlocksFixed) {
            if(isDangerous(world.getBlockIdAt(shellBlock))) {
                if (!placeBlockAt(shellBlock)) {
                    tempBlockBlacklist.add(shellBlock);
                    continue;
                }
                actionTaken = true;
                break;
            }
        }

        if(!actionTaken) { // nothing to shell, start digging
            BlockLocation[] digBlocksFixed = new BlockLocation[digBlocks.length];
            for (int i = 0; i < digBlocks.length; i++) {
                digBlocksFixed[i] = FromUniformRelativeLocation(digBlocks[i]);
            }

            for(BlockLocation digBlock : digBlocksFixed) {
                int digBlockId = world.getBlockIdAt(digBlock);
                if(!isEmpty(digBlockId)) {
                    if(BlockType.getById(digBlockId).isIndestructable() || !breakBlock(digBlock)) {
                        permBlockBlacklist.add(digBlock);
                        permBlockBlacklist.add(finalTarget);
                       return;
                    }
                    actionTaken = true;
                    break;
                }
            }

            if(!actionTaken) {
                BlockLocation stepLocationFixed = FromUniformRelativeLocation(stepLocation);
                player.face(stepLocationFixed);
                setActivity(new WalkActivity(bot, stepLocationFixed));
            }
        }
    }

    @Override
    public synchronized boolean isActive() {
        return running;
    }

    private boolean breakBlock(BlockLocation location) {
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

        if (player.breakBlock(location))
            return true;
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
        if(!type.isSolid())
            return true;
        return false;
    }

    private boolean placeBlockAt(BlockLocation location) {
        //TODO: Anti-lag
        /*if (lastPlacement != null && lastPlacement.equals(location)) {
            lastPlacement = null;
            return false;
        }*/
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
            //lastPlacement = location;
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
