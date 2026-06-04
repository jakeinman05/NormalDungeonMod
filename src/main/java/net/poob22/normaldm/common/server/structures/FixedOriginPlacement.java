package net.poob22.normaldm.common.server.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.poob22.normaldm.NormalDungeonMod;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FixedOriginPlacement extends StructurePlacement {
    public static final Codec<FixedOriginPlacement> CODEC = RecordCodecBuilder.create(instance -> placementCodec(instance).apply(instance, FixedOriginPlacement::new));

    @SuppressWarnings("deprecation")
    public FixedOriginPlacement(Vec3i pLocateOffset, FrequencyReductionMethod pFrequencyReductionMethod, float pFrequency, int pSalt, Optional<ExclusionZone> pExclusionZone) {
        super(pLocateOffset, pFrequencyReductionMethod, pFrequency, pSalt, pExclusionZone);
    }

    @Override
    public boolean isStructureChunk(@NotNull ChunkGeneratorStructureState pStructureState, int pX, int pZ) {
        return isPlacementChunk(pStructureState, pX, pZ);
    }

    @Override
    protected boolean isPlacementChunk(@NotNull ChunkGeneratorStructureState structureState, int x, int z) {
        return x == 0 && z == 0;
    }

    @Override
    public @NotNull StructurePlacementType<?> type() {
        return NDMStructurePlacementTypes.FIXED_ORIGIN_PLACEMENT.get();
    }
}
