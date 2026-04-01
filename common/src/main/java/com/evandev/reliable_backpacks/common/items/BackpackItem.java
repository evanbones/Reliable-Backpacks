package com.evandev.reliable_backpacks.common.items;

import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class BackpackItem extends BlockItem implements DyeableLeatherItem {
    public BackpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.CHEST;
    }

    public @NotNull SoundEvent getEquipSound() {
        return BPSounds.BACKPACK_EQUIP;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        EquipmentSlot equipmentSlot = EquipmentSlot.CHEST;
        ItemStack equipped = player.getItemBySlot(equipmentSlot);
        if (equipped.isEmpty()) {
            player.setItemSlot(equipmentSlot, itemStack.copy());
            itemStack.setCount(0);
            level.playSound(null, player.blockPosition(), BPSounds.BACKPACK_EQUIP, SoundSource.PLAYERS, 1.0F, 1.0F);
            return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
        }
        return InteractionResultHolder.pass(itemStack);
    }
}