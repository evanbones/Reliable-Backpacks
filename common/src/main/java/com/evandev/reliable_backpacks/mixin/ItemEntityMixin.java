package com.evandev.reliable_backpacks.mixin;

import com.evandev.reliable_backpacks.common.blocks.BackpackBlockEntity;
import com.evandev.reliable_backpacks.common.events.BackpackPickupEvents;
import com.evandev.reliable_backpacks.registry.BPBlocks;
import com.evandev.reliable_backpacks.registry.BPItems;
import com.evandev.reliable_backpacks.registry.BPSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void onPlayerTouch(Player player, CallbackInfo ci) {
        if (!this.level().isClientSide() && BackpackPickupEvents.onItemEntityPickup(player, (ItemEntity) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ItemStack itemStack = this.getItem();

        if (!itemStack.is(BPItems.BACKPACK)) {
            return;
        }

        boolean hasContainer = itemStack.hasTag() && itemStack.getTag().contains("BlockEntityTag") && itemStack.getTag().getCompound("BlockEntityTag").contains("Items");
        boolean isEmpty = !hasContainer || itemStack.getTag().getCompound("BlockEntityTag").getList("Items", 10).isEmpty();

        if (hasContainer && !isEmpty) {
            ItemEntity itemEntity = (ItemEntity) (Object) this;

            if (itemEntity.getAge() > 0) {
                this.setExtendedLifetime();
            }

            this.setDeltaMovement(this.getDeltaMovement().multiply(0.9, 1.0, 0.9));
            if (this.isInWater() || this.isInLava()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, 20.0, 0.0));
            }

            Level level = this.level();
            BlockPos pos = this.blockPosition();
            boolean isUnobstructed = level.getBlockState(pos.above()).canBeReplaced() &&
                    (!level.getFluidState(pos.above()).isSource() || !level.getBlockState(pos.above(2)).canBeReplaced());

            if ((!level.getBlockState(pos).is(BlockTags.REPLACEABLE) || level.getFluidState(pos).isSource()) && isUnobstructed) {

                BlockState state = BPBlocks.BACKPACK.defaultBlockState()
                        .setValue(FACING, this.getDirection())
                        .setValue(FLOATING, level.getFluidState(pos).isSource() && !level.getFluidState(pos.above()).isSource())
                        .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER);

                BackpackBlockEntity blockEntity = new BackpackBlockEntity(pos.above(), state);
                CompoundTag nbt = itemStack.getTagElement("BlockEntityTag");
                if (nbt != null) {
                    blockEntity.load(nbt);
                }

                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos.above(), state);
                    level.setBlockEntity(blockEntity);
                    level.playSound(null, pos.above(), BPSounds.BACKPACK_PLACE, SoundSource.BLOCKS);
                }

                this.discard();
            }
        }
    }
}