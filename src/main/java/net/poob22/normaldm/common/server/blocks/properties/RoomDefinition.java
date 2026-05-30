package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.poob22.normaldm.common.server.blocks.properties.rotation_helpers.LShapedTransformHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RoomDefinition {
    String name;

    List<RoomVolume> volumes;

    RoomType type;

    public RoomDefinition(@NotNull String name, @NotNull RoomType type, RoomVolume... volumes) {
        this.name = name;
        this.type = type;
        this.volumes = List.of(volumes);
    }

    public List<RoomVolume> getVolumes(Direction dir) {
        switch(this.getType()) {
            case SQUARE: break;
            case HALLWAY:
                switch(dir) {
                    case NORTH, SOUTH:
                        return this.volumes;
                    case EAST, WEST:
                        List<RoomVolume> vols = new ArrayList<>();
                        BlockPos min = new BlockPos(this.volumes.get(0).min.getZ(), this.volumes.get(0).min.getY(), this.volumes.get(0).min.getX());
                        BlockPos max = new BlockPos(this.volumes.get(0).max.getZ(), this.volumes.get(0).max.getY(), this.volumes.get(0).max.getX());
                        vols.add(new RoomVolume(min, max));
                        return vols;
                }

            case L_SHAPED:
                return LShapedTransformHelper.getTransformedRoomVolumes(dir);

        }

        return this.volumes;
    }

    public RoomType getType() {
        return this.type;
    }

    public String toString() {
        return name;
    }
}
