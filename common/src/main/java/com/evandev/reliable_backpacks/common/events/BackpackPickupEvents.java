package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.Backpacks;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Objects;

import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.FACING;
import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.WATERLOGGED;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Backpacks.MODID)
public class BackpackPickupEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickBlock (PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        BlockPos pos = event.getPos();
        Block block = level.getBlockState(pos).getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);

        ItemStack heldItem = event.getItemStack();
        ItemStack chestSlotItem = player.getItemBySlot(EquipmentSlot.CHEST);

        boolean hasBackpack = chestSlotItem.is(BPItems.BACKPACK);
        boolean hasChestPlate = !chestSlotItem.isEmpty();
        boolean isAbove = (pos.above().getY() > player.getEyeY());
        boolean isUnobstructed = level.isUnobstructed(BPBlocks.BACKPACK.get().defaultBlockState(), pos.above(),
                CollisionContext.of(player)) && level.getBlockState(pos.above()).canBeReplaced();

        //PICKUP
        if (player.isShiftKeyDown() && !hasChestPlate && block == BPBlocks.BACKPACK.get() && blockEntity != null) {

            player.swing(InteractionHand.MAIN_HAND);
            ItemStack itemstack = new ItemStack(BPBlocks.BACKPACK);
            itemstack.applyComponents(blockEntity.collectComponents());
            player.setItemSlot(EquipmentSlot.CHEST, itemstack);
            addParticles(level, pos);

            if (!level.isClientSide) {
                level.removeBlockEntity(pos);
                level.removeBlock(pos, false);
            }
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }

        //PLACEMENT
        if (player.isShiftKeyDown() && heldItem.isEmpty() && hasBackpack && event.getFace() == Direction.UP && !isAbove && isUnobstructed) {

            player.swing(InteractionHand.MAIN_HAND);
            //player.swingingArm = InteractionHand.MAIN_HAND;


            BlockState state = BPBlocks.BACKPACK.get().defaultBlockState()
                    .setValue(FACING, player.getDirection())
                    .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER);

            blockEntity = new BackpackBlockEntity(pos.above(), state);
            blockEntity.applyComponentsFromItemStack(chestSlotItem);



            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos.above(), state);
                level.setBlockEntity(blockEntity);

                //((BackpackBlockEntity)blockEntity).updateColor();
                //blockEntity.getUpdateTag(level.registryAccess());

                chestSlotItem.shrink(1);
                level.playSound(null, pos.above(), BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS);
            }
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    //ARMOR SWAPPING
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Item item = event.getItemStack().getItem();
        EquipmentSlot slot = null;

        if (item instanceof ArmorItem) { slot = ((ArmorItem)item).getEquipmentSlot(); }
        if (item instanceof Equipable) { slot = ((Equipable)item).getEquipmentSlot(); }

        if (slot == EquipmentSlot.CHEST && event.getEntity().getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    //ITEM PICKUP
    @SubscribeEvent
    public static void  onItemEntityPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack itemStack = itemEntity.getItem();
        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(itemStack.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);

        if (itemStack.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
            Player player = event.getPlayer();
            if (player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() && !itemEntity.hasPickUpDelay()) {
                player.setItemSlot(EquipmentSlot.CHEST, itemStack);
                player.take(itemEntity, 1);
                itemEntity.discard();
                player.awardStat(Stats.ITEM_PICKED_UP.get(itemStack.getItem()), 1);
                player.onItemPickup(itemEntity);
            }
            event.setCanPickup(TriState.FALSE);
        }
    }

    private static void addParticles(Level level, BlockPos pos) {
        for (int i = 0; i < 4; i++) {
            level.addParticle(ParticleTypes.DUST_PLUME, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0,0);
        }
    }
}
