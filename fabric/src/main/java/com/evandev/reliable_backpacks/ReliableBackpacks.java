package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.common.events.EntityInteractionEvents;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.CreativeModeTab;

public class ReliableBackpacks implements ModInitializer {
    @Override
    public void onInitialize() {
        Backpacks.init();

        UseBlockCallback.EVENT.register(BackpackPickupEvents::onRightClickBlock);

        UseItemCallback.EVENT.register((player, level, hand) -> {
            var result = BackpackPickupEvents.onRightClickItem(player, hand);
            if (result.consumesAction()) {
                return InteractionResultHolder.success(player.getItemInHand(hand));
            }
            if (result.shouldSwing()) {
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                EntityInteractionEvents.onEntityInteract(player, entity)
        );

        ResourceKey<CreativeModeTab> toolsTab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, new ResourceLocation("tools_and_utilities"));

        ItemGroupEvents.modifyEntriesEvent(toolsTab).register(entries -> {
            entries.accept(BPItems.BACKPACK);
        });
    }
}