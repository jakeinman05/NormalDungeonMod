package net.poob22.normaldm.common.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.blockentities.DungeonMobSpawner;
import net.poob22.normaldm.common.server.entity.living.DungeonMob;
import net.poob22.normaldm.common.server.items.DungeonMobSpawnEgg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DungeonMobSpawnerBlock extends BaseEntityBlock {
    public DungeonMobSpawnerBlock() {
        super(Properties.of().strength(100.0F).sound(SoundType.STONE));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new DungeonMobSpawner(pos, state);
    }

    @Override
    public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if(!level.isClientSide) {
            NormalDungeonMod.LOGGER.info("use attempt");
            BlockEntity blockEntity = level.getBlockEntity(pos);
            ItemStack handItem = player.getItemInHand(interactionHand);
            if(blockEntity instanceof DungeonMobSpawner spawner && !player.isShiftKeyDown()) {
                if(!handItem.isEmpty() && handItem.getItem() instanceof DungeonMobSpawnEgg egg) {
                    EntityType<DungeonMob> type = egg.getType();
                    NormalDungeonMod.LOGGER.info("type: " + type);
                    if(type != null) {
                        spawner.setMobToSpawn(type);
                        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_HIT_PLAYER, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.sendSystemMessage(Component.literal("Spawner has been set to spawn: " + type));
                    }
                } else if(handItem.isEmpty()) {
                    player.sendSystemMessage(Component.literal("Spawner has been set to spawn: " + spawner.getMobToSpawn()));
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.use(state, level, pos, player, interactionHand, blockHitResult);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return super.getShape(pState, pLevel, pPos, pContext);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }
}
