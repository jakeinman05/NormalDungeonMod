package net.poob22.normaldm.common.server.blocks.properties;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class RoomDefinition {
    String name;

    BlockPos min;
    BlockPos max;

    RoomType type;

    public RoomDefinition(@NotNull String name, @NotNull BlockPos min, @NotNull BlockPos max, @NotNull RoomType type) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.type = type;
    }

    public AABB createRoomBounds(BlockPos origin, boolean playerBounds) {
        AABB box = new AABB(origin);
        AABB bBox = new AABB(
                box.minX + min.getX(),
                box.minY + min.getY(),
                box.minZ + min.getZ(),
                box.maxX + max.getX(),
                box.maxY + max.getY(),
                box.maxZ + max.getZ()
        );
        return playerBounds ? bBox.deflate(1.25) : bBox;
    }

    public BlockPos getMin() {
        return min;
    }

    public BlockPos getMax() {
        return max;
    }

    public String toString() {
        return name.toUpperCase();
    }
}
