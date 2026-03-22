package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.evandev.reliable_backpacks.common.blocks.BackpackBlock.*;

@Mixin(value = ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements TraceableEntity {

    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract void setExtendedLifetime();

    @Inject(method = "playerTouch", at = @At("HEAD"))
    public void onPlayerTouch(Player player, CallbackInfo ci) {
        if (!this.level().isClientSide()) {
            BackpackPickupEvents.onItemEntityPickup(player, (ItemEntity) (Object) this);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ItemStack itemStack = this.getItem();

        if (!itemStack.is(BPItems.BACKPACK)) {
            return;
        }

        boolean hasContainer = itemStack.has(DataComponents.CONTAINER);
        ItemContainerContents container = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        boolean isEmpty = !hasContainer || container.stream().allMatch(ItemStack::isEmpty);

        if (hasContainer && !isEmpty) {
            ItemEntity itemEntity = (ItemEntity) (Object) this;

            if (itemEntity.getAge() > 0) {
                this.setExtendedLifetime();
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1.0, 0.9));
            if (this.isInWater() || this.isInLava()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.02, 0.0));
            }

            Level level = this.level();
            BlockPos pos = this.blockPosition();
            BlockPos targetPos = level.getBlockState(pos).canBeReplaced() ? pos : pos.above();

            boolean isUnobstructed = level.getBlockState(targetPos).canBeReplaced() &&
                    (!level.getFluidState(targetPos).isSource() || !level.getBlockState(targetPos.above()).canBeReplaced());

            if ((this.onGround() || level.getFluidState(pos).isSource()) && isUnobstructed) {

                BlockState state = BPBlocks.BACKPACK.defaultBlockState()
                        .setValue(FACING, this.getDirection())
                        .setValue(FLOATING, level.getFluidState(targetPos.below()).isSource() && !level.getFluidState(targetPos).isSource())
                        .setValue(WATERLOGGED, level.getFluidState(targetPos).getType() == Fluids.WATER);

                BackpackBlockEntity blockEntity = new BackpackBlockEntity(targetPos, state);
                blockEntity.applyComponentsFromItemStack(itemStack);

                blockEntity.newlyPlaced = true;
                blockEntity.placeTicks = 0;

                if (!level.isClientSide) {
                    level.setBlockAndUpdate(targetPos, state);
                    level.setBlockEntity(blockEntity);
                    level.playSound(null, targetPos, BPSounds.BACKPACK_PLACE.value(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                this.discard();
            }
        }
    }
}