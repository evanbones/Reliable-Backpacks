package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class TrinketsCompat {
    public static ItemStack getBackpack(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity)
                .flatMap(comp -> comp.getEquipped(BPItems.BACKPACK).stream().findFirst())
                .map(tuple -> tuple.getB())
                .orElse(ItemStack.EMPTY);
    }

    public static boolean canEquipBackpack(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).map(comp -> {
            var chestGroup = comp.getInventory().get("chest");
            if (chestGroup != null && chestGroup.get("back") != null) {
                var backSlot = chestGroup.get("back");
                for (int i = 0; i < backSlot.getContainerSize(); i++) {
                    if (backSlot.getItem(i).isEmpty()) return true;
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        return TrinketsApi.getTrinketComponent(entity).map(comp -> {
            var chestGroup = comp.getInventory().get("chest");
            if (chestGroup != null && chestGroup.get("back") != null) {
                var backSlot = chestGroup.get("back");
                for (int i = 0; i < backSlot.getContainerSize(); i++) {
                    if (backSlot.getItem(i).isEmpty()) {
                        backSlot.setItem(i, stack);
                        return true;
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean unequipBackpack(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity).map(comp -> {
            var equipped = comp.getEquipped(BPItems.BACKPACK);
            if (!equipped.isEmpty()) {
                var tuple = equipped.get(0);
                tuple.getA().inventory().setItem(tuple.getA().index(), ItemStack.EMPTY);
                return true;
            }
            return false;
        }).orElse(false);
    }
}