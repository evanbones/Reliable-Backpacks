package com.evandev.reliable_backpacks.registry;

import com.evandev.reliable_backpacks.Backpacks;
import com.evandev.reliable_backpacks.client.models.BackpackModel;
import com.evandev.reliable_backpacks.client.models.variants.OtherBackpackModel;
import com.evandev.reliable_backpacks.client.rendering.BackpackBlockRenderer;
import com.evandev.reliable_backpacks.client.rendering.BackpackLayer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = Backpacks.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BPLayers {

    public static final ModelLayerLocation BACKPACK = getLocation("backpack");
    public static final ModelLayerLocation BACKPACK_BLOCK = getLocation("backpack_block");

    public static final ModelLayerLocation OTHER_BACKPACK = getLocation("other_backpack");
    public static final ModelLayerLocation OTHER_BACKPACK_BLOCK = getLocation("other_backpack_block");

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    BPItems.BACKPACK.asItem(),
                    ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, "dyed"),
                    (stack, level, player, seed) -> isDyed(stack)
            );
        });
    }

    private static ModelLayerLocation getLocation(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Backpacks.MODID, name), "main");
    }

    private static float isDyed(ItemStack stack) {
        return stack.has(DataComponents.DYED_COLOR) ? 1 : 0;
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BACKPACK, BackpackModel::createBodyLayer);
        event.registerLayerDefinition(BACKPACK_BLOCK, BackpackModel::createBlockLayer);

        event.registerLayerDefinition(OTHER_BACKPACK, OtherBackpackModel::createBodyLayer);
        event.registerLayerDefinition(OTHER_BACKPACK_BLOCK, OtherBackpackModel::createBlockLayer);
    }


    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                BPBlockEntities.BACKPACK.get(),
                BackpackBlockRenderer::new
        );
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model skin : event.getSkins()) {

            if (event.getSkin(skin) instanceof PlayerRenderer playerRenderer) {

                playerRenderer.addLayer(new BackpackLayer<>(playerRenderer, event.getEntityModels()));
            }
        }
    }

    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(entityType);

            if (renderer instanceof HumanoidMobRenderer humanoidMobRenderer) {
                humanoidMobRenderer.addLayer(new BackpackLayer(humanoidMobRenderer, event.getEntityModels()));

            } else if (renderer instanceof ArmorStandRenderer armorStandRenderer) {
                armorStandRenderer.addLayer(new BackpackLayer<>(armorStandRenderer, event.getEntityModels()));
            }
        }
    }
}
