package com.evandev.reliable_backpacks.common.menus;

import com.evandev.reliable_backpacks.config.ModConfig;
import com.evandev.reliable_backpacks.registry.BPMenus;
import com.evandev.reliable_backpacks.registry.BPTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BackpackMenu extends AbstractContainerMenu {
    private final Container container;
    private final int containerRows;

    public BackpackMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(ModConfig.get().backpackRows * 9));
    }

    public BackpackMenu(int containerId, Inventory playerInventory, Container container) {
        super(BPMenus.BACKPACK, containerId);
        this.containerRows = ModConfig.get().backpackRows;
        checkContainerSize(container, this.containerRows * 9);
        this.container = container;
        container.startOpen(playerInventory.player);
        int i = (this.containerRows - 4) * 18;

        for (int j = 0; j < this.containerRows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(container, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return !stack.is(BPTags.BACKPACK_BLACKLIST) && stack.getItem().canFitInsideContainerItems();
                    }
                });
            }
        }

        for (int l = 0; l < 3; ++l) {
            for (int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(playerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInventory, i1, 8 + i1 * 18, 161 + i));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    public int getRowCount() {
        return this.containerRows;
    }
}