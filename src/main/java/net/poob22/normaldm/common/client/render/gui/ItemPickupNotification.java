package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.poob22.normaldm.NormalDungeonMod;

public class ItemPickupNotification extends Notification {
    public Component title = null;
    public Component subtitle = null;

    public boolean isReady = false;

    int titleTimer = 0;
    int scaleFactor = 1;
    int color = 0XFFFFFFFF;

    public ItemPickupNotification(Component title, Component subtitle, int age, int titleTimer, int color, int scaleFactor) {
        this.title = title;
        this.subtitle = subtitle;
        this.age = age;
        this.titleTimer = titleTimer;
        this.color = color;
        this.scaleFactor = scaleFactor;
    }

    public ItemPickupNotification(Component title, Component subtitle, int timer, int scaleFactor) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleTimer = timer;
        if(scaleFactor > 0)
            this.scaleFactor = scaleFactor;
    }

    public ItemPickupNotification(Component title, Component subtitle, int timer) {
        this.title = title;
        this.subtitle = subtitle;
        this.titleTimer = timer;
    }

    public void renderText(float partialTick, GuiGraphics gui) {
        if(isFinished) return;

        Minecraft mc = Minecraft.getInstance();

        if(age + partialTick >= titleTimer) {
            isFinished = true;
            NormalDungeonMod.LOGGER.info("Done rendering");
            return;
        }

        int fadeTime = 10;
        int renderColor = color;
        if(age >= (titleTimer - fadeTime)) {
            float prog = Mth.clamp(((float)titleTimer - (age + partialTick))/fadeTime, 0.0f, 1.0f);
            int alpha = (int)(255 * prog);
            if(alpha <= 5) {
                return;
            }
            renderColor = (alpha << 24) | (renderColor & 0x00FFFFFF);
        }

        // render title
        if(title != null && titleTimer > 0) {
            gui.pose().pushPose();
            gui.pose().scale(scaleFactor, scaleFactor, 1);

            int width = mc.font.width(title);
            gui.pose().translate((float) -width/2, 0, 0);

            gui.drawString(mc.font, title, gui.guiWidth()/(2 * scaleFactor), gui.guiHeight()/(5 * scaleFactor), renderColor, true);

            gui.pose().popPose();
        }

        if(subtitle != null && titleTimer > 0) {
            int s = scaleFactor/2;

            gui.pose().pushPose();
            gui.pose().scale(s, s, 1);

            int width = mc.font.width(subtitle);
            gui.pose().translate((float) -width/2, 0, 0);

            gui.drawString(mc.font, subtitle, gui.guiWidth()/(2 * s), gui.guiHeight()/(5 * s) + (mc.font.lineHeight * scaleFactor), renderColor, true);

            gui.pose().popPose();
        }
    }

    public void setReady() {
        isReady = true;
    }
}
