package net.poob22.normaldm.common.server.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMDamageTypes {
    public static final ResourceKey<DamageType> BEAM_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, "beam_damage"));
}