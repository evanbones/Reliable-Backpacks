package com.evandev.reliable_backpacks.client;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.client.models.BackpackModel;
import com.evandev.reliable_backpacks.client.models.variants.OtherBackpackModel;
import com.evandev.reliable_backpacks.client.rendering.BackpackBlockRenderer;
import com.evandev.reliable_backpacks.client.rendering.BackpackLayer;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.networking.BackpackPayloadHandler;
import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPLayers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ReliableBackpacksClient {
    public static void init(IEventBus modEventBus) {
        ClientConfigSetup.register();
        modEventBus.addListener(ReliableBackpacksClient::registerLayers);
        modEventBus.addListener(ReliableBackpacksClient::registerRenderers);
        modEventBus.addListener(ReliableBackpacksClient::addPlayerLayers);
        modEventBus.addListener(ReliableBackpacksClient::onClientSetup);
        modEventBus.addListener(ReliableBackpacksClient::registerItemColors);
    }

    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BPLayers.BACKPACK, BackpackModel::createBodyLayer);
        event.registerLayerDefinition(BPLayers.BACKPACK_BLOCK, BackpackModel::createBlockLayer);
        event.registerLayerDefinition(BPLayers.OTHER_BACKPACK, OtherBackpackModel::createBodyLayer);
        event.registerLayerDefinition(BPLayers.OTHER_BACKPACK_BLOCK, OtherBackpackModel::createBlockLayer);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BPBlockEntities.BACKPACK, BackpackBlockRenderer::new);
    }

    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
    }

    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.getItem() instanceof DyeableLeatherItem dyeable) {
                return dyeable.getColor(stack);
            }
            return -1;
        }, BPItems.BACKPACK);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(BPItems.BACKPACK, new ResourceLocation(Constants.MOD_ID, "dyed"),
                    (stack, level, entity, seed) -> {
                        return stack.getTagElement("display") != null && stack.getTagElement("display").contains("color", 99) ? 1.0F : 0.0F;
                    }
            );
        });
    }

    public static void handleBackpackOpenPayload(BackpackOpenPayload payload) {
        BackpackPayloadHandler.handleClientData(payload, Minecraft.getInstance().player);
    }
}
