package com.evandev.reliable_backpacks.client;

import com.evandev.reliable_backpacks.client.integration.ClothConfigIntegration;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class ClientConfigSetup {
    public static void register() {
        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ClothConfigIntegration.createScreen(parent))
            );
        }
    }
}