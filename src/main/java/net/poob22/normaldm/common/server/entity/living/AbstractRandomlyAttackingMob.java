package net.poob22.normaldm.common.server.entity.living;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractRandomlyAttackingMob extends DungeonMob {
    protected static final EntityDataAccessor<Boolean> ATTACKING = SynchedEntityData.defineId(AbstractRandomlyAttackingMob.class, EntityDataSerializers.BOOLEAN);
    public static final AnimationState AttackAnimationState = new AnimationState();

    protected int DEFAULT_ATTACK_INTERVAL;
    protected int DEFAULT_ATTACK_TICKS;

    protected int attackTicks;
    protected int attackInterval; // how long to wait before trying to attack again
    protected int attackOnTick;

    protected AbstractRandomlyAttackingMob(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACKING, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("attacking", this.entityData.get(ATTACKING));
        tag.putInt("attackTicks", this.attackTicks);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.entityData.set(ATTACKING, tag.getBoolean("attacking"));
        this.attackTicks = tag.getInt("attackTicks");
    }

    @Override
    public void tick() {
        if(getAttackInterval() > 0) setAttackInterval(getAttackInterval() - 1);

        if(isAttacking()) {
            setAttackTicks(getAttackTicks() - 1);

            if(getAttackTicks() <= 0) {
                setAttacking(false);
                setAttackTicks(DEFAULT_ATTACK_TICKS);
            }
        }

        super.tick();
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if(pId == 4) {
            AttackAnimationState.start(this.tickCount);
        }
        super.handleEntityEvent(pId);
    }

    public abstract void performAttack();

    public void setAttackTicks(int ticks) {
        this.attackTicks = ticks;
    }

    public int getAttackTicks() {
        return this.attackTicks;
    }

    public void setAttackOnTick(int tickToAttackOn) {
        this.attackOnTick = tickToAttackOn;
    }

    public boolean shouldPerformAttack() {
        return this.attackTicks > 0 && this.attackTicks % this.attackOnTick == 0;
    }

    public void resetAttackInterval() {
        this.attackInterval = DEFAULT_ATTACK_INTERVAL;
    }

    public void setAttackInterval(int interval) {
        this.attackInterval = interval;
    }

    public int getAttackInterval() {
        return this.attackInterval;
    }

    public void setDefaultAttackTicks(int ticks) {
        DEFAULT_ATTACK_TICKS = ticks;
    }

    public void setDefaultAttackInterval(int interval) {
        DEFAULT_ATTACK_INTERVAL = interval;
    }

    public boolean isAttacking() {
        return this.entityData.get(ATTACKING);
    }

    public void setAttacking(boolean attacking) {
        this.entityData.set(ATTACKING, attacking);
    }
}
