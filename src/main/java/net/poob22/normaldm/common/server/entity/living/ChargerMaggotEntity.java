package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.ChargeAttackCardinalDirectionGoal;
import net.poob22.normaldm.common.server.entity.ai.RandomStrollCardinalDirectionsGoal;
import net.poob22.normaldm.common.server.entity.definition.IChargingMob;
import org.jetbrains.annotations.NotNull;

public class ChargerMaggotEntity extends DungeonMob implements IChargingMob {
    private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(ChargerMaggotEntity.class, EntityDataSerializers.BOOLEAN);

    public ChargerMaggotEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setDeathParticleAmount(12);
        this.setHurtParticleAmount(8);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(CHARGING, false);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.setCharging(false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ChargeAttackCardinalDirectionGoal(this, 2.0F, 20, 40, false, true));
        this.goalSelector.addGoal(2, new RandomStrollCardinalDirectionsGoal(this, 1.0D, false));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.isCharging()) {
            float rot = this.getYRot();
            this.setYRot(rot);
            this.setYHeadRot(rot);
            this.setYBodyRot(rot);
        }
    }

    @Override
    public boolean isCharging() {
        return this.entityData.get(CHARGING);
    }

    @Override
    public void wallHitReaction(double damage) {
        this.hurt(this.damageSources().flyIntoWall(), (float) (damage));
    }

    @Override
    public void entityHitReaction() {
    }

    @Override
    public void setCharging(boolean charging) {
        this.entityData.set(CHARGING, charging);
    }
}
