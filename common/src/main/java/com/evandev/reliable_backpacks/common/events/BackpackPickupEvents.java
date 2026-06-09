package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;

public class BackpackPickupEvents {

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        ItemStack chestSlotItem = Services.PLATFORM.getBackpack(player);

        boolean hasBackpack = !chestSlotItem.isEmpty();
        boolean canEquip = Services.PLATFORM.canEquipBackpack(player);

        // PICKUP
        if (player.isShiftKeyDown() && canEquip && !hasBackpack && block == BPBlocks.BACKPACK && blockEntity != null) {
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);

            if (blockEntity instanceof BackpackBlockEntity backpackEntity) {
                if (backpackEntity.getBackpackItemTag() != null) {
                    itemstack.setTag(backpackEntity.getBackpackItemTag().copy());
                }
            }

            CompoundTag nbt = blockEntity.saveWithoutMetadata();
            itemstack.addTagElement("BlockEntityTag", nbt);

            if (blockEntity instanceof BackpackBlockEntity backpackEntity && backpackEntity.getColor() != 0) {
                itemstack.getOrCreateTagElement("display").putInt("color", backpackEntity.getColor());
            }

            Services.PLATFORM.equipBackpack(player, itemstack);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
                level.playSound(null, pos, BPSounds.BACKPACK_EQUIP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // PLACEMENT
        boolean isMainHand = hand == InteractionHand.MAIN_HAND;
        boolean mainHandEmpty = player.getMainHandItem().isEmpty();

        if (isMainHand && player.isShiftKeyDown() && mainHandEmpty && hasBackpack && hitResult.getDirection() == Direction.UP) {
            if (!chestSlotItem.isEmpty()) {
                BlockPlaceContext context = new BlockPlaceContext(player, hand, chestSlotItem, hitResult);
                BPItems.BACKPACK.place(context);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onRightClickItem(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(BPItems.BACKPACK) && !Services.PLATFORM.getBackpack(player).isEmpty()) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static boolean onItemEntityPickup(Player player, ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.hasTag() && itemStack.getTag().contains("BlockEntityTag");
        boolean isEmpty = !hasContainer || !itemStack.getTag().getCompound("BlockEntityTag").contains("Items") || itemStack.getTag().getCompound("BlockEntityTag").getList("Items", 10).isEmpty();

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            if (Services.PLATFORM.canEquipBackpack(player) && !itemEntity.hasPickUpDelay()) {
                Services.PLATFORM.equipBackpack(player, itemStack.copy());
                itemStack.shrink(1);

                player.take(itemEntity, 1);
                itemEntity.discard();
                player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), 1);
                player.onItemPickup(itemEntity);

                player.level().playSound(null, player.blockPosition(), BPSounds.BACKPACK_EQUIP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            return true;
        }
        return false;
    }
}