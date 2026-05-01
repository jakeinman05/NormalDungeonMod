package net.poob22.normaldm.common.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.events.RoomBBRenderer;
import net.poob22.normaldm.common.server.blocks.blockentities.NDMBlockEntities;
import net.poob22.normaldm.common.server.blocks.blockentities.RoomControllerBlockEntity;
import net.poob22.normaldm.common.server.blocks.properties.RoomDefinitions;
import net.poob22.normaldm.common.server.items.DungeonWandItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RoomControllerBlock extends BaseEntityBlock {
    protected RoomControllerBlock() {
        super(BlockBehaviour.Properties.of().strength(1000.0f).sound(SoundType.DEEPSLATE));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new RoomControllerBlockEntity(pPos, pState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> entity) {
        return createTickerHelper(entity, NDMBlockEntities.ROOM_CONTROLLER.get(), RoomControllerBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if(level.isClientSide) {
            if(itemStack.getItem() instanceof DungeonWandItem && blockEntity instanceof RoomControllerBlockEntity roomController) {
                NormalDungeonMod.LOGGER.info("bam1");
                 if(!player.isShiftKeyDown()) {
                    NormalDungeonMod.LOGGER.info("bam3");
                    cycleRoomType(roomController);
                }
            } if(itemStack.isEmpty()) {
                if(player.isShiftKeyDown()) {
                    NormalDungeonMod.LOGGER.info("bam2");
                    RoomBBRenderer.toggle(pos);
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return InteractionResult.CONSUME;
    }

    public void cycleRoomType(RoomControllerBlockEntity rc) {
        int index = RoomDefinitions.ROOM_TYPES.indexOf(rc.RoomType);
        index = (index + 1) % RoomDefinitions.ROOM_TYPES.size();
        rc.RoomType = RoomDefinitions.ROOM_TYPES.get(index);
        NormalDungeonMod.LOGGER.info("Room Type: " + rc.RoomType.toString());
        //rc.initBounds();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }
}
