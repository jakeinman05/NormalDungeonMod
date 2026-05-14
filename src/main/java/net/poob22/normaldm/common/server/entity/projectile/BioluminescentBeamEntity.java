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
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.packet.BeamPointsToClientPacket;
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
    public static final EntityDataAccessor<Integer> POINTS_SIZE = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);

    private boolean beamBuilt = false;
    private boolean isStatic = false;
    private boolean pointsDirty = false;
    private boolean firstTickInit = false;

    public Vec3 beamEnd;

    public LivingEntity shooter;
    public UUID shooterUuid;
    public List<Vec3> points;
    public List<Vec3> pointso;
    protected int lifeTime;

    private LaserType type;
    protected double segmentRadius = 0.5;
    protected float laserDistance = 0;

    protected List<LivingEntity> justHit = new ArrayList<>();

    public BioluminescentBeamEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.points = new ArrayList<>();
        this.pointso = new ArrayList<>();
        this.beamEnd = Vec3.ZERO;
    }
    public BioluminescentBeamEntity(Level level, LivingEntity shooter, int lifeTime, float laserDistance, boolean isStatic, LaserType type) {
        this(NDMEntities.BIOLUMINESCENT_BEAM_SEGMENT.get(), level);
        this.shooter = shooter;
        this.shooterUuid = shooter.getUUID();
        this.entityData.set(SHOOTER_UUID, shooter.getId());

        // true for entities shooting in one direction without looking around
        this.isStatic = isStatic;
        this.type = type;

        this.lifeTime = lifeTime;
        this.laserDistance = laserDistance;
        this.setBeamEnd(beamEnd);
    }

    @Override
    public void tick() {
        if(!level().isClientSide) {
            if(this.lifeTime > 0) {
                this.lifeTime--;

                if(firstTickInit) {
                    if(!beamBuilt)
                        switch(this.type) {
                            case STRAIGHT:
                                buildStraitBeam();
                                break;
                            case HOMING:
                                buildStraitBeam();
                                break;
                        }

                    if(!points.isEmpty()) {
                        this.setPos(points.get(0).x, points.get(0).y - 0.5, points.get(0).z);
                    }

                    if(pointsDirty) {
                        PacketHandler.sendToTracking(this, new BeamPointsToClientPacket(this.getId(), this.points, getBeamEnd()));
                        pointsDirty = false;
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
            } else {
                this.remove(RemovalReason.DISCARDED);
            }
        }

        if(!firstTickInit)
            firstTickInit = true;

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
        this.entityData.define(POINTS_SIZE, 0);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.lifeTime = tag.getInt("lifeTime");
        this.shooterUuid = tag.getUUID("shooterUuid");
        setBeamEnd(new Vec3(tag.getDouble("beamEndx"), tag.getDouble("beamEndy"), tag.getDouble("beamEndz")));

        setPointsSize(tag.getInt("numPoints"));
        this.points = new ArrayList<>();
        for(int i = 0; i < getPointsSize(); i++) {
            this.points.add(new Vec3(tag.getDouble("point" + i + "x"), tag.getDouble("point" + i + "y"), tag.getDouble("point" + i + "z")));
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("lifeTime", this.lifeTime);
        tag.putUUID("shooterUuid", this.shooterUuid);
        int size = getPointsSize();
        tag.putInt("numPoints", size);
        tag.putDouble("beamEndx", this.beamEnd.x);
        tag.putDouble("beamEndy", this.beamEnd.y);
        tag.putDouble("beamEndz", this.beamEnd.z);

        for(int i = 0; i < size; i++) {
            String vecX = "point" + i + "x";
            String vecY = "point" + i + "y";
            String vecZ = "point" + i + "z";

            tag.putDouble(vecX, points.get(i).x);
            tag.putDouble(vecY, points.get(i).y);
            tag.putDouble(vecZ, points.get(i).z);
        }
    }

    private void buildStraitBeam() {
        if(!level().isClientSide && this.shooter != null) {
            Vec3 start;

            if(this.shooter instanceof Player) start = this.shooter.getEyePosition().subtract(0, 1, 0);
            else start = this.shooter.getEyePosition();
            Vec3 direction = this.shooter.getViewVector(1.0F);
            start = start.add(direction.scale(1.0F));
            Vec3 end = start.add(direction.scale(laserDistance));
            BlockHitResult hitResult = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.shooter));
            Vec3 endHit = hitResult.getLocation();
            double diffLen = endHit.subtract(start).length();

            this.setBeamEnd(endHit);

            // add old points
            this.pointso.clear();
            this.pointso.addAll(this.points);
            clearPoints();
            for(float i = 0; i < diffLen + 0.75; i += 0.75F){
                this.addPoint(start.add(direction.scale(i)));
            }
        }

        if(this.isStatic)
            beamBuilt = true;
    }

    private boolean checkSegmentDamage() {
        if(!points.isEmpty()) {
            for(int i = 1; i < getPointsSize(); i++) {
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
                    NormalDungeonMod.LOGGER.info("Nearest point index: " + minIdx);

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
        setPointsSize(this.points.size());
        setPointsDirty();
    }

    public void setPoints(List<Vec3> pointsToAdd, int size) {
        for(int i = 0; i < size; i++) {
            this.points.add(pointsToAdd.get(i));
        }
        setPointsSize(size);
    }

    public void setPointsDirty() {
        this.pointsDirty = true;
    }

    public void removePoint(Vec3 point) {
        if(this.points.remove(point))
            setPointsDirty();
        else
            NormalDungeonMod.LOGGER.warn("Point to remove: {} is not in the list for beam entity: {}", point.toString(), this);

        this.entityData.set(POINTS_SIZE, this.points.size());
    }

    public void clearPoints() {
        this.points.clear();
        this.entityData.set(POINTS_SIZE, 0);
        setPointsDirty();
    }

    private void setPointsSize(int size) {
        this.entityData.set(POINTS_SIZE, size);
        //NormalDungeonMod.LOGGER.info("Size: {}", getPointsSize());
    }

    public int getPointsSize() {
        return this.entityData.get(POINTS_SIZE);
    }

    public void setBeamEnd(Vec3 beamEnd) {
        this.beamEnd = beamEnd;
    }

    public Vec3 getBeamEnd() {
        return this.beamEnd;
    }

    private void addJustHit(LivingEntity entity) {
        this.justHit.add(entity);
    }

    private void clearJustHit() {
        this.justHit.clear();
    }
}
