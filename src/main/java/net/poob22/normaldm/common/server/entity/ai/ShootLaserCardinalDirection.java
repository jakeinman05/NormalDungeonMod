package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import net.poob22.normaldm.common.server.entity.projectile.BaseBioluminescentBeamEntity;

import java.util.EnumSet;
import java.util.List;

public class ShootLaserCardinalDirection extends Goal {
    DungeonMob mob;
    LivingEntity target;
    int LASER_TIME;
    int SHOT_COOLDOWN;
    int CHARGE_UP_TIME;

    int charge;
    int shotInterval;
    Vec3 lookVec;
    boolean laserActive = false;

    BaseBioluminescentBeamEntity beam;
    float SHOT_DISTANCE = 50.0F;
    Direction[] ALL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    public ShootLaserCardinalDirection(DungeonMob mob, int laserTime, int shotCooldown, int chargeUpTime) {
        this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.MOVE));

        this.mob = mob;
        this.LASER_TIME = laserTime;
        this.SHOT_COOLDOWN = shotCooldown;
        this.CHARGE_UP_TIME = chargeUpTime;

        this.shotInterval = 40;
    }

    @Override
    public boolean canUse() {
        if(this.mob.getTarget() == null) return false;

        if(--shotInterval <= 0) {
            for(Direction d : ALL_DIRECTIONS) {
                lookVec = getShootVec(d);
                AABB box = new AABB(mob.position(), lookVec).inflate(0.7F);
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
        if(laserActive) {
            return this.beam != null && !this.beam.isRemoved();
        }
        return this.mob.getTarget() != null;
    }

    @Override
    public void start() {
        // start charge up
        mob.getLookControl().setLookAt(lookVec);
        mob.getNavigation().stop();

        this.charge = CHARGE_UP_TIME;

        super.start();
    }

    @Override
    public void stop() {
        this.shotInterval = SHOT_COOLDOWN;
        laserActive = false;
        super.stop();
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(lookVec);

        if(!this.mob.level().isClientSide()) {
            if(charge-- <= 0) {
                if(!laserActive) {
                    if(shootLaser()) {
                        NormalDungeonMod.LOGGER.info("Laser shot!");
                    }
                    laserActive = true;
                }
            } else {
                System.out.println("\rCharging laser...");
            }
        }

        super.tick();
    }

    private Vec3 getShootVec(Direction direction) {
        Vec3 start = mob.getEyePosition();
        Vec3 dirVec = new Vec3(direction.getStepX(), 0, direction.getStepZ());
        Vec3 end = start.add(dirVec.scale(SHOT_DISTANCE));

        HitResult hit = mob.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }

    private boolean shootLaser() {
        if(!mob.level().isClientSide) {
            this.beam = new BaseBioluminescentBeamEntity(this.mob.level(), this.mob, LASER_TIME, SHOT_DISTANCE, false);
            this.beam.setPos(mob.getX(), mob.getEyeY(), mob.getZ());
            return mob.level().addFreshEntity(beam);
        }
        return false;
    }
}
