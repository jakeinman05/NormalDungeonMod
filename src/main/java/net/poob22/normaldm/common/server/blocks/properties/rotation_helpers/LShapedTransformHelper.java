package net.poob22.normaldm.common.server.blocks.properties.rotation_helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.poob22.normaldm.common.server.blocks.properties.RoomVolume;

import java.util.List;

public class LShapedTransformHelper {
    private static final List<RoomVolume> NORTH = List.of(
            new RoomVolume(new BlockPos(6, 1, 18), new BlockPos(-6, 8 ,-6)),
            new RoomVolume(new BlockPos(18, 1, 6), new BlockPos(-6, 8, -6))
    );

    private static final List<RoomVolume> EAST = List.of(
            new RoomVolume(new BlockPos(7, 1, 19), new BlockPos(-7, 8 ,-7)),
            new RoomVolume(new BlockPos(-18, 1, -6), new BlockPos(6, 8, 6))
    );

    private static final List<RoomVolume> SOUTH = List.of(
            new RoomVolume(new BlockPos(-6, 1, -18), new BlockPos(6, 8 ,6)),
            new RoomVolume(new BlockPos(-18, 1, -6), new BlockPos(6, 8, 6))
    );

    private static final List<RoomVolume> WEST = List.of(
            new RoomVolume(new BlockPos(-6, 1, -18), new BlockPos(6, 8 ,6)),
            new RoomVolume(new BlockPos(19, 1, 7), new BlockPos(-7, 8, -7))
    );

    public static List<RoomVolume> getTransformedRoomVolumes(Direction direction) {
        return switch(direction) {
            case DOWN -> null;
            case UP -> null;
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };
    }

}
