package com.evandev.reliable_backpacks.platform;

import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.evandev.reliable_backpacks.compat.AccessoriesHelper;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;
import java.util.List;

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
        if (isModLoaded("accessories")) {
            if (AccessoriesHelper.canEquipBackpack(player)) return true;
        }
        return player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    @Override
    public void equipBackpack(Player player, ItemStack stack) {
        if (isModLoaded("accessories")) {
            if (AccessoriesHelper.equipBackpack(player, stack)) return;
        }
        player.setItemSlot(EquipmentSlot.CHEST, stack);
    }

    @Override
    public boolean isBackpackEquipped(LivingEntity livingEntity) {
        return !getEquippedBackpack(livingEntity).isEmpty();
    }

    @Override
    public ItemStack getEquippedBackpack(LivingEntity livingEntity) {
        if (livingEntity.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            return livingEntity.getItemBySlot(EquipmentSlot.CHEST);
        }
        if (isModLoaded("accessories")) {
            return AccessoriesHelper.getEquippedBackpack(livingEntity);
        }
        return ItemStack.EMPTY;
    }
}