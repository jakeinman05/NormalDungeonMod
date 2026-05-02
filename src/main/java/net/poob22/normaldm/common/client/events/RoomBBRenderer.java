package net.poob22.normaldm.common.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.blockentities.RoomControllerBlockEntity;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RoomBBRenderer {
    private static final Set<BlockPos> DEBUG_ROOMS = new HashSet<>();

    public static void toggle(BlockPos pos) {
        if(!DEBUG_ROOMS.add(pos)) {
            DEBUG_ROOMS.remove(pos);
            NormalDungeonMod.LOGGER.info("Removed " + pos);
        } else {
            NormalDungeonMod.LOGGER.info("Added " + pos);
        }
    }

    @SubscribeEvent
    public static void onRender(RenderLevelStageEvent event) {
        if(event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        Level level = mc.level;
        if(level == null) {
            NormalDungeonMod.LOGGER.error("Level is null");
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(-camPos.x(), -camPos.y(), -camPos.z());

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer builder = buffer.getBuffer(RenderType.lines());

        for(BlockPos pos : DEBUG_ROOMS) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if(blockEntity instanceof RoomControllerBlockEntity roomController) {
                AABB box = roomController.getRoomBounds();
                AABB playerBox = roomController.getPlayerRoomBounds();
                if(box != null && playerBox != null) {
                    LevelRenderer.renderLineBox(poseStack, builder, box, 1.0f, 0.0f, 0.0f, 1.0f);
                    LevelRenderer.renderLineBox(poseStack, builder, playerBox, 0.0f, 0.0f, 1.0f, 1.0f);
                }
            }
        }

        poseStack.popPose();
        buffer.endBatch(RenderType.lines());
    }
}
