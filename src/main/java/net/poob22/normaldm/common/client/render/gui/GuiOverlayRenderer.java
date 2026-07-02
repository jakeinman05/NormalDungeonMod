package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.capability.data.StatsDataComponent;
import net.poob22.normaldm.common.server.combat.capability.data.stats.StatType;

import static net.poob22.normaldm.common.server.combat.CombatUtil.calculateReach;
import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT;

public class GuiOverlayRenderer {
    public static void render(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if(player != null) {
            player.getCapability(COMBAT).ifPresent(c -> {
                if(c.showDebug()) {
                    gui.drawString(mc.font, "Combat Debug", 10, 126, 0XFFFFFF);
                    gui.drawString(mc.font, String.format("Reach: %.2f", calculateReach(player, player.getDeltaMovement())), 10, 136, 0XFFFFFF);
                    gui.drawString(mc.font, "Capability Present: " + player.getCapability(COMBAT).isPresent(), 10, 146, 0XFFFFFF);

                    gui.drawString(mc.font, "Combos: " + c.getCombosData().getCombos(), 10, 156, 0XFFFFFF);
                    gui.drawString(mc.font, "Combo Timer: " + Math.max(0, c.getCombosData().getComboTimer()), 10, 166, 0XFFFFFF);
                    gui.drawString(mc.font, "Cooldown: " + c.getCooldownData().getCooldown(), 10, 176, 0XFFFFFF);

                    StatsDataComponent stats = c.getStats();
                    for(int i = 0; i < StatType.values().length; i++) {
                        gui.drawString(mc.font, StatType.values()[i].getSerializedName() + ": " + stats.getStat(StatType.values()[i]), 10, 186 + (i* 10), 0XFFFFFF);
                    }
                }
            });


        }
    }
}
