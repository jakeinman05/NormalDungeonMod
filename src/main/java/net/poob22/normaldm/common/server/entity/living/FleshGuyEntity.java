package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.ai.AiUtil;
import net.poob22.normaldm.common.server.entity.ai.DungeonMobMeleeGoal;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FleshGuyEntity extends DungeonMob {
    public static final EntityDataAccessor<Boolean> CLOSE = SynchedEntityData.defineId(FleshGuyEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Integer> TYPE_INT = SynchedEntityData.defineId(FleshGuyEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Float> ARM_PROGRESS = SynchedEntityData.defineId(FleshGuyEntity.class, EntityDataSerializers.FLOAT);

    public float prevArmProgress;
    public float armProgress;

    public FleshGuyEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHurtParticleAmount(12);
        this.setDeathParticleAmount(30);
        this.setDoDeathPool(false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 6.0D).add(Attributes.MOVEMENT_SPEED, 0.333).add(Attributes.ATTACK_DAMAGE, 1.5F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new DungeonMobMeleeGoal(this, 1.0D, 0));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CLOSE, false);
        this.entityData.define(TYPE_INT, 0);
        this.entityData.define(ARM_PROGRESS, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("close", isClose());
        tag.putInt("type", getTypeInt());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        setClose(tag.getBoolean("close"));
        setTypeInt(tag.getInt("type"));
    }

    @Override
    protected void tickDeath() {
        if(!this.level().isClientSide) {
            FleshBlobEntity blob = DungeonMobs.FLESH_BLOB.entityType.get().create(this.level());
            blob.setPos(this.getX(), this.getY(), this.getZ());
            blob.setTypeInt(this.getTypeInt());
            level().addFreshEntity(blob);
        }
        super.tickDeath();
    }

    @Override
    public void aiStep() {
        if(!this.level().isClientSide() && this.getTarget() != null) {
            // start progress from 25 (5 blocks)
            float dist = (float) this.distanceToSqr(this.getTarget());
            if(dist < 25.0D) {
                setArmSwingProgress(Mth.clamp((25.0F - dist) / 12.5F, 0.0F, 1.0F));
            } else {
                setArmSwingProgress(0.0F);
            }
        }

        if(this.level().isClientSide) {
            prevArmProgress = armProgress;
            armProgress = getArmSwingProgress();
        }

        super.aiStep();
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        initializeSpawn();
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void initializeSpawn() {
        float n = random.nextFloat();

        if(n > 0.35) setTypeInt(0);
        else if(n < 0.35F && n >= 0.2F) setTypeInt(1); // slow
        else if(n < 0.2F && n >= 0.1F) setTypeInt(2); // fast
        else if(n < 0.1F) setTypeInt(3); // see

        switch(getTypeInt()) {
            case 1:
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2879D);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2D);
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(7.0D);
                this.heal(1);
                break;
            case 2:
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(5.0D);
                this.heal(1);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.35D);
                break;
            default:
                break;
        }

        super.initializeSpawn();
    }

    public boolean isClose() {
        return this.entityData.get(CLOSE);
    }

    public void setClose(boolean value) {
        this.entityData.set(CLOSE, value);
    }

    public int getTypeInt() {
        return this.entityData.get(TYPE_INT);
    }

    public void setTypeInt(int value) {
        this.entityData.set(TYPE_INT, value);
    }

    private void setArmSwingProgress(float value) {
        this.entityData.set(ARM_PROGRESS, value);
    }

    public float getArmSwingProgress() {
        return this.entityData.get(ARM_PROGRESS);
    }
}
