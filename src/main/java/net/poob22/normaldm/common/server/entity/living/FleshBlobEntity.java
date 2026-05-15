package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.RetreatGoal;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;

public class FleshBlobEntity extends DungeonMob {
    public static EntityDataAccessor<Integer> RESPAWN_TIMER = SynchedEntityData.defineId(FleshBlobEntity.class, EntityDataSerializers.INT);
    public static EntityDataAccessor<Integer> TYPE_TO_SPAWN = SynchedEntityData.defineId(FleshBlobEntity.class, EntityDataSerializers.INT);
    public final AnimationState throbAnimation = new AnimationState();

    public FleshBlobEntity(EntityType<? extends FleshBlobEntity> entityType, Level world) {
        super(entityType, world);
        this.setRespawnTimer(120);
        this.throbAnimation.start(this.tickCount);
        this.setHurtParticleAmount(7);
        this.setDeathParticleAmount(20);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new BlobRetreatGoal(this, 64));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 5.0D).add(Attributes.KNOCKBACK_RESISTANCE, 2.0D).add(Attributes.MOVEMENT_SPEED, 0.1D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RESPAWN_TIMER, 120);
        this.entityData.define(TYPE_TO_SPAWN, 0);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RespawnTimer", this.getRespawnTimer());
        tag.putInt("TypeToSpawn", getTypeInt());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.setRespawnTimer(tag.getInt("RespawnTimer"));
        this.setTypeInt(getTypeInt());
    }

    public void setRespawnTimer(int timerValue) {
        this.entityData.set(RESPAWN_TIMER, timerValue);
    }

    private int getRespawnTimer() {
        return this.entityData.get(RESPAWN_TIMER);
    }

    public void setTypeInt(int type) {
        this.entityData.set(TYPE_TO_SPAWN, type);
    }

    public int getTypeInt() {
        return this.entityData.get(TYPE_TO_SPAWN);
    }

    @Override
    public void tick() {
        super.tick();

        if(getRespawnTimer() <= 0) {
            if(!this.level().isClientSide) {
                FleshGuyEntity guy = DungeonMobs.FLESH_GUY.entityType.get().create(level());
                guy.setHealth(this.getHealth() + 2);
                guy.setPos(this.position());
                guy.setTypeInt(this.getTypeInt());
                level().addFreshEntity(guy);
            }
            this.remove(RemovalReason.DISCARDED);
        }
        this.setRespawnTimer(this.getRespawnTimer() - 1);
    }

    static class BlobRetreatGoal extends RetreatGoal {
        FleshBlobEntity blob;

        public BlobRetreatGoal(FleshBlobEntity mob, double retreatDistance) {
            super(mob, retreatDistance);
            this.blob = mob;
        }

        @Override
        public boolean canUse() {
            return this.blob.getTypeInt() == 3 && super.canUse();
        }
    }
}
