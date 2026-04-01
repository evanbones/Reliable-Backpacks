package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BPSounds {
    public static final SoundEvent BACKPACK_PLACE = new SoundEvent(new ResourceLocation(Constants.MOD_ID, "block.backpack.place"));
    public static final SoundEvent BACKPACK_OPEN = new SoundEvent(new ResourceLocation(Constants.MOD_ID, "block.backpack.open"));
    public static final SoundEvent BACKPACK_CLOSE = new SoundEvent(new ResourceLocation(Constants.MOD_ID, "block.backpack.close"));
    public static final SoundEvent BACKPACK_EQUIP = new SoundEvent(new ResourceLocation(Constants.MOD_ID, "item.backpack.equip"));

    public static void init() {
        if (Services.PLATFORM.getPlatformName().equals("Fabric")) {
            Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "block.backpack.place"), BACKPACK_PLACE);
            Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "block.backpack.open"), BACKPACK_OPEN);
            Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "block.backpack.close"), BACKPACK_CLOSE);
            Registry.register(Registry.SOUND_EVENT, new ResourceLocation(Constants.MOD_ID, "item.backpack.equip"), BACKPACK_EQUIP);
        }
    }
}