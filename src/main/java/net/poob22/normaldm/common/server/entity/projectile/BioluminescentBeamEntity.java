package net.poob22.normaldm.common.server.entity.projectile;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.packet.BeamValuesToClientPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.server.entity.definition.LaserType;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BioluminescentBeamEntity extends Entity {
    public static final EntityDataAccessor<Integer> SHOOTER_UUID = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LASER_DISTANCE = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);
    protected double segmentRadius = 0.5;
    protected double segmentLength = 0.75;
    protected float DEFAULT_LERP_STRENGTH = 0.07F;

    private boolean isStatic = false;
    private boolean beamBuilt = false;

    public LivingEntity shooter;
    public UUID shooterUuid;
    public List<Vec3> points;
    public List<Vec3> pointso;
    private LivingEntity target;

    private LaserType type;

    public Vec3 shooterPos = Vec3.ZERO;
    public Vec3 shooterViewVec = Vec3.ZERO;
    public Vec3 targetPos = Vec3.ZERO;
    public float lerpStrength = DEFAULT_LERP_STRENGTH;

    protected List<LivingEntity> justHit = new ArrayList<>();

    public BioluminescentBeamEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.points = new ArrayList<>();
        this.pointso = new ArrayList<>();
    }
    public BioluminescentBeamEntity(Level level, LivingEntity shooter, LivingEntity target, int lifeTime, int laserDistance, boolean isStatic, LaserType type) {
        this(NDMEntities.BIOLUMINESCENT_BEAM_SEGMENT.get(), level);
        this.shooter = shooter;
        this.shooterUuid = shooter.getUUID();
        this.entityData.set(SHOOTER_UUID, shooter.getId());

        this.isStatic = isStatic;
        this.type = type;
        this.target = target;

        this.setLifetime(lifeTime);
        this.setLaserDistance(laserDistance);
    }

    @Override
    public void tick() {
        if(this.getLifetime() > 0) {
            this.setLifetime(this.getLifetime() - 1);

            if(!level().isClientSide) {
                if(!beamBuilt) {
                    switch(this.type) {
                        case STRAIGHT:
                            shooterPos = this.shooter.getEyePosition();
                            shooterViewVec = this.shooter.getViewVector(1.0F);
                            targetPos = Vec3.ZERO;
                            lerpStrength = 0.0F;
                            sendBeamPacket();
                            constructBeamPoints(lerpStrength);
                            break;
                        case HOMING:
                            shooterPos = this.shooter.getEyePosition();
                            shooterViewVec = this.shooter.getViewVector(1.0F);
                            targetPos = this.target.getBoundingBox().getCenter();
                            sendBeamPacket();
                            constructBeamPoints(lerpStrength);
                            break;
                        case FOLLOWING:
                            shooterPos = this.shooter.getEyePosition();
                            shooterViewVec = this.shooter.getViewVector(1.0F);
                            targetPos = this.target.getBoundingBox().getCenter();
                            this.lerpStrength = (float) (DEFAULT_LERP_STRENGTH + 0.15/this.getLifetime());
                            sendBeamPacket();
                            constructBeamPoints(lerpStrength);
                            break;
                    }
                }

                if(this.tickCount % 3 == 0) {
                    clearJustHit();
                    if(checkSegmentDamage()) {
                        if(level() instanceof ServerLevel sl) {
                            for(LivingEntity entity : justHit) {
                                Vec3 entityCenter = entity.getBoundingBox().getCenter();
                                sl.sendParticles(ParticleTypes.SMOKE, entityCenter.x, entityCenter.y, entityCenter.z, 5, random.nextDouble(), random.nextDouble(), random.nextDouble(), 0.0F);
                            }
                        }
                    }

                    for(Vec3 point : points) {
                        ((ServerLevel) level()).sendParticles(ParticleTypes.SMOKE, point.x, point.y, point.z, 1, 0, 0, 0, 0);
                    }
                }

                if(this.shooter == null || !this.shooter.isAlive()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }
        } else {
            this.remove(RemovalReason.DISCARDED);
        }

        super.tick();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if(key.equals(SHOOTER_UUID) && level().isClientSide) {
            int id = this.entityData.get(SHOOTER_UUID);
            Entity entity = level().getEntity(id);
            if(entity instanceof LivingEntity l) {
                shooter = l;
            }
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(SHOOTER_UUID, -1);
        this.entityData.define(LIFETIME, 0);
        this.entityData.define(LASER_DISTANCE, 0);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.shooterUuid = tag.getUUID("shooterUuid");
        setLifetime(tag.getInt("lifetime"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putUUID("shooterUuid", this.shooterUuid);
        tag.putInt("lifetime", this.getLifetime());
        tag.putInt("laserDistance", this.getLaserDistance());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        tag.putUUID("shooterUuid", this.shooterUuid);
        this.setLifetime(tag.getInt("lifetime"));
        this.setLaserDistance(tag.getInt("laserDistance"));
    }

    public void constructBeamPoints(double lerpStrength) {
        if(this.shooter != null) {
            NormalDungeonMod.LOGGER.info("Constructing beam points for Client = " + this.level().isClientSide);
            clearPoints();
            Vec3 currentDir = this.shooterViewVec;

            Vec3 start;
            if(this.shooter instanceof Player) start = shooterPos.subtract(0, 1, 0);
            else start = shooterPos;
            // push start forward by 1 block
            start = start.add(currentDir.scale(1.0F));
            // add starting point to list
            addPoint(start);

            Vec3 end;
            boolean hitBlock = false;
            int segments = 1;

            while(!hitBlock) {
                // lerp direction towards target pos
                if(targetPos != null && !targetPos.equals(Vec3.ZERO)) {
                    Vec3 toTargetDir = targetPos.subtract(start).normalize();
                    currentDir = currentDir.lerp(toTargetDir, lerpStrength).normalize();
                }
                // clip with segment length size
                end = start.add(currentDir.scale(segmentLength));
                BlockHitResult hitResult = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.shooter));
                // add ending point
                addPoint(end);
                segments++;

                if(segments >= getLaserDistance()) {
                    break;
                }

                // new starting point at the end of each clip
                start = hitResult.getLocation();

                // if clip hits a block, end loop
                if(hitResult.getType() == HitResult.Type.BLOCK) {
                    hitBlock = true;
                }
            }
        }

        //if(this.isStatic) beamBuilt = true;
    }

    private boolean checkSegmentDamage() {
        if(!points.isEmpty()) {
            for(int i = 1; i < points.size(); i++) {
                Vec3 start = points.get(i - 1);
                Vec3 end = points.get(i);
                AABB box = new AABB(start, end).inflate(1.0D);
                List<LivingEntity> closeEntities = level().getEntitiesOfClass(LivingEntity.class, box, p -> !(p.is(shooter)));

                for(LivingEntity ent : closeEntities) {
                    if(this.shooter instanceof DungeonMob && ent instanceof DungeonMob) continue;
                    if(this.justHit.contains(ent)) continue;

                    Vec3[] pointsOnEntity = new Vec3[] {
                            ent.position(),
                            ent.getEyePosition(),
                            ent.getBoundingBox().getCenter()
                    };

                    double minDist = Double.MAX_VALUE;
                    int indexCount = 0;
                    int minIdx = 0;
                    for(Vec3 points : pointsOnEntity) {
                        if(start.distanceTo(points) < minDist) {
                            minDist = start.distanceTo(points);
                            minIdx = indexCount;
                        }
                        indexCount++;
                    }

                    Vec3 nearestPos = pointsOnEntity[minIdx];
                    Vec3 AB = end.subtract(start);
                    Vec3 AP = nearestPos.subtract(start);

                    double dot = AP.dot(AB);
                    double ABMag2 = AB.lengthSqr();
                    if(ABMag2 < 1e-6) continue;

                    double proj = dot/ABMag2;
                    double t = Mth.clamp(proj, 0, 1);

                    Vec3 closestPoint = start.add(AB.scale(t));
                    Vec3 vec = nearestPos.subtract(closestPoint);

                    double totalRadius = ent.getBbWidth()/2 + segmentRadius;
                    if(vec.lengthSqr() < totalRadius * totalRadius) {
                        if(ent.hurt(damageSources().mobAttack(this.shooter), 1)) {
                            addJustHit(ent);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void addPoint(Vec3 point) {
        this.points.add(point);
    }

    public void clearPoints() {
        pointso.clear();
        pointso.addAll(points);
        points.clear();
    }

    private void addJustHit(LivingEntity entity) {
        this.justHit.add(entity);
    }

    private void clearJustHit() {
        this.justHit.clear();
    }

    private void setLifetime(int lifetime) {
        this.entityData.set(LIFETIME, lifetime);
    }

    private int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    private void setLaserDistance(int distance) {
        this.entityData.set(LASER_DISTANCE, distance);
    }

    private int getLaserDistance() {
        return this.entityData.get(LASER_DISTANCE);
    }

    private void sendBeamPacket() {
        PacketHandler.sendToTracking(this, new BeamValuesToClientPacket(this.getId(), this.shooterPos, this.shooterViewVec, this.targetPos, this.lerpStrength));
    }
}
