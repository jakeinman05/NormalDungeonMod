package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import java.util.EnumSet;

public class DungeonMobMeleeGoal extends Goal {
    private static final int ATTACK_COOLDOWN_TIMER = 15;

    private final DungeonMob mob;
    private final double speedMod;
    protected int attackCooldown;

    protected int RECALC_PATH_TIME;
    private int recalculatePath = 0;

    public DungeonMobMeleeGoal(DungeonMob mob, double speedModifier, int pathingInterval) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.mob = mob;
        this.speedMod = speedModifier;
        this.RECALC_PATH_TIME = pathingInterval;
    }

    @Override
    public boolean canUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if(target == null) return;

        this.mob.getLookControl().setLookAt(target);

        if(this.attackCooldown > 0) this.attackCooldown--;

        if(!this.mob.level().isClientSide()) {
            if(recalculatePath > 0) this.recalculatePath--;

            else {
                recalculatePath = RECALC_PATH_TIME;
                this.mob.getNavigation().moveTo(target, speedMod);
            }

            if(target.isAlive() && attackCooldown <= 0) {
                if(AiUtil.checkDamage(mob, target, 0)) {
                    this.attackCooldown = ATTACK_COOLDOWN_TIMER;
                }
            }
        }

        super.tick();
    }
}
