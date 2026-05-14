package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.RandomStrollCardinalDirectionsGoal;
import net.poob22.normaldm.common.server.entity.ai.ShootLaserCardinalDirectionGoal;
import net.poob22.normaldm.common.server.entity.definition.LaserType;
import org.jetbrains.annotations.NotNull;

public class CrescentEntity extends AnimatedLaserShootingMob {
    public static final EntityDataAccessor<Boolean> TALL = SynchedEntityData.defineId(CrescentEntity.class, EntityDataSerializers.BOOLEAN);

    public CrescentEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHurtParticleAmount(12);
        setDeathParticleAmount(22);
        setChargeUpDuration(20);
        setLaserDuration(30);
        setLaserDistance(50);
        setLaserStatic(false);
        setLaserType(LaserType.STRAIGHT);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new ShootLaserCardinalDirectionGoal(this, 20));
        this.goalSelector.addGoal(1, new RandomStrollCardinalDirectionsGoal(this, 1.0D, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.27D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    public void aiStep() {
        if(isCharging() || isShooting()) {
            this.getNavigation().stop();
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0F);
        } else {
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0F);
        }

        super.aiStep();
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pDimensions) {
        return 0.53125F;
    }
}
