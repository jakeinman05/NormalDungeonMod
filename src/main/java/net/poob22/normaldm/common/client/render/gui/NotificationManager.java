package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;

public class NotificationManager {
    public static ArrayList<ItemTitleRenderer> itemTitles = new ArrayList<>();

    public static void showItemTitles(float partialTicks, GuiGraphics gui) {
        for(ItemTitleRenderer itemTitleRenderer : itemTitles) {
            itemTitleRenderer.renderText(partialTicks, gui);
        }
    }

    public static boolean isInQueue(ItemTitleRenderer itemTitleRenderer) {
        return itemTitles.contains(itemTitleRenderer);
    }

    public static void addItemTitleRenderer(ItemTitleRenderer itemTitleRenderer) {
        itemTitles.add(itemTitleRenderer);
    }

    public static void tick() {
        itemTitles.forEach(ItemTitleRenderer::tick);
        itemTitles.removeIf(ItemTitleRenderer::isFinished);
    }
}
