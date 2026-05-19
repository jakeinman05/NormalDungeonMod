package net.poob22.normaldm.common.server.entity.ai;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.apache.commons.lang3.RandomUtils;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public class AiUtil {
    public static boolean checkDamage(DungeonMob mob, LivingEntity target, double addReach) {
        if(mob.hasLineOfSight(target) && mob.distanceTo(target) <= (mob.getBbWidth() + addReach) + target.getBbWidth() + 0.1F) {
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

    public static void sendParticlesInBox(AABB box, SimpleParticleType particleType, int amount, ServerLevel level, RandomSource random) {
        Vec3 centerBox = box.getCenter();

        for(int i = 0; i < amount; i++) {
            double x = Mth.lerp(random.nextDouble(), box.minX, box.maxX);
            double y = Mth.lerp(random.nextDouble(), box.minY, box.maxY);
            double z = Mth.lerp(random.nextDouble(), box.minZ, box.maxZ);

            Vec3 vec3 = new Vec3(x, y, z);
            Vec3 d = vec3.subtract(centerBox).normalize();

            if(d.lengthSqr() < 1e-6) {
                d = new Vec3(0, 1, 0);
            }

            double speed = 0.2 + random.nextDouble() * 0.4;
            Vec3 v = d.scale(speed);
            v = v.add((random.nextDouble() - 0.5) * 0.1, (random.nextDouble() - 0.5) * 0.1, (random.nextDouble() - 0.5) * 0.1);

            level.sendParticles(particleType, x, y, z, 0, v.x, v.y, v.z, 1.0D);
        }
    }

    public static Vector3f toVec3f(Vec3 v) {
        return new Vector3f((float) v.x, (float) v.y, (float) v.z);
    }

    public static Vec3 toVec3(Vector3f v) {
        return new Vec3(v.x, v.y, v.z);
    }
}
