package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class CuriosHelper {

    public static boolean canEquipBackpack(Player player) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(player);
        if (curios.isPresent() && curios.get().getCurios().get("back") != null) {
            var inventory = curios.get().getCurios().get("back").getStacks();
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).isEmpty()) return true;
            }
        }
        return false;
    }

    public static boolean equipBackpack(Player player, ItemStack stack) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(player);
        if (curios.isPresent() && curios.get().getCurios().get("back") != null) {
            var inventory = curios.get().getCurios().get("back").getStacks();
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    inventory.setStackInSlot(i, stack);
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack getEquippedBackpack(LivingEntity livingEntity) {
        Optional<SlotResult> result = CuriosApi.getCuriosInventory(livingEntity).flatMap(inv -> inv.findFirstCurio(BPItems.BACKPACK));
        return result.map(SlotResult::stack).orElse(ItemStack.EMPTY);
    }

    public static boolean isBackpackVisible(LivingEntity livingEntity) {
        Optional<SlotResult> result = CuriosApi.getCuriosInventory(livingEntity).flatMap(inv -> inv.findFirstCurio(BPItems.BACKPACK));
        return result.map(SlotResult::slotContext).map(SlotContext::visible).orElse(true);
    }
}