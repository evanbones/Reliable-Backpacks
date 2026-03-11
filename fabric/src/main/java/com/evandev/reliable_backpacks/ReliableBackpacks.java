package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ReliableBackpacks implements ModInitializer {
    @Override
    public void onInitialize() {
        Backpacks.init();

        PayloadTypeRegistry.playS2C().register(BackpackOpenPayload.TYPE, BackpackOpenPayload.STREAM_CODEC);
    }
}