package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.common.items.BackpackItemContainer;
import com.evandev.reliable_backpacks.common.menus.BackpackMenu;
import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class EntityInteractionEvents {

    public static InteractionResult onEntityInteract(Player player, Entity targetEntity) {
        if (!ModConfig.get().enableEntityStealing) {
            return InteractionResult.PASS;
        }

        LivingEntity target = targetEntity instanceof LivingEntity ? (LivingEntity) targetEntity : null;
        ItemStack item = target != null ? Services.PLATFORM.getBackpack(target) : ItemStack.EMPTY;

        if (target != null && item.is(BPItems.BACKPACK) && isBehind(player, target)) {

            if (!player.level().isClientSide()) {
                Objects.requireNonNull(player.getServer()).execute(() -> {
                    if (player.distanceTo(target) < 5) {
                        BackpackItemContainer container = new BackpackItemContainer(target, player);

                        Services.PLATFORM.openMenu((ServerPlayer) player, new SimpleMenuProvider((id, playerInv, playerEntity) -> {
                            return new BackpackMenu(id, playerInv, container);
                        }, Component.translatable("container.backpack")));
                    }
                });
            }

            return InteractionResult.sidedSuccess(player.level().isClientSide());
        }
        return InteractionResult.PASS;
    }

    public static boolean isBehind(Player player, LivingEntity target) {
        float t = 1.0F;
        Vec3 vector = player.getPosition(t).subtract(target.getPosition(t)).normalize();
        vector = new Vec3(vector.x, 0, vector.z);
        return target.getViewVector(t).dot(vector) < 0;
    }
}