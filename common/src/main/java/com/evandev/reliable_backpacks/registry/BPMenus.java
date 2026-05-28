package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.menus.BackpackMenu;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class BPMenus {
    public static final MenuType<BackpackMenu> BACKPACK = Services.PLATFORM.createMenuType(BackpackMenu::new);

    public static void init() {
        if (Services.PLATFORM.getPlatformName().equalsIgnoreCase("Fabric")) {
            Registry.register(
                    BuiltInRegistries.MENU,
                    new ResourceLocation(Constants.MOD_ID, "backpack"),
                    BACKPACK
            );
        }
    }
}