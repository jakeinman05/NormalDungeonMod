package net.poob22.normaldm.common.server.items;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class DungeonMobSpawnEgg extends ForgeSpawnEggItem {

    public DungeonMobSpawnEgg(Supplier<? extends EntityType<? extends DungeonMob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @SuppressWarnings("unchecked")
    public EntityType<DungeonMob> getType() {
        return (EntityType<DungeonMob>) this.getDefaultType();
    }
}
