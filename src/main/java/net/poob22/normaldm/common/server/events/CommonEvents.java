package net.poob22.normaldm.common.server.events;

import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.poob22.normaldm.common.server.blocks.blockentities.RoomControllerBlockEntity;

public class CommonEvents {
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load e) {
        if(!e.getLevel().isClientSide()) {
            RoomControllerBlockEntity.LOCKED_DOORS.clear();
        }
    }
}
