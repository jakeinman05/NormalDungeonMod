package net.poob22.normaldm.common.server.structures;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

public class NDMStructurePlacementTypes {
    public static final DeferredRegister<StructurePlacementType<?>> REG = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, MODID);

    public static final RegistryObject<StructurePlacementType<FixedOriginPlacement>> FIXED_ORIGIN_PLACEMENT = REG.register("fixed_origin", () -> () -> FixedOriginPlacement.CODEC);
}
