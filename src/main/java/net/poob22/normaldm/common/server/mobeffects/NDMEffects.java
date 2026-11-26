package net.poob22.normaldm.common.server.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> ACID = EFFECTS.register("acid", AcidEffect::new);
}
