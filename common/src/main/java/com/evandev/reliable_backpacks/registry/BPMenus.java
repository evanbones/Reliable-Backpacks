package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.menus.BackpackMenu;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class BPMenus {
    public static MenuType<BackpackMenu> BACKPACK;

    public static void init() {
        BACKPACK = Registry.register(
                BuiltInRegistries.MENU,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"),
                Services.PLATFORM.createMenuType(BackpackMenu::new)
        );
    }
}