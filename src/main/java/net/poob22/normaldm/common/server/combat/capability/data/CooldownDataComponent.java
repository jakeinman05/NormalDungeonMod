package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.world.entity.player.Player;

public class CooldownDataComponent extends SyncableComponent {
    private final float MAX_COOLDOWN = 40;
    float cooldown;

    public void setCooldown(float cooldown) {
        this.cooldown = Math.min(cooldown, MAX_COOLDOWN);
        this.markDirty();
    }

    public float getCooldown() {
        return cooldown;
    }

    public boolean noCooldown() {
        return cooldown <= 0;
    }

    public void sync(float cooldown) {
        this.cooldown = cooldown;
    }

    public void tick(Player player) {
        if(cooldown > 0) {
            //NormalDungeonMod.LOGGER.info("Cooldown: " + cooldown);
            cooldown--;
        } else {
            cooldown = 0;
        }
    }
}
