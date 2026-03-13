package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.client.ClientConfigSetup;
import com.evandev.reliable_backpacks.client.ReliableBackpacksClient;
import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.common.events.EntityInteractionEvents;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.networking.BackpackPayloadHandler;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class ReliableBackpacks {
    private static boolean registered = false;

    public ReliableBackpacks(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::registerNetworking);
        modEventBus.addListener(this::addCreative);

        if (FMLEnvironment.dist.isClient()) {
            ClientConfigSetup.register(modContainer);
            modEventBus.addListener(ReliableBackpacksClient::registerLayers);
            modEventBus.addListener(ReliableBackpacksClient::registerItemColors);
            modEventBus.addListener(ReliableBackpacksClient::onClientSetup);
            modEventBus.addListener(ReliableBackpacksClient::registerRenderers);
            modEventBus.addListener(ReliableBackpacksClient::addPlayerLayers);
        }

        NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(this::onRightClickItem);
        NeoForge.EVENT_BUS.addListener(this::onEntityInteract);
    }

    private void onRegister(RegisterEvent event) {
        if (!registered) {
            Backpacks.init();
            registered = true;
        }
    }

    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(
                BackpackOpenPayload.TYPE,
                BackpackOpenPayload.STREAM_CODEC,
                (payload, context) -> BackpackPayloadHandler.handleClientData(payload, context.player())
        );
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = BackpackPickupEvents.onRightClickBlock(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(com.evandev.reliable_backpacks.registry.BPItems.BACKPACK);
        }
    }

    private void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        InteractionResult result = BackpackPickupEvents.onRightClickItem(event.getEntity(), event.getHand());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = EntityInteractionEvents.onEntityInteract(event.getEntity(), event.getTarget());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
}