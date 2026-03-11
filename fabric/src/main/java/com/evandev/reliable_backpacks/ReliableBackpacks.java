package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.networking.BackpackPayloadHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ReliableBackpacks implements ModInitializer {
    @Override
    public void onInitialize() {
        Backpacks.init();
        CommonClass.init();

        PayloadTypeRegistry.playS2C().register(BackpackOpenPayload.TYPE, BackpackOpenPayload.STREAM_CODEC);
    }
}