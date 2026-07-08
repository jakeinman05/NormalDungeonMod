package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class CombosDataComponent extends SyncableComponent {
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
    private final int DEFAULT_TIMER_RESET = 10;
    private int combos;
    private float comboTimer = 0;

    public int getCombos() {
        return combos;
    }

    public float getComboTimer() {
        return comboTimer;
    }

    public void setComboTimer(Player player) {
        // calculate based on player stats
        if(player.getCapability(COMBAT).isPresent()) {
            player.getCapability(COMBAT).ifPresent(c -> {
                float attackSpeed = c.getStats().get(StatType.ATTACK_SPEED);
                float comboMultiplier = c.getStats().get(StatType.COMBO_MULTIPLIER);
                this.comboTimer = attackSpeed * comboMultiplier + (5 * comboMultiplier);
            });
        }
        else
            this.comboTimer = DEFAULT_TIMER_RESET;
    }

    public void setComboTimer(float timer) {
        // calculate based on player stats
        this.comboTimer = timer;
    }

    public void incrementCombos(Player player) {
        combos++;
        setComboTimer(player);

        if(!player.level().isClientSide()) {
            markDirty();
        }

        NormalDungeonMod.LOGGER.info("Combos: " + combos);
    }

    public void resetCombos(Player player) {
        combos = 0;

        if(!player.level().isClientSide()) {
            markDirty();
        }
    }

    /// Networking section


    public void sync(int combos, float timer) {
        this.combos = combos;
        if(!(combos == 0))
            this.setComboTimer(timer);
        else this.comboTimer = 0;
    }

    public void tick(Player player) {
        /// Combo Logic
        if(combos > 0) {
            if(comboTimer > 0) {
                comboTimer--;

                if(comboTimer <= 0) {
                    NormalDungeonMod.LOGGER.info("Combos reset");
                    resetCombos(player);
                }
            }
        }
    }
}
