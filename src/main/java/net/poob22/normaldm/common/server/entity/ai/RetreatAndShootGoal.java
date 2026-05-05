package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.definition.IReloadingMob;
import net.poob22.normaldm.common.server.entity.definition.IShootingMob;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import net.poob22.normaldm.common.server.entity.projectile.BaseShotEntity;

public class RetreatAndShootGoal<T extends DungeonMob & IShootingMob> extends Goal {
    protected int IS_AWAY_CHECK_INTERVAL = 3;

    protected final T mob;
    protected Player target;
    protected double retreatDistance;
    float shotVelocity;
    boolean needsReload = false;

    boolean isAway = false;

    protected int checkLineOfSightTimer = 0;
    protected int waitToShootTimer = 0;

    public RetreatAndShootGoal(T mob, float shotVelocity, double retreatDistance) {
        this.mob = mob;
        this.retreatDistance = retreatDistance;
        this.shotVelocity = shotVelocity;
    }

    @Override
    public boolean canUse() {
        if(this.mob.getTarget() != null && this.mob.getTarget() instanceof Player) {
            this.target = (Player) this.mob.getTarget();

            if(mob instanceof IReloadingMob) {
                this.needsReload = true;
            }
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target instanceof Player && this.target.isAlive();
    }

    @Override
    public void tick() {
        if(!this.mob.level().isClientSide()) {
            // fleeing
            if(this.mob.tickCount % IS_AWAY_CHECK_INTERVAL == 0) {
                if(this.mob.distanceTo(this.target) < this.retreatDistance) {
                    setIsAway(false);
                    fleeFromTarget();
                } else {
                    setIsAway(true);
                }
            }

            if(isAway()) {
                this.mob.getLookControl().setLookAt(this.target);
                waitToShootTimer++;

                if(waitToShootTimer % 30 == 0) {
                    if(canShoot()) {
                        if(shootProjectile()) {
                            if(this.mob instanceof IReloadingMob bob) {
                                bob.setReloaded(false);
                            }
                        }
                    }
                }
                if(!this.mob.hasLineOfSight(this.target))
                    reposition();
                else {
                    checkLineOfSightTimer++;
                    if(checkLineOfSightTimer % 4 == 0) {
                        this.mob.getNavigation().stop();
                    }
                }
            }
        }

        super.tick();
    }

    protected boolean canShoot() {
        if(isAway() && mob.hasLineOfSight(this.target)) {
            Vec3 toPlayer = this.target.position().subtract(this.mob.position()).normalize();
            double d0 = this.mob.getViewVector(1.0F).dot(toPlayer);
            if(d0 > 0.9) {
                if(needsReload && mob instanceof IReloadingMob) {
                    return ((IReloadingMob) mob).isReloaded();
                }
                return true;
            }
        }
        return false;
    }

    protected boolean shootProjectile() {
        if(mob.level().isClientSide()) return false;

        BaseShotEntity projectile = mob.createProjectile();

        projectile.setOwner(this.mob);
        projectile.setPos(this.mob.getX(), this.mob.getEyeY() - 0.1, this.mob.getZ());

        Vec3 lookAngle = mob.getViewVector(1.0F);

        projectile.shoot(lookAngle.x, lookAngle.y, lookAngle.z, shotVelocity, 0.0F);
        return mob.level().addFreshEntity(projectile);
    }

    protected void reposition() {
        this.mob.getNavigation().moveTo(target, 1.0D);
    }

    protected void fleeFromTarget() {
        if(this.target != null && this.target.isAlive()) {
            Vec3 targetVec;
            if(mob.distanceTo(this.target) < 1.0) {
                targetVec = DefaultRandomPos.getPos(this.mob, (int )this.retreatDistance * 2, 2);
            }
            else {
                targetVec = DefaultRandomPos.getPosAway(this.mob, (int) this.retreatDistance + 4, 2, target.position());
            }

            if(targetVec != null) {
                this.mob.getNavigation().moveTo(targetVec.x, targetVec.y, targetVec.z, 1.0D);
            }
        }
    }

    protected void setIsAway(boolean isAway) {
        this.isAway = isAway;
    }

    protected boolean isAway() {
        return isAway;
    }
}
