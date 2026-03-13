package com.evandev.reliable_backpacks.common.blocks;

import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BackpackBlock extends BaseEntityBlock implements Equipable, EntityBlock, SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FLOATING = BooleanProperty.create("floating");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE_X = Block.box(3.0, 0.0, 4.0, 13.0, 11.0, 12.0);
    protected static final VoxelShape SHAPE_Z = Block.box(4.0, 0.0, 3.0, 12.0, 11.0, 13.0);
    protected static final VoxelShape FLOATING_SHAPE_X = Block.box(3.0, 0.0, 4.0, 13.0, 8.0, 12.0);
    protected static final VoxelShape FLOATING_SHAPE_Z = Block.box(4.0, 0.0, 3.0, 12.0, 8.0, 13.0);

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

    protected @NotNull BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state.setValue(FLOATING, level.getFluidState(currentPos.below()).isSource());
    }

    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, BPBlockEntities.BACKPACK, BackpackBlockEntity::tick);
    }

    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public @NotNull Holder<SoundEvent> getEquipSound() {
        return BPSounds.BACKPACK_EQUIP;
    }

    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
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

    protected void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            level.updateNeighbourForOutputSignal(pos, this);
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof BackpackBlockEntity backpack) {
            if (!level.isClientSide && player.isCreative() && !backpack.isEmpty()) {
                ItemStack itemStack = new ItemStack(this);
                itemStack.applyComponents(backpack.collectComponents());
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, itemStack);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);

        if (blockEntity instanceof BackpackBlockEntity backpack) {
            for (ItemStack drop : drops) {
                if (drop.is(this.asItem())) {
                    drop.applyComponents(backpack.collectComponents());
                }
            }
        }
        return drops;
    }

    protected @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
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

    public @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(FLOATING);
        builder.add(WATERLOGGED);
    }
}