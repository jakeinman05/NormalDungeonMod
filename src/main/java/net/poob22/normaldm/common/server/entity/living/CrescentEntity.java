package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.RandomStrollCardinalDirectionsGoal;
import org.jetbrains.annotations.NotNull;

public class CrescentEntity extends DungeonMob {

    public CrescentEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHurtParticleAmount(12);
        setDeathParticleAmount(22);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new RandomStrollCardinalDirectionsGoal(this, 1.0D, false));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.27D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pPose, @NotNull EntityDimensions pDimensions) {
        return 0.53125F;
    }
}
