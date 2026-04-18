package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.Objects;

import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.FACING;
import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.WATERLOGGED;

public class BackpackPickupEvents {

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        ItemStack heldItem = player.getItemInHand(hand);

        boolean hasBackpack = Services.PLATFORM.isBackpackEquipped(player);
        boolean canEquip = Services.PLATFORM.canEquipBackpack(player);
        BlockPos targetPos = level.getBlockState(pos).canBeReplaced() ? pos : pos.relative(hitResult.getDirection());
        boolean isAbove = (targetPos.getY() > player.getEyeY());
        boolean isUnobstructed = !level.isOutsideBuildHeight(targetPos) &&
                level.isUnobstructed(BPBlocks.BACKPACK.defaultBlockState(), targetPos, CollisionContext.of(player)) &&
                level.getBlockState(targetPos).canBeReplaced();

        // PICKUP
        if (player.isShiftKeyDown() && canEquip && block == BPBlocks.BACKPACK && blockEntity != null) {
            player.swing(InteractionHand.MAIN_HAND);
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            itemstack.applyComponents(blockEntity.collectComponents());
            Services.PLATFORM.equipBackpack(player, itemstack);
            addParticles(level, pos);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // PLACEMENT
        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
        boolean handsEmpty = player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty();

        if (isMainHand && player.isShiftKeyDown() && handsEmpty && hasBackpack && (hitResult.getDirection() == Direction.UP || level.getBlockState(pos).canBeReplaced()) && !isAbove && isUnobstructed) {
            ItemStack backpackStack = Services.PLATFORM.getEquippedBackpack(player);

            if (!backpackStack.isEmpty()) {
                player.swing(InteractionHand.MAIN_HAND);

                BlockState state = BPBlocks.BACKPACK.defaultBlockState()
                        .setValue(FACING, player.getDirection())
                        .setValue(WATERLOGGED, level.getFluidState(targetPos).getType() == Fluids.WATER);

                blockEntity = new BackpackBlockEntity(targetPos, state);
                blockEntity.applyComponentsFromItemStack(backpackStack);

                if (!level.isClientSide) {
                    level.setBlockAndUpdate(targetPos, state);
                    level.setBlockEntity(blockEntity);

                    backpackStack.shrink(1);
                    level.playSound(null, targetPos, BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onRightClickItem(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public static void onItemEntityPickup(Player player, ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(itemStack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            if (Services.PLATFORM.canEquipBackpack(player) && !itemEntity.hasPickUpDelay()) {
                Services.PLATFORM.equipBackpack(player, itemStack.copy());
                itemStack.shrink(1);

                player.take(itemEntity, 1);
                itemEntity.discard();
                player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), 1);
                player.onItemPickup(itemEntity);
            }
        }
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
        }
    }
}