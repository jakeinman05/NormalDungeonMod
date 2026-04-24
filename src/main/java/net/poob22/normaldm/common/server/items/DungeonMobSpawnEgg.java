package net.poob22.normaldm.common.server.items;

import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;

import java.util.function.Supplier;

public class DungeonMobSpawnEgg extends ForgeSpawnEggItem {
    public DungeonMobSpawnEgg(Supplier<? extends EntityType<? extends DungeonMob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }
}
