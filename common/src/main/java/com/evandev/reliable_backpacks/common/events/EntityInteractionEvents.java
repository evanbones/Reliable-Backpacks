package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.common.items.BackpackItemContainer;
import com.evandev.reliable_backpacks.platform.Services;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class EntityInteractionEvents {

    public static InteractionResult onEntityInteract(Player player, Entity targetEntity) {
        LivingEntity target = targetEntity instanceof LivingEntity ? (LivingEntity) targetEntity : null;
        ItemStack item = target != null ? Services.PLATFORM.getBackpack(target) : ItemStack.EMPTY;

        if (target != null && !item.isEmpty() && isBehind(player, target)) {
            if (!player.level().isClientSide()) {
                BackpackItemContainer container = new BackpackItemContainer(target, player);
                player.openMenu(new SimpleMenuProvider((a, b, c) -> new ShulkerBoxMenu(a, player.getInventory(), container), Component.translatable("container.backpack")));
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