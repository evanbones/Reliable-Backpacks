package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import io.wispforest.accessories.api.AccessoriesAPI;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.api.slot.SlotEntryReference;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class AccessoriesHelper {

    public static boolean canEquipBackpack(Player player) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get("back");
            if (container != null) {
                for (int i = 0; i < container.getSize(); i++) {
                    if (container.getAccessories().getItem(i).isEmpty()) {
                        SlotReference ref = SlotReference.of(player, "back", i);
                        if (AccessoriesAPI.canInsertIntoSlot(new ItemStack(BPItems.BACKPACK), ref)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean equipBackpack(Player player, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            AccessoriesContainer container = capability.getContainers().get("back");
            if (container != null) {
                for (int i = 0; i < container.getSize(); i++) {
                    if (container.getAccessories().getItem(i).isEmpty()) {
                        SlotReference ref = SlotReference.of(player, "back", i);
                        if (AccessoriesAPI.canInsertIntoSlot(stack, ref)) {
                            container.getAccessories().setItem(i, stack);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack getEquippedBackpack(LivingEntity livingEntity) {
        AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
        if (capability != null) {
            List<SlotEntryReference> equipped = capability.getEquipped(BPItems.BACKPACK);
            if (!equipped.isEmpty()) {
                return equipped.getFirst().stack();
            }
        }
        return ItemStack.EMPTY;
    }
}
