package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

import java.util.Set;

public class CuriosCompat {

    public static ItemStack getBackpack(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().findFirstCurio(entity, BPItems.BACKPACK)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static boolean canEquipBackpack(LivingEntity entity) {
        Set<String> validSlots = CuriosApi.getCuriosHelper().getCurioTags(BPItems.BACKPACK);
        if (validSlots.isEmpty()) return false;

        return CuriosApi.getCuriosHelper().getCuriosHandler(entity).resolve().map(handler -> {
            for (String slotId : validSlots) {
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
        Set<String> validSlots = CuriosApi.getCuriosHelper().getCurioTags(stack.getItem());
        if (validSlots.isEmpty()) return false;

        return CuriosApi.getCuriosHelper().getCuriosHandler(entity).resolve().map(handler -> {
            for (String slotId : validSlots) {
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
        return CuriosApi.getCuriosHelper().findFirstCurio(entity, BPItems.BACKPACK)
                .map(slotResult -> {
                    var id = slotResult.slotContext().identifier();
                    var index = slotResult.slotContext().index();

                    var optionalHandler = CuriosApi.getCuriosHelper().getCuriosHandler(entity).resolve();
                    if (optionalHandler.isPresent()) {
                        var handler2 = optionalHandler.get().getCurios().get(id);
                        if (handler2 != null) {
                            handler2.getStacks().setStackInSlot(index, ItemStack.EMPTY);
                            return true;
                        }
                    }
                    return false;
                }).orElse(false);
    }

    public static boolean isBackpackVisible(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().findFirstCurio(entity, BPItems.BACKPACK)
                .map(slotResult -> slotResult.slotContext().visible())
                .orElse(true);
    }
}