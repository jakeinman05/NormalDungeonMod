package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.common.server.entity.ai.AiUtil;
import net.poob22.normaldm.common.server.entity.ai.DungeonMobMeleeGoal;
import net.poob22.normaldm.common.server.entity.registry.DungeonMobs;

import java.util.ArrayList;
import java.util.List;

public class FleshGuyEntity extends DungeonMob {
    private final List<EntityType<? extends DungeonMob>> toSpawn = new ArrayList<>();

    public FleshGuyEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHurtParticleAmount(12);
        this.setDeathParticleAmount(30);

        toSpawn.add(DungeonMobs.FLESH_BLOB.entityType.get());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return DungeonMob.createDungeonMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.41).add(Attributes.ATTACK_DAMAGE, 2.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new DungeonMobMeleeGoal(this, 1.0D));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    protected void tickDeath() {
        if(!this.level().isClientSide) {
            AiUtil.spawnEnemiesOnDeath(this, toSpawn, false);
        }
        super.tickDeath();
    }
}
