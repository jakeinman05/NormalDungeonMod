package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ItemTitleRenderer {
    public Component title = null;
    public Component subtitle = null;

    public boolean isReady = false;
    public boolean isFinished = false;

    int age = 0;
    int titleTimer = 0;
    int scaleFactor = 1;


    public ItemTitleRenderer(Component title, Component subtitle, int timer, int scaleFactor) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleTimer = timer;
        if(scaleFactor > 0)
            this.scaleFactor = scaleFactor;
    }

    public ItemTitleRenderer(Component title, Component subtitle, int timer) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleTimer = timer;
    }

    public void tick() {
        age++;
    }

    public void renderText(float partialTicks, GuiGraphics gui) {
        Minecraft mc = Minecraft.getInstance();

        // do timing up here for both title and subtitle

        // render title
        if(title != null && titleTimer > 0 && !isFinished) {
            float renderAge = age + partialTicks;
            int opacity = 255;

            gui.pose().pushPose();
            gui.pose().scale(scaleFactor, scaleFactor, 1);

            int width = mc.font.width(title);
            gui.pose().translate((float) -width/2, 0, 0);

            gui.drawString(mc.font, title, gui.guiWidth()/(2 * scaleFactor), gui.guiHeight()/(5 * scaleFactor), 0XFFFFFF, true);

            gui.pose().popPose();
        }
        if(subtitle != null && titleTimer > 0 && !isFinished) {
            int s = scaleFactor/2;
            float renderAge = age + partialTicks;
            int opacity = 255;

            gui.pose().pushPose();
            gui.pose().scale(s, s, 1);

            int width = mc.font.width(subtitle);
            int height = mc.font.lineHeight;
            gui.pose().translate((float) -width/2, 0, 0);

            gui.drawString(mc.font, subtitle, gui.guiWidth()/(2 * s), gui.guiHeight()/(5 * s) + (mc.font.lineHeight * scaleFactor), 0XFFFFFF, true);

            gui.pose().popPose();
        }

        if(age >= titleTimer) {
            isFinished = true;
        }
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setReady() {
        isReady = true;
    }
}
