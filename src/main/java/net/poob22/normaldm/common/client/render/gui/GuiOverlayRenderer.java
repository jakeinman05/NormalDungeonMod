package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.poob22.normaldm.common.server.combat.MainCombatHandler;

public class GuiOverlayRenderer {
    private static double GRAVITY_CONSTANT = 0.0784000015258789;

    public static void render(GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if(player != null) {
            gui.drawString(mc.font, "Combat Debug", 10, 126, 0XFFFFFF);
            gui.drawString(mc.font, "Speed: " + (player.onGround() ? (player.getDeltaMovement().length() - GRAVITY_CONSTANT) : player.getDeltaMovement().length()) + ((player.onGround() ? " onGround" : " In air")), 10, 136, 0XFFFFFF);
            gui.drawString(mc.font, "Reach: " + MainCombatHandler.calculateReach(player, player.getDeltaMovement()), 10, 146, 0XFFFFFF);
        }
    }
}
