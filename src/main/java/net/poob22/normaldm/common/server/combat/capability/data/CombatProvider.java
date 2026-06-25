package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CombatProvider implements ICapabilityProvider {
    private final PlayerCombatData data = new PlayerCombatData();

    private final LazyOptional<PlayerCombatData> optional = LazyOptional.of(() -> data);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return optional.cast();
    }
}
