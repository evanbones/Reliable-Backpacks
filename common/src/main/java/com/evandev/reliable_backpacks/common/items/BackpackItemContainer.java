package com.evandev.reliable_backpacks.common.items;

import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BackpackItemContainer extends SimpleContainer {
    LivingEntity target;
    Player player;
    ItemStack itemStack;
    Level level;

    public BackpackItemContainer(LivingEntity target, Player player) {
        super(27);
        this.target = target;
        this.player = player;
        this.itemStack = target.getItemBySlot(EquipmentSlot.CHEST);
        this.level = target.level();
    }

    public boolean stillValid(@NotNull Player player) {
        return target != null &&
                itemStack.is(BPItems.BACKPACK) &&
                itemStack.has(DataComponents.CONTAINER) &&
                player.distanceTo(target) < 5;
    }

    public void setChanged() {
        target.getItemBySlot(EquipmentSlot.CHEST).set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.getItems()));
        super.setChanged();
    }

    @Override
    public void startOpen(@NotNull Player player) {
        Services.PLATFORM.sendToTracking(target, new BackpackOpenPayload(true, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_OPEN, SoundSource.PLAYERS);
        super.startOpen(player);
    }

    @Override
    public void stopOpen(@NotNull Player player) {
        Services.PLATFORM.sendToTracking(target, new BackpackOpenPayload(false, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_CLOSE, SoundSource.PLAYERS);
        super.stopOpen(player);
    }
}