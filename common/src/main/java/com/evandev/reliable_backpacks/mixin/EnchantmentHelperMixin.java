package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "hasBindingCurse", at = @At("HEAD"), cancellable = true)
    private static void reliable_backpacks$hasBindingCurse(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(BPItems.BACKPACK)) {
            boolean hasContainer = stack.hasTag() && stack.getTag().contains("BlockEntityTag");
            if (hasContainer) {
                CompoundTag bet = stack.getTag().getCompound("BlockEntityTag");
                if (bet.contains("Items") && !bet.getList("Items", 10).isEmpty()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}