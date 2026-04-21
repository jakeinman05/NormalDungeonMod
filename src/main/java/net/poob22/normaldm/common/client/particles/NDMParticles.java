package net.poob22.normaldm.common.client.particles;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMParticles {
    public static final DeferredRegister<ParticleType<?>> DEF_REG = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<SimpleParticleType> BLOOD_POOL = DEF_REG.register("blood_pool", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> HURT_PARTICLE = DEF_REG.register("hurt_particle", () -> new SimpleParticleType(false));

    public static void register(IEventBus bus) {
        DEF_REG.register(bus);
    }
}
