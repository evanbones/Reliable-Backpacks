package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/entity/item/ItemEntity;)Z", at = @At("HEAD"), cancellable = true)
    private static void reliable_backpacks$preventBackpackPickup(Container container, ItemEntity itemEntity, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = itemEntity.getItem();

        if (stack.is(BPItems.BACKPACK)) {
            ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
            if (contents != null && contents.nonEmptyItems().iterator().hasNext()) {
                cir.setReturnValue(false);
            }
        }
    }
}