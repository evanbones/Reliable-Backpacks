package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class TrinketsHelper {

    public static boolean canEquipBackpack(Player player) {
        Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(player);
        if (comp.isPresent()) {
            var group = comp.get().getInventory().get("chest");
            if (group != null && group.get("back") != null) {
                var slot = group.get("back");
                for (int i = 0; i < slot.getContainerSize(); i++) {
                    if (slot.getItem(i).isEmpty()) return true;
                }
            }
        }
        return false;
    }

    public static boolean equipBackpack(Player player, ItemStack stack) {
        Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(player);
        if (comp.isPresent()) {
            var group = comp.get().getInventory().get("chest");
            if (group != null && group.get("back") != null) {
                var slot = group.get("back");
                for (int i = 0; i < slot.getContainerSize(); i++) {
                    if (slot.getItem(i).isEmpty()) {
                        slot.setItem(i, stack);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean unequipBackpack(Player player) {
        Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(player);
        if (comp.isPresent()) {
            var group = comp.get().getInventory().get("chest");
            if (group != null && group.get("back") != null) {
                var slot = group.get("back");
                for (int i = 0; i < slot.getContainerSize(); i++) {
                    if (slot.getItem(i).is(BPItems.BACKPACK)) {
                        slot.setItem(i, ItemStack.EMPTY);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static ItemStack getEquippedBackpack(LivingEntity livingEntity) {
        Optional<TrinketComponent> comp = TrinketsApi.getTrinketComponent(livingEntity);
        if (comp.isPresent()) {
            var equipped = comp.get().getEquipped(BPItems.BACKPACK);
            if (!equipped.isEmpty()) {
                return equipped.getFirst().getB();
            }
        }
        return ItemStack.EMPTY;
    }

    public static boolean isBackpackVisible(LivingEntity livingEntity) {
        return true;
    }
}