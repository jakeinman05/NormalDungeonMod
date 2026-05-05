package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

public class FleshGuyEntity extends DungeonMob {
    public FleshGuyEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHurtParticleAmount(12);
        this.setDeathParticleAmount(40);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.41).add(Attributes.ATTACK_DAMAGE, 2.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    protected void tickDeath() {
        if(!this.level().isClientSide) {
            NormalDungeonMod.LOGGER.info("Spawn blob");
            spawnFleshBlob();
        }
        super.tickDeath();
    }

    private void spawnFleshBlob() {
        EntityType<?> type = DungeonMobs.FLESH_BLOB.entityType.get();
        FleshBlobEntity blob = (FleshBlobEntity) type.create(this.level());
        if(blob != null) {
            blob.setRespawnTimer(120);
            blob.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(blob);
        }
    }
}
