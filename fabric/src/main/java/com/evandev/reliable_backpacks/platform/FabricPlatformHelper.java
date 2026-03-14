package com.evandev.reliable_backpacks.platform;

import com.evandev.reliable_backpacks.compat.TrinketsCompat;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

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
    public void sendToTracking(Entity target, BackpackOpenPayload payload) {
        if (!target.level().isClientSide()) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(payload.isOpen());
            buf.writeInt(payload.id());
            ResourceLocation packetId = new ResourceLocation("reliable_backpacks", "backpack_open");

            for (ServerPlayer player : PlayerLookup.tracking(target)) {
                ServerPlayNetworking.send(player, packetId, buf);
            }
            if (target instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, packetId, buf);
            }
        }
    }

    @Override
    public ItemStack getBackpack(LivingEntity entity) {
        if (isModLoaded("trinkets")) {
            ItemStack trinket = TrinketsCompat.getBackpack(entity);
            if (!trinket.isEmpty()) return trinket;
        }
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(BPItems.BACKPACK) ? chest : ItemStack.EMPTY;
    }

    @Override
    public boolean canEquipBackpack(LivingEntity entity) {
        if (isModLoaded("trinkets") && TrinketsCompat.canEquipBackpack(entity)) return true;
        return entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    @Override
    public boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        if (isModLoaded("trinkets") && TrinketsCompat.equipBackpack(entity, stack)) return true;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.CHEST, stack);
            return true;
        }
        return false;
    }

    @Override
    public void unequipBackpack(LivingEntity entity) {
        if (isModLoaded("trinkets") && TrinketsCompat.unequipBackpack(entity)) return;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            entity.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        }
    }
}