package net.poob22.normaldm.common.server.events;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.blockentities.RoomControllerBlockEntity;

import static net.poob22.normaldm.NormalDungeonMod.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class LockedInteractionHandler {
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event){
        Level level = event.getLevel();
        if(level.isClientSide) return;
        //NormalDungeonMod.LOGGER.info("Event fired");
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        if(state.getBlock() instanceof DoorBlock){
            //NormalDungeonMod.LOGGER.info("Is in fact a door");
            if(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER){
                //NormalDungeonMod.LOGGER.info("Door upper click registered");
                pos = pos.below();
            }
            //NormalDungeonMod.LOGGER.info("Pos is at: " + pos);
            //NormalDungeonMod.LOGGER.info("LOCKED_DOORS size: " + RoomControllerBlockEntity.LOCKED_DOORS.size());
            //NormalDungeonMod.LOGGER.info("LOCKED_DOORS contents: " + RoomControllerBlockEntity.LOCKED_DOORS);
            if(RoomControllerBlockEntity.LOCKED_DOORS.contains(pos)){
                //event.getEntity().sendSystemMessage(Component.literal("Door is Locked"));
                event.setCanceled(true);
                //NormalDungeonMod.LOGGER.info("Cancel Attempted");
                return;
            } else {
                //NormalDungeonMod.LOGGER.info("Door is in fact NOT in the set");
            }
        }
        //NormalDungeonMod.LOGGER.error("Cancel Failed");
    }
}
