package net.poob22.normaldm.common.server.combat.capability.data;

import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.items.stat_modifiers.StatItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryDataComponent {
    private final List<StatItem> inventory = new ArrayList<>();

    public void addItem(StatItem item) {
        inventory.add(item);
    }

    public StatItem removeItem(StatItem item) {
        if(inventory.contains(item)) {
            if(inventory.remove(item)) {
                return item;
            }
        }

        return null;
    }

    public StatItem getItem(StatItem item) {
        if(inventory.contains(item)) {
            return item;
        }
        NormalDungeonMod.LOGGER.warn("Item: {} not found in inventory", item);
        return null;
    }

    public List<StatItem> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public void clearInventory() {
        inventory.clear();
    }
}
