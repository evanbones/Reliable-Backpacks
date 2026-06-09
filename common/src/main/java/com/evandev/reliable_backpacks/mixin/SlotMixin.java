package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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

    @Inject(
            method = "mayPlace",
            at = @At("HEAD"),
            cancellable = true
    )
    public void mayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot thisSlot = (Slot) (Object) this;

        if (thisSlot.container instanceof Inventory) {
            int slotIndex = thisSlot.getContainerSlot();
            if (slotIndex < 36 || (slotIndex == 38 && backpacks$hasBackSlotMod()) || slotIndex == 40) {
                if (backpacks$isNonEmptyBackpack(stack)) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    public void mayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        Slot thisSlot = (Slot) (Object) this;
        if (thisSlot.container instanceof Inventory) {
            int slotIndex = thisSlot.getContainerSlot();
            if (slotIndex < 36 || slotIndex == 40) {
                if (backpacks$isNonEmptyBackpack(thisSlot.getItem())) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Unique
    private static boolean backpacks$isNonEmptyBackpack(ItemStack stack) {
        if (!stack.is(BPItems.BACKPACK)) return false;

        boolean hasContainer = stack.hasTag() && stack.getTag().contains("BlockEntityTag");
        if (!hasContainer) return false;

        CompoundTag bet = stack.getTag().getCompound("BlockEntityTag");
        return bet.contains("Items") && !bet.getList("Items", 10).isEmpty();
    }

    @Unique
    private static boolean backpacks$hasBackSlotMod() {
        ModConfig config = ModConfig.get();
        return (config.enableCuriosIntegration && Services.PLATFORM.isModLoaded("curios"))
                || (config.enableTrinketsIntegration && Services.PLATFORM.isModLoaded("trinkets"));
    }
}