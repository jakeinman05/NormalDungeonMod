package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
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

    public List<AABB> createRoomBounds(BlockPos origin, boolean playerBounds) {
        if(!playerBounds){
            List<AABB> roomBounds = new ArrayList<>();
            for(RoomVolume v : this.volumes){
                roomBounds.add(v.toAABB(origin));
            }
            return roomBounds;
        }
        List<AABB> playerBoxes = new ArrayList<>();
        for(RoomVolume v : this.volumes){
            playerBoxes.add(v.toAABB(origin).deflate(1.25D));
        }
        return playerBoxes;
    }

    public List<BlockPos> getMinOfVolumes() {
        List<BlockPos> pos = new ArrayList<>();
        for(RoomVolume v : this.volumes){
            pos.add(v.min);
        }
        return pos;
    }

    public List<BlockPos> getMaxOfVolumes() {
        List<BlockPos> pos = new ArrayList<>();
        for(RoomVolume v : this.volumes){
            pos.add(v.max);
        }
        return pos;
    }

    public String toString() {
        return name.toUpperCase();
    }
}
