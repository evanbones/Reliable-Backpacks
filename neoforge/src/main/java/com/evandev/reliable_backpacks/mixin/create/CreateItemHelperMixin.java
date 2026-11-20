package com.evandev.reliable_backpacks.mixin.create;

import com.evandev.reliable_backpacks.common.items.BackpackItem;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemHelper.class, remap = false)
public abstract class CreateItemHelperMixin {

    @Inject(method = "fromItemEntity", at = @At("HEAD"), cancellable = true)
    private static void reliable_backpacks$ignoreFilledBackpacks(Entity entity, CallbackInfoReturnable<ItemStack> cir) {
        if (entity instanceof ItemEntity itemEntity && BackpackItem.hasContents(itemEntity.getItem())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
