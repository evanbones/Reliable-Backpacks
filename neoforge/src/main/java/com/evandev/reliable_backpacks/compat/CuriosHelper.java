package com.evandev.reliable_backpacks.compat;

import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.TriState;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.event.CurioCanEquipEvent;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.Optional;

public class CuriosHelper {

    public static void registerEvents() {
        NeoForge.EVENT_BUS.addListener(CuriosHelper::onCanEquip);
    }

    private static void onCanEquip(CurioCanEquipEvent event) {
        if (!ModConfig.get().enableCuriosIntegration || !ModConfig.get().enableChestSlot) return;
        if (!event.getStack().is(BPItems.BACKPACK)) return;

        if (event.getSlotContext().entity().getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            event.setEquipResult(TriState.FALSE);
        }
    }

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

    public static boolean unequipBackpack(Player player) {
        Optional<ICuriosItemHandler> curios = CuriosApi.getCuriosInventory(player);
        if (curios.isPresent() && curios.get().getCurios().get("back") != null) {
            var inventory = curios.get().getCurios().get("back").getStacks();
            for (int i = 0; i < inventory.getSlots(); i++) {
                if (inventory.getStackInSlot(i).is(BPItems.BACKPACK)) {
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
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