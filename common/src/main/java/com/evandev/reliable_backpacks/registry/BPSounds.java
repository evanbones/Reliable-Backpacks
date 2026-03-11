package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Backpacks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BPSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Backpacks.MODID);

    public static final Holder<SoundEvent> BACKPACK_PLACE = SOUND_EVENTS.register("block.backpack.place", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BACKPACK_OPEN = SOUND_EVENTS.register("block.backpack.open", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BACKPACK_CLOSE = SOUND_EVENTS.register("block.backpack.close", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> BACKPACK_EQUIP = SOUND_EVENTS.register("item.backpack.equip", SoundEvent::createVariableRangeEvent);
}
