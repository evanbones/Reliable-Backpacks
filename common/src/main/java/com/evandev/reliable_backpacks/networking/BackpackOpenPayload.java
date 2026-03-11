package com.evandev.reliable_backpacks.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BackpackOpenPayload(boolean isOpen, int id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<BackpackOpenPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("mymod", "my_data"));

    public static final StreamCodec<ByteBuf, BackpackOpenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            BackpackOpenPayload::isOpen,
            ByteBufCodecs.INT,
            BackpackOpenPayload::id,
            BackpackOpenPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
