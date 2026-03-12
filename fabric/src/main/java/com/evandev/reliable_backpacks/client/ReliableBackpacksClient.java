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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeableLeatherItem;

public class ReliableBackpacksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(BPLayers.BACKPACK, BackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BPLayers.BACKPACK_BLOCK, BackpackModel::createBlockLayer);
        EntityModelLayerRegistry.registerModelLayer(BPLayers.OTHER_BACKPACK, OtherBackpackModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(BPLayers.OTHER_BACKPACK_BLOCK, OtherBackpackModel::createBlockLayer);

        BlockEntityRenderers.register(BPBlockEntities.BACKPACK, BackpackBlockRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(new ResourceLocation("reliable_backpacks", "backpack_open"), (client, handler, buf, responseSender) -> {
            boolean isOpen = buf.readBoolean();
            int id = buf.readInt();
            BackpackOpenPayload payload = new BackpackOpenPayload(isOpen, id);

            client.execute(() -> {
                BackpackPayloadHandler.handleClientData(payload, client.player);
            });
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                registrationHelper.register(new BackpackLayer<>(playerRenderer, context.getModelSet()));
            }
        });

        ItemProperties.register(BPItems.BACKPACK, new ResourceLocation(Constants.MOD_ID, "dyed"),
                (stack, level, entity, seed) -> {
                    return stack.getTagElement("display") != null && stack.getTagElement("display").contains("color", 99) ? 1.0F : 0.0F;
                }
        );

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex == 1 && stack.getItem() instanceof DyeableLeatherItem dyeable) {
                return dyeable.getColor(stack);
            }
            return -1;
        }, BPItems.BACKPACK);
    }
}