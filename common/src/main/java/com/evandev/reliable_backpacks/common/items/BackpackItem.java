package com.evandev.reliable_backpacks.common.items;

import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class BackpackItem extends BlockItem implements Equipable {
    public BackpackItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public @NotNull Holder<SoundEvent> getEquipSound() {
        return BPSounds.BACKPACK_EQUIP;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (blockHitResult.getType() == HitResult.Type.BLOCK && level.getBlockState(blockHitResult.getBlockPos()).canBeReplaced()) {
            return InteractionResultHolder.pass(itemStack);
        }

        return this.swapWithEquipmentSlot(this, level, player, hand);
    }
}