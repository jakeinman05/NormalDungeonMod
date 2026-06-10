package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.server.entity.living.AnimatedLaserShootingMob;

import java.util.EnumSet;
import java.util.List;

public class ShootLaserCardinalDirectionGoal extends Goal {
    AnimatedLaserShootingMob mob;
    LivingEntity target;

    int SHOT_COOLDOWN;
    int shotInterval;

    Vec3 lookVec;
    Direction[] ALL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public ShootLaserCardinalDirectionGoal(AnimatedLaserShootingMob mob, int shotCooldown) {
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));

        this.mob = mob;
        this.SHOT_COOLDOWN = shotCooldown;

        this.shotInterval = 40;
    }

    @Override
    public boolean canUse() {
        if(this.mob.getTarget() == null) return false;

        if(--shotInterval <= 0) {
            for(Direction d : ALL_DIRECTIONS) {
                lookVec = getShootVec(d);
                AABB box = new AABB(mob.position(), lookVec).inflate(0.5F);
                List<Player> players = mob.level().getEntitiesOfClass(Player.class, box, player -> player != null && player.isAlive() && !player.isSpectator() && !player.isCreative());

                if(!players.isEmpty()) {
                    Player target = (Player) mob.getTarget();
                    if(target != null && players.contains(target)) {
                        this.target = target;
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.target != null && this.target.isAlive() && (mob.isCharging() || mob.isShooting());
    }

    @Override
    public void start() {
        // start charge up
        mob.getLookControl().setLookAt(lookVec);
        mob.getNavigation().stop();

        mob.setCharging(true);

        super.start();
    }

    @Override
    public void stop() {
        this.shotInterval = SHOT_COOLDOWN;
        if(!(this.mob.beam == null)) {
            if(!this.mob.beam.isRemoved()) {
                this.mob.beam.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        super.stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(lookVec);

        super.tick();
    }

    private Vec3 getShootVec(Direction direction) {
        Vec3 start = mob.getEyePosition();
        Vec3 dirVec = new Vec3(direction.getStepX(), 0, direction.getStepZ());
        Vec3 end = start.add(dirVec.scale(mob.getLaserDistance()));

        HitResult hit = mob.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }
}
