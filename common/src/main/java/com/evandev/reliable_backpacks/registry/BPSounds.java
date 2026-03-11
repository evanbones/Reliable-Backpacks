package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Backpacks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BPSounds {
    public static SoundEvent BACKPACK_PLACE;
    public static SoundEvent BACKPACK_OPEN;
    public static SoundEvent BACKPACK_CLOSE;
    public static SoundEvent BACKPACK_EQUIP;

    public static void init() {
        BACKPACK_PLACE = register("block.backpack.place");
        BACKPACK_OPEN = register("block.backpack.open");
        BACKPACK_CLOSE = register("block.backpack.close");
        BACKPACK_EQUIP = register("item.backpack.equip");
    }

    private static SoundEvent register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}