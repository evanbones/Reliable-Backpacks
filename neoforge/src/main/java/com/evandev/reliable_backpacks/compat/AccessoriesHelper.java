package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
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
            return capability.canEquipAccessory(new ItemStack(BPItems.BACKPACK), false) != null;
        }
        return false;
    }

    public static boolean equipBackpack(Player player, ItemStack stack) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            return capability.attemptToEquipAccessory(stack) != null;
        }
        return false;
    }

    public static boolean unequipBackpack(Player player) {
        AccessoriesCapability capability = AccessoriesCapability.get(player);
        if (capability != null) {
            List<SlotEntryReference> equipped = capability.getEquipped(BPItems.BACKPACK);
            if (!equipped.isEmpty()) {
                equipped.getFirst().reference().setStack(ItemStack.EMPTY);
                return true;
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

    public static boolean isBackpackVisible(LivingEntity livingEntity) {
        AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
        if (capability != null) {
            List<SlotEntryReference> equipped = capability.getEquipped(BPItems.BACKPACK);
            if (!equipped.isEmpty()) {
                SlotReference ref = equipped.getFirst().reference();
                AccessoriesContainer container = ref.slotContainer();
                if (container != null) {
                    return container.shouldRender(ref.slot());
                }
            }
        }
        return true;
    }
}
