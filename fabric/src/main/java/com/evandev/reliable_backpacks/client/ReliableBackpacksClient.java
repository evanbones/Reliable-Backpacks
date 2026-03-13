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
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.DyedItemColor;

public class ReliableBackpacksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(BPLayers.BACKPACK, BackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BPLayers.BACKPACK_BLOCK, BackpackModel::createBlockLayer);
        EntityModelLayerRegistry.registerModelLayer(BPLayers.OTHER_BACKPACK, OtherBackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BPLayers.OTHER_BACKPACK_BLOCK, OtherBackpackModel::createBlockLayer);

        BlockEntityRenderers.register(BPBlockEntities.BACKPACK, BackpackBlockRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(BackpackOpenPayload.TYPE, (payload, context) -> {
            BackpackPayloadHandler.handleClientData(payload, context.player());
        });

        ItemProperties.register(BPItems.BACKPACK, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "dyed"),
                (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            return tintIndex == 1 ? DyedItemColor.getOrDefault(stack, -1) : -1;
        }, BPItems.BACKPACK);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                registrationHelper.register(new BackpackLayer<>(playerRenderer, context.getModelSet()));
            }
        });
    }
}