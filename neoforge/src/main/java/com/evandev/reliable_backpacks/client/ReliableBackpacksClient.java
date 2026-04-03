package com.evandev.reliable_backpacks.client;

import com.evandev.reliable_backpacks.Constants;
import com.evandev.reliable_backpacks.client.models.BackpackModel;
import com.evandev.reliable_backpacks.client.models.variants.OtherBackpackModel;
import com.evandev.reliable_backpacks.client.rendering.BackpackBlockRenderer;
import com.evandev.reliable_backpacks.client.rendering.BackpackLayer;
import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPLayers;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.component.DyedItemColor;
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
        event.register((stack, tintIndex) -> tintIndex == 1 ? DyedItemColor.getOrDefault(stack, -1) : -1, BPItems.BACKPACK);
    }

    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    BPItems.BACKPACK,
                    ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "dyed"),
                    (stack, level, entity, seed) -> stack.has(DataComponents.DYED_COLOR) ? 1.0F : 0.0F
            );
        });
    }

    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {
            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
        if (event.getRenderer(EntityType.ARMOR_STAND) instanceof ArmorStandRenderer armorStandRenderer) {
            armorStandRenderer.addLayer(new BackpackLayer<>(armorStandRenderer, event.getEntityModels()));
        }
    }
}