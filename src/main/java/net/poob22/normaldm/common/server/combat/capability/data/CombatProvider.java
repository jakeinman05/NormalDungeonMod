package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.poob22.normaldm.common.server.combat.capability.PlayerCombatCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombatProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {
    private final PlayerCombatCapability data = new PlayerCombatCapability();

    private final LazyOptional<PlayerCombatCapability> optional = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return optional.cast();
    }

    @Override
    public CompoundTag serializeNBT() {
        return data.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        data.deserializeNBT(tag);
    }
}
