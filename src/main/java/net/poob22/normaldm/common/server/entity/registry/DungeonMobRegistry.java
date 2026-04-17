package net.poob22.normaldm.common.server.entity.registry;

import net.minecraft.world.entity.Mob;
import net.poob22.normaldm.common.server.entity.definition.DungeonMobDefinition;

import java.util.ArrayList;
import java.util.List;

public class DungeonMobRegistry {
    public static final List<DungeonMobDefinition<?>> MOBS = new ArrayList<>();

    public static <T extends Mob> DungeonMobDefinition<T> register(DungeonMobDefinition<T> d) {
        MOBS.add(d);
        return d;
    }
}
