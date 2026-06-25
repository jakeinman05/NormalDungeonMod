package net.poob22.normaldm.common.server.combat.capability.data;

import net.minecraft.world.entity.player.Player;

public class PlayerCombatData {
    private final CombosDataComponent combos = new CombosDataComponent();
    private final CooldownDataComponent cooldown = new CooldownDataComponent();

    public CombosDataComponent getCombosData() {
        return combos;
    }

    public CooldownDataComponent getCooldownData() {
        return cooldown;
    }

    public void syncCombosData(int combos) {
        getCombosData().setCombos(combos);
        getCombosData().setComboTimer();
    }


    public void tick(Player player) {
        combos.tick(player);
        cooldown.tick(player);
    }
}
