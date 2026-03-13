package com.evandev.reliable_backpacks;

import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.common.events.EntityInteractionEvents;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.CreativeModeTabs;

public class ReliableBackpacks implements ModInitializer {
    @Override
    public void onInitialize() {
        Backpacks.init();

        PayloadTypeRegistry.playS2C().register(BackpackOpenPayload.TYPE, BackpackOpenPayload.STREAM_CODEC);

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

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(content -> {
            content.accept(BPItems.BACKPACK);
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
                EntityInteractionEvents.onEntityInteract(player, entity)
        );
    }
}