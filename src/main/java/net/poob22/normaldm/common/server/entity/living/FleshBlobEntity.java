package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;

public class FleshBlobEntity extends DungeonMob {
    public static EntityDataAccessor<Integer> RESPAWN_TIMER = SynchedEntityData.defineId(FleshBlobEntity.class, EntityDataSerializers.INT);
    int startTimer;

    protected FleshBlobEntity(EntityType<? extends Monster> pEntityType, Level pLevel, int timerValue) {
        super(pEntityType, pLevel);
        this.startTimer = timerValue;
        this.setParticleType(ParticleTypes.CRIT);
        this.setHurtParticleAmount(4);
        this.setDeathParticleAmount(10);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RESPAWN_TIMER, this.startTimer);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("RespawnTimer", this.getRespawnTimer());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.setRespawnTimer(tag.getInt("RespawnTimer"));
    }

    private void setRespawnTimer(int timerValue) {
        this.entityData.set(RESPAWN_TIMER, timerValue);
    }

    private int getRespawnTimer() {
        return this.entityData.get(RESPAWN_TIMER);
    }

    @Override
    public void tick() {
        super.tick();

        if(getRespawnTimer() <= 0) {
            EntityType<?> type = NDMEntities.get("flesh_guy");
            if(!this.level().isClientSide) {
                FleshGuyEntity fleshGuy = (FleshGuyEntity) type.create(this.level());
                if(fleshGuy != null) {
                    //fleshGuy.setHealth(this.getHealth() * something);
                    fleshGuy.setPos(this.getX(), this.getY(), this.getZ());
                    this.level().addFreshEntity(fleshGuy);
                } else {
                    NormalDungeonMod.LOGGER.error("Entity to spawn is null!");
                }
            }
            this.kill();
        }
    }
}
