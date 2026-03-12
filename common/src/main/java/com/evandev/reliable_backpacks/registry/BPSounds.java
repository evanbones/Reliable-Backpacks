package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BPSounds {
    public static final SoundEvent BACKPACK_PLACE = SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "block.backpack.place"));
    public static final SoundEvent BACKPACK_OPEN = SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "block.backpack.open"));
    public static final SoundEvent BACKPACK_CLOSE = SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "block.backpack.close"));
    public static final SoundEvent BACKPACK_EQUIP = SoundEvent.createVariableRangeEvent(new ResourceLocation(Constants.MOD_ID, "item.backpack.equip"));

    public static void init() {
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) {
            Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "block.backpack.place"), BACKPACK_PLACE);
            Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "block.backpack.open"), BACKPACK_OPEN);
            Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "block.backpack.close"), BACKPACK_CLOSE);
            Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "item.backpack.equip"), BACKPACK_EQUIP);
        }
    }
}