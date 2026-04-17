package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.packet.BloodPoolPacket;
import net.poob22.normaldm.common.client.packet.PacketHandler;

public class DungeonMob extends Monster {
    SimpleParticleType particle;
    int deathParticleAmount;
    int hurtParticleAmount;

    protected DungeonMob(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public void setParticleType(SimpleParticleType pType) {
        this.particle = pType;
    }

    public SimpleParticleType getParticleType() {
        return this.particle;
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
            sendParticles(false);
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void tickDeath() {
        if(!this.level().isClientSide) {
            sendParticles(true);
            PacketHandler.sendToTracking(this, new BloodPoolPacket(this.getX(), this.getY(), this.getZ(), this.getBbWidth() * 1.5F));
        }
        this.remove(RemovalReason.KILLED);
    }

    private void sendParticles(boolean type) {
        AABB box = this.getBoundingBox();
        double offsetX = (box.maxX - box.minX) / 2;
        double offsetY = (box.maxY - box.minY) / 2;
        double offsetZ = (box.maxZ - box.minZ) / 2;
        int amount = type ? getDeathParticleAmount() : getHurtParticleAmount();
        ((ServerLevel)this.level()).sendParticles(getParticleType(), this.getX(), this.getY(), this.getZ(), amount, offsetX, offsetY, offsetZ, 0.1D);
    }
}
