package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class RandomStrollCardinalDirectionsGoal extends Goal {
    DungeonMob mob;
    double speedMod;

    Direction[] ALL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    int moveInDirectionTime;
    Direction moveDir;
    Vec3 movementVec;

    int stillTimer = 10;
    BlockPos lastBlockPos;

    public RandomStrollCardinalDirectionsGoal(PathfinderMob mob, double speedMod) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = (DungeonMob) mob;
        this.speedMod = speedMod;
        this.movementVec = Vec3.ZERO;
        this.moveInDirectionTime = 0;
        this.moveDir = Direction.NORTH;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return mob != null && mob.isAlive() && mob.getNavigation().isInProgress();
    }

    @Override
    public void tick() {
        super.tick();

        if(moveInDirectionTime <= 0 || isBlocked(moveDir) || mob.getNavigation().isDone()) {
            pickNewDirection();
        } else {
            //moveForward();
            moveInDirectionTime--;
        }

        if(lastBlockPos != null && lastBlockPos.equals(mob.blockPosition())) {
            stillTimer--;
            if(stillTimer <= 0) {
                pickNewDirection();
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

    private void moveForward() {
        mob.setYRot(moveDir.toYRot());
        Vec3 m = new Vec3(mob.getX() + moveDir.getStepX(), mob.getY(), mob.getZ() + moveDir.getStepZ());
        mob.getNavigation().moveTo(m.x(), m.y(), m.z(), speedMod);
    }

    private Vec3 getForwardVec() {
        double moveSpeed = mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * speedMod;
        double dx = moveDir.getStepX() * moveSpeed;
        double dz = moveDir.getStepZ() * moveSpeed;

        return new Vec3(dx, mob.getDeltaMovement().y(), dz);
    }

    private boolean isBlocked(Direction d) {
        BlockPos next = mob.blockPosition().relative(d);
        return !mob.level().getBlockState(next).isAir() || !mob.level().getBlockState(next.relative(d)).isAir();
    }
}
