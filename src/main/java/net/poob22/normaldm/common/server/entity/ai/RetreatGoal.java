package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

public class RetreatGoal extends Goal {
    DungeonMob mob;
    protected double retreatDistance;
    private final double speedMultiplier;
    private LivingEntity target;

    public RetreatGoal(DungeonMob mob, double retreatDistance, double speedMultiplier) {
        this.mob = mob;
        this.retreatDistance = retreatDistance;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public boolean canUse() {
        this.target = mob.getTarget();
        return target != null && this.mob.distanceToSqr(target) < this.retreatDistance;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void tick() {
        fleeFromTarget();
        super.tick();
    }

    protected void fleeFromTarget() {
        if(this.target != null && this.target.isAlive()) {
            Vec3 targetVec;
            if(mob.distanceTo(this.target) < 1.0) {
                targetVec = DefaultRandomPos.getPos(this.mob, (int)this.retreatDistance * 2, 2);
            }
            else {
                targetVec = DefaultRandomPos.getPosAway(this.mob, (int) this.retreatDistance + 4, 2, target.position());
            }

            if(targetVec != null) {
                this.mob.getNavigation().moveTo(targetVec.x, targetVec.y, targetVec.z, speedMultiplier);
            }
        }
    }
}
