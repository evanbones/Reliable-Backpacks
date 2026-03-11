package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.common.blocks.BackpackBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public class BPBlocks {
    public static final BackpackBlock BACKPACK = new BackpackBlock(ofFullCopy(Blocks.BROWN_WOOL)
            .sound(new SoundType(1.0F, 1.0F,
                    SoundEvents.WOOL_BREAK,
                    SoundEvents.WOOL_STEP,
                    BPSounds.BACKPACK_PLACE.value(),
                    SoundEvents.WOOL_HIT,
                    SoundEvents.WOOL_FALL))
            .forceSolidOn());

    public static void init() {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "backpack"), BACKPACK);
    }
}