package net.poob22.normaldm.common.server.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.misc.NDMDamageTypes;
import net.poob22.normaldm.common.server.entity.ai.AiUtil;
import net.poob22.normaldm.common.server.entity.definition.LaserType;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BioluminescentBeamEntity extends Entity {
    public static final EntityDataAccessor<Integer> SHOOTER_UUID = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> LASER_DISTANCE = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> STATIC = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Vector3f> SHOOTER_POS = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> SHOOTER_VIEW_ANGLE = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Vector3f> TARGET_POS = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.VECTOR3);
    public static final EntityDataAccessor<Float> LERP_STRENGTH = SynchedEntityData.defineId(BioluminescentBeamEntity.class, EntityDataSerializers.FLOAT);

    protected double segmentRadius = 0.5;
    protected double segmentLength = 0.75;
    protected float DEFAULT_LERP_STRENGTH = 0.065F;
    protected int MAX_LIFETIME;
    protected float damage;

    public float s = 1.0F;
    public float so = 1.0F;

    private boolean beamBuilt = false;

    Vec3 lastTargetPos;

    public LivingEntity shooter;
    public UUID shooterUuid;
    public List<Vec3> points;
    public List<Vec3> pointso;
    private LivingEntity target;
    private BlockHitResult hitPoint;

    private LaserType type;

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

        // scale damage based on players damage stat in dungeon
        this.damage = this.shooter instanceof Player ? 1.0F : 1.0F;

        this.type = type;
        this.target = target;

        this.MAX_LIFETIME = lifeTime;
        this.setLifetime(MAX_LIFETIME);
        this.setLaserDistance(laserDistance);
        this.setStatic(isStatic);
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
        this.entityData.define(STATIC, false);
        this.entityData.define(SHOOTER_POS, new Vector3f(0.0F, 0.0F, 0.0F));
        this.entityData.define(SHOOTER_VIEW_ANGLE, new Vector3f(0.0F, 0.0F, 0.0F));
        this.entityData.define(TARGET_POS, new Vector3f(0.0F, 0.0F, 0.0F));
        this.entityData.define(LERP_STRENGTH, 0.0F);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.shooterUuid = tag.getUUID("shooterUuid");
        this.setLifetime(tag.getInt("lifetime"));
        this.setLaserDistance(tag.getInt("laserDistance"));
        this.setStatic(tag.getBoolean("static"));
        this.setLerpStrength(tag.getFloat("lerpStrength"));
        this.setShooterPos(loadVec3fData(tag, "shooterPos"));
        this.setShooterViewVector(loadVec3fData(tag, "shooterViewAngle"));
        this.setTargetPos(loadVec3fData(tag, "targetPos"));
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putUUID("shooterUuid", this.shooterUuid);
        tag.putInt("lifetime", this.getLifetime());
        tag.putInt("laserDistance", this.getLaserDistance());
        tag.putBoolean("static", this.isStatic());
        tag.putFloat("lerpStrength", getLerpStrength());
        saveVec3fData(tag, entityData.get(SHOOTER_POS), "shooterPos");
        saveVec3fData(tag, entityData.get(SHOOTER_VIEW_ANGLE), "shooterViewAngle");
        saveVec3fData(tag, entityData.get(TARGET_POS), "targetPos");
    }

    @Override
    public void tick() {
        if(this.getLifetime() > 0) {
            this.setLifetime(this.getLifetime() - 1);

            if(!level().isClientSide) {
                if(!beamBuilt && !(this.type == null)) {
                    switch(this.type) {
                        case STRAIGHT:
                            setShooterPos(this.shooter.getEyePosition());
                            setShooterViewVector(this.shooter.getViewVector(1.0F));

                            Vec3 desiredTarget = getShooterPos().add(getShooterViewVector().scale(getLaserDistance()));
                            if(lastTargetPos == null)
                                lastTargetPos = desiredTarget;

                            lastTargetPos = lastTargetPos.lerp(desiredTarget, 0.4);

                            setTargetPos(lastTargetPos);
                            setLerpStrength(DEFAULT_LERP_STRENGTH);
                            constructBeamPoints(getLerpStrength());
                            break;
                        case HOMING:
                            setShooterPos(this.shooter.getEyePosition());
                            setShooterViewVector(this.shooter.getViewVector(1.0F));
                            setTargetPos(this.target.getBoundingBox().getCenter());
                            setLerpStrength(DEFAULT_LERP_STRENGTH);
                            constructBeamPoints(getLerpStrength());
                            break;
                        case FOLLOWING:
                            setShooterPos(this.shooter.getEyePosition());
                            setShooterViewVector(this.shooter.getViewVector(1.0F));
                            setTargetPos(this.target.getBoundingBox().getCenter());
                            setLerpStrength((float) (DEFAULT_LERP_STRENGTH + (0.055 * (1.0F - ((double) this.getLifetime() / this.MAX_LIFETIME)))));
                            constructBeamPoints(getLerpStrength());
                            break;
                    }
                }

                if(level() instanceof ServerLevel sl) {
                    if(this.tickCount % 2 == 0) {
                        clearJustHit();
                        if(checkSegmentDamage()) {
                            for(LivingEntity entity : justHit) {
                                Vec3 entityCenter = entity.getBoundingBox().getCenter();
                                sl.sendParticles(ParticleTypes.SMOKE, entityCenter.x, entityCenter.y, entityCenter.z, 5, random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5, 0.0F);
                            }
                        }
                    }

                    if(hitPoint != null) {
                        BlockPos pos = new BlockPos(hitPoint.getBlockPos());
                        BlockState state = sl.getBlockState(pos);
                        if(state.getBlock() == Blocks.AIR) {
                            pos.relative(hitPoint.getDirection());
                            state = sl.getBlockState(pos);
                        }
                        BlockParticleOption type = new BlockParticleOption(ParticleTypes.BLOCK, state);
                        Vec3 v = hitPoint.getLocation();
                        sl.sendParticles(type, v.x, v.y, v.z, 5, random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5, 0.0F);
                    }

                    for(int i = 1; i < points.size(); i++) {
                        if(random.nextDouble() > 0.8) {
                            Vec3 v0 = points.get(i - 1);
                            Vec3 v1 = points.get(i);
                            Vec3 pos = v0.lerp(v1, random.nextDouble());
                            sl.sendParticles(NDMParticles.BEAM_PLASMA_PARTICLE.get(), pos.x, pos.y, pos.z, 1, random.nextDouble() - 0.5, 0, random.nextDouble() - 0.5, 0.0F);
                        }
                    }
                }

                if(this.shooter == null || !this.shooter.isAlive()) {
                    this.remove(RemovalReason.DISCARDED);
                }
            }

            if(this.level().isClientSide) {
                constructBeamPoints(getLerpStrength());

                if(this.getLifetime() <= 10) {
                    this.so = s;
                    this.s = (float)(1.0 - (1.0 - this.getLifetime() * 0.1));
                }
            }

        } else {
            this.remove(RemovalReason.DISCARDED);
        }

        super.tick();
    }

    public void constructBeamPoints(double lerpStrength) {
        if(this.shooter != null) {
            clearPoints();
            Vec3 currentDir = getShooterViewVector();

            Vec3 start;
            if(this.shooter instanceof Player) start = getShooterPos().subtract(0, 1, 0);
            else start = getShooterPos();
            start = start.add(currentDir.scale(1.0F));
            addPoint(start);

            Vec3 end;
            boolean hitBlock = false;
            int segments = 1;

            while(!hitBlock) {
                if(getTargetPos().lengthSqr() > 1e-6) {
                    Vec3 toTargetDir = getTargetPos().subtract(start).normalize();
                    currentDir = currentDir.lerp(toTargetDir, lerpStrength).normalize();
                }

                end = start.add(currentDir.scale(segmentLength));
                BlockHitResult hitResult = this.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.shooter));

                addPoint(end);
                segments++;

                if(segments >= getLaserDistance()) {
                    break;
                }

                start = hitResult.getLocation();

                if(hitResult.getType() == HitResult.Type.BLOCK) {
                    this.hitPoint = hitResult;
                    hitBlock = true;
                } else {
                    this.hitPoint = null;
                }
            }
        }

        if(isStatic()) {
            beamBuilt = true;
            if(this.level().isClientSide) {
                this.pointso.addAll(this.points);
            }
        }
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
                        var damageHolder = shooter.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(NDMDamageTypes.BEAM_DAMAGE);
                        DamageSource source = new DamageSource(damageHolder, shooter);
                        if(ent.hurt(source, damage)) {
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
        if(entity instanceof Player)
            this.justHit.add(entity);
    }

    private void clearJustHit() {
        this.justHit.clear();
    }

    private void setLifetime(int lifetime) {
        this.entityData.set(LIFETIME, lifetime);
    }

    public int getLifetime() {
        return this.entityData.get(LIFETIME);
    }

    private void setLaserDistance(int distance) {
        this.entityData.set(LASER_DISTANCE, distance);
    }

    private int getLaserDistance() {
        return this.entityData.get(LASER_DISTANCE);
    }

    private void setStatic(boolean isStatic) {
        this.entityData.set(STATIC, isStatic);
    }

    private boolean isStatic() {
        return this.entityData.get(STATIC);
    }

    private void setShooterPos(Vec3 shooterPos) {
        Vector3f v = AiUtil.toVec3f(shooterPos);
        this.entityData.set(SHOOTER_POS, v);
    }

    private Vec3 getShooterPos() {
        return AiUtil.toVec3(this.entityData.get(SHOOTER_POS));
    }

    private void setShooterViewVector(Vec3 viewAngle) {
        Vector3f v = AiUtil.toVec3f(viewAngle);
        this.entityData.set(SHOOTER_VIEW_ANGLE, v);
    }

    private Vec3 getShooterViewVector() {
        return AiUtil.toVec3(this.entityData.get(SHOOTER_VIEW_ANGLE));
    }

    private void setTargetPos(Vec3 targetPos) {
        Vector3f v = AiUtil.toVec3f(targetPos);
        this.entityData.set(TARGET_POS, v);
    }

    private Vec3 getTargetPos() {
        return AiUtil.toVec3(this.entityData.get(TARGET_POS));
    }

    private void setLerpStrength(float lerpStrength) {
        this.entityData.set(LERP_STRENGTH, lerpStrength);
    }

    private float getLerpStrength() {
        return this.entityData.get(LERP_STRENGTH);
    }

    private void saveVec3fData(CompoundTag tag, Vector3f v, String type) {
        tag.putFloat(type + "x", v.x);
        tag.putFloat(type + "y", v.y);
        tag.putFloat(type + "z", v.z);
    }

    private Vec3 loadVec3fData(CompoundTag tag, String type) {
        float x = tag.getFloat(type + "x");
        float y = tag.getFloat(type + "y");
        float z = tag.getFloat(type + "z");
        Vector3f vf= new Vector3f(x, y, z);
        return AiUtil.toVec3(vf);
    }
}
