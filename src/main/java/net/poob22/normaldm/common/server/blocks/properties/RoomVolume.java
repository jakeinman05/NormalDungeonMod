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
        double ox = origin.getX();
        double oy = origin.getY();
        double oz = origin.getZ();

        double minX = ox + this.min.getX();
        double minY = oy + this.min.getY();
        double minZ = oz + this.min.getZ();

        double maxX = ox + this.max.getX() + 1;
        double maxY = oy + this.max.getY();
        double maxZ = oz + this.max.getZ() + 1;

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        return isPlayerBound ? box.deflate(1.25D) : box;
    }
}
