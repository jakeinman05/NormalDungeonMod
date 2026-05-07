package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.AbstractRandomlyAttackingMob;

public class RandomlyAttackGoal extends Goal {
    AbstractRandomlyAttackingMob mob;

    public RandomlyAttackGoal(AbstractRandomlyAttackingMob mob) {
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        if(mob.getAttackInterval() <= 0) {
            return this.mob.isAlive() && this.mob.getRandom().nextInt(200) == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.isAlive() && mob.isAttacking();
    }

    @Override
    public void start() {
        NormalDungeonMod.LOGGER.info("attacking");
        this.mob.setAttacking(true);
        this.mob.level().broadcastEntityEvent(this.mob, (byte)4);
    }



    @Override
    public void tick() {
        if(this.mob.isAlive() && this.mob.shouldPerformAttack()) {
            this.mob.performAttack();
        }

        super.tick();
    }
}
