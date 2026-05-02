package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class RoomVolume {
    public final BlockPos min;
    public final BlockPos max;

    public RoomVolume(BlockPos min, BlockPos max) {
        this.min = min;
        this.max = max;
    }

    public AABB toAABB(BlockPos origin, boolean isPlayerBound) {
        BlockPos a = origin.offset(this.min);
        BlockPos b = origin.offset(this.max);

        AABB box = new AABB(
                Math.min(a.getX(), b.getX()),
                Math.min(a.getY(), b.getY()),
                Math.min(a.getZ(), b.getZ()),
                Math.max(a.getX(), b.getX()),
                Math.max(a.getY(), b.getY()),
                Math.max(a.getZ(), b.getZ())
        );

        return isPlayerBound ? box.deflate(1.25D) : box;
    }
}
