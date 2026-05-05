package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Objects;

public class AiUtil {
    public static boolean checkDamage(DungeonMob mob, LivingEntity target) {
        if(mob.hasLineOfSight(target) && mob.distanceTo(target) <= (mob.getBbWidth() + 0.3F) + target.getBbWidth() + 0.0F) {
            return target.hurt(target.damageSources().mobAttack(mob), (float) Objects.requireNonNull(mob.getAttribute(Attributes.ATTACK_DAMAGE)).getValue());
        }
        return false;
    }

    public static void spawnEnemiesOnDeath(DungeonMob mob, List<EntityType<? extends DungeonMob>> toSpawn, boolean randomOffset) {
        if(!(mob.level() instanceof ServerLevel level)) return;

        for(EntityType<? extends DungeonMob> spawn : toSpawn) {
            Vec3 position = mob.position();

            if(randomOffset) {
                double offsetX = (level.random.nextDouble() - 0.5) * 2;
                double offsetZ = (level.random.nextDouble() - 0.5) * 2;

                position = position.add(offsetX, 0, offsetZ);
            }

            DungeonMob dungeonMob = spawn.create(level);
            if(dungeonMob != null) {
                dungeonMob.setPos(position.x, position.y, position.z);
                level.addFreshEntity(dungeonMob);
            }
        }
    }

    public static void spawnRandomEnemiesOnDeath(DungeonMob mob, List<EntityType<? extends DungeonMob>> toSpawn, int count, boolean randomOffset) {
        if(!(mob.level() instanceof ServerLevel level)) return;

        Vec3 position = mob.position();

        for(int i = 0; i < count; i++) {
            if(randomOffset) {
                double offsetX = (level.random.nextDouble() - 0.5) * 2;
                double offsetZ = (level.random.nextDouble() - 0.5) * 2;

                position = position.add(offsetX, 0, offsetZ);
            }

            int index = RandomUtils.nextInt(0, toSpawn.size());
            EntityType<? extends DungeonMob> spawn = toSpawn.get(index);

            DungeonMob dungeonMob = spawn.create(mob.level());
            if(dungeonMob != null) {
                dungeonMob.setPos(position.x, position.y, position.z);
                mob.level().addFreshEntity(dungeonMob);
            }
        }
    }
}
