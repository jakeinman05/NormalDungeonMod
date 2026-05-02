package net.poob22.normaldm.common.server.blocks.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DungeonMobSpawner extends BlockEntity {
    EntityType<? extends DungeonMob> mobToSpawn;
    Map<UUID, EntityType<? extends DungeonMob>> map = new HashMap<>();

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
            if(getMobToSpawn() != null) {
                DungeonMob mob = getMobToSpawn().create(level);
                if(mob != null) {
                    mob.setPos(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ());
                    //maybe add some particles here
                    level.addFreshEntity(mob);
                    level.destroyBlock(this.getBlockPos(), false);
                }
            } else {
                NormalDungeonMod.LOGGER.warn("Spawner unable to spawn mob at " + this.getBlockPos() + " because entity type is null");
                level.destroyBlock(this.getBlockPos(), false);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if(mobToSpawn != null) {
            ResourceLocation enemyId = BuiltInRegistries.ENTITY_TYPE.getKey(mobToSpawn);
            tag.putString("enemy", enemyId.toString());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        String enemyId = tag.getString("enemy");
        ResourceLocation id = ResourceLocation.tryParse(enemyId);
        EntityType<? extends DungeonMob> type = (EntityType<? extends DungeonMob>) BuiltInRegistries.ENTITY_TYPE.get(id);
        if(type != null) {
            this.setMobToSpawn(type);
        } else {
            NormalDungeonMod.LOGGER.warn("Attempted to deserialize DungeonMobSpawner from null entity type at spawner pos: " + this.getBlockPos());
        }
    }
}
