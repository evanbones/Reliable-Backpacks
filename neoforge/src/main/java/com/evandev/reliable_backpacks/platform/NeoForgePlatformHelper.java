package com.evandev.reliable_backpacks.platform;

import com.evandev.reliable_backpacks.compat.AccessoriesHelper;
import com.evandev.reliable_backpacks.compat.CuriosHelper;
import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public void sendToTracking(Entity target, CustomPacketPayload payload) {
        if (!target.level().isClientSide()) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(target, payload);
        }
    }

    @Override
    public boolean canEquipBackpack(Player player) {
        if (isBackpackEquipped(player)) return false;

        if (ModConfig.get().enableAccessoriesIntegration && isModLoaded("accessories")) {
            if (AccessoriesHelper.canEquipBackpack(player)) return true;
        }
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            if (CuriosHelper.canEquipBackpack(player)) return true;
        }
        if (ModConfig.get().enableChestSlot) {
            return player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        }
        return false;
    }

    @Override
    public void equipBackpack(Player player, ItemStack stack) {
        if (ModConfig.get().enableAccessoriesIntegration && isModLoaded("accessories")) {
            if (AccessoriesHelper.equipBackpack(player, stack)) return;
        }
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            if (CuriosHelper.equipBackpack(player, stack)) return;
        }
        if (ModConfig.get().enableChestSlot) {
            player.setItemSlot(EquipmentSlot.CHEST, stack);
        }
    }

    @Override
    public ItemStack getEquippedBackpack(LivingEntity livingEntity) {
        if (ModConfig.get().enableAccessoriesIntegration && isModLoaded("accessories")) {
            ItemStack accStack = AccessoriesHelper.getEquippedBackpack(livingEntity);
            if (!accStack.isEmpty()) return accStack;
        }
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            ItemStack curiosStack = CuriosHelper.getEquippedBackpack(livingEntity);
            if (!curiosStack.isEmpty()) return curiosStack;
        }
        if (ModConfig.get().enableChestSlot) {
            ItemStack chest = livingEntity.getItemBySlot(EquipmentSlot.CHEST);
            return chest.is(BPItems.BACKPACK) ? chest : ItemStack.EMPTY;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isBackpackVisible(LivingEntity livingEntity) {
        if (ModConfig.get().enableAccessoriesIntegration && isModLoaded("accessories")) {
            if (!AccessoriesHelper.getEquippedBackpack(livingEntity).isEmpty()) {
                return AccessoriesHelper.isBackpackVisible(livingEntity);
            }
        }

        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            if (!CuriosHelper.getEquippedBackpack(livingEntity).isEmpty()) {
                return CuriosHelper.isBackpackVisible(livingEntity);
            }
        }

        if (ModConfig.get().enableChestSlot) {
            return livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK);
        }
        return false;
    }

    @Override
    public boolean isBackpackEquipped(LivingEntity livingEntity) {
        return !getEquippedBackpack(livingEntity).isEmpty();
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IPlatformHelper.MenuFactory<T> factory) {
        return IMenuTypeExtension.create((windowId, inv, data) -> factory.create(windowId, inv));
    }
}