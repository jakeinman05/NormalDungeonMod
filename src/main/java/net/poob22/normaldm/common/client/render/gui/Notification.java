package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.gui.GuiGraphics;

public abstract class Notification {
    int age = 0;
    boolean isFinished = false;

    public abstract void renderText(float partialTick, GuiGraphics gui);

    protected void tick() {
        age++;
    }

    protected void finish() {
        isFinished = true;
    }

    protected boolean isFinished() {
        return isFinished;
    }
}
