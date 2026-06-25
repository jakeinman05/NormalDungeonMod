package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.NormalDungeonMod;

public class CooldownDataComponent {
    private final int MAX_COOLDOWN = 40;
    int cooldown;

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldown() {
        return cooldown;
    }

    public boolean noCooldown() {
        return cooldown <= 0;
    }

    public void tick(Player player) {
        if(cooldown > 0) {
            NormalDungeonMod.LOGGER.info("Cooldown: " + cooldown);
            cooldown--;
        }
    }
}
