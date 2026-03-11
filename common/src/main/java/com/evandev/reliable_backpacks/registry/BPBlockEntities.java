package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BPBlockEntities {
    public static BlockEntityType<BackpackBlockEntity> BACKPACK;

    public static void init() {
        BACKPACK = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"),
                BlockEntityType.Builder.of(BackpackBlockEntity::new, BPBlocks.BACKPACK).build(null)
        );
    }
}