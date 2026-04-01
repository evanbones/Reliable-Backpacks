package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.items.BackpackItem;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public class BPItems {
    public static final BackpackItem BACKPACK = new BackpackItem(BPBlocks.BACKPACK, new Item.Properties()
            .tab(CreativeModeTab.TAB_TOOLS)
            .stacksTo(1)
            .fireResistant());

    public static void init() {
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) {
            Registry.register(Registry.ITEM, new ResourceLocation(Constants.MOD_ID, "backpack"), BACKPACK);
        }
    }
}