package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.definition.LaserType;
import net.poob22.normaldm.common.server.entity.projectile.BioluminescentBeamEntity;
import org.jetbrains.annotations.NotNull;

public abstract class AnimatedLaserShootingMob extends DungeonMob {
    protected static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(AnimatedLaserShootingMob.class, EntityDataSerializers.BOOLEAN);
    protected static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(AnimatedLaserShootingMob.class, EntityDataSerializers.BOOLEAN);

    public final AnimationState chargingAnimation = new AnimationState();
    public final AnimationState shootingAnimation = new AnimationState();

    protected LaserType LASER_TYPE;
    protected int LASER_DURATION;
    protected int CHARGE_UP_DURATION;
    protected int LASER_DISTANCE;
    protected boolean IS_STATIC;
    private int chargeTime = 0;

    public BioluminescentBeamEntity beam;

    protected AnimatedLaserShootingMob(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGING, false);
        this.entityData.define(SHOOTING, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("chargingUp", isCharging());
        tag.putBoolean("shooting", isShooting());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        setCharging(tag.getBoolean("chargingUp"));
        setShooting(tag.getBoolean("shooting"));
    }

    @Override
    public void tick() {
        if(!this.level().isClientSide) {
            if(isCharging()) {
                this.level().broadcastEntityEvent(this, (byte)3);

                chargeTime++;
                if(chargeTime >= CHARGE_UP_DURATION) {
                    this.setShooting(true);
                    this.shootBeam();
                    this.level().broadcastEntityEvent(this, (byte)5);
                    this.level().broadcastEntityEvent(this, (byte) 4);
                    this.setCharging(false);
                }
            } else {
                chargeTime = 0;
            }

            if(isShooting()) {
                if(this.beam == null || beam.isRemoved()) {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                    setShooting(false);
                }
            }
        }

        super.tick();
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId == 3)
            this.chargingAnimation.startIfStopped(this.tickCount);
        if(pId == 4)
            this.shootingAnimation.startIfStopped(this.tickCount);
        if(pId == 5)
            this.chargingAnimation.stop();
        if(pId == 6)
            this.shootingAnimation.stop();
        //super.handleEntityEvent(pId);
    }

    public void setCharging(boolean pCharging) {
        this.entityData.set(CHARGING, pCharging);
    }

    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    public void setShooting(boolean pShooting) {
        this.entityData.set(SHOOTING, pShooting);
    }

    public boolean isShooting() {
        return this.entityData.get(SHOOTING);
    }

    public void setLaserType(LaserType pLaserType) {
        this.LASER_TYPE = pLaserType;
    }

    public LaserType getLaserType() {
        return LASER_TYPE;
    }

    public void setChargeUpDuration(int pChargingDuration) {
        this.CHARGE_UP_DURATION = pChargingDuration;
    }

    public int getChargeUpDuration() {
        return this.CHARGE_UP_DURATION;
    }

    public void setLaserDuration(int pLaserDuration) {
        this.LASER_DURATION = pLaserDuration;
    }

    public int getLaserDuration() {
        return this.LASER_DURATION;
    }

    public void setLaserDistance(int pLaserDistance) {
        this.LASER_DISTANCE = pLaserDistance;
    }

    public int getLaserDistance() {
        return this.LASER_DISTANCE;
    }

    public void setLaserStatic(boolean pIsStatic) {
        this.IS_STATIC = pIsStatic;
    }

    public boolean isLaserStatic() {
        return this.IS_STATIC;
    }

    public abstract boolean shootBeam();
}
