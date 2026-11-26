package net.poob22.normaldm.common.client.render.state;

import net.minecraft.world.entity.Entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ClientEffectState {
    // add additional effect sets
    private static final Set<UUID> ACID_ENTITIES = new HashSet<>();

    public static void set(UUID id, boolean active) {
        if(active) {
            ACID_ENTITIES.add(id);
        } else {
            ACID_ENTITIES.remove(id);
        }
    }

    public static boolean hasAcid(Entity entity) {
        return ACID_ENTITIES.contains(entity.getUUID());
    }

    public static void removeFromAcid(Entity entity) {
        ACID_ENTITIES.remove(entity.getUUID());
    }

    public static void clearAcid() {
        ACID_ENTITIES.clear();
    }
}
