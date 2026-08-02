package net.poob22.normaldm.common.client.render.gui;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayDeque;
import java.util.Queue;

public class NotificationManager {
    //public static ArrayList<Notification> notifications = new ArrayList<>();
    public static Queue<Notification> notifications = new ArrayDeque<>();

    public static void displayFirstNotification(float partialTicks, GuiGraphics gui) {
//        for(Notification notification : notifications) {
//            notification.renderText(partialTicks, gui);
//        }
        if(notifications.isEmpty()) return;

        notifications.peek().renderText(partialTicks, gui);

    }

    public static boolean isInQueue(ItemPickupNotification itemPickupNotification) {
        return notifications.contains(itemPickupNotification);
    }

    public static void addItemPickupNotification(ItemPickupNotification itemPickupNotification) {
        notifications.offer(itemPickupNotification);
    }

    public static void tick() {
        if(notifications.isEmpty()) return;

        notifications.peek().tick();

        if(notifications.peek().isFinished()) {
            notifications.poll();
        }
    }
}
