package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.common.items.BackpackItemContainer;
import com.evandev.reliable_backpacks.registry.BPTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ShulkerBoxSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShulkerBoxSlot.class)
public abstract class ShulkerBoxSlotMixin extends Slot {

    public ShulkerBoxSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    public void mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (this.container instanceof BackpackBlockEntity || this.container instanceof BackpackItemContainer) {
            if (stack.is(BPTags.BACKPACK_BLACKLIST) || !stack.getItem().canFitInsideContainerItems()) {
                cir.setReturnValue(false);
            }
        }
    }
}