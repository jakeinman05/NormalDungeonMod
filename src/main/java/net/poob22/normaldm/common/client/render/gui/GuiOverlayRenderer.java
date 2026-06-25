package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static net.poob22.normaldm.common.server.combat.CombatUtil.calculateReach;
import static net.poob22.normaldm.common.server.combat.capability.CombatInternalCapabilities.COMBAT_DATA_CAPABILITY;

public class GuiOverlayRenderer {
    private static double GRAVITY_CONSTANT = 0.0784000015258789;

    public static void render(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if(player != null) {
            gui.drawString(mc.font, "Combat Debug", 10, 126, 0XFFFFFF);
            gui.drawString(mc.font, "Reach: " + calculateReach(player, player.getDeltaMovement()), 10, 136, 0XFFFFFF);
            gui.drawString(mc.font, "Capability Present: " + player.getCapability(COMBAT_DATA_CAPABILITY).isPresent(), 10, 146, 0XFFFFFF);

            AtomicInteger combos = new AtomicInteger();
            AtomicLong comboTimer = new AtomicLong();
            player.getCapability(COMBAT_DATA_CAPABILITY).ifPresent(c -> {
                combos.set(c.getCombosData().getCombos());
                comboTimer.set(c.getCombosData().getComboTimer());
            });
            if(comboTimer.get() - Minecraft.getInstance().level.getGameTime() <= 0) combos.set(0);

            gui.drawString(mc.font, "Combos: " + combos.get(), 10, 156, 0XFFFFFF);
            gui.drawString(mc.font, "Combo Timer: " + Math.max(0, (comboTimer.get() - Minecraft.getInstance().level.getGameTime())), 10, 166, 0XFFFFFF);
        }
    }
}
