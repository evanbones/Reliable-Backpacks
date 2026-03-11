package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotMixin extends Slot {
    public ArmorSlotMixin(Container container, int slot, int x, int y) {
        super(container, slot, x, y);
    }

    @Inject(
            method = "mayPickup",
            at = @At("HEAD"),
            cancellable = true
    )
    public void mayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack item = this.getItem();
        boolean hasContainer = item.has(DataComponents.CONTAINER);
        boolean isEmpty = Objects.equals(item.get(DataComponents.CONTAINER), ItemContainerContents.EMPTY);
        if (item.is(BPItems.BACKPACK) && hasContainer && !isEmpty) { cir.setReturnValue(false); }
    }
}
