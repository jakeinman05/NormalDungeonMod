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
    int IS_AWAY_CHECK_INTERVAL = 5;

    private final T mob;
    Player target;
    double retreatDistance;
    float shotVelocity;
    boolean needsReload = false;

    boolean isAway = false;

    int tooCloseTimer = 0;
    int checkLineOfSightTimer = 0;
    int waitToShootTimer = 0;

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
                    tooCloseTimer++;

                    if(tooCloseTimer % 2 == 0) {
                        setIsAway(false);
                        if(this.mob.getNavigation().isDone()) {
                            NormalDungeonMod.LOGGER.info("attempting to retreat");
                            fleeFromTarget();
                        }
                    } else {
                        NormalDungeonMod.LOGGER.info("close timer: " + tooCloseTimer);
                    }
                } else {
                    //NormalDungeonMod.LOGGER.info("is away");
                    setIsAway(true);
                }
            }

            if(isAway()) {
                this.mob.getLookControl().setLookAt(this.target);
                waitToShootTimer++;

                if(waitToShootTimer % 30 == 0) {
                    if(canShoot()) {
                        if(shootProjectile()) {
                            NormalDungeonMod.LOGGER.info("BAM");
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
                    NormalDungeonMod.LOGGER.info("los timer: " + checkLineOfSightTimer);
                    if(checkLineOfSightTimer % 4 == 0) {
                        NormalDungeonMod.LOGGER.info("In line of sight");
                        this.mob.getNavigation().stop();
                    }
                }
            }
        }

        super.tick();
    }

    private boolean canShoot() {
        if(isAway() && mob.hasLineOfSight(this.target)) {
            if(needsReload && mob instanceof IReloadingMob) {
                return ((IReloadingMob) mob).isReloaded();
            }
            return true;
        }
        return false;
    }

    private boolean shootProjectile() {
        if(mob.level().isClientSide()) return false;

        BaseShotEntity projectile = mob.createProjectile();

        projectile.setOwner(this.mob);
        projectile.setPos(this.mob.getX(), this.mob.getEyeY() - 0.1, this.mob.getZ());

        Vec3 lookAngle = mob.getViewVector(1.0F);

        projectile.shoot(lookAngle.x, lookAngle.y, lookAngle.z, shotVelocity, 0.0F);
        return mob.level().addFreshEntity(projectile);
    }

    private void reposition() {
        //Vec3 towardsTarget = this.target.getDeltaMovement();
        this.mob.getNavigation().moveTo(target, 1.0D);
    }

    private void fleeFromTarget() {
        if(this.target != null && this.target.isAlive()) {
            Vec3 targetVec = DefaultRandomPos.getPos(this.mob, (int )this.retreatDistance * 2, 3);
            if(this.mob.getRandom().nextInt(2) == 0) {
                targetVec = DefaultRandomPos.getPosAway(this.mob, (int) retreatDistance * 2, 4, target.getDeltaMovement());
            }

            if(targetVec != null) {
                this.mob.getNavigation().moveTo(targetVec.x, targetVec.y, targetVec.z, 1.0D);
            } else {
                NormalDungeonMod.LOGGER.warn("away vec is null");
            }
        }
    }

    private void setIsAway(boolean isAway) {
        this.isAway = isAway;
    }

    private boolean isAway() {
        return isAway;
    }
}
