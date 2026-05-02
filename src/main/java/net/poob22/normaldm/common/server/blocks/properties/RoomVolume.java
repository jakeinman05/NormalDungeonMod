package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class RoomVolume {
    BlockPos min;
    BlockPos max;

    public RoomVolume(BlockPos min, BlockPos max) {
        this.min = min;
        this.max = max;
    }

    public AABB toAABB(BlockPos origin) {
        return new AABB(origin.offset(this.min), origin.offset(this.max));
    }
}
