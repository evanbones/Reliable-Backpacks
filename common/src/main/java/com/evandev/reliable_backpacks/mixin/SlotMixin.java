package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    public void mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot thisSlot = (Slot) (Object) this;

        if (thisSlot.container instanceof Inventory) {
            if (thisSlot.getContainerSlot() == 38) return;

            if (isNonEmptyBackpack(stack)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private boolean isNonEmptyBackpack(ItemStack stack) {
        if (!stack.is(BPItems.BACKPACK)) return false;

        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents == null) return false;

        return contents.nonEmptyItems().iterator().hasNext();
    }
}