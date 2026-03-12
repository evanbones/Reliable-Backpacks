package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BPTags {
    public static final TagKey<Item> BACKPACK_BLACKLIST = TagKey.create(Registries.ITEM, new ResourceLocation(Constants.MOD_ID, "backpack_blacklist"));
}