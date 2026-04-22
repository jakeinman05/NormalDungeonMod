package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.definition.IChargingMob;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

public class ChargeAttackCardinalDirectionGoal extends Goal {
    DungeonMob mob;
    IChargingMob chargingMob;
    double speedMod;
    int chargeDist;
    LivingEntity target;
    int chargeCooldown;
    int chargeInterval;
    boolean takeWallDamage;
    boolean randomInterval;

    boolean stopCharge = false;

    Direction[] ALL_DIRECTIONS = {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    Direction chargeDir;

    // add a windup boolean that can be passed into the goal for future entity use
    public ChargeAttackCardinalDirectionGoal(DungeonMob mob, double speedMod, int chargeDistance, int chargeCooldown, boolean takeWallDamage, boolean randomInterval) {
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        this.mob = mob;
        if(mob instanceof IChargingMob m) this.chargingMob = m;
        this.speedMod = speedMod;
        this.chargeDist = chargeDistance;
        this.chargeCooldown = chargeCooldown;
        this.takeWallDamage = takeWallDamage;
        this.chargeInterval = 0;
        this.randomInterval = randomInterval;
    }

    @Override
    public boolean canUse() {
        if(!(mob instanceof  IChargingMob)) {
            NormalDungeonMod.LOGGER.error("Mob must extend IChargingMob for ChargeAttackCardinalDirectionsGoal!");
            return false;
        } else if(chargingMob == null) {
            return false;
        } else if(chargingMob.isCharging()) {
            return false;
        }

        if(mob.getTarget() == null && !mob.onGround()) {
            return false;
        }
        if(this.chargeInterval > 0) {
            this.chargeInterval--;
            return false;
        }

        for(Direction d : ALL_DIRECTIONS) {
            Vec3 chargeVec = getChargeVec(d);
            AABB box = new AABB(mob.position(), chargeVec).inflate(0.7F);
            List<Player> players = mob.level().getEntitiesOfClass(Player.class, box, player -> player != null && player.isAlive() && !player.isSpectator() && !player.isCreative());

            if(!players.isEmpty()) {
                Player target = (Player) mob.getTarget();
                if(target != null && players.contains(target)) {
                    this.target = target;
                    chargeDir = d;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void start() {
        super.start();
        mob.getNavigation().stop();
        this.chargingMob.setCharging(true);
        mob.setTarget(null);
        mob.getLookControl().setLookAt(mob);
    }

    @Override
    public boolean canContinueToUse() {
        return !stopCharge;
    }

    @Override
    public void stop() {
        if(!randomInterval) {
            this.chargeInterval = chargeCooldown;
        } else {
            this.chargeInterval = chargeCooldown/2 + mob.getRandom().nextInt(chargeCooldown/2);
        }

        this.stopCharge = false;
        this.chargingMob.setCharging(false);
        super.stop();
    }

    @Override
    public void tick() {
        super.tick();

        mob.getNavigation().stop();

        double speed = mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * speedMod;
        Vec3 move = new Vec3(chargeDir.getStepX() * speed, mob.onGround() ? 0 : mob.getDeltaMovement().y, chargeDir.getStepZ() * speed);
        mob.setDeltaMovement(move);

        if(checkDamage()) {
            chargingMob.entityHitReaction();
            this.stopCharge = true;
        }

        if(hitWall()) {
            if(this.takeWallDamage)
                chargingMob.wallHitReaction(mob.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) * speedMod);

            this.stopCharge = true;
        }

        mob.setYRot(chargeDir.toYRot());
    }

    private Vec3 getChargeVec(Direction direction) {
        Vec3 start = mob.position();
        Vec3 dirVec = new Vec3(direction.getStepX(), 0, direction.getStepZ());
        Vec3 end = start.add(dirVec.scale(chargeDist));

        HitResult hit = mob.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mob));
        return hit.getType() == HitResult.Type.MISS ? end : hit.getLocation();
    }

    private boolean checkDamage() {
        if(mob.hasLineOfSight(target) && mob.distanceTo(target) <= (mob.getBbWidth() + 0.3F) + target.getBbWidth() + 0.0F) {
            return target.hurt(target.damageSources().mobAttack(mob), (float) Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).getValue());
        }
        return false;
    }

    private boolean hitWall() {
        Vec3 dirVec = new Vec3(chargeDir.getStepX(), 0, chargeDir.getStepZ());
        AABB movedBox = mob.getBoundingBox().move(dirVec.scale(0.5));
        return !mob.level().noCollision(mob, movedBox) || !mob.level().getBlockState(mob.blockPosition()).isAir();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}