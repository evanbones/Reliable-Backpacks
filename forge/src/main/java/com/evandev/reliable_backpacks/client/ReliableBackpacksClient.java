package com.evandev.reliable_backpacks.client;

import com.evandev.reliable_backpacks.client.models.BackpackModel;
import com.evandev.reliable_backpacks.client.models.variants.OtherBackpackModel;
import com.evandev.reliable_backpacks.client.rendering.BackpackBlockRenderer;
import com.evandev.reliable_backpacks.client.rendering.BackpackLayer;
import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPLayers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;

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

    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
    }
}