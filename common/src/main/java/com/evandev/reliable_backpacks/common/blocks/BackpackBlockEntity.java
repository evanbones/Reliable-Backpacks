package com.evandev.reliable_backpacks.common.blocks;

import com.evandev.reliable_backpacks.registry.BPBlockEntities;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ShulkerBoxMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class BackpackBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> itemStacks;
    public int openTicks;
    public boolean newlyPlaced;
    public int placeTicks;
    public int floatTicks;
    public boolean open;
    private int openCount;
    private int color;

    public BackpackBlockEntity(BlockPos pos, BlockState blockState) {
        super(BPBlockEntities.BACKPACK, pos, blockState);
        this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
        this.newlyPlaced = true;
    }


    public int getColor() {
        return color;
    }

    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            openCount = type;
            if (openCount == 0) { openTicks = 10; }
            if (openCount == 1) { openTicks = 0; }
            open = openCount > 0;
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
            return true;
        } else {
            return super.triggerEvent(id, type);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BackpackBlockEntity blockEntity) {
        if (blockEntity.open && blockEntity.openTicks < 10) { ++blockEntity.openTicks; }
        if (!blockEntity.open && blockEntity.openTicks > 0) { --blockEntity.openTicks; }

        if (blockEntity.newlyPlaced && blockEntity.placeTicks < 20) { ++blockEntity.placeTicks; }
        if (blockEntity.placeTicks == 20) { blockEntity.newlyPlaced = false; }

        if (blockEntity.floatTicks < 90) { ++blockEntity.floatTicks; }
        if (blockEntity.floatTicks == 90) { blockEntity.floatTicks = 0; }
    }

    public void onOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            if (this.openCount < 0) {
                this.openCount = 0;
            }
            ++openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, openCount);
            if (this.openCount == 1) {
                this.level.gameEvent(player, GameEvent.CONTAINER_OPEN, this.worldPosition);
                this.level.playSound(null, this.getBlockPos(), BPSounds.BACKPACK_OPEN.value(), SoundSource.BLOCKS);
            }
        }
    }

    public void stopOpen(@NotNull Player player) {
        if (!this.remove && !player.isSpectator()) {
            --openCount;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, openCount);
            if (this.openCount <= 0) {
                this.level.gameEvent(player, GameEvent.CONTAINER_CLOSE, this.worldPosition);
                this.level.playSound(null, this.getBlockPos(), BPSounds.BACKPACK_CLOSE.value(), SoundSource.BLOCKS);
            }
        }
    }

    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.backpack");
    }


    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.itemStacks;
    }

    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.itemStacks = items;
    }


    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new ShulkerBoxMenu(id, player, this);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        this.loadFromTag(tag, registries);
    }

    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.itemStacks, false, registries);
        }
        tag.putInt("FloatTicks", this.floatTicks);
        tag.putBoolean("NewlyPlaced", this.newlyPlaced);
        tag.putInt("Color", this.color);
        setChanged();
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();

        saveAdditional(tag, registries);
        return tag;
    }

    protected void applyImplicitComponents(BlockEntity.@NotNull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        DyedItemColor dyedItemColor = componentInput.get(DataComponents.DYED_COLOR);
        this.color = dyedItemColor != null ? dyedItemColor.rgb() : 0;
    }

    protected void collectImplicitComponents(DataComponentMap.@NotNull Builder components) {
        super.collectImplicitComponents(components);
        if (color != 0) {
            components.set(DataComponents.DYED_COLOR, new DyedItemColor(color, true));
        }
    }

    public void loadFromTag(CompoundTag tag, HolderLookup.Provider levelRegistry) {
        this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag) && tag.contains("Items", 9)) {
            ContainerHelper.loadAllItems(tag, this.itemStacks, levelRegistry);
        }
        this.floatTicks = tag.getInt("FloatTicks");
        this.newlyPlaced = tag.getBoolean("NewlyPlaced");
        this.color = tag.getInt("Color");
    }

    public int getContainerSize() {
        return this.itemStacks.size();
    }
}
