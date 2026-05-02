package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.poob22.normaldm.NormalDungeonMod;

import java.util.*;

public class RoomDefinitions {
    public static final List<RoomDefinition> ROOM_TYPES = new ArrayList<RoomDefinition>();

    public static final RoomDefinition SMALL = register(
            new RoomDefinition(
                    "small",
                    RoomType.SMALL,
                    new RoomVolume(new BlockPos(-5, 1, -5), new BlockPos(6, 7, 6))
            )
    );

    public static final RoomDefinition SMALL_HALLWAY_NS = register(
            new RoomDefinition(
                "small_hallway_ns",
                    RoomType.SMALL_HALLWAY,
                    new RoomVolume(new BlockPos(-3, 1, -6), new BlockPos(4, 7, 7))
            )
    );

    public static final RoomDefinition SMALL_HALLWAY_EW = register(
            new RoomDefinition(
                    "small_hallway_ew",
                    RoomType.SMALL_HALLWAY,
                    new RoomVolume(new BlockPos(-6, 1, -3), new BlockPos(7, 7, 4))
            )
    );

    public static final RoomDefinition MEDIUM = register(
            new RoomDefinition(
                    "medium",
                    RoomType.MEDIUM,
                    new RoomVolume(new BlockPos(-8, 1, -8), new BlockPos(9, 7, 9))
            )
    );

    public static final RoomDefinition LARGE = register(
            new RoomDefinition(
                    "large",
                    RoomType.LARGE,
                    new RoomVolume(new BlockPos(-10, 1, -10), new BlockPos(11, 8, 11))
            )
    );

    public static final RoomDefinition SMALL_L_SHAPED = register(
            new RoomDefinition(
                    "small_l_shaped",
                    RoomType.L_SHAPED,
                    new RoomVolume(new BlockPos(-11, 1, -3), new BlockPos(5, 7, 4)),
                    new RoomVolume(new BlockPos(-4, 1, -8), new BlockPos(5, 7, 4))
            )
    );

    public static RoomDefinition get(String roomType){
        for(RoomDefinition room : ROOM_TYPES){
            if(Objects.equals(room.toString(), roomType)){
                return room;
            }
        }
        NormalDungeonMod.LOGGER.error("Room Type not found! Returning null for attempted type: " + roomType);
        return null;
    }

    private static RoomDefinition register(RoomDefinition def) {
        ROOM_TYPES.add(def);
        return def;
    }
}
