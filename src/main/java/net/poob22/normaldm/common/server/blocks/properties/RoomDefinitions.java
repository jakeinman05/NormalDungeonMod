package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.NormalDungeonMod;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RoomDefinitions {
    public static final List<RoomDefinition> ROOM_TYPES = new ArrayList<RoomDefinition>();

    public static final RoomDefinition SMALL = register(
            new RoomDefinition(
                    "small",
                    RoomType.SMALL,
                    new RoomVolume(new BlockPos(-5, 1, -5), new BlockPos(5, 7, 5))
            )
    );

    public static final RoomDefinition SMALL_HALLWAY_NS = register(
            new RoomDefinition(
                "small_hallway_ns",
                    new BlockPos(-3, 1, -6),
                    new BlockPos(3, 7, 6),
                    RoomType.SMALL_HALLWAY
            )
    );

    public static final RoomDefinition SMALL_HALLWAY_EW = register(
            new RoomDefinition(
                    "small_hallway_ew",
                    new BlockPos(-6, 1, -3),
                    new BlockPos(6, 7, 3),
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
