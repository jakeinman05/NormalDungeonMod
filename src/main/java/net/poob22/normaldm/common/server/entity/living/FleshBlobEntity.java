package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.registry.NDMEntities;

public class FleshBlobEntity extends DungeonMob {
    public static EntityDataAccessor<Integer> RESPAWN_TIMER = SynchedEntityData.defineId(FleshBlobEntity.class, EntityDataSerializers.INT);
    public final AnimationState throbAnimation = new AnimationState();

    public FleshBlobEntity(EntityType<? extends FleshBlobEntity> entityType, Level world) {
        super(entityType, world);
        this.throbAnimation.start(this.tickCount);
        this.setParticleType(ParticleTypes.CRIT);
        this.setHurtParticleAmount(4);
        this.setDeathParticleAmount(10);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.KNOCKBACK_RESISTANCE, 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RESPAWN_TIMER, 120);
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

    public void setRespawnTimer(int timerValue) {
        this.entityData.set(RESPAWN_TIMER, timerValue);
    }

    private int getRespawnTimer() {
        return this.entityData.get(RESPAWN_TIMER);
    }

    @Override
    public void tick() {
        super.tick();

        if(getRespawnTimer() <= 0) {
            NormalDungeonMod.LOGGER.warn("Respawn timer hit 0");
            if(!this.level().isClientSide) {
                spawnFleshGuy();
            }
            this.remove(RemovalReason.DISCARDED);
        }
        NormalDungeonMod.LOGGER.info("Respawn timer: " + getRespawnTimer());
        this.setRespawnTimer(this.getRespawnTimer() - 1);
    }

    private void spawnFleshGuy() {
        EntityType<?> type = NDMEntities.get("flesh_guy");
        FleshGuyEntity fleshGuy = (FleshGuyEntity) type.create(this.level());
        if(fleshGuy != null) {
            fleshGuy.setHealth(Math.min(fleshGuy.getMaxHealth(), (fleshGuy.getMaxHealth()/3) + this.getHealth()));
            fleshGuy.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(fleshGuy);
        } else {
            NormalDungeonMod.LOGGER.error("Entity to spawn is null!");
        }
    }
}
