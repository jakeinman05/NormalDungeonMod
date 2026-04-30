package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class RandomStrollCardinalDirectionsGoal extends Goal {
    DungeonMob mob;
    double speedMod;
    boolean damaging;
    LivingEntity target;

    Direction[] ALL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    int moveInDirectionTime;
    Direction moveDir;
    Vec3 movementVec;

    int stillTimer = 10;
    BlockPos lastBlockPos;

    public RandomStrollCardinalDirectionsGoal(PathfinderMob mob, double speedMod, boolean damaging) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = (DungeonMob) mob;
        this.speedMod = speedMod;
        this.damaging = damaging;
        this.movementVec = Vec3.ZERO;
        this.moveInDirectionTime = 0;
        this.moveDir = Direction.NORTH;
    }

    @Override
    public boolean canUse() {
        if(this.damaging) {
            LivingEntity target = this.mob.getTarget();
            if(target == null) {
                NormalDungeonMod.LOGGER.error("Target is null for entity {}", mob);
                this.target = null;
            } else {
                this.target = target;
            }
        }
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return mob != null && mob.isAlive() && mob.getNavigation().isInProgress();
    }

    @Override
    public void tick() {
        super.tick();

        if(this.damaging) {
            if(this.target != null)
                checkDamage();
        }

        if(moveInDirectionTime <= 0 || isBlocked(moveDir) || mob.getNavigation().isDone() || mob.getRandom().nextInt(100) == 0) {
            pickNewDirection();
        } else {
            moveInDirectionTime--;
        }

        if(lastBlockPos != null && lastBlockPos.equals(mob.blockPosition())) {
            stillTimer--;
            if(stillTimer <= 0) {
                List<Direction> opposite = pickOppositeDirection();
                moveDir = opposite.get(0);
            }
        } else {
            stillTimer = 10;
        }
        lastBlockPos = mob.blockPosition();
    }

    private void pickNewDirection() {
        List<Direction> validDirections = Arrays.stream(ALL_DIRECTIONS).filter(d -> d != moveDir && !isBlocked(d) && d != moveDir.getOpposite()).toList();
        if(!validDirections.isEmpty()) {
            moveDir = validDirections.get(mob.getRandom().nextInt(validDirections.size()));
        } else {
            moveDir = ALL_DIRECTIONS[mob.getRandom().nextInt(ALL_DIRECTIONS.length)];
        }

        moveInDirectionTime = 10 + mob.getRandom().nextInt(50);

        BlockPos blockPos = mob.blockPosition().relative(moveDir, 30);
        mob.getNavigation().moveTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), speedMod);
    }

    private List<Direction> pickOppositeDirection() {
        return Arrays.stream(ALL_DIRECTIONS).filter(d -> d == moveDir.getOpposite()).toList();
    }

    private boolean isBlocked(Direction d) {
        BlockPos next = mob.blockPosition().relative(d);
        return !mob.level().getBlockState(next).isAir();
        // || !mob.level().getBlockState(next.relative(d)).isAir()
    }

    private void checkDamage() {
        if(mob.hasLineOfSight(target) && mob.distanceTo(target) <= (mob.getBbWidth() + 0.3F) + target.getBbWidth() + 0.0F) {
            target.hurt(target.damageSources().mobAttack(mob), (float) Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).getValue());
        }
    }
}
