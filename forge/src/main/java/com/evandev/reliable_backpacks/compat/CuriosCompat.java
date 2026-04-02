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
        ItemStack backpackStack = new ItemStack(BPItems.BACKPACK);
        var validSlots = CuriosApi.getItemStackSlots(backpackStack, entity);
        if (validSlots.isEmpty()) return false;

        return CuriosApi.getCuriosInventory(entity).resolve().map(handler -> {
            for (String slotId : validSlots.keySet()) {
                var curioHandler = handler.getCurios().get(slotId);
                if (curioHandler != null) {
                    for (int i = 0; i < curioHandler.getSlots(); i++) {
                        if (curioHandler.getStacks().getStackInSlot(i).isEmpty()) return true;
                    }
                }
            }
            return false;
        }).orElse(false);
    }

    public static boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        var validSlots = CuriosApi.getItemStackSlots(stack, entity);
        if (validSlots.isEmpty()) return false;

        return CuriosApi.getCuriosInventory(entity).resolve().map(handler -> {
            for (String slotId : validSlots.keySet()) {
                var curioHandler = handler.getCurios().get(slotId);
                if (curioHandler != null) {
                    for (int i = 0; i < curioHandler.getSlots(); i++) {
                        if (curioHandler.getStacks().getStackInSlot(i).isEmpty()) {
                            curioHandler.getStacks().setStackInSlot(i, stack);
                            return true;
                        }
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

                    return CuriosApi.getCuriosInventory(entity).resolve().map(handler2 -> {
                        var curioHandler = handler2.getCurios().get(id);
                        if (curioHandler != null) {
                            curioHandler.getStacks().setStackInSlot(index, ItemStack.EMPTY);
                            return true;
                        }
                        return false;
                    }).orElse(false);
                }).orElse(false);
    }

    public static boolean isBackpackVisible(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity).resolve()
                .flatMap(handler -> handler.findFirstCurio(BPItems.BACKPACK))
                .map(slotResult -> slotResult.slotContext().visible())
                .orElse(true);
    }
}