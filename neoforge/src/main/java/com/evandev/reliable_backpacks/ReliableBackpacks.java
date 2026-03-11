package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.client.ClientConfigSetup;
import com.evandev.reliable_backpacks.client.ReliableBackpacksClient;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.networking.BackpackPayloadHandler;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ReliableBackpacks {
    private static boolean registered = false;

    public ReliableBackpacks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerNetworking);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(modContainer);
            modEventBus.addListener(ReliableBackpacksClient::registerLayers);
            modEventBus.addListener(ReliableBackpacksClient::registerRenderers);
            modEventBus.addListener(ReliableBackpacksClient::addPlayerLayers);
        }
    }

    private void onRegister(RegisterEvent event) {
        if (!registered) {
            Backpacks.init();
            registered = true;
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CommonClass.init();
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                BackpackOpenPayload.TYPE,
                BackpackOpenPayload.STREAM_CODEC,
                (payload, context) -> BackpackPayloadHandler.handleClientData(payload, (Player) context.player())
        );
    }
}