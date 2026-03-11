package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Backpacks;
import com.evandev.reliable_backpacks.common.items.BackpackItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BPItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Backpacks.MODID);

    public static final DeferredItem<BackpackItem> BACKPACK = ITEMS.register("backpack",
            () -> new BackpackItem(BPBlocks.BACKPACK.get(), new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()));
}
