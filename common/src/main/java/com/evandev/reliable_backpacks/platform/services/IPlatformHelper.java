package com.evandev.reliable_backpacks.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.nio.file.Path;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    /**
     * Gets the configuration directory for the current platform.
     *
     * @return The path to the config directory.
     */
    Path getConfigDirectory();

    /**
     * Checks if the code is running on the physical client.
     * @return True if on the client, false if on a dedicated server.
     */
    boolean isPhysicalClient();

    /**
     * Sends a packet to all players tracking the given entity, and the entity itself if it's a player.
     */
    void sendToTracking(Entity target, CustomPacketPayload payload);

    /**
     * Checks if the given player can equip a backpack.
     */
    boolean canEquipBackpack(Player player);

    /**
     * Equips a backpack to the given player.
     */
    void equipBackpack(Player player, ItemStack stack);

    /**
     * Checks if the player has a backpack equipped in any slot (Chest or Accessories).
     */
    boolean isBackpackEquipped(LivingEntity livingEntity);

    /**
     * Gets the equipped backpack stack from the player.
     */
    ItemStack getEquippedBackpack(LivingEntity livingEntity);
}