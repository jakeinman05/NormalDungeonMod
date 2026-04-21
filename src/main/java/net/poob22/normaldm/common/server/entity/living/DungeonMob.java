package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.common.client.packet.BloodPoolPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;
import net.poob22.normaldm.common.client.particles.NDMParticles;

public class DungeonMob extends Monster {
    public static EntityDataAccessor<Boolean> IN_DUNGEON = SynchedEntityData.defineId(DungeonMob.class, EntityDataSerializers.BOOLEAN);
    SimpleParticleType hurtParticle;
    SimpleParticleType deathParticle;
    int deathParticleAmount;
    int hurtParticleAmount;

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
        double offsetX = (box.maxX - box.minX) / 2;
        double offsetY = (box.maxY - box.minY) / 2;
        double offsetZ = (box.maxZ - box.minZ) / 2;
        SimpleParticleType particleType = type == 1 ? getDeathParticleType() : getHurtParticleType();
        int amount = type == 1 ? getDeathParticleAmount() : getHurtParticleAmount();
        ((ServerLevel)this.level()).sendParticles(particleType, this.getX(), this.getY(), this.getZ(), amount, offsetX, offsetY, offsetZ, 0.1D);
    }

    @Override
    public void checkDespawn() {
        if(this.isInDungeon()) {
            return;
        }
        super.checkDespawn();
    }
}
