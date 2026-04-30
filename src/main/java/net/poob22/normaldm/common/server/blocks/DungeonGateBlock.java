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
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.poob22.normaldm.NormalDungeonMod;
import net.poob22.normaldm.common.client.particles.NDMParticles;
import net.poob22.normaldm.common.server.blocks.properties.GateState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DungeonGateBlock extends Block {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<GateState> STATE = EnumProperty.create("state", GateState.class);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final IntegerProperty FRAMES = IntegerProperty.create("frames", 0, 6);
    public static final BooleanProperty HAS_BEEN_OPENED = BooleanProperty.create("opened");

    protected static final VoxelShape NS_SHAPE = Block.box(0.0D, 0.0D, 5.0D, 16.0D, 16.0D, 11.0D);
    protected static final VoxelShape EW_SHAPE = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 16.0D, 16.0D);

    public DungeonGateBlock() {
        super(Properties.of().strength(100.0F).sound(SoundType.WOOD));
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER).setValue(STATE, GateState.CLOSED).setValue(FACING, Direction.NORTH).setValue(FRAMES, 0).setValue(HAS_BEEN_OPENED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(HALF, STATE, FACING, FRAMES, HAS_BEEN_OPENED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        GateState currentState = state.getValue(STATE);
        return switch (currentState) {
            case CLOSED -> {
                this.setGateState(level, pos, state, GateState.OPENING);
                yield InteractionResult.sidedSuccess(level.isClientSide);
            }
            case LOCKED -> {
                if (level.isClientSide) {
                    player.sendSystemMessage(Component.literal("Gate is Locked"));
                }
                yield InteractionResult.CONSUME;
            }
            case OPENING -> InteractionResult.CONSUME;
            default -> InteractionResult.PASS;
        };
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if(state.getValue(STATE) == GateState.OPENING) {
            int currentFrame = state.getValue(FRAMES);
            int[] times = {10, 7, 5, 2, 2, 2, 40};

            if(currentFrame < 6) {
                int nextFrame = currentFrame + 1;
                updateAnimationState(level, pos, state, state.setValue(FRAMES, nextFrame));
                level.scheduleTick(pos, this, times[nextFrame]);

            } else {
                spawnDestroyParticles(level, state, pos, random);
                updateAnimationState(level, pos, state, state.setValue(FRAMES, 0).setValue(STATE, GateState.OPEN));
            }
        } else if(state.getValue(FRAMES) != 0) {
            updateAnimationState(level, pos, state, state.setValue(FRAMES, 0));
        }

        super.tick(state, level, pos, random);
    }

    ///  UTIL METHODS

    public GateState getGateState(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if(state.getBlock() instanceof DungeonGateBlock) {
            return state.getValue(STATE);
        }
        NormalDungeonMod.LOGGER.error("Pos at {} is attempting to return the GateState of a non-DungeonGate block", pos);
        return GateState.CLOSED;
    }

    public void setGateState(Level level, BlockPos pos, BlockState state, GateState gateState) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos partnerPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        boolean opened = gateState == GateState.OPENING;
        BlockState newState = (opened && !state.getValue(HAS_BEEN_OPENED)) ? state.setValue(STATE, gateState).setValue(HAS_BEEN_OPENED, true) : state.setValue(STATE, gateState);

        level.setBlock(pos, newState, 3);
        BlockState partnerState = level.getBlockState(partnerPos);
        BlockState newPartnerState = (opened && !partnerState.getValue(HAS_BEEN_OPENED)) ? partnerState.setValue(STATE, gateState).setValue(HAS_BEEN_OPENED, true) : partnerState.setValue(STATE, gateState);
        if(partnerState.is(this)) {
            level.setBlock(partnerPos, newPartnerState, 3);
        }
        if(gateState == GateState.OPENING) {
            level.scheduleTick(pos, this, 10);
        }
        if(state.getValue(STATE) == GateState.LOCKED && gateState == GateState.CLOSED && getHasBeenOpened(state)) {
            setGateState(level, pos, state, GateState.OPENING);
        }
    }

    public boolean getHasBeenOpened(BlockState state) {
        return state.getValue(HAS_BEEN_OPENED);
    }

    public static boolean isLowerHalf(BlockState state) {
        return state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    private void updateAnimationState(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        DoubleBlockHalf half = newState.getValue(HALF);
        BlockPos partnerPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState partnerState = level.getBlockState(partnerPos);

        level.setBlock(pos, newState, 3);
        level.sendBlockUpdated(pos, oldState, newState, 3);
        if(partnerState.is(this)) {
            level.setBlock(partnerPos, partnerState.setValue(STATE, newState.getValue(STATE)).setValue(FRAMES, newState.getValue(FRAMES)), 3);
            level.sendBlockUpdated(pos, oldState, newState, 3);
        }
    }

    protected void spawnDestroyParticles(ServerLevel level, BlockState state, BlockPos pos, RandomSource random) {
        VoxelShape voxelShape = this.getVoxelShape(state);
        int yShift = (state.getValue(HALF) == DoubleBlockHalf.UPPER) ? 1 : -1;
        double xCenter = pos.getX() + voxelShape.bounds().getCenter().x;
        double yCenter = pos.getY() + voxelShape.bounds().getCenter().y;
        double zCenter = pos.getZ() + voxelShape.bounds().getCenter().z;
        NormalDungeonMod.LOGGER.info("{}, {}, {}", xCenter, yCenter, zCenter);
        level.sendParticles(NDMParticles.FLESH_PARTICLE.get(),
                xCenter, yCenter, zCenter,
                18,
                (random.nextDouble() - 0.5) * 0.8,
                (random.nextDouble() - 0.5) * 0.8,
                (random.nextDouble() - 0.5) * 0.8,
                1.0D);
        level.sendParticles(NDMParticles.FLESH_PARTICLE.get(),
                xCenter, yCenter - yShift, zCenter,
                18,
                (random.nextDouble() - 0.5) * 0.8,
                (random.nextDouble() - 0.5) * 0.8,
                (random.nextDouble() - 0.5) * 0.8,
                1.0D);
    }

    /// BLOCK OVERRIDES

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

    private VoxelShape getVoxelShape(BlockState state) {
        GateState gateState = state.getValue(STATE);
        if(gateState == GateState.OPEN) {
            return Shapes.empty();
        }
        Direction direction = state.getValue(FACING);
        return (direction == Direction.NORTH || direction == Direction.SOUTH) ? NS_SHAPE : EW_SHAPE;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        return getVoxelShape(state);
    }
}
