package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class CuriosCompat {
    public static ItemStack getBackpack(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(handler -> handler.findFirstCurio(BPItems.BACKPACK))
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static boolean canEquipBackpack(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve().map(handler -> {
            var backHandler = handler.getCurios().get("back");
            if (backHandler != null) {
                for (int i = 0; i < backHandler.getSlots(); i++) {
                    if (backHandler.getStacks().getStackInSlot(i).isEmpty()) return true;
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        return CuriosApi.getCuriosInventory(entity).resolve().map(handler -> {
            var backHandler = handler.getCurios().get("back");
            if (backHandler != null) {
                for (int i = 0; i < backHandler.getSlots(); i++) {
                    if (backHandler.getStacks().getStackInSlot(i).isEmpty()) {
                        backHandler.getStacks().setStackInSlot(i, stack);
                        return true;
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean unequipBackpack(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(handler -> handler.findFirstCurio(BPItems.BACKPACK))
                .map(slotResult -> {
                    var id = slotResult.slotContext().identifier();
                    var index = slotResult.slotContext().index();
                    var handler2 = CuriosApi.getCuriosInventory(entity).resolve().get().getCurios().get(id);
                    if (handler2 != null) {
                        handler2.getStacks().setStackInSlot(index, ItemStack.EMPTY);
                        return true;
                    }
                    return false;
                }).orElse(false);
    }
}