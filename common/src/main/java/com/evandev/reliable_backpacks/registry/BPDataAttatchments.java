package com.evandev.reliable_backpacks.registry;

import com.mojang.serialization.Codec;
import com.evandev.reliable_backpacks.Backpacks;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BPDataAttatchments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Backpacks.MODID);

    public static final Supplier<AttachmentType<Integer>> OPEN_COUNT = ATTACHMENT_TYPES.register(
            "open_count", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );

    public static final Supplier<AttachmentType<Integer>> OPEN_TICKS = ATTACHMENT_TYPES.register(
            "open_ticks", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).build()
    );
}
