package com.evandev.reliable_backpacks.common.items;

import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.networking.BackpackOpenPayload;
import com.evandev.reliable_backpacks.platform.Services;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import com.evandev.reliable_backpacks.registry.BPTags;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BackpackItemContainer extends SimpleContainer {
    LivingEntity target;
    Player player;
    ItemStack itemStack;
    Level level;

    public BackpackItemContainer(LivingEntity target, Player player) {
        super(ModConfig.get().backpackRows * 9);
        this.target = target;
        this.player = player;
        this.itemStack = Services.PLATFORM.getBackpack(target);
        this.level = target.level();

        NonNullList<ItemStack> temp = NonNullList.withSize(256, ItemStack.EMPTY);
        CompoundTag tag = this.itemStack.getTagElement("BlockEntityTag");
        if (tag != null && tag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(tag, temp);
        }

        boolean overflowDropped = false;
        for (int i = 0; i < temp.size(); i++) {
            ItemStack stack = temp.get(i);
            if (!stack.isEmpty()) {
                if (i < this.getContainerSize()) {
                    this.setItem(i, stack);
                } else if (!this.level.isClientSide()) {
                    this.target.spawnAtLocation(stack);
                    overflowDropped = true;
                }
            }
        }

        if (overflowDropped) {
            this.setChanged();
        }
    }

    public boolean stillValid(@NotNull Player player) {
        return target != null && !Services.PLATFORM.getBackpack(target).isEmpty() && player.distanceTo(target) < 5;
    }

    @Override
    public void setChanged() {
        CompoundTag tag = Services.PLATFORM.getBackpack(target).getOrCreateTagElement("BlockEntityTag");
        NonNullList<ItemStack> items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < this.getContainerSize(); i++) {
            items.set(i, this.getItem(i));
        }
        ContainerHelper.saveAllItems(tag, items);
        super.setChanged();
    }

    @Override
    public void startOpen(@NotNull Player player) {
        Services.PLATFORM.sendToTracking(target, new BackpackOpenPayload(true, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_OPEN, SoundSource.PLAYERS, 1.0F, 1.0F);
        super.startOpen(player);
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (stack.is(BPTags.BACKPACK_BLACKLIST) || !stack.getItem().canFitInsideContainerItems()) {
            return false;
        }
        return super.canPlaceItem(index, stack);
    }

    @Override
    public void stopOpen(@NotNull Player player) {
        Services.PLATFORM.sendToTracking(target, new BackpackOpenPayload(false, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_CLOSE, SoundSource.PLAYERS, 1.0F, 1.0F);
        super.stopOpen(player);
    }
}