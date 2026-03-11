package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Backpacks;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BPBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Backpacks.MODID);


    public static final Supplier<BlockEntityType<BackpackBlockEntity>> BACKPACK = BLOCK_ENTITY_TYPES.register(
            "backpack",
            () -> BlockEntityType.Builder.of(BackpackBlockEntity::new, BPBlocks.BACKPACK.get()).build(null));
}
