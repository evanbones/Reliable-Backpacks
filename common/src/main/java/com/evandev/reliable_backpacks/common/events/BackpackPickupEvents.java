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
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack chestSlotItem = player.getItemBySlot(EquipmentSlot.CHEST);

        boolean hasBackpack = chestSlotItem.is(BPItems.BACKPACK);
        boolean hasChestPlate = !chestSlotItem.isEmpty();
        boolean isAbove = (pos.above().getY() > player.getEyeY());
        boolean isUnobstructed = level.isUnobstructed(BPBlocks.BACKPACK.defaultBlockState(), pos.above(),
                CollisionContext.of(player)) && level.getBlockState(pos.above()).canBeReplaced();

        // PICKUP
        if (player.isShiftKeyDown() && !hasChestPlate && block == BPBlocks.BACKPACK && blockEntity != null) {
            player.swing(InteractionHand.MAIN_HAND);
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            CompoundTag nbt = blockEntity.saveWithoutMetadata();
            itemstack.addTagElement("BlockEntityTag", nbt);
            player.setItemSlot(EquipmentSlot.CHEST, itemstack);
            addParticles(level, pos);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // PLACEMENT
        if (player.isShiftKeyDown() && heldItem.isEmpty() && hasBackpack && hitResult.getDirection() == Direction.UP && !isAbove && isUnobstructed) {
            player.swing(InteractionHand.MAIN_HAND);

            BlockState state = BPBlocks.BACKPACK.defaultBlockState()
                    .setValue(FACING, player.getDirection())
                    .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER);

            blockEntity = new BackpackBlockEntity(pos.above(), state);
            CompoundTag nbt = chestSlotItem.getTagElement("BlockEntityTag");
            if (nbt != null) {
                blockEntity.load(nbt);
            }

            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos.above(), state);
                level.setBlockEntity(blockEntity);

                chestSlotItem.shrink(1);
                level.playSound(null, pos.above(), BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
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
            }
            return true;
        }
        return false;
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0);
        }
    }
}