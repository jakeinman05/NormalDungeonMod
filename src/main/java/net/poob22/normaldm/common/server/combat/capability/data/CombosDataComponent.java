package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.NormalDungeonMod;

public class CombosDataComponent {
    /*
    Scale combo reset with attack cooldown
    Basically make a method that returns a calculated timer reset
    that allows players with slow punching to still gain combos
    Something like: playerCooldown + 20 (still has a second to land a punch)
    *Edit* Make the added time be less than the cooldown,
    so it's still like if the player swings and misses -> missed combo,
    but they have a short time frame to still pause and land consecutive hits.
    So maybe something like: playerCooldown + (playerCooldown/2)
     */
    private final int comboTimerReset = 30;
    private int combos;
    private long comboTimer = 0;

    public int getCombos() {
        return combos;
    }

    public void setCombos(int combos) {
        this.combos = combos;
    }

    public long getComboTimer() {
        return comboTimer;
    }

    public void setComboTimer() {
        this.comboTimer = comboTimerReset + Minecraft.getInstance().level.getGameTime();
    }

    public void incrementCombos() {
        combos++;
        NormalDungeonMod.LOGGER.info("Combos: " + combos);
        // reset timer for each combo increment
        comboTimer = comboTimerReset + Minecraft.getInstance().level.getGameTime();
    }

    public void resetCombos() {
        combos = 0;
    }

    public void tick(Player player) {
        /// Combo Logic
        if(combos > 0) {
            if(player.level().getGameTime() >= comboTimer) {
                NormalDungeonMod.LOGGER.info("Combos reset");
                resetCombos();
            }
        }
    }
}
