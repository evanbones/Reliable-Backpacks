package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class BPSounds {
    public static Holder<SoundEvent> BACKPACK_PLACE;
    public static Holder<SoundEvent> BACKPACK_OPEN;
    public static Holder<SoundEvent> BACKPACK_CLOSE;
    public static Holder<SoundEvent> BACKPACK_EQUIP;

    public static void init() {
        BACKPACK_PLACE = register("block.backpack.place");
        BACKPACK_OPEN = register("block.backpack.open");
        BACKPACK_CLOSE = register("block.backpack.close");
        BACKPACK_EQUIP = register("item.backpack.equip");
    }

    private static Holder<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(Constants.MOD_ID, name);
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }
}