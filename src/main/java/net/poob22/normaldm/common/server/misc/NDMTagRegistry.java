package net.poob22.normaldm.common.server.misc;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMTagRegistry {
    public static final TagKey<DamageType> BEAM_DAMAGE = registerDamageTypeTag("beam_damage");
    public static final TagKey<DamageType> DUNGEON_WEAPON = registerDamageTypeTag("dungeon_weapon");

    private static TagKey<DamageType> registerDamageTypeTag(String key) {
        return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, key));
    }
}
