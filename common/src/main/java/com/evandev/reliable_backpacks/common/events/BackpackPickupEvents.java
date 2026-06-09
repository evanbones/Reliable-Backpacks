package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

import java.util.Objects;

public class BackpackPickupEvents {

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        boolean hasBackpack = Services.PLATFORM.isBackpackEquipped(player);
        boolean canEquip = Services.PLATFORM.canEquipBackpack(player);

        // PICKUP
        if (player.isShiftKeyDown() && canEquip && block == BPBlocks.BACKPACK && blockEntity != null) {
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
        boolean mainHandEmpty = player.getMainHandItem().isEmpty();

        if (isMainHand && player.isShiftKeyDown() && mainHandEmpty && hasBackpack && hitResult.getDirection() == Direction.UP) {
            ItemStack backpackStack = Services.PLATFORM.getEquippedBackpack(player);

            if (!backpackStack.isEmpty()) {
                if (!level.getBlockState(pos).useWithoutItem(level, player, hitResult).consumesAction()) {
                    BlockPlaceContext context = new BlockPlaceContext(player, hand, backpackStack, hitResult);
                    BPItems.BACKPACK.place(context);
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
