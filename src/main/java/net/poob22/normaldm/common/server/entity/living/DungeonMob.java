package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.client.packet.BloodPoolPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.particles.NDMParticles;

public class DungeonMob extends Monster {
    public static EntityDataAccessor<Boolean> IN_DUNGEON = SynchedEntityData.defineId(DungeonMob.class, EntityDataSerializers.BOOLEAN);
    SimpleParticleType hurtParticle;
    SimpleParticleType deathParticle;
    int deathParticleAmount;
    int hurtParticleAmount;

    /// Add an isFriendly property in case of enemies being friendly to players
    protected DungeonMob(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        setHurtParticleType(NDMParticles.HURT_PARTICLE.get());
        setDeathParticleType(NDMParticles.FLESH_PARTICLE.get());
        setHurtParticleAmount(10);
        setDeathParticleAmount(8);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IN_DUNGEON, false);
    }

    public static AttributeSupplier.Builder createDungeonMobAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 40.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public void setInDungeon(boolean inDungeon) {
        this.entityData.set(IN_DUNGEON, inDungeon);
    }

    public boolean isInDungeon() {
        return this.entityData.get(IN_DUNGEON);
    }

    public void setHurtParticleType(SimpleParticleType pType) {
        this.hurtParticle = pType;
    }

    public SimpleParticleType getHurtParticleType() {
        return this.hurtParticle;
    }

    public void setDeathParticleType(SimpleParticleType pType) {
        this.deathParticle = pType;
    }

    public SimpleParticleType getDeathParticleType() {
        return this.deathParticle;
    }

    public void setDeathParticleAmount(int deathParticleAmount) {
        this.deathParticleAmount = deathParticleAmount;
    }

    public int getDeathParticleAmount() {
        return this.deathParticleAmount > 0 ? this.deathParticleAmount : 10;
    }

    public void setHurtParticleAmount(int hurtParticleAmount) {
        if(hurtParticleAmount <= 0) {
            this.hurtParticleAmount = getDeathParticleAmount();
        } else {
            this.hurtParticleAmount = hurtParticleAmount;
        }
    }

    public int getHurtParticleAmount() {
        return this.hurtParticleAmount;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if(!this.level().isClientSide) {
            sendParticles((byte) 0);
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void tickDeath() {
        if(!this.level().isClientSide) {
            sendParticles((byte) 1);
            float sizeMultiplier = 1.3F + this.getRandom().nextInt(1);
            PacketHandler.sendToTracking(this, new BloodPoolPacket(this.getX(), this.getY(), this.getZ(), this.getBbWidth() * sizeMultiplier));
        }
        this.remove(RemovalReason.KILLED);
    }

    public void sendParticles(byte type) {
        AABB box = this.getBoundingBox();
        Vec3 centerBox = box.getCenter();

        SimpleParticleType particleType = type == 1 ? getDeathParticleType() : getHurtParticleType();
        int amount = type == 1 ? getDeathParticleAmount() : getHurtParticleAmount();

        RandomSource rand = this.level().getRandom();

        for(int i = 0; i < amount; i++) {
            double x = Mth.lerp(rand.nextDouble(), box.minX, box.maxX);
            double y = Mth.lerp(rand.nextDouble(), box.minY, box.maxY);
            double z = Mth.lerp(rand.nextDouble(), box.minZ, box.maxZ);

            Vec3 vec3 = new Vec3(x, y, z);
            Vec3 d = vec3.subtract(centerBox).normalize();

            if(d.lengthSqr() < 1e-6) {
                d = new Vec3(0, 1, 0);
            }

            double speed = 0.2 + rand.nextDouble() * 0.4;
            Vec3 v = d.scale(speed);
            v = v.add((rand.nextDouble() - 0.5) * 0.1, (rand.nextDouble() - 0.5) * 0.1, (rand.nextDouble() - 0.5) * 0.1);

            ((ServerLevel)this.level()).sendParticles(particleType, x, y, z, 0, v.x, v.y, v.z, 1.0D);
        }

    }

    @Override
    public int getExperienceReward() {
        return this.getRandom().nextInt(4) == 0 ? super.getExperienceReward() : 0;
    }

    @Override
    public void checkDespawn() {
        if(this.isInDungeon()) {
            return;
        }
        super.checkDespawn();
    }

    public void initializeSpawn() {}
}
