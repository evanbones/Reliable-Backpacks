package com.evandev.reliable_backpacks.common.items;

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
import net.minecraft.world.entity.EquipmentSlot;
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
        super(27);
        this.target = target;
        this.player = player;
        this.itemStack = target.getItemBySlot(EquipmentSlot.CHEST);
        this.level = target.level();

        CompoundTag tag = this.itemStack.getTagElement("BlockEntityTag");
        if (tag != null && tag.contains("Items", 9)) {
            NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, items);
            for (int i = 0; i < items.size(); i++) {
                this.setItem(i, items.get(i));
            }
        }
    }

    public boolean stillValid(@NotNull Player player) {
        return target != null &&
                itemStack.is(BPItems.BACKPACK) &&
                player.distanceTo(target) < 5;
    }

    @Override
    public void setChanged() {
        CompoundTag tag = target.getItemBySlot(EquipmentSlot.CHEST).getOrCreateTagElement("BlockEntityTag");
        NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
        for (int i = 0; i < this.getContainerSize(); i++) {
            items.set(i, this.getItem(i));
        }
        ContainerHelper.saveAllItems(tag, items);
        super.setChanged();
    }

    @Override
    public void startOpen(@NotNull Player player) {
        Services.PLATFORM.sendToTracking(target, new BackpackOpenPayload(true, target.getId()));
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_OPEN, SoundSource.PLAYERS);
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
        target.level().playSound(null, target.blockPosition(), BPSounds.BACKPACK_CLOSE, SoundSource.PLAYERS);
        super.stopOpen(player);
    }
}