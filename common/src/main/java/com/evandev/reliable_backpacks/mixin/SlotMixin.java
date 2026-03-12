package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @Shadow
    public abstract ItemStack getItem();

    @Inject(
            method = "mayPickup",
            at = @At("HEAD"),
            cancellable = true
    )
    public void mayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        Slot thisSlot = (Slot) (Object) this;

        if (thisSlot.container instanceof Inventory && thisSlot.getContainerSlot() == 38) {
            ItemStack item = this.getItem();

            boolean hasContainer = item.hasTag() && item.getTag().contains("BlockEntityTag");
            boolean isEmpty = !hasContainer || !item.getTag().getCompound("BlockEntityTag").contains("Items") || item.getTag().getCompound("BlockEntityTag").getList("Items", 10).isEmpty();

            if (item.is(BPItems.BACKPACK) && hasContainer && !isEmpty) {
                cir.setReturnValue(false);
            }
        }
    }
}