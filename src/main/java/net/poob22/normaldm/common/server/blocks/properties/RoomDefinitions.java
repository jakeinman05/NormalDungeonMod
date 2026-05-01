package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class RoomDefinitions {
    public static final List<RoomDefinition> ROOM_TYPES = new ArrayList<RoomDefinition>();

    public static final RoomDefinition SMALL = register(
            new RoomDefinition(
                    "small",
                    new BlockPos(-5, 1, -5),
                    new BlockPos(5, 7, 5),
                    RoomType.SMALL
            )
    );

    public static final RoomDefinition SMALL_HALLWAY = register(
            new RoomDefinition(
                "small_hallway",
                    new BlockPos(-3, 1, -6),
                    new BlockPos(3, 7, 6),
                    RoomType.SMALL_HALLWAY
            )
    );

    public static final RoomDefinition MEDIUM = register(
            new RoomDefinition(
                    "medium",
                    new BlockPos(-8, 1, -8),
                    new BlockPos(8, 7, 8),
                    RoomType.MEDIUM
            )
    );

    public static final RoomDefinition LARGE = register(
            new RoomDefinition(
                    "large",
                    new BlockPos(-10, 1, -10),
                    new BlockPos(10, 8, 10),
                    RoomType.LARGE
            )
    );


    private static RoomDefinition register(RoomDefinition def) {
        ROOM_TYPES.add(def);
        return def;
    }
}
