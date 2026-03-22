package com.evandev.reliable_backpacks.platform;

import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.evandev.reliable_backpacks.compat.AccessoriesHelper;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;
import java.util.List;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public void sendToTracking(Entity target, CustomPacketPayload payload) {
        if (!target.level().isClientSide()) {
            for (ServerPlayer player : PlayerLookup.tracking(target)) {
                ServerPlayNetworking.send(player, payload);
            }
            if (target instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, payload);
            }
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