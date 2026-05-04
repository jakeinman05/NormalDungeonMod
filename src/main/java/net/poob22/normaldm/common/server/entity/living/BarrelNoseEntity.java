package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.RetreatAndShootGoal;
import net.poob22.normaldm.common.server.entity.definition.IReloadingMob;
import net.poob22.normaldm.common.server.entity.definition.IShootingMob;
import net.poob22.normaldm.common.server.entity.projectile.BaseShotEntity;
import net.poob22.normaldm.common.server.entity.projectile.SnotShotEntity;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;
import org.jetbrains.annotations.NotNull;

public class BarrelNoseEntity extends DungeonMob implements IShootingMob, IReloadingMob {
    private static final EntityDataAccessor<Boolean> RELOADED = SynchedEntityData.defineId(BarrelNoseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(BarrelNoseEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> RELOAD_TIME = SynchedEntityData.defineId(BarrelNoseEntity.class, EntityDataSerializers.INT);

    public final AnimationState shoot = new AnimationState();

    private static final int DEFAULT_RELOAD_TIME = 100;
    private int reloadTime = 60;

    public BarrelNoseEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHurtParticleAmount(10);
        this.setDeathParticleAmount(18);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RELOADED, false);
        this.entityData.define(SHOOTING, false);
        this.entityData.define(RELOAD_TIME, DEFAULT_RELOAD_TIME);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        tag.putInt("reloadTime", this.reloadTime);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.reloadTime = tag.getInt("reloadTime");
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RetreatAndShootGoal<>(this, 0.7F, 5.0D));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.31D);
    }

    @Override
    public void tick() {
        if(!isReloaded()) {
            reloadTime -= 1;
            //NormalDungeonMod.LOGGER.info("BarrelNoseEntity: reloading " + reloadTime);
            if(reloadTime <= 0) {
                //NormalDungeonMod.LOGGER.info("BarrelNoseEntity: reload complete");
                setReloaded(true);
                reloadTime = DEFAULT_RELOAD_TIME;
            }
        }

        super.tick();
    }

    @Override
    public boolean isReloaded() {
        return this.entityData.get(RELOADED);
    }

    @Override
    public void setReloaded(boolean reloaded) {
        this.entityData.set(RELOADED, reloaded);
    }

    @Override
    public int getReloadTime(int reloadTime) {
        return DEFAULT_RELOAD_TIME;
    }

    @Override
    public BaseShotEntity createProjectile() {
        return new SnotShotEntity(NDMEntities.SNOT_SHOT.get(), this.level());
    }

    /// create a static class overriding the RetreatAndShootGoal to add states for animation
}
