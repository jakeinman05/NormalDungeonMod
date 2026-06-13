package net.poob22.normaldm.common.server.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CombatEvents {
    @SubscribeEvent
    public static void combatPunch(PlayerInteractEvent.LeftClickEmpty event) {
        Level eventLevel = event.getLevel();
        Player player = event.getEntity();
    }
}
