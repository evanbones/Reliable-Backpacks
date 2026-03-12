package com.evandev.reliable_backpacks.common.blocks;

import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackpackBlock extends BaseEntityBlock implements Equipable, EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING;
    public static final BooleanProperty FLOATING;
    public static final BooleanProperty WATERLOGGED;
    protected static final VoxelShape SHAPE_X;
    protected static final VoxelShape SHAPE_Z;
    protected static final VoxelShape FLOATING_SHAPE_X;
    protected static final VoxelShape FLOATING_SHAPE_Z;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        FLOATING = BooleanProperty.create("floating");
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SHAPE_X = Block.box(3.0, 0.0, 4.0, 13.0, 11.0, 12.0);
        SHAPE_Z = Block.box(4.0, 0.0, 3.0, 12.0, 11.0, 13.0);
        FLOATING_SHAPE_X = Block.box(3.0, 0.0, 4.0, 13.0, 8.0, 12.0);
        FLOATING_SHAPE_Z = Block.box(4.0, 0.0, 3.0, 12.0, 8.0, 13.0);
    }

    public BackpackBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FLOATING, false)
                .setValue(WATERLOGGED, false));
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(FLOATING, level.getFluidState(pos.below()).isSource() && !level.getFluidState(pos).isSource())
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    public @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(FLOATING, level.getFluidState(currentPos.below()).isSource());
    }

    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, BPBlockEntities.BACKPACK, BackpackBlockEntity::tick);
    }

    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public @NotNull SoundEvent getEquipSound() {
        return BPSounds.BACKPACK_EQUIP;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BackpackBlockEntity backpackBlockEntity) {
                player.openMenu(backpackBlockEntity);
                backpackBlockEntity.onOpen(player);
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.PASS;
            }
        }
    }

    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BackpackBlockEntity backpackBlockEntity) {
                Containers.dropContents(level, pos, backpackBlockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @NotNull
    public VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(FACING);
        if (state.getValue(FLOATING)) {
            return direction.getAxis() == Direction.Axis.X ? FLOATING_SHAPE_Z : FLOATING_SHAPE_X;
        } else {
            return direction.getAxis() == Direction.Axis.X ? SHAPE_Z : SHAPE_X;
        }

    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BackpackBlockEntity(pos, state);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FLOATING);
        builder.add(WATERLOGGED);
    }
}
