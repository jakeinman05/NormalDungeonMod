package net.poob22.normaldm.common.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.server.blocks.properties.GateState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DungeonGateBlock extends Block {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<GateState> STATE = EnumProperty.create("state", GateState.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    protected static final VoxelShape NS_SHAPE = Block.box(0.0D, 0.0D, 5.0D, 16.0D, 16.0D, 11.0D);
    protected static final VoxelShape EW_SHAPE = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 16.0D, 16.0D);

    public DungeonGateBlock() {
        super(Properties.of().strength(100.0F).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(STATE, GateState.CLOSED).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(HALF, STATE, FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        GateState currentState = state.getValue(STATE);
        switch(currentState) {
//            case CLOSED: // switch to opening and consume
//                this.setGateState(level, pos, state, GateState.OPENING);
//                return InteractionResult.sidedSuccess(level.isClientSide);
//            case LOCKED: // just consume
//                if(level.isClientSide) {
//                    player.sendSystemMessage(Component.literal("Gate is Locked"));
//                }
//                return InteractionResult.CONSUME;
//            case OPENING:
//                return InteractionResult.CONSUME;
            case CLOSED:
                this.setGateState(level, pos, state, GateState.LOCKED);
                NormalDungeonMod.LOGGER.info("Gate is now " + this.getGateState(level, pos));
                return InteractionResult.CONSUME;
            case LOCKED:
                this.setGateState(level, pos, state, GateState.CLOSED);
                NormalDungeonMod.LOGGER.info("Gate is now " + this.getGateState(level, pos));
                return InteractionResult.CONSUME;

            default: return InteractionResult.PASS;
        }
    }

    public GateState getGateState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if(state.getBlock() instanceof DungeonGateBlock) {
            return state.getValue(STATE);
        }
        NormalDungeonMod.LOGGER.error("Pos at {} is attempting to return the GateState of a non-DungeonGate block", pos);
        return GateState.CLOSED;
    }

    public void setGateState(Level level, BlockPos pos, BlockState state, GateState newState) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos partnerPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();

        level.setBlock(pos, state.setValue(STATE, newState), 3);
        BlockState partnerState = level.getBlockState(partnerPos);
        if(partnerState.is(this)) {
            level.setBlock(partnerPos, partnerState.setValue(STATE, newState), 3);
        }

        if(newState == GateState.OPENING) {
            level.scheduleTick(pos, this, 20); // figure out what this does
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(FACING, state.getValue(FACING)), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if(state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos clickedPos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        if(clickedPos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(clickedPos.above()).canBeReplaced()) {
            return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(HALF, DoubleBlockHalf.LOWER);
        }
        return null;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if(!level.isClientSide) {
            DoubleBlockHalf doubleblockhalf = state.getValue(HALF);
            if (doubleblockhalf == DoubleBlockHalf.UPPER) {
                BlockPos blockpos = pos.below();
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(blockpos, blockstate1, 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
            if (doubleblockhalf == DoubleBlockHalf.LOWER) {
                BlockPos blockpos = pos.above();
                BlockState blockstate = level.getBlockState(blockpos);
                if (blockstate.is(state.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.UPPER) {
                    BlockState blockstate1 = blockstate.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                    level.setBlock(blockpos, blockstate1, 35);
                    level.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        GateState gateState = state.getValue(STATE);
        if(gateState == GateState.OPEN) {
            return Shapes.empty();
        }
        Direction direction = state.getValue(FACING);
        return (direction == Direction.NORTH || direction == Direction.SOUTH) ? NS_SHAPE : EW_SHAPE;
    }
}
