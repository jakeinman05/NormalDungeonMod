package net.poob22.normaldm.common.server.blocks.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

public class DungeonMobSpawner extends BlockEntity {
    EntityType<? extends DungeonMob> mobToSpawn;

    public DungeonMobSpawner(BlockPos pPos, BlockState pBlockState) {
        super(NDMBlockEntities.DUNGEON_MOB_SPAWNER.get(), pPos, pBlockState);
    }

    public void setMobToSpawn(EntityType<? extends DungeonMob> mobToSpawn) {
        this.mobToSpawn = mobToSpawn;
    }

    public EntityType<? extends DungeonMob> getMobToSpawn() {
        return mobToSpawn;
    }

    public void spawnMob() {
        Level level = this.getLevel();

        if(level != null) {
            DungeonMob mob = getMobToSpawn().create(level);
            if(mob != null) {
                mob.setPos(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
                //maybe add some particles here
                level.addFreshEntity(mob);
            }
        }
    }
}
