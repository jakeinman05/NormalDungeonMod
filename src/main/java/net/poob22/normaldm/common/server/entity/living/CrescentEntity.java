package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.entity.ai.RandomStrollCardinalDirectionsGoal;
import net.poob22.normaldm.common.server.entity.ai.ShootLaserCardinalDirectionGoal;
import net.poob22.normaldm.common.server.entity.definition.LaserType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrescentEntity extends AnimatedLaserShootingMob {
    public static final EntityDataAccessor<Boolean> TALL = SynchedEntityData.defineId(CrescentEntity.class, EntityDataSerializers.BOOLEAN);
    private boolean pendingTall;

    public CrescentEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHurtParticleAmount(12);
        setDeathParticleAmount(22);
        setChargeUpDuration(20);
        setLaserDuration(30);
        setLaserDistance(50);
        setLaserStatic(false);
        setLaserType(LaserType.STRAIGHT);

        this.pendingTall = random.nextDouble() > 0.3;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ShootLaserCardinalDirectionGoal(this, 20));
        this.goalSelector.addGoal(1, new RandomStrollCardinalDirectionsGoal(this, 1.0D, 250, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.27D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TALL, pendingTall);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("tall", isTall());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.setTall(tag.getBoolean("tall"));
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if(TALL.equals(key)) {
            this.refreshDimensions();
        }
    }

    @Override
    public void tick() {
        if(this.pendingTall) {
            setTall(true);
            pendingTall = false;
        }

        super.tick();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!this.level().isClientSide) {
            if(this.getHealth() > 0.5F && this.random.nextFloat() > 0.4F)
                makeShort();
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void aiStep() {
        if(isCharging() || isShooting()) {
            this.getNavigation().stop();
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
        } else {
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        }

        if(isTall()) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.17D);
        } else {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.27D);
        }

        super.aiStep();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor pLevel, @NotNull DifficultyInstance pDifficulty, @NotNull MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        SpawnGroupData data = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);

        boolean tall = random.nextDouble() > 0.3;
        this.setTall(tall);
        this.refreshDimensions();

        return data;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pPose) {
        return isTall() ? EntityDimensions.scalable(0.625F, 2.09375F) : EntityDimensions.scalable(0.625F, 0.85F);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pDimensions) {
        return isTall() ? 1.84375F : 0.53125F;
    }

    private void setTall(boolean pTall) {
        if(pTall) {
            setHurtParticleAmount(20);
            setDeathParticleAmount(30);
        } else {
            setHurtParticleAmount(12);
            setDeathParticleAmount(22);
        }

        this.entityData.set(TALL, pTall);
    }

    public boolean isTall() {
        return this.entityData.get(TALL);
    }

    private void makeShort() {
        if(isTall()) {
            setTall(false);

            if(this.level() instanceof ServerLevel sl) sl.sendParticles(NDMParticles.FLESH_PARTICLE.get(), this.getX(), this.getY(), this.getZ(), 10, random.nextFloat() - 0.5, random.nextFloat() * 1.25, random.nextFloat() - 0.5, 1.0F);
        }
    }
}
