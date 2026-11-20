package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotMixin extends Slot {

    public ArmorSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
    private void reliable_backpacks$onlyOneBackpack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(BPItems.BACKPACK) || !(this.container instanceof Inventory inventory)) return;

        ItemStack equipped = Services.PLATFORM.getEquippedBackpack(inventory.player);
        if (!equipped.isEmpty() && equipped != this.getItem()) {
            cir.setReturnValue(false);
        }
    }
}
