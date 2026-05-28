package com.evandev.reliable_backpacks.platform;

import com.evandev.reliable_backpacks.ReliableBackpacks;
import com.evandev.reliable_backpacks.compat.CuriosCompat;
import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.platform.services.IPlatformHelper;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.extensions.IForgeMenuType;
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
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            return CuriosCompat.getBackpack(entity);
        }
        ItemStack chest = entity.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(BPItems.BACKPACK) ? chest : ItemStack.EMPTY;
    }

    @Override
    public boolean canEquipBackpack(LivingEntity entity) {
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            return CuriosCompat.canEquipBackpack(entity);
        }
        return entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
    }

    @Override
    public boolean equipBackpack(LivingEntity entity, ItemStack stack) {
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            return CuriosCompat.equipBackpack(entity, stack);
        }
        if (entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            entity.setItemSlot(EquipmentSlot.CHEST, stack);
            return true;
        }
        return false;
    }

    @Override
    public void unequipBackpack(LivingEntity entity) {
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            CuriosCompat.unequipBackpack(entity);
            return;
        }
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(BPItems.BACKPACK)) {
            entity.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean isBackpackVisible(LivingEntity entity) {
        if (ModConfig.get().enableCuriosIntegration && isModLoaded("curios")) {
            return CuriosCompat.isBackpackVisible(entity);
        }
        return true;
    }

    @Override
    public void openMenu(ServerPlayer player, MenuProvider provider) {
        player.openMenu(provider);
    }

    @Override
    public <T extends AbstractContainerMenu> MenuType<T> createMenuType(IPlatformHelper.MenuFactory<T> factory) {
        return IForgeMenuType.create(
                (windowId, inv, data) -> factory.create(windowId, inv)
        );
    }
}