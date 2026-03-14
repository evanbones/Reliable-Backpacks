package com.evandev.reliable_backpacks.platform;

import com.evandev.reliable_backpacks.ReliableBackpacks;
import com.evandev.reliable_backpacks.compat.CuriosCompat;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
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
    public void sendToTracking(Entity target, BackpackOpenPayload payload) {
        if (!target.level().isClientSide()) {
            ReliableBackpacks.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> target), payload);
        }
    }

    @Override
    public ItemStack getBackpack(LivingEntity entity) {
        if (isModLoaded("curios")) {
            ItemStack curio = CuriosCompat.getBackpack(entity);
            if (!curio.isEmpty()) return curio;
        }
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(BPItems.BACKPACK) ? chest : ItemStack.EMPTY;
    }

    @Override
    public boolean canEquipBackpack(LivingEntity entity) {
        if (isModLoaded("curios") && CuriosCompat.canEquipBackpack(entity)) return true;
        return entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    @Override
    public boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        if (isModLoaded("curios") && CuriosCompat.equipBackpack(entity, stack)) return true;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.CHEST, stack);
            return true;
        }
        return false;
    }

    @Override
    public void unequipBackpack(LivingEntity entity) {
        if (isModLoaded("curios") && CuriosCompat.unequipBackpack(entity)) return;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            entity.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        }
    }
}