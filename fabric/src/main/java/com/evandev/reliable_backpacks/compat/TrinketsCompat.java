package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class TrinketsCompat {
    public static ItemStack getBackpack(LivingEntity entity) {
        return TrinketsApi.getTrinketComponent(entity)
                .flatMap(comp -> comp.getEquipped(BPItems.BACKPACK).stream().findFirst())
                .map(tuple -> tuple.getB())
                .orElse(ItemStack.EMPTY);
    }

    public static boolean canEquipBackpack(LivingEntity entity) {
        ItemStack backpackStack = new ItemStack(BPItems.BACKPACK);
        return TrinketsApi.getTrinketComponent(entity).map(comp -> {
            for (Map<String, TrinketInventory> group : comp.getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        SlotReference ref = new SlotReference(inventory, i);

                        if (TrinketsApi.evaluatePredicateSet(inventory.getSlotType().getValidatorPredicates(), backpackStack, ref, entity)) {
                            if (inventory.getItem(i).isEmpty()) return true;
                        }
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        return TrinketsApi.getTrinketComponent(entity).map(comp -> {
            for (Map<String, TrinketInventory> group : comp.getInventory().values()) {
                for (TrinketInventory inventory : group.values()) {
                    for (int i = 0; i < inventory.getContainerSize(); i++) {
                        SlotReference ref = new SlotReference(inventory, i);

                        if (TrinketsApi.evaluatePredicateSet(inventory.getSlotType().getValidatorPredicates(), stack, ref, entity)) {
                            if (inventory.getItem(i).isEmpty()) {
                                inventory.setItem(i, stack);
                                return true;
                            }
                        }
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