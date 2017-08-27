package org.darkstorm.minecraft.darkbot.ai;

import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;

public class XRayMiningMatrices {
    public static BlockLocation[] PlayerBlocks = new BlockLocation[] {
            // head level
            new BlockLocation(0, 1, 0),
            // legs level
            new BlockLocation(0, 0, 0)
    };

    public static BlockLocation[] ShellBlocksDown = new BlockLocation[] {
            // head level
            new BlockLocation(0, 1, 2),
            new BlockLocation(1, 1, 1),
            new BlockLocation(-1, 1, 1),
            // legs level
            new BlockLocation(0, 0, 2),
            new BlockLocation(1, 0, 1),
            new BlockLocation(-1, 0, 1),
            // ground level
            new BlockLocation(0, -1, 2),
            new BlockLocation(1, -1, 1),
            new BlockLocation(-1, -1, 1),
            new BlockLocation(0, -2, 1)
    };

    public static BlockLocation[] DigBlocksDown = new BlockLocation[] {
            // head level
            new BlockLocation(0, 1, 1),
            // legs level
            new BlockLocation(0, 0, 1),
            // ground level
            new BlockLocation(0, -1, 1)
    };

    public static BlockLocation StepLocationDown = new BlockLocation(0, -1, 1);

    public static BlockLocation[] ShellBlocksUp = new BlockLocation[] {
            // overhead level
            new BlockLocation(0, 3, 0),
            new BlockLocation(1, 2, 0),
            new BlockLocation(-1, 2, 0),

            new BlockLocation(0, 3, 1),
            new BlockLocation(1, 2, 1),
            new BlockLocation(-1, 2, 1),
            new BlockLocation(0, 2, 2),
            // head level
            new BlockLocation(1, 1, 1),
            new BlockLocation(-1, 1, 1),
            new BlockLocation(0, 1, 2),
            //feet level
            new BlockLocation(0, 0, 1),
    };

    public static BlockLocation[] DigBlocksUp = new BlockLocation[] {
            // overhead level
            new BlockLocation(0, 2, 0),
            new BlockLocation(0, 2, 1),
            // head level
            new BlockLocation(0, 1, 1),
    };

    public static BlockLocation StepLocationUp = new BlockLocation(0, 1, 1);

    public static BlockLocation[] ShellBlocksStraight = new BlockLocation[] {
            // head level
            new BlockLocation(0, 2, 1),
            new BlockLocation(1, 1, 1),
            new BlockLocation(-1, 1, 1),
            new BlockLocation(1, 1, 0),
            new BlockLocation(-1, 1, 0),
            // legs level
            new BlockLocation(1, 0, 1),
            new BlockLocation(-1, 0, 1),
            // ground level
            new BlockLocation(0, -1, 1),
    };

    public static BlockLocation[] DigBlocksStraight = new BlockLocation[] {
            // head level
            new BlockLocation(0, 1, 1),
            // legs level
            new BlockLocation(0, 0, 1),
    };

    public static BlockLocation StepLocationStraight = new BlockLocation(0, 0, 1);
}
