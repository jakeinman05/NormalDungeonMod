package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.poob22.normaldm.NormalDungeonMod;

import java.util.*;

public class RoomDefinitions {
    public static final List<RoomDefinition> ROOM_TYPES = new ArrayList<RoomDefinition>();

    public static final RoomDefinition SMALL = register(
            new RoomDefinition(
                    "small",
                    RoomType.SQUARE,
                    new RoomVolume(new BlockPos(-6, 1, -6), new BlockPos(6, 8, 6))
            )
    );

    public static final RoomDefinition SMALL_HALLWAY = register(
            new RoomDefinition(
                "small_hallway",
                    RoomType.HALLWAY,
                    new RoomVolume(new BlockPos(-4, 1, -6), new BlockPos(4, 8, 6))
            )
    );

    public static final RoomDefinition LARGE_HALLWAY = register(
            new RoomDefinition(
                    "large_hallway",
                    RoomType.HALLWAY,
                    new RoomVolume(new BlockPos(-6, 1, -12), new BlockPos(6, 8, 12))
            )
    );

    public static final RoomDefinition LARGE_HALLWAY_SLIM = register(
            new RoomDefinition(
                    "large_hallway_slim",
                    RoomType.HALLWAY,
                    new RoomVolume(new BlockPos(-4, 1, -12), new BlockPos(4, 8, 12))
            )
    );

    public static final RoomDefinition LARGE = register(
            new RoomDefinition(
                    "large",
                    RoomType.SQUARE,
                    new RoomVolume(new BlockPos(-12, 1, -12), new BlockPos(12, 8, 12))
            )
    );

    public static final RoomDefinition SMALL_L_SHAPED = register(
            new RoomDefinition(
                    "small_l_shaped",
                    RoomType.L_SHAPED,
                    new RoomVolume(new BlockPos(-6, 1, -18), new BlockPos(6, 8, 6)), // NS
                    new RoomVolume(new BlockPos(-18, 1, -6), new BlockPos(6, 8, 6))  // EW
            )
    );

    public static RoomDefinition get(String roomType){
        for(RoomDefinition room : ROOM_TYPES){
            if(Objects.equals(room.toString(), roomType)){
                return room;
            }
        }
        NormalDungeonMod.LOGGER.error("Invalid Room Type: " + roomType);
        return null;
    }

    private static RoomDefinition register(RoomDefinition def) {
        ROOM_TYPES.add(def);
        return def;
    }
}
