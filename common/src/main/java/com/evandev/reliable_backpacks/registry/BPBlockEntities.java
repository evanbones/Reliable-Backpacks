package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BPBlockEntities {
    public static void init() {
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) {
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(Constants.MOD_ID, "backpack"), BACKPACK);
        }
    }

    public static final BlockEntityType<BackpackBlockEntity> BACKPACK = BlockEntityType.Builder.of(BackpackBlockEntity::new, BPBlocks.BACKPACK).build(null);
}