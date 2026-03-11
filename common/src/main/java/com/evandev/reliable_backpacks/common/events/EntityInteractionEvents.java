package com.evandev.reliable_backpacks.common.events;

import com.evandev.reliable_backpacks.Backpacks;
import com.evandev.reliable_backpacks.common.items.BackpackItemContainer;
import com.evandev.reliable_backpacks.registry.BPItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = Backpacks.MODID)
public class EntityInteractionEvents{

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();
        LivingEntity target = event.getTarget() instanceof LivingEntity ? (LivingEntity) event.getTarget() : null;
        ItemStack item = target != null ? target.getItemBySlot(EquipmentSlot.CHEST) : null;

        if (target != null && item.is(BPItems.BACKPACK) && isBehind(player, target)) {
            BackpackItemContainer container = new BackpackItemContainer(target, player);
            if (!item.has(DataComponents.CONTAINER)) { item.set(DataComponents.CONTAINER, ItemContainerContents.EMPTY); }
            item.get(DataComponents.CONTAINER).copyInto(container.getItems());
            player.openMenu(new SimpleMenuProvider((a, b, c) -> new ShulkerBoxMenu(a, player.getInventory(), container), Component.translatable("container.backpack")));
            event.setCancellationResult(InteractionResult.CONSUME);
            event.setCanceled(true);
        }
    }

    public static boolean isBehind(Player player, LivingEntity target) {
        float t = 1.0F;
        Vec3 vector = player.getPosition(t).subtract(target.getPosition(t)).normalize();
        vector = new Vec3(vector.x, 0, vector.z);
        return target.getViewVector(t).dot(vector) < 0;
    }
}
