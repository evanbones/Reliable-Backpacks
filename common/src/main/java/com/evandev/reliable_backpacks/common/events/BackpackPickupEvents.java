package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;

import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.FACING;
import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.WATERLOGGED;

public class BackpackPickupEvents {

    public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState clickedState = level.getBlockState(pos);
        Block block = clickedState.getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack chestSlotItem = player.getItemBySlot(EquipmentSlot.CHEST);

        boolean hasBackpack = chestSlotItem.is(BPItems.BACKPACK);
        boolean hasChestPlate = !chestSlotItem.isEmpty();

        BlockPos targetPos = clickedState.canBeReplaced() ? pos : pos.relative(hitResult.getDirection());
        boolean isAbove = (targetPos.getY() > player.getEyeY());
        boolean isUnobstructed = level.isUnobstructed(BPBlocks.BACKPACK.defaultBlockState(), targetPos,
                CollisionContext.of(player)) && level.getBlockState(targetPos).canBeReplaced();

        // PICKUP
        if (player.isShiftKeyDown() && !hasChestPlate && block == BPBlocks.BACKPACK && blockEntity != null) {
            player.swing(InteractionHand.MAIN_HAND);
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            CompoundTag nbt = blockEntity.saveWithoutMetadata();
            itemstack.addTagElement("BlockEntityTag", nbt);

            if (blockEntity instanceof BackpackBlockEntity backpackEntity && backpackEntity.getColor() != 0) {
                itemstack.getOrCreateTagElement("display").putInt("color", backpackEntity.getColor());
            }

            player.setItemSlot(EquipmentSlot.CHEST, itemstack);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
                level.playSound(null, pos, BPSounds.BACKPACK_EQUIP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // PLACEMENT
        if (player.isShiftKeyDown() && heldItem.isEmpty() && hasBackpack && (hitResult.getDirection() == Direction.UP || clickedState.canBeReplaced()) && !isAbove && isUnobstructed) {
            player.swing(InteractionHand.MAIN_HAND);

            BlockState state = BPBlocks.BACKPACK.defaultBlockState()
                    .setValue(FACING, player.getDirection())
                    .setValue(WATERLOGGED, level.getFluidState(targetPos).getType() == Fluids.WATER);

            BackpackBlockEntity newBlockEntity = new BackpackBlockEntity(targetPos, state);
            CompoundTag nbt = chestSlotItem.getTagElement("BlockEntityTag");
            if (nbt != null) {
                newBlockEntity.load(nbt);
            }

            CompoundTag displayTag = chestSlotItem.getTagElement("display");
            if (displayTag != null && displayTag.contains("color", 99)) {
                newBlockEntity.setColor(displayTag.getInt("color"));
            }

            newBlockEntity.newlyPlaced = true;
            newBlockEntity.placeTicks = 0;

            if (!level.isClientSide) {
                level.setBlockAndUpdate(targetPos, state);
                level.setBlockEntity(newBlockEntity);

                chestSlotItem.shrink(1);
                level.playSound(null, targetPos, BPSounds.BACKPACK_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
                for (int i = 0; i < 4; i++) {
                    level.addParticle(ParticleTypes.CLOUD, targetPos.getX() + 0.5D, targetPos.getY() + 0.2D, targetPos.getZ() + 0.5D, 0.0D, 0.02D, 0.0D);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static InteractionResult onRightClickItem(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        EquipmentSlot slot = null;

        if (item instanceof ArmorItem) {
            slot = ((ArmorItem) item).getEquipmentSlot();
        }
        if (item instanceof Equipable) {
            slot = ((Equipable) item).getEquipmentSlot();
        }

        if (slot == EquipmentSlot.CHEST && player.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            return InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    public static boolean onItemEntityPickup(Player player, ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.hasTag() && itemStack.getTag().contains("BlockEntityTag");
        boolean isEmpty = !hasContainer || !itemStack.getTag().getCompound("BlockEntityTag").contains("Items") || itemStack.getTag().getCompound("BlockEntityTag").getList("Items", 10).isEmpty();

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && !itemEntity.hasPickUpDelay()) {
                player.setItemSlot(EquipmentSlot.CHEST, itemStack);
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