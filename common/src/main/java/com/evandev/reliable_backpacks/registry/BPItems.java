package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.items.BackpackItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class BPItems {
    public static final BackpackItem BACKPACK = new BackpackItem(BPBlocks.BACKPACK, new Item.Properties()
            .stacksTo(1)
            .fireResistant());

    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"), BACKPACK);
    }
}