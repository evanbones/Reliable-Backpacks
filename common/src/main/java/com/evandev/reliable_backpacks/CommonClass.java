package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.config.ModConfig;
import net.minecraft.server.MinecraftServer;

public class CommonClass {
    public static void init() {
        ModConfig.load();
    }
}