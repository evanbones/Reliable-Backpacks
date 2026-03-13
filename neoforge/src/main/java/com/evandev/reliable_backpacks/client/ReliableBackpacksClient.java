package com.evandev.reliable_backpacks.client;

import com.evandev.reliable_backpacks.client.models.BackpackModel;
import com.evandev.reliable_backpacks.client.models.variants.OtherBackpackModel;
import com.evandev.reliable_backpacks.client.rendering.BackpackBlockRenderer;
import com.evandev.reliable_backpacks.client.rendering.BackpackLayer;
import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPLayers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ReliableBackpacksClient {
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BPLayers.BACKPACK, BackpackModel::createBodyLayer);
        event.registerLayerDefinition(BPLayers.BACKPACK_BLOCK, BackpackModel::createBlockLayer);
        event.registerLayerDefinition(BPLayers.OTHER_BACKPACK, OtherBackpackModel::createBodyLayer);
        event.registerLayerDefinition(BPLayers.OTHER_BACKPACK_BLOCK, OtherBackpackModel::createBlockLayer);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BPBlockEntities.BACKPACK, BackpackBlockRenderer::new);
    }

    public static void registerItemColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            return tintIndex == 1 ? net.minecraft.world.item.component.DyedItemColor.getOrDefault(stack, -1) : -1;
        }, com.evandev.reliable_backpacks.registry.BPItems.BACKPACK);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.item.ItemProperties.register(
                    com.evandev.reliable_backpacks.registry.BPItems.BACKPACK,
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(com.evandev.reliable_backpacks.Constants.MOD_ID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(net.minecraft.core.component.DataComponents.DYED_COLOR) ? 1.0F : 0.0F
            );
        });
    }

    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
    }
}