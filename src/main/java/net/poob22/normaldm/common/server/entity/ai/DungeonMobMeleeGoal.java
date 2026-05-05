package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

public class DungeonMobMeleeGoal extends Goal {
    private static final int ATTACK_COOLDOWN_TIMER = 15;

    DungeonMob mob;
    protected int attackCooldown;

    public DungeonMobMeleeGoal(DungeonMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        if(this.attackCooldown > 0) this.attackCooldown--;

        if(!this.mob.level().isClientSide() && this.mob.getTarget() != null) {
            LivingEntity target = this.mob.getTarget();
            this.mob.getNavigation().moveTo(target, 1.0D);

            if(target.isAlive() && attackCooldown <= 0) {
                if(AiUtil.checkDamage(mob, target)) {
                    this.attackCooldown = ATTACK_COOLDOWN_TIMER;
                }
            }
        }

        super.tick();
    }
}
